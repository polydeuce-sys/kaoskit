package com.polydeucesys.kaos.core.behaviours;

import com.polydeucesys.kaos.core.BaseBehaviour;
import com.polydeucesys.kaos.core.ConfigurationException;
import com.polydeucesys.kaos.core.RandomGenerator;

import java.util.Random;

/*
 *  Copyright 2016 Polydeuce-Sys Ltd
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

/**
 * A BaseBehaviour which calls {@link Thread#sleep(long)} for a random time span in the configured range
 * (defaulting to 1-100ms)
 * Created by kevinmclellan on 14/09/2016.
 */
public class RandomSleeper extends BaseBehaviour {
    private static final String INVALID_MAX_FMT = "Invalid max sleep time %d. Must be > 0";
    private final long maxSleep;

    public RandomSleeper(){
        this.maxSleep = 100L;
    }

    public RandomSleeper( long maxSleep ){
        if(maxSleep < 0) throw new ConfigurationException(String.format(INVALID_MAX_FMT, maxSleep));
        this.maxSleep = maxSleep;
    }

    long maxSleep(){
        return maxSleep;
    }

    private volatile long sleepTime = -1L;

    // exposed for testing
    long lastSleep(){
        return sleepTime;
    }

    public boolean doExecute() {
        boolean done = false;
        long start = System.currentTimeMillis();
        sleepTime = RandomGenerator.nextLong(maxSleep);
        while(!done) {
            try {
                long delta_t = System.currentTimeMillis() - start;
                if(delta_t < sleepTime){
                    Thread.sleep(sleepTime - delta_t);
                }
                done = true;
            } catch (InterruptedException e) {
            }
        }
        return true;
    }

}
