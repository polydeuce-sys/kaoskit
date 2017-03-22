package com.polydeucesys.kaos.core.behaviours.conditions;
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

import com.polydeucesys.kaos.core.BaseBehaviour;

/**
 * Base class for conditionals used in the IfBehaviour.
 * Created by kevinmclellan on 25/01/2017.
 */
public abstract class BaseCondition<T> extends BaseBehaviour<T> {

    @Override
    public void doStart() {

    }

    @Override
    public void doStop() {

    }

    @Override
    public boolean doExecute() throws Exception {
        return false;
    }

    public abstract boolean doExecute( T returnValue ) throws Exception;

    @Override
    public boolean execute( T returnValue ) throws Exception {
        boolean result = doExecute(returnValue);
        monitorResult(result);
        return result;
    }
}
