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

/**
 * Conditional behaviour which, if the {@code condition} evaluates to {@code true} will execute the
 * {@code t} Behaviour, otherwise it will evaluate the {@code f} Behaviour.
 *
 * Created by kevinmclellan on 29/09/2016.
 */
public class IfBehaviour<T> extends KaosBase implements Behaviour<T> {
    private static final String CONDITION_MESSAGE = "If condiditon returned %s, executing %s";
    private final BaseBehaviour condition;
    private final BaseBehaviour t;
    private final BaseBehaviour f;

    IfBehaviour(BaseBehaviour condition, BaseBehaviour t, BaseBehaviour f){
        this.condition = condition;
        this.t = t;
        this.f = f;
    }

    @Override
    public boolean execute() throws Exception{
        boolean flag = condition.execute();
        boolean retVal = flag?t.execute():f.execute();
        getMonitor().message(String.format(CONDITION_MESSAGE, flag, flag?t.name():f.name()));
        return retVal;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean execute(T returnValue) throws Exception {
        boolean flag = condition.execute(returnValue);
        boolean retVal = flag?t.execute(returnValue):f.execute(returnValue);
        getMonitor().message(String.format(CONDITION_MESSAGE, flag, flag?t.name():f.name()));
        return retVal;
    }

    @Override
    public void doStart() {
        condition.doStart();
        t.doStart();
        f.doStart();
    }

    @Override
    public void doStop() {
        condition.doStop();
        t.doStop();
        f.doStop();
    }
}
