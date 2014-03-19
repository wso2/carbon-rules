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

import org.wso2.carbon.rule.common.RuleSet;
import org.wso2.carbon.rule.common.Rule;
import org.wso2.carbon.rule.common.util.Constants;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;
import java.util.*;

public class RuleSetHelper {

    public static RuleSet getRuleSet(OMElement ruleSetElement) {

        RuleSet ruleSet = new RuleSet();

        Iterator ruleIterator =
                ruleSetElement.getChildrenWithName(new QName(
                        Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_RULE));

        OMElement ruleElement;
        List<Rule> rules = new ArrayList<Rule>();

        for (; ruleIterator.hasNext();) {
            ruleElement = (OMElement) ruleIterator.next();
            rules.add(RuleHelper.getRule(ruleElement));
        }

        ruleSet.setRules(rules);

        OMElement propertiesElement =
                ruleSetElement.getFirstChildWithName(new QName(
                        Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_PROPERTIES));
        Map<String, String> properties = new HashMap<String, String>();
        if (propertiesElement != null) {

            Iterator propertiesIter =
                    propertiesElement.getChildrenWithName(new QName(
                            Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_PROPERTY));
            OMElement property;
            for (; propertiesIter.hasNext();) {
                property = (OMElement) propertiesIter.next();
                properties.put(HelperUtil.getAttributeValue(property, Constants.RULE_CONF_ATTR_NAME),
                        HelperUtil.getAttributeValue(property, Constants.RULE_CONF_ATTR_VALE));
            }

        }
        ruleSet.setProperties(properties);
        return ruleSet;
    }
}
