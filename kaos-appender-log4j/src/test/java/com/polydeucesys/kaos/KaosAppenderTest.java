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

import com.polydeucesys.kaos.core.StringListMonitor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import javax.xml.ws.WebServiceException;
import java.nio.file.FileSystemNotFoundException;

import static com.polydeucesys.kaos.core.ConfigurationFactory.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;

/**
 * Created by kevinmclellan on 26/10/2016.
 */
public class KaosAppenderTest {

    @Before
    public void setConfigFactory(){
        System.setProperty(CONFIGURATION_CLASS_KEY, "com.polydeucesys.kaos.UnitTestConfigurationImpl");
    }

    @After
    public void cleanProps(){
        System.clearProperty(CONFIGURATION_CLASS_KEY);
    }

    @AfterClass
    public static void shutdownLoggers(){
        LogManager.shutdown();
    }

    @Test
    public void testAppenderExecutesBehaviours(){
        Logger testLogger = LogManager.getLogger("UnitTest1");
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

    @Test
    public void testAppenderRegexExecutesBehaviours(){
        Logger testLogger = LogManager.getLogger("UnitTest2");

        boolean didThrow = false;

        try {
            testLogger.info("This is just a line");
        }catch(Exception e){
            fail();
        }
        try {
            testLogger.info("So is this I think");
        }catch(Exception e){
            fail();
        }
        try {
            testLogger.info("DoThrow on this line");
            fail();
        }catch(Exception e){
            if(e instanceof WebServiceException || e instanceof FileSystemNotFoundException){

            }else{
                throw e;
            }
            didThrow = true;
        }

        assertTrue(didThrow);
    }
}
