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

package org.wso2.carbon.rule.backend.drools;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntime;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntimeFactory;

import java.util.Map;
import java.util.Properties;

public class DroolsBackendRuntimeFactory implements RuleBackendRuntimeFactory{

    public RuleBackendRuntime getRuleBackendRuntime(Map<String, String> properties,
                                                    ClassLoader classLoader){

        ClassLoader existingClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);

        Properties knowledgeBaseProperties = new Properties();
        knowledgeBaseProperties.putAll(properties);

        KnowledgeBaseConfiguration knowledgeBaseConfiguration
                = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(knowledgeBaseProperties, classLoader);

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(
                knowledgeBaseConfiguration);
        KnowledgeBuilderConfiguration builderConfiguration
                = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(
                builderConfiguration);

        Thread.currentThread().setContextClassLoader(existingClassLoader);
        
        return new DroolsBackendRuntime(knowledgeBase, knowledgeBuilder , classLoader);
    }
}
