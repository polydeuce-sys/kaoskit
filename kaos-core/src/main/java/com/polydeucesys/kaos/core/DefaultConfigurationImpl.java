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

import com.polydeucesys.kaos.core.behaviours.ExceptionThrower;
import com.polydeucesys.kaos.core.behaviours.Interrupter;
import com.polydeucesys.kaos.core.behaviours.RandomSleeper;

import java.util.*;

import static com.polydeucesys.kaos.core.ConfigurationFactory.*;

/**
 * Provides a very simple System property based configuration for KaosKit. This supports only
 * before and after behaviours and only the {@link com.polydeucesys.kaos.core.behaviours.RandomSleeper}
 * and {@link com.polydeucesys.kaos.core.behaviours.ExceptionThrower} behaviours (no {@link Modifier} support.
 *
 * The {@link com.polydeucesys.kaos.core.behaviours.ExceptionThrower} can be implicitly wrapped in a
 * {@link Sometimes}.
 * Parameters are set as {@code ";"} separated key-value pairs. Only one config per type of
 * Behaviour can exist. So the {@code ExceptionThrower} behaviour is set with the
 * {@code "com.polydeucesys.kaos.conf.default.throw.params"} property, and the same
 * config aplied even if there is a thrower both before and after. Both the {@code RandomSleeper}
 * and {@code ExceptionThrower} allow an {@code odds} parameter (a {@code double} between 0.0 and 1.0,
 * which implicitly wraps the {@code Behaviour} in a {@code Sometimes}.
 *
 * The {@code RandomSleeper} is cond=figured with the {@code "com.polydeucesys.kaos.conf.default.sleep.params"}
 * property.
 * Created by kevinmclellan on 20/10/2016.
 */
class DefaultConfigurationImpl implements Configuration {
    private static final String SLEEP = "sleep";
    private static final String THROW = "throw";
    private static final String INTERRUPT = "interrupt";

    private static final String ODDS = "odds";
    private static final String MAX = "max";
    private static final String CAN_THROW = "throws";
    private static final String STATES = "states";
    private static final String ONLY_FIRST = "first";



    // err messages
    private static final String MISSING_PARAM = "Missing Required Config %s from %s";
    private static final String UNKNOWN_BEHAVIOUR = "Unrecognized behviour type in config";

    private static final Map<String, Strategy> defaultMap;


    static {
        defaultMap = initConfig();
    }

    private static Monitor buildMonitor(){
        String monitorClazz = System.getProperty(MONITOR_CLASS_KEY, "");
        Monitor m = null;
        if(!monitorClazz.isEmpty()){
            try {
                m = (Monitor)Class.forName(monitorClazz).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new ConfigurationException(e);
            }
        }else{
            m = new Monitor() {
                @Override
                public void message(String message) {
                    System.out.println(message);
                }

                @Override
                public void message(String message, Map<String, String> context) {
                    message(message);
                }
            };
        }
        return m;
    }

    private static Behaviour buildSleepBehaviour(){
        String sleepParams = System.getProperty(SLEEP_PARAMS_KEY,"");
        Behaviour sleepBehaviour = new RandomSleeper();
        if(!sleepParams.isEmpty()){
            Map<String, String> sleepParamMap = paramsToMap(sleepParams);
            RandomSleeper s = (sleepParamMap.containsKey(MAX)?
                    new RandomSleeper(Long.parseLong(sleepParamMap.get(MAX))):new RandomSleeper());
            if(sleepParamMap.containsKey(ODDS)){
                sleepBehaviour = new SometimesBehaviour(Float.parseFloat(sleepParamMap.get(ODDS)), s);
            }else{
                sleepBehaviour = s;
            }
        }
        return sleepBehaviour;
    }

    private static Behaviour buildThrowBehaviour(){
        String throwParams = System.getProperty(THROW_PARAMS_KEY,"");
        Behaviour throwBehaviour = null;
        if(!throwParams.isEmpty()){
            Map<String, String> throwParamMap = paramsToMap(throwParams);
            if(!throwParamMap.containsKey(CAN_THROW))
                throw new ConfigurationException(String.format(MISSING_PARAM, CAN_THROW, THROW_PARAMS_KEY));
            String[] throwList = throwParamMap.get(CAN_THROW).split(",");
            List<Exception> canThrow = new ArrayList<>(throwList.length);
            for(String exClazz : throwList){
                try {
                    Exception nextEx = (Exception) Class.forName(exClazz.trim()).newInstance();
                    canThrow.add(nextEx);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    throw new ConfigurationException(e.getMessage(), e);
                }
            }
            if(canThrow.size() == 0){
                throw new ConfigurationException(String.format(MISSING_PARAM, CAN_THROW, THROW_PARAMS_KEY));
            }
            ExceptionThrower et = new ExceptionThrower(canThrow);
            if(throwParamMap.containsKey(ODDS)){
                throwBehaviour = new SometimesBehaviour(Float.parseFloat(throwParamMap.get(ODDS)),et);
            }else {
                throwBehaviour = et;
            }
        }
        return throwBehaviour;
    }

