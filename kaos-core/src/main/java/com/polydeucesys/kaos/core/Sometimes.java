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
 * A Sometimes provides a control for a {@link Behaviour} or {@link Modifier} to execute
 * randomly.
 * Created by kevinmclellan on 03/10/2016.
 */
public class Sometimes{
    private static final String INVALID_ODDS_FMT = "Odds must be float between 0.0 and 1.0. %f invalid";
    private float odds;

    public Sometimes( float odds ){
        if(odds < 0.0f || odds > 1.0f)
            throw new ConfigurationException(String.format(INVALID_ODDS_FMT, odds));
        this.odds = odds;
    }

    public float roll(){
        return RandomGenerator.nextFloat();
    }

    public boolean isPerform(){
        boolean willPerform = (roll() <= odds);
        return willPerform;
    }

    // visbile for testing
    float odds(){
        return odds;
    }

}
