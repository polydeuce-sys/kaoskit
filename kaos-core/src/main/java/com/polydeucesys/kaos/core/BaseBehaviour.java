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
/**
 * A BaseBehaviour is an abstract superclass implementing {@link Behaviour}. A behaviour is an action
 * which has an effect on the running of the program it is in. For example, a
 * {@link com.polydeucesys.kaos.core.behaviours.RandomSleeper} behaviour running
 * in a log appender can be used to cause jitter in the timings of calls to 3rd party libraries
 * which having logging. A behaviour may also be used to throw errors, interrupt sleeping threads or
 * perform any other action which is not directly altering a parameter in a call.
 * Created by kevinmclellan on 13/09/2016.
 */
public abstract class BaseBehaviour<T> extends KaosBase implements Behaviour<T> {

    private static final String DID_EXECUTE = "Executed %s";
    private static final String DID_NOT_EXECUTE = "Did Not Execute %s";

    /**
     * Optionally perfom some action. Returns a flag indicating if the action has
     * been performed or not. This can be used to build flow control in composite
     * behaviours
     * @return true if Behaviour has performed its' defined action
     */
    public abstract boolean doExecute() throws Exception;

    protected void monitorResult( boolean result ){
        getMonitor().message(String.format(result?DID_EXECUTE:DID_NOT_EXECUTE, name()));
    }


    @Override
    public boolean execute() throws Exception {
        boolean result = doExecute();
        monitorResult(result);
        return result;
    }

    @Override
    public boolean execute( T returnValue ) throws Exception {
        return execute();
    }

    @Override
    public void doStart() {
// NOOP
    }

    @Override
    public void doStop() {
// NOOP
    }

}
