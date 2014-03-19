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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.description.java2wsdl.NamespaceGenerator;
import org.apache.axis2.description.java2wsdl.DefaultNamespaceGenerator;
import org.wso2.carbon.rule.common.Fact;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

public class HelperUtil {

    public static String getAttributeValue(OMElement omElement, String attributeName){
        return omElement.getAttributeValue(new QName("", attributeName));
    }

    public static void processFactDefaultValues(List<Fact> facts, ClassLoader classLoader)
            throws RuleConfigurationException {
        for (Fact fact : facts) {
            try {
                //keep the class instances here to avoid class loading issues at the runtime.
                fact.setTypeClass(classLoader.loadClass(fact.getType()));
                if (fact.getElementName() == null){
                    String typeName = fact.getType();
                    if (typeName.indexOf(".") > -1){
                        typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
                    }

                    //make first letter lower case
                    typeName = typeName.substring(0,1).toLowerCase() + typeName.substring(1);
                    fact.setElementName(typeName);
                }

                if (fact.getNamespace() == null){
                    NamespaceGenerator namespaceGenerator = new DefaultNamespaceGenerator();
                    fact.setNamespace(
                            namespaceGenerator.namespaceFromPackageName(fact.getTypeClass().getPackage().getName()).toString());
                }

            } catch (ClassNotFoundException e) {
                throw new RuleConfigurationException("Can not load the class", e);
            }
        }
    }

    public static Map<String, String> getPrefixToNamespaceMap(OMElement omElement) {
        Iterator iterator = omElement.getAllDeclaredNamespaces();
        OMNamespace omNamespace;
        Map<String,String> prefixToNamespaceMap = new HashMap<String, String>();
        for (; iterator.hasNext();) {
            omNamespace = (OMNamespace) iterator.next();
            prefixToNamespaceMap.put(omNamespace.getPrefix(), omNamespace.getNamespaceURI());
        }
        return prefixToNamespaceMap;
    }
}
