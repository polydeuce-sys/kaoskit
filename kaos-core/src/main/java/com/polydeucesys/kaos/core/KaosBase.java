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

import java.util.Map;

/**
 * Base class used for Behaviour and Modifier implementations in this framework.
 * Created by kevinmclellan on 03/10/2016.
 */
public abstract class KaosBase implements Monitorable {
    private static final String INVALID_CONFIG = "Invalid configuration of instance %s";
    private static final String STARTED = "Started %s";
    private static final String STOPPED = "Stopped %s";


    private Monitor monitor = new NoOpMonitor();

    private volatile boolean started = false;

    public abstract void doStart();

    public abstract void doStop();

    public final void start(){
        doStart();
        monitor.message(String.format(STARTED,name()));
        started = true;
    }

    public final boolean isStarted(){
        return started;
    }

    public final void stop(){
        doStop();
        monitor.message(String.format(STOPPED,name()));
        started = false;
    }


    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    protected Monitor getMonitor(){
        return monitor;
    }

    public String name(){
        return this.getClass().getName();
    }

}
