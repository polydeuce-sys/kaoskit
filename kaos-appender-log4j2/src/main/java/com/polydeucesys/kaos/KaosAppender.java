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

import com.polydeucesys.kaos.core.GenericKaosRunner;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Created by kevinmclellan on 31/10/2016.
 */
@Plugin(name = "KaosAppender", category = "Core", elementType = "appender", printObject = true)
public class KaosAppender extends AbstractAppender {

    @PluginFactory
    public static KaosAppender createAppender(@PluginAttribute("name") String name,
                                              @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                              @PluginElement("Layout") Layout layout,
                                              @PluginElement("Filters") Filter filter,
                                              @PluginAttribute("strategyName") String strategyName) {
        if (name == null) {
            LOGGER.error("No name provided for StubAppender");
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        KaosAppender appender = new KaosAppender(name, filter, layout, ignoreExceptions);
        appender.setStrategyName(strategyName);
        return appender;
    }

    private String strategyName;
    private GenericKaosRunner kaosRunner = new GenericKaosRunner();

    private class KaosErrorHandler implements ErrorHandler {

        @Override
        public void error(String msg) {
            kaosRunner.errorHandler().error(msg);
        }

        @Override
        public void error(String msg, Throwable t) {
            kaosRunner.errorHandler().error(msg, t);
        }

        @Override
        public void error(String msg, LogEvent event, Throwable t) {
            kaosRunner.errorHandler().error(msg, event, t);
        }
    }

    protected KaosAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
        setHandler(new KaosErrorHandler());
    }

    public void setStrategyName(String strategyName) {
        kaosRunner.setStrategyName(strategyName);
    }

    @Override
    public void append(LogEvent log) {
        try {
            kaosRunner.causeKaos(log.getMessage().getFormattedMessage());
        } catch (Exception e) {
            error(e.getMessage(), e);
        }
    }

    @Override
    public void start() {
        super.start();
        kaosRunner.start();
    }

    @Override
    public void stop() {
        super.stop();
        kaosRunner.stop();
    }

    @Override
    protected boolean stop(final long timeout, final TimeUnit timeUnit, final boolean changeLifeCycleState) {
        boolean result = false;
        try {
            kaosRunner.stop();
        } finally {
            result = super.stop(timeout, timeUnit, changeLifeCycleState);
        }
        return result;
    }
}