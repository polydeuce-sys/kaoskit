package com.polydeucesys.kaos.example

import java.io.{BufferedReader, InputStreamReader}
import java.net.{ServerSocket, Socket}

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, Future, Promise}
import scala.io.Source
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/*
 * Copyright (c) 2017 Polydeuce-Sys Ltd
 *  
 *
 *       Licensed under the Apache License, Version 2.0 (the "License");
 *       you may not use this file except in compliance with the License.
 *       You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *       Unless required by applicable law or agreed to in writing, software
 *       distributed under the License is distributed on an "AS IS" BASIS,
 *       WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *       See the License for the specific language governing permissions and
 *       limitations under the License.
 */
/**
  * Start a string sharing server. The server takes is't config from the system properties it
  * is started with as follows:
  *
  * {@code "kaos.string.server.id"} - Server Id that will be used for all messages
  * {@code "kaos.string.server.port"} - Server Port that will be used to listen for messages
  * {@code "kaos.string.server.wka"} - Well Known Address list of other cluster members. This is formatted
  * as "serverId,host,port|serverId,host,port". Addresses are separated by | and formed of a comma separated
  * list of serverId, host and port.
  *
  * Created by kevinmclellan on 28/02/2017.
  */
object StringSharer extends App with LazyLogging{

  private final case class ClusterServer(serverId: String, host:String, port: Int )

  private final class StringServerListenThread( socket: Socket,
                                                messageHandlerPromise : Promise[MessageHandler] )
    extends Thread with LazyLogging{
    @volatile var done = false;
    val headerRegex = """'messageType':\s*'([A-Z]+)'""".r


    def processMessage( msg: String ) ={
      headerRegex.findFirstMatchIn(msg) match {
        case Some(matched) => messageHandler.handle(
          (matched.group(1) match {
            case "CS" => ClientStringMessage(msg)
            case "S" => StringMessage(msg)
            case "C" => ConnectMessage(msg)
            case "HB" => HeartbeatMessage(msg)
          }).getOrElse(throw MessageException(s"Failed to parse $msg") ))
        case None => {
          throw MessageException(s"Failed to parse header for $msg")
        }
      }
    }

    override def run: Unit = {
      val messageHandler = Await.result( messageHandlerPromise.future, Duration.Inf)
      val listenReader = Source.fromInputStream(socket.getInputStream())
      while(!done){
        logger.debug("waiting for input")
        val msg = listenReader.getLines().next()
        logger.debug(s"got $msg")
        Future[Unit]{
          processMessage(msg)
        } onComplete{
          case Success(_) => logger.debug(s"processed $msg")
          case Failure(e) => logger.error(s"error in processing ${e.getMessage}")
        }
      }
    }
  }


  private final class StringServerThread( serverIdentity: ServerIdentity,
                                          messageHandlerPromise: Promise[MessageHandler]) extends Thread {

    @volatile var done = false;

    override def run: Unit = {
      while(!done){
        val listen = serverIdentity.serverSocket.accept()
        (new StringServerListenThread(listen, messageHandlerPromise)).start()
      }

    }
  }

  private final class HBThread( serverIdentity : ServerIdentity, router : Router ) extends Thread with LazyLogging{
    val frequency = System.getProperty("kaos.string.server.hb.freq", "500").toLong

    override def run: Unit = {
      logger.info(s"Launched ${this.getClass().getName()}")
      while(true){
        logger.debug("sending BH")
        router.beat()
        Thread.sleep(frequency)
      }
    }
  }


  private def configureServerIdentity( ) = {
    logger.info(s"Reading server parameters")
    val serverId = System.getProperty("kaos.string.server.id")
    val serverPort = System.getProperty("kaos.string.server.port").toInt
    new ServerIdentity(serverId, serverPort)
  }

  private def readClusterConfig() = {
    val wkaString = System.getProperty("kaos.string.server.wka")
    logger.info(s"Configured with WKA list : $wkaString")
    wkaString.split("\\|").map(_.split(",")
      .map(_.trim)).map((a)=>new ClusterServer(a(0), a(1), a(2).toInt)).toList
  }

  private def configureTracker():Tracker = {
    val frequency = System.getProperty("kaos.string.server.hb.freq", "500").toLong
    val warn = System.getProperty("kaos.string.server.hb.warn", "3").toInt
    val died = System.getProperty("kaos.string.server.hb.dead", "5").toInt
    new Tracker(frequency, warn, died,
      (sid)=>{logger.warn(s"Missed $warn hb from $sid")},
      (sid)=>{logger.error(s"Missed $died hb from $sid. Marking as dead.");
        router.clearRouterEntry(sid)})
  }

  private def configureRouter( serverIdentity: ServerIdentity,
                               wka: List[ClusterServer],
                               tracker: Tracker) = {
    logger.info("Configure router")
    val router = new Router(serverIdentity, tracker)
    wka.foreach( (s)=>router.addRouterEntry(s.serverId, s.host, s.port))
    router
  }

  private val messageHandlerPromise:Promise[MessageHandler] = Promise()
  private val serverIdentity= configureServerIdentity()
  private val serverThread = new StringServerThread(serverIdentity, messageHandlerPromise)
  serverThread.start()
  private val wka = readClusterConfig()
  private val tracker = configureTracker()
  private val router = configureRouter(serverIdentity, wka, tracker)
  private val messageHandler = new MessageHandler(router, tracker)
  messageHandlerPromise.success(messageHandler)
  private val hbThread = new HBThread(serverIdentity, router);
  router.connect()
  hbThread.start();
  tracker.start()
}
