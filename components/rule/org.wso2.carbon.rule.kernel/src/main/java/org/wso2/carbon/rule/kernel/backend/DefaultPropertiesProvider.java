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

import java.util.Map;

/**
 * This class was introduced to locate any default proprieties required by the target rule engine.
 * <p/>
 * This is to overcome some limitations in the JSR 94 API. e.g giving the class loader for the rule set
 */
@SuppressWarnings("unused")
public interface DefaultPropertiesProvider {

    /**
     * Returns any default properties required at the rule service provider initialization
     *
     * @param propertyClassLoader <code>ClassLoader</code> to be used by the the rule service provider
     * @return a map of default properties to be used when initiating the rule service provider
     */
    public Map<String, Object> getRuleServiceProviderDefaultProperties(
            ClassLoader propertyClassLoader);

    /**
     * Returns any default properties required at the rule set creation
     *
     * @param propertyClassLoader <code>ClassLoader</code> to be used by the the rule service provider
     *                            to resolve any classes required when creating the rule set
     * @return a map of default properties to be used when creating the rule set
     */
    public Map<String, Object> getRuleExecutionSetCreationDefaultProperties(
            ClassLoader propertyClassLoader);

}
