package com.polydeucesys.kaos.example

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
  * Created by kevinmclellan on 21/02/2017.
  */
class MessageHandler( router: Router,tracker : Tracker ) extends LazyLogging{
  def handle( message: Message ) = message match {
    case connect@ConnectMessage(senderId,_,_,_) => {
      logger.info(s"Connection message from $senderId")
      tracker.receive(message)
    }
    case hb@HeartbeatMessage(senderId,_) => {
      logger.debug(s"Heartbeat message from $senderId")
      tracker.receive(hb)
    }
    case sm@StringMessage(senderId,_,_) => {
      if(tracker.isLive(senderId))
        logger.info(s"Sender $senderId propagated $sm")
      else
        logger.error(s"Error message received from invalid sender $senderId")
    }
    case csm@ClientStringMessage(senderId,_,_) => {
      logger.info(s"String message from $senderId.Propagating to other servers")
      router.propagate(csm)
    }
  }

}
