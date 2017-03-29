package com.polydeucesys.kaos.core.behaviours;
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

import com.polydeucesys.kaos.core.RandomReset;
import com.polydeucesys.kaos.core.SometimesBehaviour;
import com.polydeucesys.kaos.core.SometimesModifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotNull;

/**
 * Created by kevinmclellan on 28/03/2017.
 */
public class SometimeBehaviourTest {

    @Before
    public void setConfigFactory(){
        System.setProperty("com.polydeucesys.kaos.test.rand.seq",
                "0.32, 0.25, 0.2, 0.54");
        RandomReset.resetRandom();
    }

    @After
    public void cleanProps(){
        System.clearProperty("com.polydeucesys.kaos.test.rand.seq");
        RandomReset.resetRandom();
    }


    @Test
    public void testSometimeBehaviour(){
        List<Exception> le = new ArrayList<>();
        le.add(new ExceptionThrowerTest.TestException1());
        ExceptionThrower et = new ExceptionThrower(le);

        SometimesBehaviour<String> sn = new SometimesBehaviour<>(0.23f, et);
        sn.start();
        // our random seq is : "0.32, 0.25, 0.2, 0.57" this uses 2 nums per iteration as
        // exception thrower uses one as well
        try {
            sn.execute();
        } catch (Exception e) {
            fail();
        }
        try {
            // 0.25 is not in the odds. didn't pick exception so only rolled once!).
            sn.execute();
        } catch (Exception e) {
            fail();
        }
        try {
            // 0.2 is not in the odds. didn't pick exception so only rolled once!).
            sn.execute();
            fail();
        } catch (Exception e) {
        }
        sn.stop();
    }
}
