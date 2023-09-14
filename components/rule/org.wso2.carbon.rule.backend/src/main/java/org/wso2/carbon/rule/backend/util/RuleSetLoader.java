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

import org.wso2.carbon.rule.common.Rule;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.util.Constants;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;


public class RuleSetLoader {
    public static final char URL_SEPARATOR_CHAR = '/';
    public static final String URL_SEPARATOR = "/";

    public static final String CONFIG_REGISTRY_PREFIX = "conf:";
    public static final String GOVERNANCE_REGISTRY_PREFIX = "gov:";
    public static final String LOCAL_REGISTRY_PREFIX = "local:";

    public static final String CONFIG_DIRECTORY_NAME = "config";
    public static final String GOVERNANCE_DIRECTORY_NAME = "governance";
    public static final String LOCAL_DIRECTORY_NAME = "local";

    public static String getHome() {
        String carbonHome = System.getProperty("carbon.home");
        if (carbonHome == null || "".equals(carbonHome) || ".".equals(carbonHome)) {
            carbonHome = getSystemDependentPath(new File(".").getAbsolutePath());
        }
        return carbonHome;
    }

    public static String getSystemDependentPath(String path) {
        return path.replace(URL_SEPARATOR_CHAR, File.separatorChar);
    }

    private static String getUri(String defaultFSRegRoot, String subDirectory) {
        return Paths.get(defaultFSRegRoot + subDirectory).toUri().normalize().toString()
                + URL_SEPARATOR;
    }




    public static InputStream getRuleSetAsStream(Rule rule, ClassLoader classLoader) throws RuleConfigurationException {
        InputStream ruleInputStream = null;

        if (rule.getSourceType().equalsIgnoreCase(Constants.RULE_SOURCE_TYPE_INLINE)) {
            ruleInputStream = new ByteArrayInputStream(rule.getValue().getBytes());
        } else if (rule.getSourceType().equalsIgnoreCase(Constants.RULE_SOURCE_TYPE_REGISTRY)) {
            String key = rule.getValue();
            String defaultFSRegRoot = getHome().replace(File.separator, URL_SEPARATOR);
            if (!defaultFSRegRoot.endsWith(URL_SEPARATOR)) {
                defaultFSRegRoot = defaultFSRegRoot + URL_SEPARATOR;
            }
            //Default registry root : <CARBON_HOME>/registry/
            defaultFSRegRoot += "registry" + URL_SEPARATOR;

            //create default file system paths for registry
            //Default registry local registry location : <CARBON_HOME>/registry/local
            String localRegistry = getUri(defaultFSRegRoot, "local");

            //Default registry config registry location : <CARBON_HOME>/registry/config
            String configRegistry = getUri(defaultFSRegRoot, "config");

            //Default registry governance registry location : <CARBON_HOME>/registry/governance
            String govRegistry = getUri(defaultFSRegRoot, "governance");

            String regRoot = defaultFSRegRoot;

            String resolvedPath = null;
            String registryRoot = "";
            String resourcePath = "";

            if (key.startsWith(CONFIG_REGISTRY_PREFIX)) {
                registryRoot = configRegistry;
                resourcePath = key.substring(CONFIG_REGISTRY_PREFIX.length());

            } else if (key.startsWith(GOVERNANCE_REGISTRY_PREFIX)) {
                registryRoot = govRegistry;
                resourcePath = key.substring(GOVERNANCE_REGISTRY_PREFIX.length());

            } else if (key.startsWith(LOCAL_REGISTRY_PREFIX)) {
                registryRoot = localRegistry;
                resourcePath = key.substring(LOCAL_REGISTRY_PREFIX.length());

            } else {
                registryRoot = govRegistry;
                resourcePath = key;
            }

            if (resourcePath.startsWith(URL_SEPARATOR)) {
                resourcePath = resourcePath.substring(1);
            }
            resolvedPath = registryRoot + resourcePath;

            //Test whether registry key has any illegel access
            File resolvedPathFile = null;
            File registryRootFile = null;
            try {
                resolvedPathFile = new File(new URI(resolvedPath));
                registryRootFile = new File(new URI(registryRoot));
                if (!resolvedPathFile.getCanonicalPath().startsWith(registryRootFile.getCanonicalPath())) {
                    throw new RuleConfigurationException("The registry key  '" + key +
                            "' is illegal which points to a location outside the registry");
                }
            } catch (URISyntaxException | IOException e) {
                throw new RuleConfigurationException("Error while resolving the canonical path of the registry key : " + key, e);
            }

            try {
                ruleInputStream = getRegistryAsStream(resolvedPathFile);
            } catch (Exception e) {
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

    private static InputStream getRegistryAsStream(File file) throws Exception {
        // Read the file into a byte array
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        // Create and return a new ByteArrayInputStream
        return new ByteArrayInputStream(fileBytes);
    }


}
