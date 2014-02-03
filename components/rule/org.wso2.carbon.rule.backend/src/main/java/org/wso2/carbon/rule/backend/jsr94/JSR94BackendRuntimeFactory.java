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

package org.wso2.carbon.rule.backend.jsr94;

import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntimeFactory;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntime;
import org.wso2.carbon.rule.kernel.backend.DefaultPropertiesProvider;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.rules.RuleServiceProviderManager;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleRuntime;
import javax.rules.ConfigurationException;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import java.util.Map;
import java.util.HashMap;
import java.rmi.RemoteException;

public class JSR94BackendRuntimeFactory implements RuleBackendRuntimeFactory {

    public static final String PROVIDER_PRO_CLASS = "class";
    public static final String PROVIDER_PRO_URI = "uri";
    public static final String PROVIDER_PRO_PROPERTY_PROVIDER = "propertyProvider";


    private static Log log = LogFactory.getLog(JSR94BackendRuntimeFactory.class);

    @Override
    public RuleBackendRuntime getRuleBackendRuntime(Map<String, String> properties, ClassLoader classLoader)
                                                                             throws RuleConfigurationException {
        String providerClassName = properties.get(PROVIDER_PRO_CLASS);
       
        Class providerClass;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Trying to initialize the RuleServiceProvider " +
                        "with class name :" + providerClassName);
            }
            // RuleServiceProviderImpl will automatically registered
            // via a static initialization block
            providerClass = Class.forName(providerClassName);
            if (log.isDebugEnabled()) {
                log.debug("RuleServiceProvider has been initialized." +
                        " provider class : " + providerClassName);
            }

        } catch (ClassNotFoundException e) {
            throw new RuleConfigurationException("Error when loading RuleServiceProvider from class " +
                    "with the name " + providerClassName, e);
        }

        String providerUri = properties.get(PROVIDER_PRO_URI);

        try {
            if (log.isDebugEnabled()) {
                log.debug("Getting RuleServiceProvider using provider Url : " + providerUri);
            }

            RuleServiceProviderManager.registerRuleServiceProvider(providerUri, providerClass, classLoader);
            // Get the rule service provider from the provider manager.
            RuleServiceProvider ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider(providerUri);

            if (ruleServiceProvider == null) {
                throw new RuleConfigurationException("There is no RuleServiceProvider" +
                        " registered for Uri :" + providerUri);
            }

            RuleAdministrator ruleAdministrator = createRuleAdministrator(ruleServiceProvider);
            LocalRuleExecutionSetProvider localProvider = createLocalRuleExecutionSetProvider(ruleAdministrator);
            RuleRuntime ruleRuntime = createRuleRuntime(ruleServiceProvider);
            JSR94BackendRuntime jsr94BackendRuntime =
                    new JSR94BackendRuntime(ruleAdministrator, ruleRuntime, localProvider, classLoader);

            String defaultPropertyProviderClassName = properties.get(PROVIDER_PRO_PROPERTY_PROVIDER);

            try {
                if (defaultPropertyProviderClassName != null){
                    Class defaultPropertyProviderClass = Class.forName(defaultPropertyProviderClassName);
                    Object defaultPropertyProvider = defaultPropertyProviderClass.newInstance();
                    jsr94BackendRuntime.setDefaultPropertiesProvider((DefaultPropertiesProvider)defaultPropertyProvider);
                }
            } catch (ClassNotFoundException e) {
                throw new RuleConfigurationException("Can not load the class " + defaultPropertyProviderClassName);
            } catch (IllegalAccessException e) {
                throw new RuleConfigurationException("Can not accesss the class " + defaultPropertyProviderClassName);
            } catch (InstantiationException e) {
                throw new RuleConfigurationException("Can not instantiate the class " + defaultPropertyProviderClassName);
            }

            return jsr94BackendRuntime;
        } catch (ConfigurationException e) {
            throw new RuleConfigurationException("Error was occurred when getting RuleServiceProvider" +
                    " which has been registered to the Url " + providerUri, e);
        }
    }

    private RuleAdministrator createRuleAdministrator(RuleServiceProvider ruleServiceProvider)
                                                                     throws RuleConfigurationException {

        try {
            if (log.isDebugEnabled()) {
                log.debug("Getting Rule Administrator from Rule Service Provider");
            }
            // Gets the RuleAdministration
            RuleAdministrator ruleAdministrator = ruleServiceProvider.getRuleAdministrator();
            if (ruleAdministrator == null) {
                throw new RuleConfigurationException("The loaded Rule Administrator is null");
            }
            return ruleAdministrator;
        } catch (ConfigurationException e) {
            throw new RuleConfigurationException("Error was occurred when creating the " +
                    "Rule Administrator from the RuleServiceProvider", e);
        }
    }

     private RuleRuntime createRuleRuntime(RuleServiceProvider ruleServiceProvider) throws RuleConfigurationException {

        try {
            // get the run time which will be the access point for runtime execution
            // of RuleExecutionSets
            RuleRuntime ruleRuntime = ruleServiceProvider.getRuleRuntime();
            if (ruleRuntime == null) {
                throw new RuleConfigurationException("The created rule runtime is null");
            }
            return ruleRuntime;

        } catch (ConfigurationException e) {
            throw new RuleConfigurationException("Error was occurred when getting RuleRuntime", e);
        }
    }

    private LocalRuleExecutionSetProvider createLocalRuleExecutionSetProvider(
            RuleAdministrator ruleAdministrator) throws RuleConfigurationException {

        // get the run time which will be the access point for runtime execution
        try {
            if (log.isDebugEnabled()) {
                log.debug("Getting LocalRuleExecutionSetProvider ");
            }

            LocalRuleExecutionSetProvider localRuleExecutionSetProvider =
                    ruleAdministrator.getLocalRuleExecutionSetProvider(new HashMap());
            if (localRuleExecutionSetProvider == null) {
                throw new RuleConfigurationException("The loaded LocalRuleExecutionSetProvider is null");
            }
            return localRuleExecutionSetProvider;

        } catch (RemoteException e) {
            throw new RuleConfigurationException("Error was occurred when getting " +
                    "the LocalRuleExecutionSetProvider ", e);
        }
    }

    
}
