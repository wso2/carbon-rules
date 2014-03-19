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

package org.wso2.carbon.rule.kernel.internal.ds;

import org.wso2.carbon.event.core.EventBroker;
import org.wso2.carbon.event.core.exception.EventBrokerException;
import org.wso2.carbon.event.core.subscription.Subscription;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.kernel.config.RuleEngineConfigService;
import org.wso2.carbon.rule.kernel.config.RuleEngineProvider;

import java.util.HashSet;
import java.util.Set;

public class RuleValueHolder {

    private static RuleValueHolder ruleValueHolder = new RuleValueHolder();
    private RuleEngineConfigService ruleEngineConfigService;
    private EventBroker eventBroker;

    private Set<Subscription> pendingSubscriptions;

    public RuleValueHolder() {
        this.pendingSubscriptions = new HashSet<Subscription>();
    }

    public static RuleValueHolder getInstance(){
        return ruleValueHolder;
    }

    public void registerRuleEngineConfigService(RuleEngineConfigService ruleEngineConfigService){
        this.ruleEngineConfigService = ruleEngineConfigService;
    }

    public RuleEngineConfigService getRuleEngineConfigService(){
        return this.ruleEngineConfigService;
    }

    public RuleEngineProvider getRuleEngineProvider(){
        RuleEngineProvider ruleEngineProvider = null;
        if (this.ruleEngineConfigService != null){
            ruleEngineProvider = this.ruleEngineConfigService.getRuleConfig().getRuleEngineProvider();
        }
        return ruleEngineProvider;
    }

    public synchronized void registerEventBroker(EventBroker eventBroker) throws RuleConfigurationException {
        this.eventBroker = eventBroker;
        //subscribes the pending subscriptions
        subscribePendingSubscriptions();
        this.pendingSubscriptions.clear();
    }

    private void subscribePendingSubscriptions() throws RuleConfigurationException {
        for (Subscription subscription : this.pendingSubscriptions) {
            try {
                this.eventBroker.subscribe(subscription);
            } catch (EventBrokerException e) {
                throw new RuleConfigurationException("Can not subscribe to topic " + subscription.getTopicName(), e);
            }
        }
    }

    public EventBroker getEventBroker(){
        return this.eventBroker;
    }

    public synchronized void addSubscription(Subscription subscription) throws RuleConfigurationException {
        if (this.eventBroker != null){
            try {
                this.eventBroker.subscribe(subscription);
            } catch (EventBrokerException e) {
                throw new RuleConfigurationException("Can not subscribe to topic " + subscription.getTopicName(), e);
            }
        } else {
            this.pendingSubscriptions.add(subscription);
        }
    }
}
