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

import org.wso2.carbon.rule.common.Rule;
import org.wso2.carbon.rule.common.util.Constants;
import org.apache.axiom.om.OMElement;

public class RuleHelper {

    public static Rule getRule(OMElement ruleElement){
        Rule rule = new Rule();
        rule.setResourceType(HelperUtil.getAttributeValue(
                 ruleElement, Constants.RULE_CONF_ATTR_RESOURCE_TYPE));
        rule.setSourceType(HelperUtil.getAttributeValue(
                 ruleElement, Constants.RULE_CONF_ATTR_SOURCE_TYPE));
        rule.setDescription(HelperUtil.getAttributeValue(
                 ruleElement, Constants.RULE_CONF_ATTR_DESCRIPTION));
        rule.setValue(ruleElement.getText());
        return rule;
    }
}
