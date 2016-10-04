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
 * Interface which defines an action which can be taken either before or after the performance of
 * some action in the target codebase. If this action is taken after a call, it can view but not change the
 * return value (unlike a modifier).
 * Created by kevinmclellan on 30/09/2016.
 */
public interface Behaviour<T> extends Monitorable{
    boolean execute() throws Exception;
    boolean execute(T returnValue ) throws Exception;
}
