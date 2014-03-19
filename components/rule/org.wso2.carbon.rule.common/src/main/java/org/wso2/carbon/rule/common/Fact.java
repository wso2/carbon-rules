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

package org.wso2.carbon.rule.common;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.wso2.carbon.rule.common.util.Constants;

import java.util.Map;

public class Fact {

    private String elementName;
    private String namespace;
    private String type;
    private String xpath;
    private Class typeClass;
    private Map<String, String> prefixToNamespaceMap;


    public OMElement toOM(){
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement ruleFactOMElement =
                omFactory.createOMElement(Constants.RULE_CONF_ELE_FACT,
                                          Constants.RULE_CONF_NAMESPACE,
                                          Constants.RULE_CONF_NAMESPACE_PREFIX);

        if((this.elementName != null) && (!this.elementName.trim().equals(""))){
            ruleFactOMElement.addAttribute(Constants.RULE_CONF_ATTR_ELEMENT_NAME,
                                            this.elementName, null);
        }
        if((this.namespace != null) && (!this.namespace.trim().equals(""))){
            ruleFactOMElement.addAttribute(Constants.RULE_CONF_ATTR_NAMESPACE,
                                            this.namespace, null);
        }
        if((this.type != null) && (!this.type.trim().equals(""))){
            ruleFactOMElement.addAttribute(Constants.RULE_CONF_ATTR_TYPE,
                                            this.type, null);
        }
        if((this.xpath != null) && (!this.xpath.trim().equals(""))){
            ruleFactOMElement.addAttribute(Constants.RULE_CONF_ATTR_XPATH,
                                            this.xpath, null);
        }
        if(this.prefixToNamespaceMap != null){

            for (String prefix : this.prefixToNamespaceMap.keySet()){

                String nsURI = this.prefixToNamespaceMap.get(prefix);
                ruleFactOMElement.declareNamespace(nsURI, prefix);

            }
        }

        return ruleFactOMElement;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public Class getTypeClass() {
        return typeClass;
    }

    public void setTypeClass(Class typeClass) {
        this.typeClass = typeClass;
    }

    public Map<String, String> getPrefixToNamespaceMap() {
        return prefixToNamespaceMap;
    }

    public void setPrefixToNamespaceMap(Map<String, String> prefixToNamespaceMap) {
        this.prefixToNamespaceMap = prefixToNamespaceMap;
    }
}
