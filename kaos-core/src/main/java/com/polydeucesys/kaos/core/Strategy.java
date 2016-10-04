package com.polydeucesys.kaos.core;

import java.net.Socket;
import java.util.List;
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
 * A {@code Strategy} is a holder for a collection of possible actions. This may be actions taken before
 * running of some aspect of a programs operation, after, or a modification of the returned value form
 * some call in the program. For example a Strategy to add chaos to a {@link Socket} might sometimes throw
 * an exception on connection, or sleep after connecting (affecting the connected system), or modify the data
 * read from another system to simulate a change to a remote API (REST response for example)
 * Created by kevinmclellan on 13/09/2016.
 */
public interface Strategy<T> extends Lifecycle
{
    List<Behaviour> beforeBehaviours();
    List<Behaviour> afterBehaviours();
    List<Modifier> modifiers();
    void executeBefore() throws Exception;
    void executeAfter(T returnValue) throws Exception;

}
