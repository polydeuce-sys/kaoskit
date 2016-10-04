package com.polydeucesys.kaos.core.behaviours;

import com.polydeucesys.kaos.core.BaseBehaviour;

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
    private final long maxSleep = 100L;
    // we're not doing cryptography. No need for SecureRandom or other such thing.
    private final Random r = new Random(System.currentTimeMillis());

    public boolean doExecute() {
        boolean done = false;
        while(!done) {
            try {
                Thread.sleep((long) (r.nextDouble() * maxSleep));
                done = true;
            } catch (InterruptedException e) {
            }
        }
        return true;
    }

    public void doStart() {
        // no need for setup
    }

    public void doStop() {
        // no need for shutdown
    }

}
