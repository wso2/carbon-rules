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

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

public class Input {

    private String wrapperElementName;
    private String nameSpace;
    private List<Fact> facts;

    public Input() {
        this.facts = new ArrayList<Fact>();
    }

    public OMElement toOM(){
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement ruleInputOMElement =
                omFactory.createOMElement(Constants.RULE_CONF_ELE_INPUT,
                                          Constants.RULE_CONF_NAMESPACE,
                                          Constants.RULE_CONF_NAMESPACE_PREFIX);
        if((this.nameSpace != null) && (!this.nameSpace.trim().equals(""))){
            ruleInputOMElement.addAttribute(Constants.RULE_CONF_ATTR_NAMESPACE,
                                            this.nameSpace, null);
        }
        if((this.wrapperElementName != null) && (!this.wrapperElementName.trim().equals(""))){
            ruleInputOMElement.addAttribute(Constants.RULE_CONF_ATTR_WRAPPER_ELEMENT_NAME,
                                            this.wrapperElementName, null);
        }

        for(Fact fact  : this.facts){
            ruleInputOMElement.addChild(fact.toOM());
        }

        return ruleInputOMElement;

    }

    public void addFact(Fact fact){
        this.facts.add(fact);
    }

    public QName getQName(){
        return new QName(this.nameSpace, this.wrapperElementName);
    }

    public String getWrapperElementName() {
        return wrapperElementName;
    }

    public void setWrapperElementName(String wrapperElementName) {
        this.wrapperElementName = wrapperElementName;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public List<Fact> getFacts() {
        return facts;
    }

    public void setFacts(List<Fact> facts) {
        this.facts = facts;
    }
}
