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
 * Defines a builder for a Strategy. If a {@link Monitor} is to be used, it must be the
 * first item defined, as it must receive messages for all other changes added.
 * Created by kevinmclellan on 27/09/2016.
 */
public interface StrategyBuilder {
    StrategyBuilder addBeforeBehaviour( Behaviour b);
    StrategyBuilder addAfterBehaviour( Behaviour b);
    StrategyBuilder addModifier( Modifier m);
    StrategyBuilder setMonitor( Monitor monitor);
    Strategy build();
}
