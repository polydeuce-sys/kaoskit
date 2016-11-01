package com.polydeucesys.kaos.core.behaviours;
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
import com.polydeucesys.kaos.core.RandomReset;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by kevinmclellan on 12/10/2016.
 */
public class RandomSleeperTest {

    @Test
    public void testSleepsAboutRight(){
        System.setProperty("com.polydeucesys.kaos.test.rand.seq",
                "0.32, 0.25, 0.2");
        RandomReset.resetRandom();
        RandomSleeper s1 = new RandomSleeper();
        RandomSleeper s2 = new RandomSleeper(1000L);
        Monitor testMonitor = new Monitor(){
            @Override
            public void message(String message) {

            }

            @Override
            public void message(String message, Map<String, String> context) {

            }
        };
        s1.setMonitor(testMonitor);
        s2.setMonitor(testMonitor);
        long tstart = System.currentTimeMillis();
        try {
            s1.execute();
            assertTrue(s1.lastSleep() == 32L);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        try {
            s2.execute();
            assertTrue(s2.lastSleep() == 250);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        try {
            s1.execute();
            assertTrue(s1.lastSleep() == 20L);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
