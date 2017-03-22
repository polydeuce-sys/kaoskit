package com.polydeucesys.kaos.example

import java.util
import java.util.concurrent.locks.{Lock, ReadWriteLock, ReentrantReadWriteLock}

import com.typesafe.scalalogging.LazyLogging

import scala.annotation.tailrec
import scala.collection.mutable

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
  * monitors processes which it is aware of by tracking
  * reception of heartbet messages. Will log errors when a server has failed
  * Created by kevinmclellan on 20/02/2017.
  */
class Tracker( frequency: Long, warningThreshold: Int, deadThreshold: Int,
               warningAction: Function1[String, Unit], senderDiedAction: Function1[String, Unit]) extends Thread with LazyLogging{

  final case class TrackerEntry( senderId: String, receivedTimestamp: Long, missed: Int)

  private[this] val trackerMap = new scala.collection.mutable.HashMap[String,TrackerEntry]
  private[this] val trackerMapLock = new ReentrantReadWriteLock()

  def isLive( senderId : String ):Boolean = trackerMap.contains(senderId)

  def receive( message : Message ) = {
    def setEntry( senderId : String, receiveTimestamp: Long ) = {
      trackerMapLock.writeLock().lock()
      try {
        logger.debug(s"Added entry for $senderId with timestamp $receiveTimestamp")
        trackerMap.put(senderId, new TrackerEntry(senderId, receiveTimestamp, 0))
      }finally{
        trackerMapLock.writeLock().unlock()
      }
    }
    message match {
      case ConnectMessage(senderId,_ , _ , _) => {
        if (trackerMap.contains(senderId))
          logger.error(s"Received connect message for $senderId. Ignoring as already connected")
        else {
          setEntry(senderId, System.currentTimeMillis())
        }
      }
      case HeartbeatMessage(senderId, sentTimestamp) => {
        val receivedTime = System.currentTimeMillis();
        if(trackerMap.contains(senderId)){
          setEntry(senderId, receivedTime)
        }else{
          logger.error(s"Received heartbeat from $senderId.No such connected server")
        }
      }
      case _ => logger.error(s"Unrecognized message $message")
    }
  }

  @tailrec
  final def iterate( ): Unit ={
    def updateEntry( entry: TrackerEntry):Unit = {
      logger.debug(s"Missed heartbeat for ${entry.senderId}")
      trackerMapLock.writeLock().lockInterruptibly()
      try{
        trackerMap.put(entry.senderId,
          new TrackerEntry(entry.senderId,
          entry.receivedTimestamp, entry.missed + 1))
      }finally{
        trackerMapLock.writeLock().unlock()
      }
    }
    def clearEntry( entry : TrackerEntry ):Unit = {
      logger.warn(s"Clearing dead entry for ${entry.senderId}")
      trackerMapLock.writeLock().lockInterruptibly()
      try{
        trackerMap.remove(entry.senderId)
      }finally{
        trackerMapLock.writeLock().unlock()
      }
    }
    val now = System.currentTimeMillis()
    logger.debug(s"Iterating known servers to check heartbeat ${this.getClass.getName()}")
    trackerMapLock.readLock().lockInterruptibly()
    val actions : mutable.MutableList[Function0[Unit]] = new mutable.MutableList()
    try{
      trackerMap.foreach( (e) => {
        val delta = now - e._2.receivedTimestamp
        logger.debug(s"checking ${e._1} got $delta with $frequency")
        if(delta > frequency){
          actions += (()=> updateEntry(e._2))
          if(e._2.missed >= deadThreshold){
            actions += (() => {clearEntry(e._2);senderDiedAction(e._1);})
          }else if(e._2.missed >= warningThreshold){
            actions += (() => warningAction(e._1))
          }
        }
      })
    }finally{
      trackerMapLock.readLock().unlock()
    }
    actions.foreach( (u) => u.apply())
    Thread.sleep(frequency)
    iterate()
  }

  override def run: Unit = {
    iterate()
  }
}
