package com.polydeucesys.kaos.example

import java.io.{BufferedWriter, OutputStreamWriter, PrintWriter}
import java.net.{ConnectException, Socket}

import com.typesafe.scalalogging.LazyLogging

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
  * ensures that messages are propagated to other servers
  * Created by kevinmclellan on 21/02/2017.
  */
class Router(serverIdentity: ServerIdentity, tracker : Tracker) extends LazyLogging {
  final case class RouterEntry( serverId: String, writer: PrintWriter)
  private[this] val routerMap = new scala.collection.mutable.HashMap[String, RouterEntry]

  private[this] def propagateInternal( msg: Jsonable , checkLive: Boolean = true) ={
    routerMap.foreach( (m) => {
      if(!checkLive || tracker.isLive(m._1)) {
        logger.debug(s"Sending $msg to ${m._1}")
        m._2.writer.println(msg.toJson())
        m._2.writer.flush()
      }else{
        logger.warn(s"Ignoring dead node ${m._1}")
      }
    })
  }

  def propagate( message : ClientStringMessage ) = {
    logger.info(s"Propagate $message to cluster")
    val msg = new StringMessage(serverIdentity.serverId, message.sourceTimestamp, message.message)
    propagateInternal(msg)
  }

  def clearRouterEntry( serverId: String ) = {
    routerMap.synchronized({
      logger.warn(s"Removing server $serverId")
      routerMap.remove(serverId)
    })
  }

  def connect() = {
      val connectMsg = new ConnectMessage(serverIdentity.serverId,
        serverIdentity.hostname, serverIdentity.port.toString, System.currentTimeMillis());
      propagateInternal(connectMsg, false)
  }

  def beat() = {
    val hb = new HeartbeatMessage(serverIdentity.serverId, System.currentTimeMillis())
    propagateInternal(hb)
  }

  def addRouterEntry( serverId: String, hostname: String, port: Int) = {
    logger.info(s"Adding router entry or $hostname $port")
    val socket:Socket = {
      var connected = false
      var s:Socket = null
      while(!connected){
        logger.debug(s"Trying connect to $hostname on $port")
        try{
          s = new Socket(hostname, port)
          connected = true
        }catch{
          case c: ConnectException => Thread.sleep(1000L);
        }
      }
      s
    }
    socket.setSoTimeout(500)
    val pw = new PrintWriter(new BufferedWriter(
      new OutputStreamWriter(socket.getOutputStream())))

    routerMap.synchronized({
      routerMap.put(serverId, new RouterEntry(serverId, pw))
    })
  }

}
