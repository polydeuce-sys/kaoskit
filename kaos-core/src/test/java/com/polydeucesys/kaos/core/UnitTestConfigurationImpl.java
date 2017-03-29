package com.polydeucesys.kaos.core;
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
import com.polydeucesys.kaos.core.behaviours.conditions.RegexBehaviour;

import java.nio.file.FileSystemNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by kevinmclellan on 28/03/2017.
 */

public class UnitTestConfigurationImpl implements Configuration {

    private final Map<String, Strategy> strategies = new HashMap<String, Strategy>();
    public UnitTestConfigurationImpl(){
        Monitor m = new StringListMonitor();
        StrategyBuilder b = new KaosStrategy.KaosStrategyBuilder();
        b.setName("s1");
        b.setMonitor(m);
        List<Exception> le1 = new LinkedList<>();
        le1.add(new FileSystemNotFoundException());
        ExceptionThrower e1 = new ExceptionThrower(le1);
        e1.setMonitor(m);
        b.addBeforeBehaviour(e1);
        strategies.put("s1", b.build());
        RegexBehaviour r1 = new RegexBehaviour("run");
        IfBehaviour<String> ifs = new IfBehaviour<>(r1, e1, new DoNothing.Behaviour<>());
        b = new KaosStrategy.KaosStrategyBuilder();
        b.setName("s2");
        b.setMonitor(m);
        b.addAfterBehaviour(ifs);
        strategies.put("s2",b.build());
    }

    @Override
    public Map<String, Strategy> strategiesByName() {
        return strategies;
    }

    @Override
    public Strategy strategyForName(String name) {
        return strategies.get(name);
    }
}
