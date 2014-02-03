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

package org.wso2.carbon.rule.mediator;

import org.apache.axiom.om.*;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.jaxen.JaxenException;
import org.wso2.carbon.rule.common.Input;
import org.wso2.carbon.rule.common.Output;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.kernel.engine.RuleEngine;
import org.wso2.carbon.rule.kernel.engine.RuleSession;
import org.wso2.carbon.rule.mediator.config.Source;
import org.wso2.carbon.rule.mediator.config.Target;

import java.util.ArrayList;
import java.util.Map;

public class RuleMediator extends AbstractMediator {

    private RuleEngine ruleEngine;

    private OMElement ruleOMElement;

    private Source source;

    private Target target;

    private Input input;

    private Output output;

    public RuleMediator(RuleEngine ruleEngine,
                        OMElement ruleOMElement,
                        Source source,
                        Target target,
                        Input input,
                        Output output) {
        this.ruleEngine = ruleEngine;
        this.ruleOMElement = ruleOMElement;
        this.source = source;
        this.target = target;
        this.input = input;
        this.output = output;
    }

    public boolean mediate(MessageContext messageContext) {

        OMElement inputOMElement = getOMElement(messageContext, this.source.getValue());

        if ((this.source.getXpath() != null) && (!this.source.getXpath().equals(""))) {
            inputOMElement = executeXpath(inputOMElement,
                                            this.source.getXpath(),
                                            this.source.getPrefixToNamespaceMap(),
                                            messageContext);
        }

        try {
            RuleSession ruleSession = ruleEngine.createSession(Constants.RULE_STATEFUL_SESSION);
            OMNode resultOMNode = ruleSession.execute(inputOMElement, this.input, this.output);

            if ((this.target.getResultXpath() != null) && (!this.target.getResultXpath().equals(""))) {
                try {
                    AXIOMXPath axiomXPath = new AXIOMXPath(this.target.getResultXpath());
                    for (String prefix : this.target.getPrefixToNamespaceMap().keySet()) {
                        axiomXPath.addNamespace(prefix, this.target.getPrefixToNamespaceMap().get(prefix));
                    }

                    //we need to create the OMDocuent element to properly support the xpath manupulations.
                    OMFactory omFactory = resultOMNode.getOMFactory();
                    OMDocument omDocument = omFactory.createOMDocument();
                    omDocument.addChild(resultOMNode);
                    resultOMNode = (OMNode) axiomXPath.selectSingleNode(resultOMNode);
                } catch (JaxenException e) {
                    handleException("Invalid xpath " + this.target.getResultXpath(), messageContext);
                }
            }

            OMElement targetOMElement = getOMElement(messageContext, this.target.getValue());

            if ((this.target.getXpath() != null) && (!this.target.getXpath().equals(""))) {
                if (targetOMElement == null) {
                    handleException("Target element is null for the target"
                            + this.target.getValue() + ". Can not apply xpaths.", messageContext);
                } else {
                    targetOMElement = executeXpath(targetOMElement,
                            this.target.getXpath(),
                            this.target.getPrefixToNamespaceMap(),
                            messageContext);
                }
            }

            if (this.target.getAction().equals(Constants.RULE_CONF_ATTR_ACTION_REPLACE)) {

                // if the target is a property set that directly.
                if (this.target.getValue().startsWith(Constants.RULE_PROPERTY_PREFIX)){
                    String propertyName = this.target.getValue().substring(1);
                    if (resultOMNode instanceof OMText){
                        messageContext.setProperty(propertyName, ((OMText)resultOMNode).getText());
                    } else {
                       messageContext.setProperty(propertyName, resultOMNode); 
                    }
                // the target element can be null only if it is a property. if it is soap header or boady target element
                // can not be null.
                } else if (targetOMElement.getPreviousOMSibling() != null) {
                    OMNode previousSibling = targetOMElement.getPreviousOMSibling();
                    targetOMElement.detach();
                    previousSibling.insertSiblingAfter(resultOMNode);

                } else if (targetOMElement.getParent() != null) {
                    OMElement parentOMElement = (OMElement) targetOMElement.getParent();
                    targetOMElement.detach();
                    parentOMElement.addChild(resultOMNode);
                } else {
                    handleException("OMElement without parent but it is not a property as well "
                            + this.source.getValue(), messageContext);
                }
            } else if (this.target.getAction().equals(Constants.RULE_CONF_ATTR_ACTION_SIBLING)){
                targetOMElement.insertSiblingAfter(resultOMNode);
            } else if (this.target.getAction().equals(Constants.RULE_CONF_ATTR_ACTION_CHILD)){
                targetOMElement.addChild(resultOMNode);
            } else {
                handleException("Invalid action " + this.target.getAction(), messageContext);
            }

        } catch (RuleRuntimeException e) {
            handleException("Can not create the rule session ", e, messageContext);
        }

        return true;
    }

    public OMElement getRuleOMElement() {
        return ruleOMElement;
    }

    public void setRuleOMElement(OMElement ruleOMElement) {
        this.ruleOMElement = ruleOMElement;
    }

    private OMElement getOMElement(MessageContext messageContext, String location) {
        OMElement omElement = null;
        if (location.equals(Constants.RULE_SOURCE_SOAP_BODY)) {
            omElement = messageContext.getEnvelope().getBody().getFirstElement();
        } else if (location.equals(Constants.RULE_SOURCE_SOAP_HEADER)) {
            omElement = messageContext.getEnvelope().getHeader();
        } else if (location.startsWith(Constants.RULE_PROPERTY_PREFIX)) {
            // property name should have started with $ so remove it
            String propertyName = location.substring(1);
            if (messageContext.getProperty(propertyName) instanceof OMElement) {
                omElement = (OMElement) messageContext.getProperty(propertyName);
            } else if (messageContext.getProperty(propertyName) instanceof ArrayList) {
                ArrayList arrayList = (ArrayList) messageContext.getProperty(propertyName);
                //here we take the first OMElement
                omElement = (OMElement) arrayList.get(0);
            }
        } else {
            handleException("invalde source value " + location, messageContext);
        }
        return omElement;
    }

    private OMElement executeXpath(OMNode inputOMElement,
                                   String xpath,
                                   Map<String, String> prefixToNamespaceMap,
                                   MessageContext messageContext) {
        OMElement omElement = null;
        try {
            AXIOMXPath axiomXPath = new AXIOMXPath(xpath);
            for (Map.Entry<String, String> entry : prefixToNamespaceMap.entrySet()) {
                axiomXPath.addNamespace(entry.getKey(), entry.getValue());
            }
            omElement = (OMElement) axiomXPath.selectSingleNode(inputOMElement);
        } catch (JaxenException e) {
            handleException("Invalid xpath " + xpath, messageContext);
        }
        return omElement;
    }


}
