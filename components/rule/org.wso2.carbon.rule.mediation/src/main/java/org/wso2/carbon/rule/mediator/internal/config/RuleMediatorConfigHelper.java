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

package org.wso2.carbon.rule.mediator.internal.config;

import org.wso2.carbon.rule.mediator.config.RuleMediatorConfig;
import org.wso2.carbon.rule.common.config.HelperUtil;
import org.wso2.carbon.rule.common.config.InputHelper;
import org.wso2.carbon.rule.common.config.OutputHelper;
import org.wso2.carbon.rule.common.config.RuleSetHelper;
import org.wso2.carbon.rule.common.util.Constants;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;

public class RuleMediatorConfigHelper {

    public static RuleMediatorConfig getRuleMediatorConfig(OMElement ruleMediatorElement) {

        RuleMediatorConfig ruleMediatorConfig = new RuleMediatorConfig();
        ruleMediatorConfig.setBackendRuntimeFactory(HelperUtil.getAttributeValue(
                ruleMediatorElement, Constants.RULE_CONF_ATTR_BACKEND_RUNTIME_FACTORY));

        OMElement sourceOMElement = ruleMediatorElement.getFirstChildWithName(
                new QName(Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_SOURCE));
        ruleMediatorConfig.setSource(SourceHelper.getSource(sourceOMElement));

        OMElement targetOMElement = ruleMediatorElement.getFirstChildWithName(
                new QName(Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_TARGET));
        ruleMediatorConfig.setTarget(TargetHelper.getTarget(targetOMElement));

        OMElement ruleSetOMElement = ruleMediatorElement.getFirstChildWithName(
                new QName(Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_RULE_SET));
        ruleMediatorConfig.setRuleSet(RuleSetHelper.getRuleSet(ruleSetOMElement));

        OMElement inputOMElement = ruleMediatorElement.getFirstChildWithName(
                new QName(Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_INPUT));
        ruleMediatorConfig.setInput(InputHelper.getInput(inputOMElement));

        OMElement outputOMElement = ruleMediatorElement.getFirstChildWithName(
                new QName(Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_OUTPUT));
        ruleMediatorConfig.setOutput(OutputHelper.getOutput(outputOMElement));
        return ruleMediatorConfig;
    }
}
