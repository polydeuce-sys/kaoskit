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

import com.polydeucesys.kaos.core.Behaviour;
import com.polydeucesys.kaos.core.KaosBase;

/**
 * Created by kevinmclellan on 25/01/2017.
 */
public class DoubleRangeBehaviour extends BaseCondition<Double>{
    private Double min;
    private Double max;
    private boolean lowerInclusive;
    private boolean upperInclusive;


    public DoubleRangeBehaviour(String lower, String upper,
                                String lowerInclusive, String upperInclusive) {
        min = lower.isEmpty() ? Double.MIN_VALUE : Double.parseDouble(lower);
        max = upper.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(upper);
        this.lowerInclusive = lowerInclusive.isEmpty()?true:Boolean.parseBoolean(lowerInclusive);
        this.upperInclusive = upperInclusive.isEmpty()?true:Boolean.parseBoolean(upperInclusive);
    }


    @Override
    public boolean doExecute(Double returnValue) throws Exception {
        return (lowerInclusive?min<=returnValue:min<returnValue) &&
                (upperInclusive?returnValue<=max:returnValue<max);
    }
}
