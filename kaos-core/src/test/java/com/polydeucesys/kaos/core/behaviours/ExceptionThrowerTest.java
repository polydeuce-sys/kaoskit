package com.polydeucesys.kaos.core.behaviours;

import com.polydeucesys.kaos.core.Monitor;
import com.polydeucesys.kaos.core.NullMonitor;
import com.polydeucesys.kaos.core.RandomReset;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

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

/**
 * Created by kevinmclellan on 04/10/2016.
 */
public class ExceptionThrowerTest {

    static class TestException1 extends Exception{

    }

    static class TestException2 extends Exception{

    }

    @Test
    public void testDoesThrow(){
        // probability is uniform, so chance of 0 is split between chance of
        // high and low. so for 2 it is 0.25 -> 0, 0.25-0.75 -> 1, 0.75-1.0->0
        System.setProperty("com.polydeucesys.kaos.test.rand.seq",
                "0.74,0.221,0.51,0.799");
        RandomReset.resetRandom();
        List<Exception> le = new ArrayList<>();
        le.add(new TestException1());
        le.add(new TestException2());
        // 0.75
        ExceptionThrower et = new ExceptionThrower(le);
        et.setMonitor(new NullMonitor());
        try{
            boolean res = et.execute();
            fail("Should throw");
        }catch( TestException2 te2){

        }catch(Exception ex){
            fail("Wrong exception");
        }
        // 0.221
        try{
            boolean res = et.execute();
            fail("Should throw");
        }catch( TestException1 te1){

        }catch(Exception ex){
            fail("Wrong exception");
        }
        // 0.51
        try{
            boolean res = et.execute();
            fail("Should throw");
        }catch( TestException2 te2){

        }catch(Exception ex){
            fail("Wrong exception");
        }
        // 0.4999
        try{
            boolean res = et.execute();
            fail("Should throw");
        }catch( TestException1 te1){

        }catch(Exception ex){
            fail("Wrong exception");
        }

    }
}