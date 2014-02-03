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

import org.wso2.carbon.rule.common.Output;
import org.wso2.carbon.rule.common.Fact;
import org.wso2.carbon.rule.common.util.Constants;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class OutputHelper {

    public static Output getOutput(OMElement outputElement){

        Output output = new Output();

        output.setNameSpace(HelperUtil.getAttributeValue(
                       outputElement, Constants.RULE_CONF_ATTR_NAMESPACE));
        output.setWrapperElementName(HelperUtil.getAttributeValue(
                       outputElement, Constants.RULE_CONF_ATTR_WRAPPER_ELEMENT_NAME));

        Iterator factIterator =
                outputElement.getChildrenWithName(new QName(
                        Constants.RULE_CONF_NAMESPACE, Constants.RULE_CONF_ELE_FACT));
        OMElement factElement;
        List<Fact> facts = new ArrayList<Fact>();
        for (; factIterator.hasNext();) {
            factElement = (OMElement) factIterator.next();
            facts.add(FactHelper.getFact(factElement));
        }
        output.setFacts(facts);
        return output;
    }
}
