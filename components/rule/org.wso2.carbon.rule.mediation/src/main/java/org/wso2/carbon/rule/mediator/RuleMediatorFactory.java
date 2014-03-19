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

package org.wso2.carbon.rule.mediator;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractMediatorFactory;
import org.wso2.carbon.rule.common.config.HelperUtil;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.kernel.engine.RuleEngine;
import org.wso2.carbon.rule.mediator.config.RuleMediatorConfig;
import org.wso2.carbon.rule.mediator.internal.config.RuleMediatorConfigHelper;

import javax.xml.namespace.QName;
import java.util.Properties;

public class RuleMediatorFactory extends AbstractMediatorFactory {

    protected Mediator createSpecificMediator(OMElement omElement, Properties properties) {

        RuleMediatorConfig ruleMediatorConfig = RuleMediatorConfigHelper.getRuleMediatorConfig(omElement);
        RuleMediator ruleMediator = null;

        try {
            processDefaultValues(ruleMediatorConfig, this.getClass().getClassLoader());
            ruleMediatorConfig.getOutput().populateClassTypes();
            RuleEngine ruleEngine = new RuleEngine(ruleMediatorConfig.getRuleSet(),
                    this.getClass().getClassLoader());
            ruleMediator = new RuleMediator(ruleEngine,
                    omElement,
                    ruleMediatorConfig.getSource(),
                    ruleMediatorConfig.getTarget(),
                    ruleMediatorConfig.getInput(),
                    ruleMediatorConfig.getOutput());
        } catch (RuleConfigurationException e) {
            handleException("Can not create the rule engine ", e);
        }
        return ruleMediator;
    }

    public QName getTagQName() {
        return new QName(Constants.RULE_CONF_NAMESPACE,
                Constants.RULE_CONF_ELE_RULE);
    }

    private void processDefaultValues(RuleMediatorConfig ruleMediatorConfig,
                                      ClassLoader classLoader) throws RuleConfigurationException {
        HelperUtil.processFactDefaultValues(ruleMediatorConfig.getInput().getFacts(), classLoader);
        HelperUtil.processFactDefaultValues(ruleMediatorConfig.getOutput().getFacts(), classLoader);

    }


}
