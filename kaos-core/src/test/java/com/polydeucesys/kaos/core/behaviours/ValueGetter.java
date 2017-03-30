package com.polydeucesys.kaos.core.behaviours;
/*
 * Copyright (c) 2016 Polydeuce-Sys Ltd
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

import java.util.List;
import java.util.Set;

/**
 * Test convenience class that can read the package private values on various behaviours to pass
 * up to config tests or similar tests which are not in the behaviour package, but require insight into
 * the values used in a behaviour.
 * Created by kevinmclellan on 26/10/2016.
 */
public class ValueGetter {

    public static long getMaxSleep(RandomSleeper rs){
        return rs.maxSleep();
    }

    public static long getLastSleep( RandomSleeper rs ) { return rs.lastSleep();}

    public static List<Exception> getExceptions(ExceptionThrower ex){
        return ex.exceptions();
    }

    public static Set<Thread.State> getSearchStates(Interrupter i){
        return i.searchStates();
    }

    public static String getThreadNamePattern( Interrupter i) { return i.threadNamePattern(); }

    public static boolean getFirstMatchOnly( Interrupter i){
        return i.isFirstMatchOnly();
    }

    private ValueGetter(){}

}
