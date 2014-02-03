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
import java.util.List;


public class RuleService {

    private String name;
    private String targetNamespace;
    private RuleSet ruleSet;
    private List<Operation> operations;
    private String scope;

    private String extension;
    private boolean editable;
    private String description;
    private boolean containsServicesXML;

    public RuleService() {

        this.operations = new ArrayList<Operation>();
        this.editable = true;
        this.containsServicesXML = false;
    }

    public OMElement toOM() {

        OMFactory omFactory = OMAbstractFactory.getOMFactory();

        OMElement ruleServiceOMElement =
                omFactory.createOMElement(Constants.RULE_CONF_ELE_RULE_SERVICE,
                        Constants.RULE_CONF_NAMESPACE,
                        Constants.RULE_CONF_NAMESPACE_PREFIX);
        ruleServiceOMElement.addAttribute(Constants.RULE_CONF_ATTR_NAME, this.name, null);
        if (this.targetNamespace != null) {
            ruleServiceOMElement.addAttribute(
                    Constants.RULE_CONF_ATTR_TARGET_NAMESPACE, this.targetNamespace, null);
        }

        if (this.scope != null) {
            ruleServiceOMElement.addAttribute(
                    Constants.RULE_CONF_ATTR_SCOPE,
                    this.scope, null);
        }
        if(this.description != null){
            ruleServiceOMElement.addAttribute(Constants.RULE_CONF_ATTR_DESCRIPTION, this.description, null);

        }

        ruleServiceOMElement.addChild(this.ruleSet.toOM());

        for (Operation operation : this.operations) {
            ruleServiceOMElement.addChild(operation.toOM());
        }

        return ruleServiceOMElement;
    }

    public Operation getOperation(String name) {
        Operation resultOperation = null;
        for (Operation operation : this.operations) {
            if (operation.getName().equals(name)) {
                resultOperation = operation;
                break;
            }
        }
        return resultOperation;
    }

    public void addOperation(Operation operation) {
        this.operations.add(operation);
    }

    public void removeOperation(String name) {

        List<Operation> tempOperations = new ArrayList<Operation>();

        for (Operation operation : this.operations) {
            if (!operation.getName().equalsIgnoreCase(name)) {
                tempOperations.add(operation);
            }

            this.operations = tempOperations;

        }


    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    public void setRuleSet(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isContainsServicesXML() {
        return containsServicesXML;
    }

    public void setContainsServicesXML(boolean containsServicesXML) {
        this.containsServicesXML = containsServicesXML;
    }
}
