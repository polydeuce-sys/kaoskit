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
 * Used for an object which changes some value before returning. For example
 * a modifiers on an wrapped InputStream could alter a {@code byte[]} read from the
 * stream to simulate a bad communication from an external service.
 *
 * Created by kevinmclellan on 27/09/2016.
 */
public interface Modifier<T> extends Monitorable{
    T modify(T original);
}
