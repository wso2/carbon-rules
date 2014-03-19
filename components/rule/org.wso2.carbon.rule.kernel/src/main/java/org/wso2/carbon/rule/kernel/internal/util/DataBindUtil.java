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

package org.wso2.carbon.rule.kernel.internal.util;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.typemapping.SimpleTypeMapper;
import org.apache.axis2.databinding.utils.BeanUtil;
import org.apache.axis2.engine.DefaultObjectSupplier;
import org.apache.axis2.util.StreamWrapper;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public class DataBindUtil {

    /**
     * Utility method to convert the java objects to OMElements
     *
     * @param object  - POJO object to be converted to an OMElement
     * @param parentQname - Top element QName for this POJO object
     * @return  - corresponding OMElement for POJO Object
     */
    public static OMElement toOM(Object object, QName parentQname) {

        OMElement omElement;
        if (SimpleTypeMapper.isSimpleType(object)) {
            OMFactory omFactory = OMAbstractFactory.getOMFactory();
            OMNamespace omNamespace = omFactory.createOMNamespace(
                    parentQname.getNamespaceURI(), parentQname.getPrefix());
            omElement = omFactory.createOMElement(parentQname.getLocalPart(), omNamespace);
            omElement.addChild(omFactory.createOMText(SimpleTypeMapper.getStringValue(object)));
        } else {
            XMLStreamReader xmlStreamReader =
                    BeanUtil.getPullParser(object, parentQname, null, true, false);
            StAXOMBuilder stAXOMBuilder = new StAXOMBuilder(new StreamWrapper(xmlStreamReader));
            omElement = stAXOMBuilder.getDocumentElement();
        }
        return omElement;
    }

    /**
     * utility method convert the OMElements to java objects.
     *
     * @param omElement - OMElement to be used to get the POJO object value
     * @param type  - class name of the POJO element to be created
     * @return    - the created fact object
     * @throws RuleRuntimeException - if there is a problem in creating POJO object
     */
    public static Object getValue(OMElement omElement,
                                  Class type)
            throws RuleRuntimeException {

        try {
            return BeanUtil.deserialize(type,
                    omElement, new DefaultObjectSupplier(), null);
        } catch (AxisFault axisFault) {
            throw new RuleRuntimeException("Error in parsing the omelement ", axisFault);
        }
    }

}
