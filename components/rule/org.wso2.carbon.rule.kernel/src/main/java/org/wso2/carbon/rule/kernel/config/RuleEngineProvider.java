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


package org.wso2.carbon.rule.kernel.config;

import java.util.Map;
import java.util.HashMap;

/**
 * class to represents the rule engine provider.
 */
public class RuleEngineProvider {

    /**
     * factory class name with implements the back end runtime.
     */
    private String className;

    /**
     * other properties of the engine provider
     */
    private Map<String,String> properties;

    public RuleEngineProvider() {
        this.properties = new HashMap<String, String>();
    }

    public void addProperty(String name, String value){
        this.properties.put(name, value);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
