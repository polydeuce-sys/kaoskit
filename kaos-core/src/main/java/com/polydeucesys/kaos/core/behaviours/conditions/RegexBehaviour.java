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
import com.polydeucesys.kaos.core.Behaviour;
import com.polydeucesys.kaos.core.KaosBase;

import java.util.regex.Pattern;

/**
 * A Regex based condition behaviour for use in the IfCondition
 * Created by kevinmclellan on 25/01/2017.
 */
public class RegexBehaviour extends BaseCondition<String> {
    private final Pattern regex;

    public RegexBehaviour( String expression ){
        regex = Pattern.compile(expression);
    }

    Pattern regex(){
        return regex;
    }

    @Override
    public boolean doExecute(String returnValue) throws Exception {
        return regex.matcher(returnValue).find();
    }
}
