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

/**
 * Randomly modify a value a given percentage of the time.
 * Created by kevinmclellan on 30/09/2016.
 */
public class SometimesModifier<T> extends Sometimes implements Modifier<T> {
    private static final String CALLED_MODIFY = "Called modify on %s";
    private static final String NOT_CALLED_MODIFY = "Did not call modify on %s";

    private final Modifier<T> sometimes;

    SometimesModifier(float odds, Modifier<T> sometimes) {
        super(odds);
        this.sometimes = sometimes;
    }

    @Override
    public T modify(T original) {
        boolean modify = isPerform();
        T res = isPerform()?sometimes.modify(original):original;
        getMonitor().message(String.format(modify?CALLED_MODIFY:NOT_CALLED_MODIFY, name()));
        return res;
    }
}
