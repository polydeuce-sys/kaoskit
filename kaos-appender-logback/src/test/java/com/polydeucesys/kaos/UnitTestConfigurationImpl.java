package com.polydeucesys.kaos;
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
import com.polydeucesys.kaos.core.behaviours.RandomSleeper;
import com.polydeucesys.kaos.core.behaviours.conditions.RegexBehaviour;

import javax.xml.ws.WebServiceException;
import java.nio.file.FileSystemNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by kevinmclellan on 07/02/2017.
 */
public class UnitTestConfigurationImpl implements Configuration {

    private final Map<String, Strategy> strategies = new HashMap<String, Strategy>();

    public UnitTestConfigurationImpl(){
        Monitor m = new StringListMonitor();
        StrategyBuilder b = new KaosStrategy.KaosStrategyBuilder();
        b.setName("test1");
        b.setMonitor(m);
        RandomSleeper s1 = new RandomSleeper(50L);
        s1.setMonitor(m);
        List<Exception> le1 = new LinkedList<>();
        le1.add(new WebServiceException());
        le1.add(new FileSystemNotFoundException());
        ExceptionThrower e1 = new ExceptionThrower(le1);
        e1.setMonitor(m);
        SometimesBehaviour se1 = new SometimesBehaviour(1.0f, e1);
        se1.setMonitor(m);
        b.addBeforeBehaviour(s1);
        b.addAfterBehaviour(se1);
        strategies.put("test1", b.build());
        StrategyBuilder b2 = new KaosStrategy.KaosStrategyBuilder();
        b2.setName("test2");
        b2.setMonitor(m);
        RegexBehaviour re = new RegexBehaviour("^DoThrow");
        IfBehaviour<String> ifs = new IfBehaviour<>(re, e1, new DoNothing.Behaviour<>());
        ifs.setMonitor(m);
        b2.addAfterBehaviour(ifs);
        strategies.put("test2", b2.build());
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
