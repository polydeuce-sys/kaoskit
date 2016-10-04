package com.polydeucesys.kaos.core;
/*
 * Copyright (c) 2016 Polydeuce-Sys Ltd
 * <p>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Random;

/**
 * Randomly perform a wrapped behaviour a given percentage of the time.
 * Created by kevinmclellan on 30/09/2016.
 */
public class SometimesBehaviour extends Sometimes implements Behaviour {
    private static final String DID_EXECUTE = "SOMETIMES: Executed %s";
    private static final String DID_NOT_EXECUTE = "SOMETIMES: Did Not Execute %s";


    private final BaseBehaviour sometimes;

    SometimesBehaviour(float odds, BaseBehaviour sometimesBehaviour){
        super(odds);
        this.sometimes = sometimesBehaviour;
    }

    @Override
    public boolean execute() throws Exception {
        boolean result =  isPerform() && sometimes.execute();
        getMonitor().message(String.format(result?DID_EXECUTE:DID_NOT_EXECUTE, name()));
        return result;    }

    @Override
    public boolean execute(Object returnValue) throws Exception {
        return execute();
    }

    @Override
    public void doStart() {
        sometimes.doStart();
    }

    @Override
    public void doStop() {
        sometimes.doStop();
    }
}
