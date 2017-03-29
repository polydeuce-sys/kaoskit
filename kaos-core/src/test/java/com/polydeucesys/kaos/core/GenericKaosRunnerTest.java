package com.polydeucesys.kaos.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.FileSystemNotFoundException;

import static com.polydeucesys.kaos.core.ConfigurationFactory.CONFIGURATION_CLASS_KEY;
import static org.junit.Assert.*;

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
 * Created by kevinmclellan on 24/03/2017.
 */
public class GenericKaosRunnerTest {

    @Before
    public void setConfigFactory(){
        System.setProperty(CONFIGURATION_CLASS_KEY,
                UnitTestConfigurationImpl.class.getCanonicalName());
    }

    @After
    public void cleanProps(){
        System.clearProperty(CONFIGURATION_CLASS_KEY);
    }


    @Test
    public void testExceptionInRunner(){
        GenericKaosRunner gkr = new GenericKaosRunner();
        gkr.setStrategyName("s1");
        gkr.start();
        try{
            gkr.causeKaos();
            fail();
        }catch( FileSystemNotFoundException fsw){
            assertTrue(true);
        }catch( Exception ex){
            fail();
        }
        gkr.stop();
    }

    @Test
    public void testExceptionInRunnerWithIf(){
        GenericKaosRunner gkr = new GenericKaosRunner();
        gkr.setStrategyName("s2");
        gkr.start();
        try{
            gkr.causeKaos("Do nothing this time");
        }catch( Exception ex){
            fail();
        }
        try{
            gkr.causeKaos("runKaos");
            fail();
        }catch( FileSystemNotFoundException fsw){
            assertTrue(true);
        }catch( Exception ex){
            fail();
        }
        gkr.stop();
        try {
            gkr.causeKaos();
            fail();
        } catch ( IllegalStateException e ) {
            assertTrue(true);
        } catch( Exception ex ){
            ex.printStackTrace();
            fail();
        }
    }

}