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

import java.util.Map;

/**
 * Interface for a configuration provider. The configuration provider will depend on the use case.
 * For a very simple case, the {@link DefaultConfigurationImpl} is available. It provides a
 * simple confiuration for a limited set of Behaviours via system properties.
 * Other use cases should use the facilities of the target applicaiton (JSON files, Dependency Injection,
 * XML files) to configure.
 * Created by kevinmclellan on 20/10/2016.
 */
public interface Configuration {
    Map<String, Strategy> strategiesByName();
    Strategy strategyForName(String name);
}
