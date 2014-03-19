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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleSet {

    private List<Rule> rules;
    private String bindURI;
    private Map<String, String> properties;
    private Map<String, String> registrationProperties;
    private Map<String, String> deregistrationProperties;


    public RuleSet() {
        this.rules = new ArrayList<Rule>();
        this.properties = new HashMap<String, String>();
    }

    public OMElement toOM(){
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement ruleSetOMElement =
                omFactory.createOMElement(Constants.RULE_CONF_ELE_RULE_SET,
                                          Constants.RULE_CONF_NAMESPACE,
                                          Constants.RULE_CONF_NAMESPACE_PREFIX);
        if(this.properties != null){
            OMElement propertiesElement =
                    omFactory.createOMElement(Constants.RULE_CONF_ELE_PROPERTIES,
                                              Constants.RULE_CONF_NAMESPACE,
                                              Constants.RULE_CONF_NAMESPACE_PREFIX);
            for( String attName : this.properties.keySet()){
                String attValue = this.properties.get(attName);

                OMElement propertyElement =
                            omFactory.createOMElement(Constants.RULE_CONF_ELE_PROPERTY,
                                                      Constants.RULE_CONF_NAMESPACE,
                                                      Constants.RULE_CONF_NAMESPACE_PREFIX);


                propertyElement.addAttribute(Constants.RULE_CONF_ATTR_NAME,attName,null);
                propertyElement.addAttribute(Constants.RULE_CONF_ATTR_VALE,attValue,null);
                 propertiesElement.addChild(propertyElement);

            }
            ruleSetOMElement.addChild(propertiesElement);
        }


        for ( Rule rule : this.rules){
                ruleSetOMElement.addChild(rule.toOM());
            }

        return ruleSetOMElement;
    }

    public void addRule(Rule rule){
        this.rules.add(rule);
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public void clearRules(){
        this.rules.clear();
    }

    public String getBindURI() {
        return bindURI;
    }

    public void setBindURI(String bindURI) {
        this.bindURI = bindURI;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getRegistrationProperties() {
        return registrationProperties;
    }

    public void setRegistrationProperties(Map<String, String> registrationProperties) {
        this.registrationProperties = registrationProperties;
    }

    public Map<String, String> getDeregistrationProperties() {
        return deregistrationProperties;
    }

    public void setDeregistrationProperties(Map<String, String> deregistrationProperties) {
        this.deregistrationProperties = deregistrationProperties;
    }
    public void addProperties(String atName, String atValue){

        this.properties.put(atName, atValue);

    }
}
