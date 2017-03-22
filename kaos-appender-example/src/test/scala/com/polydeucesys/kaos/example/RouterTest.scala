package com.polydeucesys.kaos.example

import java.io._
import java.net.{ServerSocket, Socket}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{FlatSpec, FunSuite}

import scala.collection.mutable
import scala.io.{Codec, Source}
import scala.util.Try

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
  * Created by kevinmclellan on 24/02/2017.
  */
class RouterTest extends FlatSpec {

  case class TestServerResult( raw:String, result: Option[Message])

  class TestServerThread(
                          port:Int,
                          done:AtomicBoolean,
                          resultContainer:scala.collection.mutable.MutableList[TestServerResult])
    extends Thread with LazyLogging{

    override def run: Unit = {
      val s:ServerSocket = new ServerSocket(port);
      logger.debug(s"Launching test reader thread on port $port")
      val listen = s.accept()
      try {
        val listenReader = Source.fromInputStream(listen.getInputStream())
        while (!done.get()) {
          logger.debug("waiting for input")
          val msg = listenReader.getLines().next()
          logger.debug(s"got $msg")
          val regex = """'messageType':\s*'([A-Z]+)'""".r
          regex.findFirstMatchIn(msg) match {
            case Some(matched) => matched.group(1) match {
              case "S" => resultContainer += new TestServerResult(msg, StringMessage(msg))
              case "C" => resultContainer += new TestServerResult(msg, ConnectMessage(msg))
              case "HB" => resultContainer += new TestServerResult(msg, HeartbeatMessage(msg))
            }
            case None => {
              fail("Regex failed. Shouldn't happen")
            }
          }
        }
      }catch{
        case t : Throwable => logger.error(s"Got throwable $t");
      }finally{
        listen.close()
      }
    }
  }

  "Router" should "propagate StringMessages" in {
    val router = new Router(new ServerIdentity("test", 9119), null)
    val off = new AtomicBoolean(false);
    val testPrt = 9119
    val results = new mutable.MutableList[TestServerResult]();
    val ts = new TestServerThread(9119, off, results)
    ts.start();
    router.addRouterEntry("testDestination", "localhost", 9119);
    val sm  = new ClientStringMessage("testSource", System.currentTimeMillis(), "Pass it on")
    router.propagate(sm);
    Thread.sleep(1000L)
    off.set(false);
    ts.join(2000L);
    assertResult(1){ results.size }
    val result = results.get(0)
    assertResult(("testDestination", "Pass it on")){
      result match {
        case Some(res) => (res.result.get.senderId, res.result.get.asInstanceOf[StringMessage].message)
        case None => fail()
      }
    }
  }
}
