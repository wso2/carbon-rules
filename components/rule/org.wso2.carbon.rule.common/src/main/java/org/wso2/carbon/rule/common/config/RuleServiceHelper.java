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

package org.wso2.carbon.rule.common.config;

import org.apache.axiom.om.OMElement;
import org.wso2.carbon.rule.common.Operation;
import org.wso2.carbon.rule.common.RuleService;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.util.Constants;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * utility class to parse the rule service OMElement and get the Rule service object structure.
 */
public class RuleServiceHelper {

    /**
     * returns the RuleService Object for given OMElement object.
     * @param ruleServiceElement - OMElement contatins the detials of the
     * @return   - corresponding Rule Service Object
     * @throws RuleConfigurationException  - if there is a problem.
     */
    public static RuleService getRuleService(OMElement ruleServiceElement)
            throws RuleConfigurationException {

        if (!ruleServiceElement.getQName().equals(new QName(
                Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_RULE_SERVICE))) {
            throw new RuleConfigurationException("Invalid rule service configuration file");
        }

        RuleService ruleService = new RuleService();

        ruleService.setName(HelperUtil.getAttributeValue(
                ruleServiceElement, Constants.RULE_CONF_ATTR_NAME));
        ruleService.setTargetNamespace(HelperUtil.getAttributeValue(
                ruleServiceElement, Constants.RULE_CONF_ATTR_TARGET_NAMESPACE));
        ruleService.setScope(HelperUtil.getAttributeValue(
                ruleServiceElement, Constants.RULE_CONF_ATTR_SCOPE));
        ruleService.setDescription(HelperUtil.getAttributeValue(
                ruleServiceElement, Constants.RULE_CONF_ATTR_DESCRIPTION));

        OMElement ruleSetOMElement =
                ruleServiceElement.getFirstChildWithName(new QName(
                        Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_RULE_SET));
        if (ruleSetOMElement == null) {
            throw new RuleConfigurationException("No rule set is defined");
        }

        ruleService.setRuleSet(RuleSetHelper.getRuleSet(ruleSetOMElement));

        List<Operation> operations = new ArrayList<Operation>();

        Iterator operationIter =
                ruleServiceElement.getChildrenWithName(new QName(
                        Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_OPERATION));
        OMElement operationElement;
        for (; operationIter.hasNext(); ) {
            operationElement = (OMElement) operationIter.next();
            operations.add(OperationHelper.getOperation(operationElement));
        }

        ruleService.setOperations(operations);

        return ruleService;
    }


}
