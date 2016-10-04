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
 * Base class for Modifier implementations Will deal with validation and
 * with monitoring messages.
 * Created by kevinmclellan on 03/10/2016.
 */
public abstract class BaseModifier<T> extends KaosBase implements Modifier<T> {
    private static final String CALLED_MODIFY = "Called modify on %s";
    protected abstract T doModify( T original );

    @Override
    public final T modify(T original) {
        getMonitor().message(String.format(CALLED_MODIFY, name()));
        return doModify(original);
    }
}
