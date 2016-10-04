package com.polydeucesys.kaos;


import com.polydeucesys.kaos.core.ExceptionHandler;
import com.polydeucesys.kaos.core.Strategy;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import java.util.concurrent.Callable;
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

/**
 * Log4J Appender which can run a Kaos strategy. This is best limited to
 * throwing RuntimeExceptions (should you feel your application is
 * capable of dealing with these), or using the RandomSleeper.
 * It does not support Modifiers, as there is nothing to return.
 *
 * Created by kevinmclellan on 30/09/2016.
 */
public class KaosAppender extends AppenderSkeleton{

    private Strategy kaosStrategy;
    private ExceptionHandler handler = new ExceptionHandler() {
        @Override
        public void handle(Exception e) {
            // NOOP by default
        }
    };

    private final Object strategyLock = new Object();

    @SuppressWarnings("unchecked")
    protected void append(LoggingEvent loggingEvent) {
        try {
            kaosStrategy.executeBefore();
            kaosStrategy.executeAfter(null);
        } catch (Exception e) {
            // NOOP
        }
    }

    @Override
    public void close() {
        synchronized(strategyLock) {
            kaosStrategy.stop();
        }
    }

    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void activateOptions(){
        synchronized(strategyLock){
            kaosStrategy.start();
        }
    }
}
