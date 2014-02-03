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

import org.apache.axis2.description.java2wsdl.DefaultSchemaGenerator;
import org.apache.axis2.description.AxisService;
import org.apache.ws.commons.schema.XmlSchema;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

/**
 * this class orverrides some methods of the axis2 schema generator. 
 */
public class RuleSchemaGenerator extends DefaultSchemaGenerator {

    public RuleSchemaGenerator(ClassLoader classLoader,
                               AxisService axisService) throws Exception {
        super(classLoader, DummyRuleService.class.getName(), null, null, axisService);
    }

    public QName generateSchema(Class<?> aClass) throws Exception {
        return super.generateSchema(aClass);
    }

    public XmlSchema getXmlSchema(String targetNamespace) {
        return super.getXmlSchema(targetNamespace);
    }

    public List<XmlSchema> getSchemaList(){
        List<XmlSchema> schemas = new ArrayList<XmlSchema>();
        schemas.addAll(this.schemaMap.values());
        return schemas;
    }
}
