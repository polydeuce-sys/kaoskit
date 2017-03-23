package com.polydeucesys.kaos.core;
/*
 * Copyright (c) 2017 Polydeuce-Sys Ltd
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

import java.util.Map;

/**
 * The no op monitor simply discards messages. It is used as the default monitor for {@link KaosBase}
 * classes. This ensures that rather than having null pointer exceptions for instances where a monitor
 * has not been configured there is simply a lack of monitoring messages (which should be the lesser
 * or two evils).
 * Created by kevinmclellan on 23/03/2017.
 */
public class NoOpMonitor implements Monitor{
    @Override
    public void message(String message) {

    }

    @Override
    public void message(String message, Map<String, String> context) {

    }
}
