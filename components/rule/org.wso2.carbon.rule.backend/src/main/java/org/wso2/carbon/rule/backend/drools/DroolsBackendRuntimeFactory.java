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

import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntime;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntimeFactory;

import java.util.Map;

public class DroolsBackendRuntimeFactory implements RuleBackendRuntimeFactory {

    public RuleBackendRuntime getRuleBackendRuntime(Map<String, String> properties,
                                                    ClassLoader classLoader) {

        ClassLoader existingClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);

            KieServices kieServices = KieServices.Factory.get();
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
            return new DroolsBackendRuntime(kieServices, kieFileSystem , classLoader);
        } finally {
            Thread.currentThread().setContextClassLoader(existingClassLoader);
        }

    }
}
