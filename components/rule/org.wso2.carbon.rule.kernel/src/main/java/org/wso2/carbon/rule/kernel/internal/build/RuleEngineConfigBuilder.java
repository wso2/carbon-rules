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

package org.wso2.carbon.rule.kernel.internal.build;

import org.wso2.carbon.rule.kernel.config.RuleEngineProvider;
import org.wso2.carbon.rule.kernel.config.RuleEngineConfig;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.config.HelperUtil;
import org.wso2.carbon.utils.ServerConstants;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.io.*;

/**
 * Create the Rule engine provider list reading the rule config file
 */
public class RuleEngineConfigBuilder {

    private static Log log = LogFactory.getLog(RuleEngineConfigBuilder.class);

    /**
     * provides the rule engine provider list
     * @return  lost of rule engine providers given in the rule conf file.
     * @throws RuleConfigurationException  - if there is a problem with configuration
     */
    public RuleEngineConfig getRuleConfig() throws RuleConfigurationException {
        OMElement ruleConfigOMElement = getOMElement();
        return getRuleConfig(ruleConfigOMElement);
    }

    private OMElement getOMElement() throws RuleConfigurationException {
        String carbonHome = System.getProperty(ServerConstants.CARBON_CONFIG_DIR_PATH);
        String path = carbonHome + File.separator + Constants.RULE_ENGINE_CONF_FILE;

        // if the cep config file not exists then simply return null.
        File ruleConfigFile = new File(path);
        if (!ruleConfigFile.exists()) {
            return null;
        }

        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(new File(path));
            XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
            StAXOMBuilder builder = new StAXOMBuilder(parser);
            OMElement omElement = builder.getDocumentElement();
            omElement.build();
            return omElement;
        } catch (FileNotFoundException e) {
            String errorMessage = Constants.RULE_ENGINE_CONF_FILE + "cannot be found in the path : " + path;
            throw new RuleConfigurationException(errorMessage, e);
        } catch (XMLStreamException e) {
            String errorMessage = "Invalid XML for " + Constants.RULE_ENGINE_CONF_FILE + " located in the path : " + path;
            throw new RuleConfigurationException(errorMessage, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.error("Can not close the input stream ", e);
            }
        }
    }


    private RuleEngineConfig getRuleConfig(OMElement ruleConfigOMElement)
            throws RuleConfigurationException {

        if (!ruleConfigOMElement.getQName().equals(new QName(Constants.RULE_ENGINE_CONF_NAMESPACE,
                Constants.RULE_ENGINE_CONF_ELE_RULE_ENGINE_CONFIG))) {
            throw new RuleConfigurationException("Invalid Rule Engine provider config file start element "
                    + ruleConfigOMElement.getQName());
        }

        RuleEngineConfig ruleEngineConfig = new RuleEngineConfig();
        OMElement ruleEngineProvierElement = ruleConfigOMElement.getFirstChildWithName(new QName(
                Constants.RULE_ENGINE_CONF_NAMESPACE,
                Constants.RULE_ENGINE_CONF_ELE_RULE_ENGINE_PROVIDER));
        ruleEngineConfig.setRuleEngineProvider(getRuleEngineProvider(ruleEngineProvierElement));

        return ruleEngineConfig;
    }

    private RuleEngineProvider getRuleEngineProvider(OMElement ruleEngineProviderElement) {
        RuleEngineProvider ruleEngineProvider = new RuleEngineProvider();
        ruleEngineProvider.setClassName(HelperUtil.getAttributeValue(ruleEngineProviderElement,
                Constants.RULE_ENGINE_CONF_ATTR_CLASS_NAME));

        Iterator propertyElementIter = ruleEngineProviderElement.getChildrenWithName(
                new QName(Constants.RULE_ENGINE_CONF_NAMESPACE, Constants.RULE_ENGINE_CONF_ELE_PROPERTY));
        OMElement propertElement;

        String name;
        String value;

        for (; propertyElementIter.hasNext();) {
            propertElement = (OMElement) propertyElementIter.next();
            name = HelperUtil.getAttributeValue(propertElement, Constants.RULE_ENGINE_CONF_ATTR_NAME);
            value = HelperUtil.getAttributeValue(propertElement, Constants.RULE_ENGINE_CONF_ATTR_VALUE);
            ruleEngineProvider.addProperty(name, value);
        }

        return ruleEngineProvider;

    }

}
