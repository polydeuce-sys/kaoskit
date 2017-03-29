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
 * Created by kevinmclellan on 20/10/2016.
 */
public enum ConfigurationFactory {
    INSTANCE;

    public static final String CONFIGURATION_CLASS_KEY = "com.polydeucesys.kaos.conf.class";

    // Keys used in default implementation
    /**
     * This property
     */
    public static final String BEFORE_BEHAVIOURS_KEY = "com.polydeucesys.kaos.conf.default.before";
    public static final String AFTER_BEHAVIOURS_KEY = "com.polydeucesys.kaos.conf.default.after";
    public static final String SLEEP_PARAMS_KEY = "com.polydeucesys.kaos.conf.default.sleep.params";
    public static final String THROW_PARAMS_KEY = "com.polydeucesys.kaos.conf.default.throw.params";
    public static final String INTERRUPT_PARAMS_KEY = "com.polydeucesys.kaos.conf.default.interrupt.params";
    public static final String MONITOR_CLASS_KEY =  "com.polydeucesys.kaos.conf.default.monitor.class";

    private static final String CONFIGURATION_FAIED_STRING = "Configuration Failed For KaosKit";

    public static ConfigurationFactory getInstance(){
        return INSTANCE;
    }

    /**
     * Return the Configuration implementation for the KaosKit. The Configuration class will be
     * determined by checking the System property {@code "com.polydeucesys.kaos.conf.class"}.
     * If there is no value, a {@link DefaultConfigurationImpl} instance will be returned
     * @return {@code Configuration} instance
     */
    public Configuration getConfiguration(){
        String configClassName = System.getProperty(CONFIGURATION_CLASS_KEY);
        Configuration impl = null;
        if(configClassName != null){
            try {
                impl = (Configuration) Class.forName(configClassName).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new ConfigurationException(CONFIGURATION_FAIED_STRING,e);
            }
        }
        if (impl == null) {
            DefaultConfigurationImpl.initConfig();
            impl = new DefaultConfigurationImpl();
        }
        return impl;
    }
}
