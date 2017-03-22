package com.polydeucesys.kaos.example

import com.typesafe.scalalogging.LazyLogging
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
  * Not really unit testing. Just a scratchpad for checking that parsing
  * works
  * Created by kevinmclellan on 07/03/2017.
  */
class ConfigParamsTest extends FlatSpec with LazyLogging{

  "wka" should "be readable " in {
    val wkaString = "stringServerOne,localhost,9115|stringServerTwo,localhost,9116"
    val chopped = wkaString.split("\\|").map(_.split(",").map(_.trim()))
    val range = Range(0, chopped.size)

    logger.info(s"Chopped in to ${chopped.size} entries $range")
    range.foreach( (i) => chopped(i).foreach( (s) => logger.info(s"$i -> $s")))
  }

}