    private static Behaviour buildInterruptBehaviour(){
        String interruptParams = System.getProperty(INTERRUPT_PARAMS_KEY,"");
        Behaviour interruptBehaviour = null;
        if(!interruptParams.isEmpty()){
            Map<String, String> interruptParamMap = paramsToMap(interruptParams);
            String[] states = interruptParamMap.containsKey(STATES)?
                    interruptParamMap.get(STATES).split(","):(new String[]{});
            Set<Thread.State> searchStates = new TreeSet<>();
            for(String state : states){
                searchStates.add(Thread.State.valueOf(state));
            }
            boolean firstOnly = !interruptParamMap.containsKey(ONLY_FIRST) || Boolean.parseBoolean(interruptParamMap.get(ONLY_FIRST));
            interruptBehaviour = new Interrupter(searchStates, firstOnly);
        }
        return interruptBehaviour;
    }


    private static void doBehaviourList( String behaviourListStr, StrategyBuilder b,
                                         Monitor m,
                                         Behaviour sleepBehaviour, Behaviour throwBehaviour,
                                         Behaviour interruptBehaviour,
                                         boolean before){
        if(!behaviourListStr.isEmpty()){
            String[] behaviourList = behaviourListStr.toLowerCase().split(",");
            for(String behaviour: behaviourList){
                String work = behaviour.trim();
                Behaviour toAdd = null;
                switch(work){
                    case SLEEP:
                        toAdd = sleepBehaviour;
                        break;
                    case THROW:
                        if(throwBehaviour == null)
                            throw new ConfigurationException(String.format(MISSING_PARAM, THROW_PARAMS_KEY,
                                BEFORE_BEHAVIOURS_KEY));
                        toAdd = throwBehaviour;
                        break;
                    case INTERRUPT:
                        if(interruptBehaviour == null)
                            throw new ConfigurationException(String.format(MISSING_PARAM, INTERRUPT_PARAMS_KEY,
                                    BEFORE_BEHAVIOURS_KEY));
                        toAdd = interruptBehaviour;
                        break;
                    default:
                        throw new ConfigurationException(UNKNOWN_BEHAVIOUR);
                }
                ((KaosBase)toAdd).setMonitor(m);
                if(before)
                    b.addBeforeBehaviour(toAdd);
                else
                    b.addAfterBehaviour(toAdd);
            }
        }
    }
    // visible for tesing
    static Map<String, Strategy> initConfig(){
        StrategyBuilder b = new KaosStrategy.KaosStrategyBuilder();
        b.setName("default");
        Monitor m = buildMonitor();
        b = b.setMonitor(m);
        String beforeBehaviours = System.getProperty(BEFORE_BEHAVIOURS_KEY,"").toLowerCase();
        String afterBehaviours = System.getProperty(AFTER_BEHAVIOURS_KEY,"").toLowerCase();
        Behaviour sleepBehaviour = buildSleepBehaviour();
        Behaviour throwBehaviour = buildThrowBehaviour();
        Behaviour interruptBehaviour = buildInterruptBehaviour();

        // befores and afters
        doBehaviourList(beforeBehaviours,b,m, sleepBehaviour, throwBehaviour, interruptBehaviour, true );
        doBehaviourList(afterBehaviours,b,m, sleepBehaviour, throwBehaviour, interruptBehaviour, false );
        return new UnmodifiableConstantStrategyMap(b.build());
    }

    private static Map<String, String> paramsToMap( String params ){
        String[] paramList = params.split(";");
        Map<String,String> props = new TreeMap<>();
        for(String p : paramList){
            String[] kv = p.trim().split("=");
            props.put(kv[0].trim().toLowerCase(), kv[1].trim());
        }
        return props;
    }


    private static class UnmodifiableConstantStrategyMap extends AbstractMap<String, Strategy> {
        private final Strategy strategy;
        private final Set<Entry<String,Strategy>> entrySet;

        private class FixedEntry implements Map.Entry<String, Strategy>{

            @Override
            public String getKey() {
                return "default";
            }

            @Override
            public Strategy getValue() {
                return strategy;
            }

            @Override
            public Strategy setValue(Strategy value) {
                throw new UnsupportedOperationException();
            }
        }

        private UnmodifiableConstantStrategyMap(Strategy strategy){
            this.strategy = strategy;
            this.entrySet = Collections.singleton((Entry<String,Strategy>)new FixedEntry());
        }

        @Override
        public Set<Entry<String, Strategy>> entrySet() {
            return entrySet;
        }

        @Override
        public boolean containsKey(Object key){
            return true;
        }

        @Override
        public Strategy get(Object key){
            return strategy;
        }
    }

    @Override
    public Map<String, Strategy> strategiesByName() {
        return defaultMap;
    }

    @Override
    public Strategy strategyForName(String name) {
        return defaultMap.get(name);
    }
}
