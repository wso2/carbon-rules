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

 import javax.xml.stream.XMLStreamConstants;

 public class Rule {

    private String sourceType;
    private String resourceType;
    private String value;
    private String description;

    public OMElement toOM(){
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement ruleOMElement =
                omFactory.createOMElement(Constants.RULE_CONF_ELE_RULE,
                                          Constants.RULE_CONF_NAMESPACE,
                                          Constants.RULE_CONF_NAMESPACE_PREFIX);
        if((this.resourceType != null) && (!this.resourceType.trim().equals(""))){
            ruleOMElement.addAttribute(Constants.RULE_CONF_ATTR_RESOURCE_TYPE,
                                       this.resourceType, null);

        }
        if((this.sourceType != null) && (!this.sourceType.trim().equals(""))){
            ruleOMElement.addAttribute(Constants.RULE_CONF_ATTR_SOURCE_TYPE,
                                        this.sourceType, null );
        }
        if((this.value != null) && (!this.value.trim().equals(""))){
            if(sourceType.equals(Constants.RULE_SOURCE_TYPE_INLINE)) {
                omFactory.createOMText(ruleOMElement, this.value, XMLStreamConstants.CDATA);
            }
            else {
                ruleOMElement.setText(value);
            }

        }

        if ((this.description != null) && (!this.description.trim().equals(""))) {
            ruleOMElement.addAttribute(Constants.RULE_CONF_ATTR_DESCRIPTION,
                    this.description, null);
        }

        return ruleOMElement;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

     public String getDescription() {
         return description;
     }

     public void setDescription(String description) {
         this.description = description;
     }
 }
