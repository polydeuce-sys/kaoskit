package com.polydeucesys.kaos.core.behaviours;
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

import com.polydeucesys.kaos.core.BaseBehaviour;
import com.polydeucesys.kaos.core.RandomGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class which throws an exception from the given list of exceptions. Note that an exception
 * thrower terminates the Strategy or conditional BaseBehaviour which contains it
 * when it throws.
 * Created by kevinmclellan on 29/09/2016.
 */
public class ExceptionThrower extends BaseBehaviour {
    private static final String EXECUTE_MESSAGE = "Throwing %s";
    private final List<Exception> exceptions;
    private final int top;

    public ExceptionThrower(List<Exception> exceptions){
        this.exceptions = Collections.unmodifiableList(exceptions);
        // max index
        this.top = exceptions.size() - 1;
    }

    List<Exception> exceptions(){
        return exceptions;
    }

    @Override
    public boolean doExecute() throws Exception{
        int index = RandomGenerator.nextInt(top);
        Exception e = exceptions.get(index);
        getMonitor().message(String.format(EXECUTE_MESSAGE, e.getClass()));
        throw e;
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
