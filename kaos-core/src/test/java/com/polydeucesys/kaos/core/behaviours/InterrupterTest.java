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

import com.polydeucesys.kaos.core.Monitor;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;


import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by kevinmclellan on 27/10/2016.
 */
public class InterrupterTest {
    private static Monitor testMonitor = new Monitor(){
        @Override
        public void message(String message) {
            System.out.println(message);
        }

        @Override
        public void message(String message, Map<String, String> context) {

        }
    };

    @Test
    public void interruptWaitingTest(){
        final AtomicBoolean flag = new AtomicBoolean(false);
        final LinkedBlockingDeque<Object> notifier = new LinkedBlockingDeque<>();
        final Object waiter = new Object();
        Set<Thread.State> states = new TreeSet<>();
        states.add(Thread.State.WAITING);

        Interrupter i = new Interrupter(states,"^WaitThread$", false);
        i.setMonitor(testMonitor);
        i.start();
        Thread waitThread = new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this) {
                        notifier.add(waiter);
                        // interrupt only happens when thread waiting
                        // on itself
                        this.wait();
                    }
                    fail("Why not interrupted?");
                }catch(InterruptedException iex){
                    System.out.println("Interrupted");
                    flag.set(true);
                }finally{
                    notifier.add(waiter);
                }
            }
        };
        waitThread.setName("WaitThread");
        waitThread.start();
        try {
            notifier.takeFirst();
            Thread.sleep(20);
        } catch (InterruptedException e) {
            // oh, the irony...
        }
        synchronized(waiter){
            try {
                i.execute();
                notifier.takeFirst();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        i.stop();
        assertTrue(flag.get());
    }
}
