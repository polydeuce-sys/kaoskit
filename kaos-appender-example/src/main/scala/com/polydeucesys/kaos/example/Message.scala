package com.polydeucesys.kaos.example

import com.typesafe.scalalogging.{LazyLogging, StrictLogging}

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
  * Created by kevinmclellan on 15/02/2017.
  */
sealed trait Message {
  def senderId : String
}

case class MessageException(message: String = null, cause: Throwable = null)
  extends Exception(if(message!=null)message else if(cause != null) cause.getMessage else null, cause){

}

object ConnectMessage extends StrictLogging{
  def apply( json : String ): Option[ConnectMessage] = {
    val regex =
      """\{\s*'messageType'\s*\:\s*'C',
        |\s*'sender'\s*\:\s*'([^']+)',
        |\s*'hostname'\s*\:\s*'([^']+)',
        |\s*'port'\s*\:\s*'([^']+)',
        |\s*'timestamp'\s*\:\s*(\d+)\s*\}""".stripMargin.replaceAll("\n", "").r
    regex.findFirstMatchIn(json) match {
      case None => {
        logger.error("Unrecognized messge format")
        None
      }
      case Some(matched) => {
        val name = matched.group(1)
        val hostname = matched.group(2)
        val port = matched.group(3)
        val sourceTimestamp = matched.group(4).toLong
        Some(new ConnectMessage(name, hostname, port, sourceTimestamp))
      }
    }
  }
}

case class ConnectMessage( senderId: String, hostname : String, port : String, sourceTimestamp : Long  )
  extends Message with Jsonable
  with StrictLogging{
  override def toJson(): String =
    s"""{'messageType': 'C',
       |'sender': '$senderId',
       |'hostname': '$hostname',
       |'port': '$port',
       |'timestamp': $sourceTimestamp }""".stripMargin.replaceAll("\n", "")
}

object HeartbeatMessage extends StrictLogging{
  def apply( json : String ): Option[HeartbeatMessage] = {
    val regex = """\{\s*'messageType':\s*'HB',\s*'sender'\:\s*'([^']+)',\s*'timestamp'\:\s*(\d+)\s*\}""".r
    regex.findFirstMatchIn(json) match {
      case None => {
        logger.error("Unrecognized messge format")
        None
      }
      case Some(matched) => {
        val name = matched.group(1)
        val sourceTimestamp = matched.group(2).toLong
        Some(new HeartbeatMessage(name, sourceTimestamp))
      }
    }
  }
}


case class HeartbeatMessage(senderId:String, sourceTimestamp : Long ) extends Message
  with Jsonable
  with StrictLogging {
  override def toJson(): String = {
    s"{ 'messageType':'HB', 'sender': '$senderId', 'timestamp': $sourceTimestamp }"
  }
}

object StringMessage extends StrictLogging {
  def apply( json : String ):Option[StringMessage] = {
    val regex =
      """\{\s*'messageType'\s*:\s*'S'\s*,
        |\s*'sender'\s*\:\s*'([^']+)'\s*,
        |\s*'timestamp'\s*\:\s*(\d+)\s*,
        |\s*'message'\s*\:\s*'([^']+)'\s*\}""".stripMargin.replaceAll("\n", "").r
    regex.findFirstMatchIn(json) match {
      case None => {
        logger.error("Unrecognized messge format")
        None
      }
      case Some(matched) => {
        val name = matched.group(1)
        val sourceTimestamp = matched.group(2).toLong
        val message = matched.group(3)
        Some(new StringMessage(name, sourceTimestamp, message))
      }
    }
  }
}


case class StringMessage(senderId: String, sourceTimestamp : Long, message : String) extends Message
  with Jsonable
  with StrictLogging{
  override def toJson(): String =
    s"""{ 'messageType':'S',
       |'sender': '$senderId',
       |'timestamp': $sourceTimestamp,
       |'message': '$message'}""".stripMargin.replaceAll("\n", "")

}

object ClientStringMessage extends StrictLogging {
  def apply( json : String ):Option[ClientStringMessage] = {
    val regex =
      """\{\s*'messageType'\s*:\s*'CS'\s*,
        |\s*'sender'\s*\:\s*'([^']+)'\s*,
        |\s*'timestamp'\s*\:\s*(\d+)\s*,
        |\s*'message'\s*\:\s*'([^']+)'\s*\}""".stripMargin.replaceAll("\n", "").r
    regex.findFirstMatchIn(json) match {
      case None => {
        logger.error("Unrecognized messge format")
        None
      }
      case Some(matched) => {
        val name = matched.group(1)
        val sourceTimestamp = matched.group(2).toLong
        val message = matched.group(3)
        Some(new ClientStringMessage(name, sourceTimestamp, message))
      }
    }
  }
}

case class ClientStringMessage(senderId: String, sourceTimestamp : Long, message : String) extends Message
  with Jsonable
  with StrictLogging{
  override def toJson(): String =
    s"""{ 'messageType':'CS',
        |'sender': '$senderId',
        |'timestamp': $sourceTimestamp,
        |'message': '$message'}""".stripMargin.replaceAll("\n", "")

}
