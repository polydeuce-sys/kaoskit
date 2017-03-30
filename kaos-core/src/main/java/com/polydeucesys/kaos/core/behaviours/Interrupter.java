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

import com.polydeucesys.kaos.core.BaseBehaviour;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Searches the populations of Threads and calls interrupt on the threads
 * as determined by the configuration. Can interupt only the first matching, or
 * all matching, can match by Thread state and Thread name. An empty set of {@code searchSttes} matches
 * any {@code ThreadState}, similarly, and empty {@code threadNamePattern} will match any {@code Thread} name.
 * Will use {@link ThreadGroup} {@code enumerate(Thread[] list) } to search the available threads.
 *
 * Created by kevinmclellan on 26/10/2016.
 */
public class Interrupter extends BaseBehaviour {
    private final Set<Thread.State> searchStates;
    private final Pattern threadNamePattern;
    private final boolean firstMatchOnly;

    public Interrupter(Set<Thread.State> searchStates,
                       String threadNamePattern,
                       boolean firstMatchOnly){
        this.searchStates = Collections.unmodifiableSet(searchStates);
        this.threadNamePattern = threadNamePattern.isEmpty()?null:Pattern.compile(threadNamePattern);
        this.firstMatchOnly = firstMatchOnly;
    }

    Set<Thread.State> searchStates(){
        return searchStates;
    }

    String threadNamePattern(){
        return threadNamePattern.pattern();
    }

    boolean isFirstMatchOnly(){
        return firstMatchOnly;
    }

    private boolean threadNameMatches( Thread t ){
        boolean match = true;
        if(threadNamePattern != null){
            Matcher m = threadNamePattern.matcher(t.getName());
            match = m.find();
        }
        return match;
    }

    @Override
    public boolean doExecute() throws Exception {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        Thread[] available = new Thread[tg.activeCount()];
        tg.enumerate(available);
        boolean didInterrupt = false;
        for(Thread t : available){
            if( searchStates.isEmpty() || searchStates.contains(t.getState()) ||
                    threadNameMatches(t)){
                t.interrupt();
                if(firstMatchOnly) return true;
                didInterrupt = true;
            }
        }
        return didInterrupt;
    }
}
