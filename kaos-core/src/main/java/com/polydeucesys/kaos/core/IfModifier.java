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

import com.polydeucesys.kaos.core.behaviours.conditions.BaseCondition;

/**
 * A conditional modifier which will execut one choice or another based on
 * the evaluation of a condition on the original value
 * Created by kevinmclellan on 20/02/2017.
 */
public class IfModifier<T> extends BaseModifier<T> {

    private static final String CONDITION_MESSAGE = "If condiditon returned %s, executing %s";
    private final BaseCondition<T> condition;
    private final BaseModifier<T> conditionTrue;
    private final BaseModifier<T> conditionFalse;

    public IfModifier(BaseCondition<T> condition, BaseModifier<T> conditionTrue, BaseModifier<T> conditionFalse) {
        this.condition = condition;
        this.conditionTrue = conditionTrue;
        this.conditionFalse = conditionFalse;
    }

    // visible for testing
    BaseCondition<T> condition(){
        return condition;
    }

    BaseModifier<T> conditionTrue(){
        return conditionTrue;
    }

    BaseModifier<T> conditionFalse(){
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
    protected T doModify(T original) {
        throw new RuntimeException("doModify called on IfModifier. This should not be possible");
    }

    @Override
    public T modify( T original ){
        T modified = original;
        try {
            if(condition().execute(original)){
                modified = conditionTrue.modify(original);
            }else{
                modified = conditionFalse.modify(original);
            }
        } catch (Exception e) {
            getMonitor().message(String.format("Condition %s threw exception %s for %s",
                    condition, e, original));
        }
        return modified;
    }
}
