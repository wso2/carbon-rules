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

import junit.framework.TestCase;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.description.AxisService;
import org.apache.ws.commons.schema.XmlSchema;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.config.HelperUtil;
import org.wso2.carbon.rule.common.RuleService;
import org.wso2.carbon.rule.common.Operation;
import org.wso2.carbon.rule.common.config.RuleServiceHelper;

import javax.xml.stream.XMLStreamException;
import java.util.List;

public class TestSchemaBuilder extends TestCase {

    public void testSchemaBuilder(){

        try {
            SchemaBuilder schemaBuilder =
                    new SchemaBuilder(this.getClass().getClassLoader(), new AxisService());
            StAXOMBuilder stAXOMBuilder =
                    new StAXOMBuilder(this.getClass().getResourceAsStream("/placeOrder.rsl"));
            RuleService ruleService =
                    RuleServiceHelper.getRuleService(stAXOMBuilder.getDocumentElement());

            for (Operation operation : ruleService.getOperations()) {
                HelperUtil.processFactDefaultValues(operation.getInput().getFacts(), this.getClass().getClassLoader());
                HelperUtil.processFactDefaultValues(operation.getOutput().getFacts(), this.getClass().getClassLoader());
            }

            List<Operation> operations = ruleService.getOperations();
            for (Operation operation : operations){
                schemaBuilder.addOperation(operation);
            }

           List<XmlSchema> xmlSchemas = schemaBuilder.getSchemaList();
           for (XmlSchema xmlSchema : xmlSchemas){
               System.out.println("Schema for " + xmlSchema.getTargetNamespace());
               xmlSchema.write(System.out);
           }
            

        } catch (RuleConfigurationException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }


    }

}
