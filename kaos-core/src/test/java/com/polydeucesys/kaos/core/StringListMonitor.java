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

import com.polydeucesys.kaos.core.Monitor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by kevinmclellan on 28/10/2016.
 */
public class StringListMonitor implements Monitor {
    private static final List<String> messages = new LinkedList<>();

    public static List<String> messages(){
        return Collections.unmodifiableList(messages);
    }

    static void reset(){
        messages.clear();
    }

    @Override
    public void message(String message) {
        messages.add(message);
    }

    @Override
    public void message(String message, Map<String, String> context) {
        message(message);
    }
}
