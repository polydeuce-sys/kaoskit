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

import java.util.Map;

/**
 * An abstrction over Logging or other form of monitoring. As the core is used to implement
 * Appenders under each of the three major logging frameworks, concrete Logging calls would
 * require separate implementation of a TestAppender type of class to collect information
 * for unit and integration tests. By abstracting with at the framework, as single custom
 * implementation can be used, while leaving the option for user of either the simple
 * Appender based implementations, or the Aspect implementation to provide thier own
 * Monitoring.
 * Created by kevinmclellan on 03/10/2016.
 */
public interface Monitor {
    void message(String message);
    void message(String message, Map<String, String> context);
}
