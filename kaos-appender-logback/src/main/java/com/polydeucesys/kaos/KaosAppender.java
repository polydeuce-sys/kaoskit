package com.polydeucesys.kaos;
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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import com.polydeucesys.kaos.core.GenericKaosRunner;
import com.polydeucesys.kaos.core.Strategy;

/**
 * Logback appender. Uses the {@link GenericKaosRunner} to cause chaos
 * on the append method.
 * Created by kevinmclellan on 01/11/2016.
 */
public class KaosAppender extends AppenderBase<ILoggingEvent> {

    private class KaosStatusListener implements StatusListener{
        @Override
        public void addStatusEvent(Status status) {
            if(status.getOrigin() == KaosAppender.this &&
                    status.getThrowable() instanceof RuntimeException) throw (RuntimeException)status.getThrowable();
        }
    }

    private String strategyName;
    private GenericKaosRunner kaosRunner = new GenericKaosRunner();

    public void setStrategyName( String strategyName ){
        kaosRunner.setStrategyName(strategyName);
    }

    @Override
    public void start(){
        super.start();
        this.getContext().getStatusManager().add(new KaosStatusListener());
        kaosRunner.start();
    }

    @Override
    public void stop(){
        super.stop();
        kaosRunner.stop();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            kaosRunner.causeKaos();
        } catch (Exception e) {
            kaosRunner.errorHandler().error(e.getMessage(), e);
            addError(e.getMessage(), e);
        }
    }
}
