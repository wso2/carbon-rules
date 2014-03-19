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

import org.wso2.carbon.rule.mediator.config.Target;
import org.wso2.carbon.rule.common.config.HelperUtil;
import org.wso2.carbon.rule.common.util.Constants;
import org.apache.axiom.om.OMElement;

public class TargetHelper {

    public static Target getTarget(OMElement targetOMElement){
        Target target = new Target();

        if (targetOMElement.getText() != null){
            target.setValue(targetOMElement.getText());
        }

        target.setXpath(HelperUtil.getAttributeValue(targetOMElement,
                                                Constants.RULE_CONF_ATTR_XPATH));
        target.setResultXpath(HelperUtil.getAttributeValue(targetOMElement,
                                                Constants.RULE_CONF_ATTR_RESULT_XPATH));
        target.setAction(HelperUtil.getAttributeValue(targetOMElement,
                                                Constants.RULE_CONF_ATTR_ACTION));
        target.setPrefixToNamespaceMap(HelperUtil.getPrefixToNamespaceMap(targetOMElement));
        return target;
    }
}
