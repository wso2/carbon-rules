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

package org.wso2.carbon.rule.mediator.config;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMAbstractFactory;
import org.wso2.carbon.rule.common.util.Constants;

import java.util.Map;

public class Source {

    private String value;
    private String xpath;
    private Map<String, String> prefixToNamespaceMap;

    public OMElement toOM(){
        OMFactory omFactory = OMAbstractFactory.getOMFactory();

        OMElement sourceElement = omFactory.createOMElement(Constants.RULE_CONF_ELE_SOURCE,
                                                              Constants.RULE_CONF_NAMESPACE,
                                                              Constants.RULE_CONF_NAMESPACE_PREFIX);
        if (this.xpath != null){
            sourceElement.addAttribute(Constants.RULE_CONF_ATTR_XPATH, this.xpath, null);
        }

        if (this.value != null){
            sourceElement.setText(this.value);
        }
        if(this.prefixToNamespaceMap != null){

            for (String prefix : this.prefixToNamespaceMap.keySet()){

                String nsURI = this.prefixToNamespaceMap.get(prefix);
                sourceElement.declareNamespace(nsURI, prefix);

            }
        }

        return sourceElement;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public Map<String, String> getPrefixToNamespaceMap() {
        return prefixToNamespaceMap;
    }

    public void setPrefixToNamespaceMap(Map<String, String> prefixToNamespaceMap) {
        this.prefixToNamespaceMap = prefixToNamespaceMap;
    }
}
