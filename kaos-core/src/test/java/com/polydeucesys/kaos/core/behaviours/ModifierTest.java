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

import com.polydeucesys.kaos.core.*;
import com.polydeucesys.kaos.core.behaviours.conditions.DoubleRangeBehaviour;
import com.polydeucesys.kaos.core.behaviours.conditions.LongRangeBehaviour;
import com.polydeucesys.kaos.core.behaviours.conditions.RegexBehaviour;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by kevinmclellan on 24/03/2017.
 */
public class ModifierTest {

    @Before
    public void setConfigFactory(){
        System.setProperty("com.polydeucesys.kaos.test.rand.seq",
                "0.32, 0.25, 0.2");
        RandomReset.resetRandom();
    }

    @After
    public void cleanProps(){
        System.clearProperty("com.polydeucesys.kaos.test.rand.seq");
        RandomReset.resetRandom();
    }

    public static class TestStringModifier extends BaseModifier<String> {

        @Override
        protected String doModify(String s) {
            // lets add a new field after the header
            // say the node includes version for use with
            return s + " got modified";
        }
    }

    public static class TestNegateModifier extends BaseModifier<Long> {

        @Override
        protected Long doModify(Long s) {
            // lets add a new field after the header
            // say the node includes version for use with
            return s * -1L;
        }
    }

    public static class TestNegateDoubleModifier extends BaseModifier<Double> {

        @Override
        protected Double doModify(Double s) {
            // lets add a new field after the header
            // say the node includes version for use with
            return s * -1.0;
        }
    }

    @Test
    public void testModifierCanModifyWithConditions(){
        StringListMonitor sm = new StringListMonitor();
        TestStringModifier tsm = new TestStringModifier();
        tsm.setMonitor(sm);
        IfModifier<String> ifMod = new IfModifier(new RegexBehaviour("^modify.+"),
                tsm, new DoNothing.Modifier<String>());
        ifMod.setMonitor(sm);
        // doesn't do anything but for illustration
        ifMod.start();
        String retval = ifMod.modify("Can't touch this");
        assertEquals("Can't touch this", retval);

        retval = ifMod.modify("modify Can't touch this");
        assertEquals("modify Can't touch this got modified", retval);
        ifMod.stop();
    }

    @Test
    public void testModifierCanModifyWithRangeConditions(){
        StringListMonitor sm = new StringListMonitor();
        TestNegateModifier tnm = new TestNegateModifier();
        tnm.setMonitor(sm);
        IfModifier<Long> ifMod = new IfModifier(new LongRangeBehaviour("5", "15"),
                tnm, new DoNothing.Modifier<Integer>());
        ifMod.setMonitor(sm);
        ifMod.start();
        Long retval = ifMod.modify(4L);
        assertEquals(4, retval.intValue());

        retval = ifMod.modify(12L);
        assertEquals(-12, retval.intValue());
        ifMod.stop();
        // Double
        TestNegateDoubleModifier tndm = new TestNegateDoubleModifier();
        tnm.setMonitor(sm);
        IfModifier<Double> ifModD = new IfModifier(new DoubleRangeBehaviour("5.5", "15.1242", "false", "true"),
                tndm, new DoNothing.Modifier<Double>());
        ifModD.setMonitor(sm);
        ifModD.start();
        Double retvalD = ifModD.modify(4.0);
        assertEquals(4.0, retvalD.doubleValue(), 0.01);

        retvalD = ifModD.modify(12.2);
        assertEquals(-12.2, retvalD.doubleValue(), 0.01);
        ifModD.stop();
    }

    @Test
    public void testNullRewriter(){
        NullRewriter<String> n = new NullRewriter<>();
        assertNull(n.modify("Does it matter?"));
    }

    @Test
    public void testSometimeModifier(){
        NullRewriter<String> n = new NullRewriter<>();
        SometimesModifier<String> sn = new SometimesModifier<>(0.23f, n);
        String toModify = "aString";
        sn.start();
        // our random seq is : "0.32, 0.25, 0.2"
        assertNotNull(sn.modify(toModify));
        assertNotNull(sn.modify(toModify));
        assertNull(sn.modify(toModify));
        sn.stop();
    }
}
