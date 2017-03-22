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
public class SometimesBehaviour<T> extends BaseBehaviour<T> {
    private static final String DID_EXECUTE = "SOMETIMES: Executed %s";
    private static final String DID_NOT_EXECUTE = "SOMETIMES: Did Not Execute %s";

    private final Sometimes condition;
    private final BaseBehaviour sometimes;

    public SometimesBehaviour(float odds, BaseBehaviour sometimesBehaviour){
        condition = new Sometimes(odds);
        this.sometimes = sometimesBehaviour;
    }

    protected Monitorable sometimes(){
        return sometimes;
    }

    Sometimes condition(){
        return condition;
    }

    @Override
    public void setMonitor(Monitor monitor) {
        super.setMonitor(monitor);
        sometimes.setMonitor(monitor);
    }

    @Override
    public void doStart() {
        super.doStart();
        sometimes.start();
    }

    @Override
    public void doStop() {
        super.doStop();
        sometimes.stop();
    }


    @Override
    public boolean doExecute() throws Exception {
        boolean result =  condition.isPerform() && sometimes.execute();
        getMonitor().message(String.format(result?DID_EXECUTE:DID_NOT_EXECUTE, name()));
        return result;
    }

    @Override
    public boolean execute( T returnValue ) throws Exception {
        boolean result =  condition.isPerform() && sometimes.execute( returnValue );
        getMonitor().message(String.format(result?DID_EXECUTE:DID_NOT_EXECUTE, name()));
        return result;
    }

}
