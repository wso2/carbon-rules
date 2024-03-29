/*
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*/
package org.wso2.carbon.rule.backend.util;


import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.rule.backend.internal.RuleServiceValueHolder;
import org.wso2.carbon.rule.common.Rule;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.util.Constants;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class RuleSetLoader {


    public static InputStream getRuleSetAsStream(Rule rule, ClassLoader classLoader) throws RuleConfigurationException {
        InputStream ruleInputStream = null;

        if (rule.getSourceType().equalsIgnoreCase(Constants.RULE_SOURCE_TYPE_INLINE)) {
            ruleInputStream = new ByteArrayInputStream(rule.getValue().getBytes());
        } else if (rule.getSourceType().equalsIgnoreCase(Constants.RULE_SOURCE_TYPE_REGISTRY)) {
            String[] vale = rule.getValue().split(":");
            String type = vale[0];
            String key = vale[1];

            try {
                ruleInputStream = getRegistryAsStream(type, key);
            } catch (RegistryException e) {
                throw new RuleConfigurationException("Can not access the registry : ", e);
            }

        } else if (rule.getSourceType().equalsIgnoreCase(Constants.RULE_SOURCE_TYPE_FILE)) {
            ruleInputStream = classLoader.getResourceAsStream(
                    Constants.RULE_SOURCE_FILE_DIRECTORY + "/" + rule.getValue());
        } else if (rule.getSourceType().equalsIgnoreCase(Constants.RULE_SOURCE_TYPE_URL)) {
            String url = rule.getValue();
            try {

                URL ruleURL = new URL(url);
                ruleInputStream = ruleURL.openStream();
            } catch (MalformedURLException e) {
                throw new RuleConfigurationException("Unknown protocol is specified : ", e);
            } catch (IOException e) {
                throw new RuleConfigurationException("Can not access the URL : ", e);
            }

        }

        return ruleInputStream;


    }

    /**
     * This method is used to get the rule set as a stream. This method is used to get the rule set as a stream
     * from the registry, file system or from the class path.
     * @param rule - rule object which contains the rule source type and the value
     * @param classLoader - class loader
     * @return - rule set as a stream
     * @throws RuleConfigurationException - if there is a problem with the configuration
     */
    public static FileInputStream getRuleSetAsFileStream(Rule rule, ClassLoader classLoader)
            throws RuleConfigurationException {
        InputStream inputStream = getRuleSetAsStream(rule, classLoader);
        File tempFile = null;
        try {
            tempFile = File.createTempFile("tempfile_" + rule.hashCode(), ".tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[4048];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            return new FileInputStream(tempFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getRegistryAsStream(String type, String key) throws RegistryException {
        RuleServiceValueHolder ruleServiceValueHolder = RuleServiceValueHolder.getInstance();
        InputStream inputStream = null;
        int tenantID = CarbonContext.getThreadLocalCarbonContext().getTenantId();

        if (type.equals(Constants.RULE_SOURCE_REGISTRY_TYPE_CONFIGURATION)) {
            inputStream =
                    ruleServiceValueHolder.getRegistryService().getConfigSystemRegistry(tenantID).get(key).getContentStream();
        } else if (type.equals(Constants.RULE_SOURCE_REGISTRY_TYPE_GOVERNANCE)) {
            inputStream =
                    ruleServiceValueHolder.getRegistryService().getGovernanceSystemRegistry(tenantID).get(key).getContentStream();

        }

        return inputStream;
    }


}
