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

import org.wso2.carbon.rule.mediator.config.Source;
import org.wso2.carbon.rule.common.config.HelperUtil;
import org.wso2.carbon.rule.common.util.Constants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class SourceHelper {

    public static Source getSource(OMElement sourceOMElement){
        Source source = new Source();

        if (sourceOMElement.getText() != null) {
            source.setValue(sourceOMElement.getText());
        }
        source.setXpath(HelperUtil.getAttributeValue(sourceOMElement,
                Constants.RULE_CONF_ATTR_XPATH));
        source.setPrefixToNamespaceMap(HelperUtil.getPrefixToNamespaceMap(sourceOMElement));
        return source;
    }
}
