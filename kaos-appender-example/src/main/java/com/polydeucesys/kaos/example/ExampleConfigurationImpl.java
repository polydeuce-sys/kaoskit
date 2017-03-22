package com.polydeucesys.kaos.example;
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
import com.polydeucesys.kaos.core.behaviours.ExceptionThrower;
import com.polydeucesys.kaos.core.behaviours.Interrupter;
import com.polydeucesys.kaos.core.behaviours.RandomSleeper;

import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Created by kevinmclellan on 13/02/2017.
 */
public class ExampleConfigurationImpl  implements Configuration {

    private final Map<String, Strategy> strategies = new HashMap<String, Strategy>();

    private class NoMonitor implements Monitor {

        @Override
        public void message(String s) {

        }

        @Override
        public void message(String s, Map<String, String> map) {

        }
    }

    private static class MessageModifier extends BaseModifier<String>{

        @Override
        protected String doModify(String s) {
            // lets add a new field after the header
            // say the node includes version for use with
            return s.replace("'sender'", "'version': '1.0.0.11', 'sender'");
        }
    }

    public ExampleConfigurationImpl(){
        Monitor m = new NoMonitor();
        StrategyBuilder b = new KaosStrategy.KaosStrategyBuilder();
        b.setName("default");
        b.setMonitor(m);
        RandomSleeper s1 = new RandomSleeper(5000L);
        Set<Thread.State> states = new TreeSet<>();
        states.add(Thread.State.WAITING);
        states.add(Thread.State.BLOCKED);
        states.add(Thread.State.TIMED_WAITING);
        Interrupter i1 = new Interrupter(states, false);
        SometimesBehaviour<Interrupter> s2 = new SometimesBehaviour(0.1f, i1);
        b.addBeforeBehaviour(s1).addAfterBehaviour(s2);
        strategies.put("default", b.build());
        // build server thread behaviour
        b = new KaosStrategy.KaosStrategyBuilder();
        b.setName("server-thread");
        b.setMonitor(m);
        b.addAfterBehaviour(i1);
        strategies.put("server-thread", b.build());
        b = new KaosStrategy.KaosStrategyBuilder();
        b.setName("hb-thread");
        b.setMonitor(m);
        b.addBeforeBehaviour(s1);
        strategies.put("hb-thread", b.build());
        b = new KaosStrategy.KaosStrategyBuilder();
        b.setName("msg-aspect");
        b.setMonitor(m);
        MessageModifier mod = new MessageModifier();
        mod.setMonitor(m);
        SometimesModifier<String> maybeModify = new SometimesModifier<String>(0.3f, mod);
        maybeModify.setMonitor(m);
        b.addModifier(maybeModify);
        strategies.put("msg-aspect", b.build());
        b = new KaosStrategy.KaosStrategyBuilder();
        b.setName("write-aspect");
        b.setMonitor(m);
        List<Exception> canThrow = new LinkedList<>();
        canThrow.add(new SocketTimeoutException("TestHandling"));
        ExceptionThrower t1 = new ExceptionThrower(canThrow);
        t1.setMonitor(m);
        SometimesBehaviour<ExceptionThrower> s3 = new SometimesBehaviour(0.1f, t1);
        s1.setMonitor(m);
        b.addBeforeBehaviour(s3);
        strategies.put("write-aspect", b.build());
    }

    @Override
    public Map<String, Strategy> strategiesByName() {
        return strategies;
    }

    @Override
    public Strategy strategyForName(String s) {
        return strategies.get(s);
    }
}
