package com.polydeucesys.kaos.core;
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

import com.polydeucesys.kaos.core.behaviours.ExceptionThrower;
import com.polydeucesys.kaos.core.behaviours.Interrupter;
import com.polydeucesys.kaos.core.behaviours.RandomSleeper;
import com.polydeucesys.kaos.core.behaviours.ValueGetter;
import com.polydeucesys.kaos.core.behaviours.conditions.RegexBehaviour;
import org.junit.Test;

import java.util.List;

import static com.polydeucesys.kaos.core.ConfigurationFactory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by kevinmclellan on 25/10/2016.
 */
public class DefaultConfigurationImplTest {
    @Test
    public void testReadsConfigProperties(){
        System.setProperty(BEFORE_BEHAVIOURS_KEY, "sleep");
        System.setProperty(AFTER_BEHAVIOURS_KEY, "throw, interrupt");
        System.setProperty(SLEEP_PARAMS_KEY, "odds=0.25;max=50");
        System.setProperty(THROW_PARAMS_KEY, "matches=QueryDB\\s+;odds=0.62;throws=java.sql.SQLException,java.rmi.server.ServerNotActiveException");
        System.setProperty(INTERRUPT_PARAMS_KEY, "states=WAITING,TIMED_WAITING;first=false");

        Configuration defConfig = ConfigurationFactory.getInstance().getConfiguration();
        assertTrue("Does not return default config class", defConfig instanceof DefaultConfigurationImpl);
        Strategy one = defConfig.strategyForName("BarbequeuBob");
        Strategy two = defConfig.strategyForName("Randimity");
        assertTrue("Not returning constant strategy", one == two);
        assertTrue(one.beforeBehaviours().size() == 1);
        assertTrue(one.afterBehaviours().size() == 2);
        List<Behaviour<?>> befores = one.beforeBehaviours();
        Behaviour<?> s = befores.get(0);
        assertTrue(s instanceof SometimesBehaviour);
        RandomSleeper wrappedSleeper = (RandomSleeper) ((SometimesBehaviour)s).sometimes();
        assertTrue(0.25f == ((SometimesBehaviour) s).condition().odds());
        assertTrue(ValueGetter.getMaxSleep(wrappedSleeper) == 50L);
        List<Behaviour<?>> afters = one.afterBehaviours();
        IfBehaviour if1 = (IfBehaviour)afters.get(0);
        RegexBehaviour r1 = (RegexBehaviour) if1.condition();
        assertEquals("QueryDB\\s+",
                com.polydeucesys.kaos.core.behaviours.conditions.ValueGetter.
                        getPattern(r1).pattern());
        SometimesBehaviour s2 = (SometimesBehaviour)if1.conditionTrue();
        assertTrue(0.62f == ((SometimesBehaviour) s2).condition().odds());
        ExceptionThrower ex = (ExceptionThrower) s2.sometimes();
        List<Exception> throwables = ValueGetter.getExceptions(ex);
        assertTrue( 2 == throwables.size());
        assertTrue(throwables.get(0) instanceof java.sql.SQLException);
        assertTrue(throwables.get(1) instanceof java.rmi.server.ServerNotActiveException);
        Interrupter i = (Interrupter) afters.get(1);
        assertTrue(!ValueGetter.getFirstMatchOnly(i));
        assertTrue(ValueGetter.getSearchStates(i).contains(Thread.State.WAITING));
        assertTrue(ValueGetter.getSearchStates(i).contains(Thread.State.TIMED_WAITING));
        assertFalse(ValueGetter.getSearchStates(i).contains(Thread.State.BLOCKED));

    }
}
