/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.rule.kernel.backend;

import org.wso2.carbon.rule.common.exception.RuleConfigurationException;

import java.util.Map;

/**
 * Factory interface to create the real back end runtimes.
 */
public interface RuleBackendRuntimeFactory {

    /**
     * This interface is used to create the Back end runtimes in different types. We use a factory to pass properties
     * and other backend specific things so that the factory and take care of creating the real runtimes.
     * @param properties - backend runtime properties
     * @param classLoader  - class loader to be used to load the classes
     * @return   - back end runtime object corresponding to the given factory
     */
     public RuleBackendRuntime getRuleBackendRuntime(Map<String, String> properties,
                                                    ClassLoader classLoader) throws RuleConfigurationException;

}
