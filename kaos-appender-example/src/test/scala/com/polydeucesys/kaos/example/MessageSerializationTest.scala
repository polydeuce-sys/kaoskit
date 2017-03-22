package com.polydeucesys.kaos.example

import org.scalatest.FlatSpec

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
  * Created by kevinmclellan on 20/02/2017.
  */
class MessageSerializationTest extends FlatSpec {

  "Message header regex" should "work" in {
    val regex = """'messageType'\s*:\s*'([A-Z]+)'""".r
    def pullMessageHeader(expected: String, m:Jsonable) = {
      assertResult(expected) {
        regex.findFirstMatchIn(m.toJson()) match {
          case Some(matched) => matched.group(1)
          case None => fail()
        }
      }
    }
    val h = new HeartbeatMessage("testSender", 148334252)
    pullMessageHeader("HB",h)
    val c = new ConnectMessage("testSender", "localhost", "80", 123456677)
    pullMessageHeader("C", c)
    val s = new StringMessage("testSender", 143829241, "my message")
    pullMessageHeader("S", s)

  }


  "Heartbeat " should "serialize and deserialize" in {
    val h = new HeartbeatMessage("testSender", 148334252)
    val h2 = new HeartbeatMessage("testSender", 148334252)
    assertResult(h2){
      HeartbeatMessage(h.toJson()).get
    }
  }

  "Connect request" should "serialize and deserialize" in {
    val c = new ConnectMessage("testSender", "localhost", "80", 123456677)
    val c2 = new ConnectMessage("testSender", "localhost", "80", 123456677)
    assertResult(c2){
      ConnectMessage(c.toJson()).get
    }
  }

  "String message" should "serialize and deserialize" in {
    val s = new StringMessage("testSender", 143829241, "my message")
    val s2 = new StringMessage("testSender", 143829241, "my message")
    assertResult(s2){
      StringMessage(s.toJson()).get
    }
  }
}
