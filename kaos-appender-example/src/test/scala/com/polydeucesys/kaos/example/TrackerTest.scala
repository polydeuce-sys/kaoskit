package com.polydeucesys.kaos.example

import java.util.concurrent.{CountDownLatch, TimeUnit}
import java.util.concurrent.atomic.AtomicBoolean

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{FlatSpec, FunSuite}

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
  * Created by kevinmclellan on 28/02/2017.
  */
class TrackerTest extends FlatSpec with LazyLogging{

  def warningAction(w: AtomicBoolean)(senderId:String) = {
    logger.info(s"Warning : missed hb for $senderId")
    w.set(true)

  }

  def senderDiedAction(d: AtomicBoolean, o: CountDownLatch)(senderId: String) = {
    logger.info(s"Got sender died for $senderId")
    d.set(true)
    o.countDown()
  }

  "tracker" should "give warning and clear when no heartbeats" in {
    val w = new AtomicBoolean(false)
    val d = new AtomicBoolean(false)
    val c = new CountDownLatch(1);
    val testTracker = new Tracker(250L, 2, 4,warningAction(w), senderDiedAction(d, c))
    testTracker.receive(new ConnectMessage("testNode", "localhost", "9191", System.currentTimeMillis()))

    testTracker.start();
    c.await(3, TimeUnit.SECONDS)
    assertResult(true){
      logger.info(s"warned: ${w.get()} died: ${d.get()}")
      w.get() && d.get()
    }
  }

}
