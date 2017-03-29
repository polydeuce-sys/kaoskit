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

import com.polydeucesys.kaos.core.behaviours.conditions.BaseCondition;

/**
 * Conditional behaviour which, if the {@code condition} evaluates to {@code true} will execute the
 * {@code conditionTrue} Behaviour, otherwise it will evaluate the {@code conditionFalse} Behaviour.
 *
 * Created by kevinmclellan on 29/09/2016.
 */
public class IfBehaviour<T> extends BaseBehaviour<T> {
    private static final String CONDITION_MESSAGE = "If condiditon returned %s, executing %s";
    private final BaseCondition<T> condition;
    private final BaseBehaviour<T> conditionTrue;
    private final BaseBehaviour<T> conditionFalse;

    public IfBehaviour(BaseCondition<T> condition, BaseBehaviour t, BaseBehaviour f){
        this.condition = condition;
        this.conditionTrue = t;
        this.conditionFalse = f;
    }

    // visible for testing
    BaseCondition<T> condition(){
        return condition;
    }

    BaseBehaviour conditionTrue(){
        return conditionTrue;
    }

    BaseBehaviour conditionFalse(){
        return conditionFalse;
    }

    @Override
    public void setMonitor(Monitor monitor){
        super.setMonitor(monitor);
        condition.setMonitor(monitor);
        conditionTrue.setMonitor(monitor);
        conditionFalse.setMonitor(monitor);
    }

    @Override
    public boolean doExecute() throws Exception {
        throw new Exception("doExecute called on IfBehaviour. This should not be possible");
    }

    @Override
    public boolean execute() throws Exception{
        boolean flag = condition.execute();
        boolean retVal = flag? conditionTrue.execute(): conditionFalse.execute();
        getMonitor().message(String.format(CONDITION_MESSAGE, flag, flag? conditionTrue.name(): conditionFalse.name()));
        return retVal;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean execute(T returnValue) throws Exception {
        boolean flag = condition.execute(returnValue);
        boolean retVal = flag? conditionTrue.execute(returnValue): conditionFalse.execute(returnValue);
        getMonitor().message(String.format(CONDITION_MESSAGE, flag, flag? conditionTrue.name(): conditionFalse.name()));
        return retVal;
    }

    @Override
    public void doStart() {
        super.doStart();
        condition.doStart();
        conditionTrue.doStart();
        conditionFalse.doStart();
    }

    @Override
    public void doStop() {
        super.doStop();
        condition.doStop();
        conditionTrue.doStop();
        conditionFalse.doStop();
    }
}
