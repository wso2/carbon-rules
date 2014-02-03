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

package org.wso2.carbon.rule.ws.internal.schema;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.axis2.description.AxisService;
import org.wso2.carbon.rule.common.Operation;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.Input;
import org.wso2.carbon.rule.common.Fact;
import org.wso2.carbon.rule.common.Output;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.HashMap;

/**
 * this class is used to generate the schema for the rule services. It generates the schema for the wrapper elements and
 * uses axis2 to generate the schema for fact classes.
 */
public class SchemaBuilder {

    private RuleSchemaGenerator ruleSchemaGenerator;

    public SchemaBuilder(ClassLoader classLoader,
                         AxisService axisService) throws RuleConfigurationException {
        try {
            this.ruleSchemaGenerator = new RuleSchemaGenerator(classLoader, axisService);
            this.ruleSchemaGenerator.setPkg2nsmap(new HashMap<String, String>());
        } catch (Exception e) {
            throw new RuleConfigurationException("Problem with creating the schema generator");
        }
    }

    public void addOperation(Operation operation) throws RuleConfigurationException {
        // create the xml message for in put element
        Input input = operation.getInput();
        generateOperationElement(input.getNameSpace(), input.getWrapperElementName(), input.getFacts());

        Output output = operation.getOutput();
        generateOperationElement(output.getNameSpace(), output.getWrapperElementName(), output.getFacts());

    }

    private void generateOperationElement(String namespace,
                                          String wrapperElementName,
                                          List<Fact> facts) throws RuleConfigurationException {
        XmlSchema xmlSchema = this.ruleSchemaGenerator.getXmlSchema(namespace);

        XmlSchemaElement xmlSchemaElement = new XmlSchemaElement();
        xmlSchemaElement.setName(wrapperElementName);
        xmlSchemaElement.setQName(new QName(namespace, wrapperElementName));

        XmlSchemaComplexType xmlSchemaComplexType = new XmlSchemaComplexType(xmlSchema);
        xmlSchemaElement.setSchemaType(xmlSchemaComplexType);

        XmlSchemaSequence xmlSchemaSequence = new XmlSchemaSequence();
        xmlSchemaComplexType.setParticle(xmlSchemaSequence);

        XmlSchemaElement factElement = null;
        for (Fact fact : facts) {
            factElement = new XmlSchemaElement();
            factElement.setName(fact.getElementName());
            factElement.setMinOccurs(0);
            factElement.setMaxOccurs(Long.MAX_VALUE);
            xmlSchemaSequence.getItems().add(factElement);
            try {
                Class factClass = fact.getTypeClass();
                //set this class package name space as the fact namespace
                this.ruleSchemaGenerator.getPkg2nsmap().put(
                                      factClass.getPackage().getName(), fact.getNamespace());
                QName complexTypeName =  this.ruleSchemaGenerator.generateSchema(factClass);
                factElement.setSchemaTypeName(complexTypeName);
            } catch (ClassNotFoundException e) {
                throw new RuleConfigurationException(" Can not find the class " + fact.getType(), e);
            } catch (Exception e) {
                throw new RuleConfigurationException(" Can not generate the schema for " + fact.getType(), e);
            }
        }

        xmlSchema.getItems().add(xmlSchemaElement);
        xmlSchema.getElements().add(new QName(namespace, wrapperElementName), xmlSchemaElement);

    }

    public List<XmlSchema> getSchemaList() {
        return this.ruleSchemaGenerator.getSchemaList();
    }
}
