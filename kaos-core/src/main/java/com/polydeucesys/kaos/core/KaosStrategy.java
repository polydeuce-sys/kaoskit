package com.polydeucesys.kaos.core;

/*
 *  Copyright 2016 Polydeuce-Sys Ltd
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The KaosStrategy is a concrete implementation of the Strategy interface. It holds an ordered collection of
 * Behaviours which it will start
 * Created by kevinmclellan on 13/09/2016.
 */
public class KaosStrategy<T> implements Strategy<T> {

    static class DefaultStrategyMonitor implements Monitor{

        @Override
        public void message(String message) {
            // NOOP
        }

        @Override
        public void message(String message, Map<String, String> context) {

        }
    }

    public static class KaosStrategyBuilder implements StrategyBuilder{
        private static final String NEW_STRATEGY = "Created new Strategy instance";
        private static final String MONITOR_AFTER_ADDING = "Monitor must be set before any behaviours are added";
        private static final String ADD_AFTER_COMPLETE = "Builder already completed";
        private static final String ADDED = "Added instance of %s as %s";

        private volatile boolean built = false;
        private Monitor monitor = new DefaultStrategyMonitor();
        private final List<Behaviour> beforeBehaviours = new ArrayList<>();
        private final List<Behaviour> afterBehaviours = new ArrayList<>();
        private final List<Modifier> modifiers = new ArrayList<>();

        @Override
        public StrategyBuilder setMonitor(Monitor monitor) {
            if(beforeBehaviours.size() + afterBehaviours.size() + modifiers.size() > 0)
                throw new IllegalStateException(MONITOR_AFTER_ADDING);
            this.monitor = monitor;
            return this;
        }

        @Override
        public synchronized StrategyBuilder addBeforeBehaviour( Behaviour b) {
            if(built) throw new IllegalStateException(ADD_AFTER_COMPLETE);
            b.setMonitor(monitor);
            beforeBehaviours.add(b);
            monitor.message(String.format(ADDED, b.name(), "before Behaviour"));
            return this;
        }

        @Override
        public StrategyBuilder addAfterBehaviour(Behaviour b) {
            if(built) throw new IllegalStateException(ADD_AFTER_COMPLETE);
            b.setMonitor(monitor);
            afterBehaviours.add(b);
            monitor.message(String.format(ADDED, b.name(), "after Behaviour"));
            return this;
        }

        @Override
        public StrategyBuilder addModifier(Modifier m) {
            if(built) throw new IllegalStateException(ADD_AFTER_COMPLETE);
            m.setMonitor(monitor);
            monitor.message(String.format(ADDED, m.name(), "Modifier"));
            modifiers.add(m);
            return this;
        }

        @Override
        public Strategy build() {
            Strategy newStrategy = new KaosStrategy(monitor, beforeBehaviours, afterBehaviours, modifiers);
            built = true;
            monitor.message(NEW_STRATEGY);
            return newStrategy;
        }

    }

    private final Monitor monitor;
    private final List<Behaviour> beforeBehaviours;
    private final List<Behaviour> afterBehaviours;
    private final List<Modifier> modifiers;


    private volatile boolean started = false;

    private KaosStrategy( Monitor monitor,
                          List<Behaviour> beforeBehaviours,
                          List<Behaviour> afterBehaviours,
                          List<Modifier> modifiers){
        this.monitor = monitor;
        this.beforeBehaviours = Collections.unmodifiableList(beforeBehaviours);
        this.afterBehaviours = Collections.unmodifiableList(afterBehaviours);
        this.modifiers = Collections.unmodifiableList(modifiers);
    }

    /**
     * Allow behaviours to aquire any required resources, start timers or other actions.
     */
    public void start() {
        for( Behaviour b : beforeBehaviours) b.start();
        for( Behaviour b : afterBehaviours) b.start();
        started = true;
    }

    private void stopList( List<Behaviour> stopList){
        final List<Behaviour> stopping = new ArrayList<>(stopList.size());
        Collections.copy(stopping ,stopList);
        Collections.reverse(stopping);
        for( Behaviour b : stopping){
            b.stop();
        }

    }

    /**
     * Signal behaviours to release resources, stop times or other actions to end thier behaviour.
     */
    public void stop() {
        stopList(beforeBehaviours);
        stopList(afterBehaviours);
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    private List<Behaviour> copyBehaviours(List<Behaviour> behaviours){
        final List<Behaviour> behaviourCopy = new ArrayList<>(behaviours.size());
        Collections.copy(behaviourCopy ,behaviours);
        return Collections.unmodifiableList(behaviourCopy);
    }

    public List<Behaviour> beforeBehaviours() {
        return copyBehaviours(beforeBehaviours);
    }

    @Override
    public List<Behaviour> afterBehaviours() {
        return copyBehaviours(afterBehaviours);
    }

    @Override
    public List<Modifier> modifiers() {
        return modifiers;
    }

    public void executeBefore() throws Exception{
        for( Behaviour b : beforeBehaviours){
            if(started)
                b.execute();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void executeAfter(T returnValue) throws Exception {
        for( Behaviour b : afterBehaviours){
            if(started)
                b.execute(returnValue);
        }    }
}
