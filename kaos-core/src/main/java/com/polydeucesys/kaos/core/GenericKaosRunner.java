package com.polydeucesys.kaos.core;
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

/**
 * An implementation of a single Strategy Kaos container. This is the basis of
 * the various LogAppenders, all of which simply instantiate an instance of
 * {@code GenericKaosRunner} and directly pass any calls to this. Uses the
 * {@link ConfigurationFactory} to get its' configuration rather than the
 * configuration files for the log framework.
 * Created by kevinmclellan on 01/11/2016.
 */
public class GenericKaosRunner implements Lifecycle{
    private static final String NO_STRATEGY_WITH_NAME = "No Strategy found for name %s";
    private static final String CANNOT_CHANGE_RUNNING_STRATEGY = "Strategy cannot be set after runner has started";

    private Strategy kaosStrategy;
    private volatile boolean isCausingKaos = false;
    private final Object lifecycleLock = new Object();
    private final GenericKaosErrorHandler errorHandler = new GenericKaosErrorHandler();

    private String strategyName;

    public static class GenericKaosErrorHandler{

        public void error(String msg) {
            // Noop - proper appenders can deal with issues
        }

        public void error(String msg, Throwable t) {
            if(t instanceof RuntimeException) throw (RuntimeException)t;
        }

        public void error(String msg, Object event, Throwable t) {
            if(t instanceof RuntimeException) throw (RuntimeException)t;
        }
    }

    public GenericKaosErrorHandler errorHandler(){
        return errorHandler;
    }

    public void setStrategyName( String strategyName){
        if(kaosStrategy != null){
            throw new ConfigurationException(CANNOT_CHANGE_RUNNING_STRATEGY);
        }
        this.strategyName = strategyName;
    }

    public void start() {
        synchronized(lifecycleLock) {
            kaosStrategy = ConfigurationFactory.getInstance().getConfiguration().strategyForName(strategyName);
            if (kaosStrategy == null) {
                throw new ConfigurationException(String.format(NO_STRATEGY_WITH_NAME, strategyName));
            }
            kaosStrategy.start();
        }
    }

    public void stop() {
        synchronized(lifecycleLock) {
            if(kaosStrategy.isStarted()) kaosStrategy.stop();
        }
    }

    public boolean isStarted() {
        return kaosStrategy.isStarted();
    }

    public void causeKaos() throws Exception{
        if(!isCausingKaos) {
            try {
                isCausingKaos = true;
                kaosStrategy.executeBefore();
                kaosStrategy.executeAfter(null);
            } finally {
                isCausingKaos = false;
            }
        }
    }

    public <T> void causeKaos( T value ) throws Exception{
        if(!isCausingKaos) {
            try {
                isCausingKaos = true;
                kaosStrategy.executeBefore();
                kaosStrategy.executeAfter(value);
            } finally {
                isCausingKaos = false;
            }
        }
    }
}
