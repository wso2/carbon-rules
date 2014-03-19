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

package org.wso2.carbon.rule.kernel.engine;

import org.apache.axiom.om.*;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.wso2.carbon.rule.kernel.backend.Session;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.wso2.carbon.rule.kernel.internal.util.DataBindUtil;
import org.wso2.carbon.rule.common.Input;
import org.wso2.carbon.rule.common.Output;
import org.wso2.carbon.rule.common.Fact;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;
import java.util.*;

/**
 * This abstract the Back end rule session with OM processing layer. This class basically converts the OMElement messages
 * to real pojo objects and send to backend. Then converts back.
 */
public class RuleSession {

    private Session session;

    public RuleSession(Session session) {
        this.session = session;
    }

    /**
     * This method converts the OMElement messages received to POJO format using Axis2 BeanUtil class and converts
     * the results back to OMElement form. In order to do these conversions it uses the input and output configurations
     * given by the user.
     * @param inputElement - in comming message OMElement
     * @param input   - input configuration which specify which classes to be used with which Elements.
     * @param output  - output configuration to be used to convert the results back to OMElements
     * @return - Result OMElement after applying the rules
     * @throws RuleRuntimeException - if there are problems with the configuration
     */
    public OMElement execute(OMElement inputElement, Input input, Output output) throws RuleRuntimeException {

        List<Object> inputFactObjects = new ArrayList<Object>();
        for (Fact fact : input.getFacts()){
            inputFactObjects.addAll(getFacts(inputElement, fact, input.getNameSpace()));
        }

        List resultObjects = this.session.execute(inputFactObjects);

        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement outWrapperElement =
                omFactory.createOMElement(new QName(output.getNameSpace(), output.getWrapperElementName()));

        for (Object object : resultObjects){
            // we need to add the object types only present in the output fact mappings.
            if (output.isFactTypeExists(object.getClass().getName())) {
                 outWrapperElement.addChild(DataBindUtil.toOM(object,
                                       output.getFactTypeQName(object.getClass().getName())));
            }
        }

        return outWrapperElement;
    }

    /**
     * return the list of facts corresponing to the given OMElement.
     * @param inputElement  - input OMElement which is to be used to get the facts.
     * @param fact  - fact configuration object
     * @param factElementNamespace  - namespace of the fact object
     * @return   - list of facts for this input element
     * @throws RuleRuntimeException - if there is a problem with converting the objects
     */
    private List<Object> getFacts(OMElement inputElement, Fact fact, String factElementNamespace) throws RuleRuntimeException {

        List<Object> results = new ArrayList<Object>();
        if ((fact.getXpath() != null) && (!fact.getXpath().equals(""))){
            try {
                AXIOMXPath axiomxPath = new AXIOMXPath(fact.getXpath());
                for (String prefix : fact.getPrefixToNamespaceMap().keySet()){
                    axiomxPath.addNamespace(prefix, fact.getPrefixToNamespaceMap().get(prefix));
                }

                List selectedNodes = axiomxPath.selectNodes(inputElement);
                for (Object selectedNode : selectedNodes){
                    if (selectedNode instanceof OMText){
                        OMText selectedOMTextNode = (OMText) selectedNode;
                        results.add(selectedOMTextNode.getText());
                    } else if (selectedNode instanceof OMElement){
                        OMElement selectedNodeOMElement = (OMElement) selectedNode;
                        results.add(DataBindUtil.getValue(selectedNodeOMElement, fact.getTypeClass()));
                    }
                }
            } catch (JaxenException e) {
               throw new RuleRuntimeException("Can not create xpath from the xpath " + fact.getXpath());
            }

        } else {
            Iterator factsIterator = inputElement.getChildElements();
            for (;factsIterator.hasNext();){
                OMElement factElement = (OMElement) factsIterator.next();
                if (factElement.getQName().equals(new QName(factElementNamespace, fact.getElementName()))){
                   results.add(DataBindUtil.getValue(factElement, fact.getTypeClass()));
                }
            }
        }
        return results;
    }

}
