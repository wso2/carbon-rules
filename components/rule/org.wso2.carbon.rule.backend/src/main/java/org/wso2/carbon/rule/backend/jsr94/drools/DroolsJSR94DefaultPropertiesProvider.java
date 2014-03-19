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

package org.wso2.carbon.rule.backend.jsr94.drools;

import org.wso2.carbon.rule.kernel.backend.DefaultPropertiesProvider;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.jsr94.rules.Constants;
import org.drools.RuleBaseConfiguration;

import java.util.Map;
import java.util.HashMap;

/**
 * Provides default properties required by the drools rule engine.
 * This is used to provide added capability to the JSR 94 API
 */
public class DroolsJSR94DefaultPropertiesProvider implements DefaultPropertiesProvider {

     public Map<String, Object> getRuleServiceProviderDefaultProperties(
            ClassLoader propertyClassLoader) {
        return new HashMap<String, Object>();
    }

    /**
     * Provides a <code>PackageBuilderConfiguration</code> and a <code>RuleBaseConfiguration</code>
     * where the root class loader is one provided as the argument
     *
     * @param propertyClassLoader ClassLoader to be used by the the rule service provider
     *                            to resolve any classes required when creating the rule set
     * @return A map of default properties to be used when creating a rule execution set
     */
    public Map<String, Object> getRuleExecutionSetCreationDefaultProperties(
            ClassLoader propertyClassLoader) {
        PackageBuilderConfiguration configuration =
                new PackageBuilderConfiguration(propertyClassLoader);
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Constants.RES_PACKAGEBUILDER_CONFIG, configuration);
        RuleBaseConfiguration baseConfiguration = new RuleBaseConfiguration(propertyClassLoader);
        properties.put(Constants.RES_RULEBASE_CONFIG, baseConfiguration);
        return properties;
    }
}
