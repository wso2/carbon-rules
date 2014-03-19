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
import org.wso2.carbon.rule.common.util.Constants;

import javax.xml.namespace.QName;

public class OperationHelper {

    public static Operation getOperation(OMElement operationElement){
        Operation operation = new Operation();

        operation.setName(HelperUtil.getAttributeValue(
                            operationElement, Constants.RULE_CONF_ATTR_NAME));

        OMElement inputOMElement =
                operationElement.getFirstChildWithName(new QName(
                        Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_INPUT));

       operation.setInput(InputHelper.getInput(inputOMElement));

        OMElement outputElement =
                operationElement.getFirstChildWithName(new QName(
                        Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_OUTPUT));
        if(outputElement != null){

            operation.setOutput(OutputHelper.getOutput(outputElement));
        }

        return operation;
    }
}
