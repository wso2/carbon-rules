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

package org.wso2.carbon.rule.kernel.engine;

import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.event.core.subscription.Subscription;
import org.wso2.carbon.rule.common.Rule;
import org.wso2.carbon.rule.common.RuleSet;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntime;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntimeFactory;
import org.wso2.carbon.rule.kernel.backend.Session;
import org.wso2.carbon.rule.kernel.config.RuleEngineConfigService;
import org.wso2.carbon.rule.kernel.config.RuleEngineProvider;
import org.wso2.carbon.rule.kernel.internal.build.RuleEngineConfigBuilder;
import org.wso2.carbon.rule.kernel.internal.config.CarbonRuleEngineConfigService;
import org.wso2.carbon.rule.kernel.internal.ds.RuleValueHolder;
import org.wso2.carbon.rule.kernel.internal.event.RuleChangeEventDispacher;

/**
 * this class is the layer in between top layers (i.e web service layer and the mediation layer) and the rule back end runtime.
 * This class is used to create the sessions which does the data binding work.
 */
public class RuleEngine {

    private RuleBackendRuntime ruleBackendRuntime;
    private RuleSet ruleSet;
    private ClassLoader factClassLoader;

    /**
     * Creates a Rule Engine Object. Rule engine object is kept per mediator and per service. When creating the rule
     * Engine it has to pass the Backe end runtime factory and the class loader to load the fact classes properly.
     * @param ruleSet  - rule set specified either in the web service descriptor file or rule mediator file.
     * @param factClassLoader   - class loader which other pojo classes may exists.
     * @throws RuleConfigurationException  - if there is a problem with the configuration
     */
    public RuleEngine(RuleSet ruleSet,
                      ClassLoader factClassLoader) throws RuleConfigurationException {

        this.ruleSet = ruleSet;
        this.factClassLoader = factClassLoader;

        //if the rules stored in registry we need to refresh the rule runtime when rules get changed.
        //so we check whether the rule source is registry and register a listner.
        RuleChangeEventDispacher ruleChangeEventDispacher = new RuleChangeEventDispacher(this);

        for (Rule rule : ruleSet.getRules()){
            if (rule.getSourceType().equals(Constants.RULE_SOURCE_TYPE_REGISTRY)){
                String updateTopicName = getUpdateTopicName(rule.getValue());
                Subscription subscription = new Subscription();
                subscription.setEventDispatcher(ruleChangeEventDispacher);
                subscription.setTopicName(updateTopicName);
                subscription.setTenantId(CarbonContext.getThreadLocalCarbonContext().getTenantId());
                subscription.setOwner(CarbonConstants.REGISTRY_SYSTEM_USERNAME);
                RuleValueHolder.getInstance().addSubscription(subscription);
            }
        }

        loadRuleBackendRuntime();

    }

    private String getUpdateTopicName(String registryKey) throws RuleConfigurationException {
        String updateTopicName = "/registry/notifications/ResourceUpdated/_system/";
        if (registryKey.startsWith(Constants.RULE_SOURCE_REGISTRY_TYPE_CONFIGURATION)){
            updateTopicName += registryKey.replace("conf:","config/");
        }else if (registryKey.startsWith(Constants.RULE_SOURCE_REGISTRY_TYPE_GOVERNANCE)){
            updateTopicName += registryKey.replace("gov:","governance/");
        } else {
            throw new RuleConfigurationException("In valide registry with registry key " + registryKey);
        }
        return updateTopicName;
    }

    public synchronized void loadRuleBackendRuntime()  throws RuleConfigurationException {
        RuleEngineProvider ruleEngineProvider = null;
        try {
            ruleEngineProvider = RuleValueHolder.getInstance().getRuleEngineProvider();

            if (ruleEngineProvider == null){
                // if the rule service is not loaded we need to create a one
                RuleEngineConfigBuilder ruleEngineConfigBuilder =  new RuleEngineConfigBuilder();
                RuleEngineConfigService ruleEngineConfigService =
                                new CarbonRuleEngineConfigService(ruleEngineConfigBuilder.getRuleConfig());
                RuleValueHolder.getInstance().registerRuleEngineConfigService(ruleEngineConfigService);
                ruleEngineProvider = RuleValueHolder.getInstance().getRuleEngineProvider();
            }

            Class ruleBackendRuntimeFactoryClass = Class.forName(ruleEngineProvider.getClassName());
            RuleBackendRuntimeFactory ruleBackendRuntimeFactory =
                    (RuleBackendRuntimeFactory) ruleBackendRuntimeFactoryClass.newInstance();
            this.ruleBackendRuntime =
                    ruleBackendRuntimeFactory.getRuleBackendRuntime(ruleEngineProvider.getProperties(), this.factClassLoader);
            this.ruleBackendRuntime.addRuleSet(this.ruleSet);

        } catch (ClassNotFoundException e) {
            throw new RuleConfigurationException("Class " + ruleEngineProvider.getClassName() + " can not be found ");
        } catch (IllegalAccessException e) {
            throw new RuleConfigurationException("Can not instantiate the " + ruleEngineProvider.getClassName() + " class");
        } catch (InstantiationException e) {
            throw new RuleConfigurationException("Can not instantiate the " + ruleEngineProvider.getClassName() + " class");
        }
    }

    /**
     * creates a session to be used with a pirticular service invocation. 
     * @param type -  whther it is stateful or state less
     * @return - session created
     * @throws RuleRuntimeException - if problem occurs when creating session
     */
    public RuleSession createSession(int type) throws RuleRuntimeException {

        Session session = this.ruleBackendRuntime.createSession(type);
        return new RuleSession(session);
    }
}
