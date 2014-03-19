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

package org.wso2.carbon.rule.ws.admin.internal.config;

import org.apache.axiom.om.OMElement;

import javax.xml.stream.XMLStreamException;
import java.io.OutputStream;

public class RuleService {

    private String name;
    private OMElement ruleServiceElemet;

    public RuleService(OMElement ruleServiceElemet) {
        this.ruleServiceElemet = ruleServiceElemet;
    }

    public void serialize(OutputStream outputStream) throws XMLStreamException {
        this.ruleServiceElemet.serialize(outputStream);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
