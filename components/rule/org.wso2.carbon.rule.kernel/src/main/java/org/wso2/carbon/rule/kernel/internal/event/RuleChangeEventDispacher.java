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

package org.wso2.carbon.rule.kernel.internal.event;

import org.wso2.carbon.event.core.subscription.EventDispatcher;
import org.wso2.carbon.event.core.subscription.Subscription;
import org.wso2.carbon.event.core.Message;
import org.wso2.carbon.rule.kernel.engine.RuleEngine;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RuleChangeEventDispacher implements EventDispatcher{

    private static Log log = LogFactory.getLog(RuleChangeEventDispacher.class);

    private RuleEngine ruleEngine;

    public RuleChangeEventDispacher(RuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public void notify(Message message, Subscription subscription) {
        try {
            this.ruleEngine.loadRuleBackendRuntime();
        } catch (RuleConfigurationException e) {
            log.error("Can not refresh the rule set ", e);
        }
    }
}
