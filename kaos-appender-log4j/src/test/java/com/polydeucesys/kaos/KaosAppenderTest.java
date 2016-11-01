package com.polydeucesys.kaos;
/*
 * Copyright (c) 2016 Polydeuce-Sys Ltd
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

import com.polydeucesys.kaos.core.Monitor;
import com.polydeucesys.kaos.core.StringListMonitor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import javax.xml.ws.WebServiceException;
import java.nio.file.FileSystemNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.polydeucesys.kaos.core.ConfigurationFactory.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by kevinmclellan on 26/10/2016.
 */
public class KaosAppenderTest {

    @Test
    public void testAppenderExecutesBehaviours(){
        System.setProperty(BEFORE_BEHAVIOURS_KEY, "sleep");
        System.setProperty(AFTER_BEHAVIOURS_KEY, "throw");
        System.setProperty(SLEEP_PARAMS_KEY, "max=50");
        System.setProperty(THROW_PARAMS_KEY, "odds=1.0;throws=javax.xml.ws.WebServiceException,java.nio.file.FileSystemNotFoundException");
        System.setProperty(MONITOR_CLASS_KEY,StringListMonitor.class.getCanonicalName());

        Logger testLogger = LogManager.getLogger("UnitTest");
        boolean didThrow = false;

        try {
            testLogger.info("Well this is a fine how do you do");
        }catch(Exception e){
           if(e instanceof WebServiceException || e instanceof FileSystemNotFoundException){

           }else{
               throw e;
           }
            didThrow = true;
        }
        boolean seenExecuted = false;

        for(String s : StringListMonitor.messages()){
            System.out.println(s);
            if(s.startsWith("Executed") && s.contains("RandomSleeper")){
                seenExecuted = true;
            }
        }
        assertTrue(seenExecuted);
        assertTrue(didThrow);
    }
}
