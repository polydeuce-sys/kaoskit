package com.polydeucesys.kaos.example

import java.io.{BufferedWriter, OutputStreamWriter, PrintWriter}
import java.net.{ConnectException, Socket}

import com.typesafe.scalalogging.LazyLogging

import scala.io.StdIn
import scala.util.Random

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
  * Created by kevinmclellan on 03/03/2017.
  */
object StringSharerClient  extends App with LazyLogging{
  final case class ClusterServer(serverId: String, writer: PrintWriter )

  private def readClusterConfig() = {
    val wkaString = System.getProperty("kaos.string.server.wka")
    def makeWriter( hostname: String, port: Int) = {
      logger.info(s"Making writer for $hostname $port")
      val socket = {
        var connected = false
        var s:Socket = null
        while(!connected){
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
      new PrintWriter(new BufferedWriter(
        new OutputStreamWriter(socket.getOutputStream())))
    }
    wkaString.split("\\|").map(_.split(",")
      .map(_.trim)).map((a)=>new ClusterServer(a(0), makeWriter(a(1), a(2).toInt))).toList
  }


  private val wka = readClusterConfig()
  private val clientId = System.getProperty("kaos.string.client.id", "client")
  private val r = new Random();
  val done = false
  while(!done){
    val msg = StdIn.readLine()
    // randomly pick a target
    val writerEntry = wka(r.nextInt(wka.size - 1))
    logger.info(s"sending $msg to ${writerEntry.serverId}")
    val writer = writerEntry.writer
    writer.println((new ClientStringMessage(clientId,System.currentTimeMillis(),  msg)).toJson());
    writer.flush()
  }


}
