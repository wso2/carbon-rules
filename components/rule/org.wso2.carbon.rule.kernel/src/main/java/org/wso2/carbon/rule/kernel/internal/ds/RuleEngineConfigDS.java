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

import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.rule.kernel.internal.build.RuleEngineConfigBuilder;
import org.wso2.carbon.rule.kernel.internal.config.CarbonRuleEngineConfigService;
import org.wso2.carbon.rule.kernel.config.RuleEngineConfigService;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.event.core.EventBroker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @scr.component name="ruleengineconfig.component" immediate="true"
 * @scr.reference name="eventbroker.service"
 * interface="org.wso2.carbon.event.core.EventBroker" cardinality="1..1"
 * policy="dynamic" bind="setEventBroker" unbind="unSetEventBroker"
 */
public class RuleEngineConfigDS {

    private static Log log = LogFactory.getLog(RuleEngineConfigDS.class);

    protected void activate(ComponentContext context) {

        RuleEngineConfigBuilder ruleEngineConfigBuilder = new RuleEngineConfigBuilder();

        try {

            RuleEngineConfigService ruleEngineConfigService = RuleValueHolder.getInstance().getRuleEngineConfigService();
            if (ruleEngineConfigService == null) {
                ruleEngineConfigService =
                        new CarbonRuleEngineConfigService(ruleEngineConfigBuilder.getRuleConfig());
                RuleValueHolder.getInstance().registerRuleEngineConfigService(ruleEngineConfigService);

            }
            context.getBundleContext().registerService(RuleEngineConfigService.class.getName(),
                            ruleEngineConfigService, null);
            log.info("Successfully registered the Rule Config service");
        } catch (RuleConfigurationException e) {
            log.error("Can not create the Rule Config service ", e);
        }

    }

    protected void setEventBroker(EventBroker eventBroker) {
        try {
            RuleValueHolder.getInstance().registerEventBroker(eventBroker);
        } catch (RuleConfigurationException e) {
            log.error("Can not configure event broker ", e);
        }
    }

    protected void unSetEventBroker(EventBroker eventBroker) {

    }

}
