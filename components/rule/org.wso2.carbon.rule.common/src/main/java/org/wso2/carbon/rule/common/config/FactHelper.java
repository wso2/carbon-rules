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

import org.wso2.carbon.rule.common.Fact;
import org.wso2.carbon.rule.common.util.Constants;
import org.apache.axiom.om.OMElement;

public class FactHelper {

    public static Fact getFact(OMElement factElement) {

        Fact fact = new Fact();
        fact.setType(HelperUtil.getAttributeValue(factElement, Constants.RULE_CONF_ATTR_TYPE));
        fact.setElementName(HelperUtil.getAttributeValue(factElement, Constants.RULE_CONF_ATTR_ELEMENT_NAME));
        fact.setNamespace(HelperUtil.getAttributeValue(factElement, Constants.RULE_CONF_ATTR_NAMESPACE));
        fact.setXpath(HelperUtil.getAttributeValue(factElement, Constants.RULE_CONF_ATTR_XPATH));
        fact.setPrefixToNamespaceMap(HelperUtil.getPrefixToNamespaceMap(factElement));
        return fact;
    }
}
