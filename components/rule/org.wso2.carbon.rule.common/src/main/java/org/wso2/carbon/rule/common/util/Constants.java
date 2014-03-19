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

package org.wso2.carbon.rule.common.util;

import javax.xml.namespace.QName;
import java.lang.String;

public interface Constants {

    public static final String RULE_CONF_NAMESPACE = "http://wso2.org/carbon/rules";
    public static final String RULE_CONF_NAMESPACE_PREFIX = "brs";

    public static final int RULE_STATELESS_SESSION = 1;
    public static final int RULE_STATEFUL_SESSION = 2;

    public static final String RULE_SOURCE_TYPE_INLINE = "inline";
    public static final String RULE_SOURCE_TYPE_REGISTRY = "registry";
    public static final String RULE_SOURCE_TYPE_FILE = "file";
    public static final String RULE_SOURCE_TYPE_URL = "url";
    public static final String RULE_SOURCE_REGISTRY_TYPE_CONFIGURATION ="conf";
    public static final String RULE_SOURCE_REGISTRY_TYPE_GOVERNANCE ="gov";


    public static final String RULE_RESOURCE_TYPE_REGULAR = "regular";
    public static final String RULE_RESOURCE_TYPE_DTABLE = "dtable";

    public static final String RULE_CONF_ELE_RULE_SERVICE = "ruleService";
    public static final String RULE_CONF_ELE_RULE_SET = "ruleSet";
    public static final String RULE_CONF_ELE_RULE = "rule";
    public static final String RULE_CONF_ELE_PROPERTIES = "properties";
    public static final String RULE_CONF_ELE_PROPERTY = "property";
    public static final String RULE_CONF_ELE_OPERATION = "operation";
    public static final String RULE_CONF_ELE_INPUT = "input";
    public static final String RULE_CONF_ELE_OUTPUT = "output";
    public static final String RULE_CONF_ELE_FACT = "fact";
    public static final String RULE_CONF_ELE_SOURCE = "source";
    public static final String RULE_CONF_ELE_TARGET = "target";

    public static final String RULE_CONF_ATTR_NAME = "name";
    public static final String RULE_CONF_ATTR_TARGET_NAMESPACE = "targetNamespace";
    public static final String RULE_CONF_ATTR_BACKEND_RUNTIME_FACTORY = "backendRuntimeFactory";
    public static final String RULE_CONF_ATTR_DESCRIPTION = "description";
    public static final String RULE_CONF_ATTR_SCOPE = "scope";
    public static final String RULE_CONF_ATTR_TYPE= "type";
    public static final String RULE_CONF_ATTR_RESOURCE_TYPE = "resourceType";
    public static final String RULE_CONF_ATTR_SOURCE_TYPE = "sourceType";
    public static final String RULE_CONF_ATTR_VALE = "value";
    public static final String RULE_CONF_ATTR_ELEMENT_NAME = "elementName";
    public static final String RULE_CONF_ATTR_NAMESPACE = "namespace";
    public static final String RULE_CONF_ATTR_WRAPPER_ELEMENT_NAME = "wrapperElementName";
    public static final String RULE_CONF_ATTR_XPATH = "xpath";
    public static final String RULE_CONF_ATTR_RESULT_XPATH = "resultXpath";
    public static final String RULE_CONF_ATTR_ACTION = "action";

    public static final String RULE_CONF_ATTR_ACTION_REPLACE = "replace";
    public static final String RULE_CONF_ATTR_ACTION_CHILD = "child";
    public static final String RULE_CONF_ATTR_ACTION_SIBLING = "sibling";

    public static final String RULE_SERVICE_ARCHIVE_EXTENSION = "aar";
    public static final String RULE_SERVICE_FILE_EXTENSION = "rsl";
    public static final String RULE_SERVICE_FILE_NAME = "service.rsl";
    public static final String RULE_SERVICE_TYPE = "rule_service";
    public static final String RULE_SERVICE_PATH = "rule_service_path";
    public static final String ATT_EXTENSION = "extension";
    public static final String RULE_SERVICE_TEMP_DIR = "ruleservices_temp";
    public static final String RULE_SERVICE_REPOSITORY_NAME = "ruleservices";
    public static final String NULL_NAMESPACE = "";

    public static final QName ATT_GENERATE_SERVICES_XML = new QName(NULL_NAMESPACE,
            "generateServicesXML");

    public static final String RULE_SESSION_OBJECT = "ruleSession";

    public static final String RULE_SOURCE_SOAP_BODY = "soapBody";
    public static final String RULE_TARGET_SOAP_BODY = "soapBody";
    public static final String RULE_SOURCE_SOAP_HEADER = "soapHeader";

    public static final String RULE_PROPERTY_PREFIX = "$";

    public static final String RULE_ENGINE_CONF_NAMESPACE = "http://wso2.org/carbon/rules/conf";
    public static final String RULE_ENGINE_CONF_FILE = "rule-engine-config.xml";


    public static final String RULE_ENGINE_CONF_ELE_RULE_ENGINE_CONFIG = "ruleEngineConfig";
    public static final String RULE_ENGINE_CONF_ELE_RULE_ENGINE_PROVIDER = "ruleEngineProvider";
    public static final String RULE_ENGINE_CONF_ELE_PROPERTY = "property";

    public static final String RULE_ENGINE_CONF_ATTR_NAME = "name";
    public static final String RULE_ENGINE_CONF_ATTR_CLASS_NAME = "className";
    public static final String RULE_ENGINE_CONF_ATTR_VALUE = "value";

    public final static String RULE_SOURCE = "source";
    public final static String RULE_SOURCE_DRL = "drl";
    public final static String RULE_SOURCE_FILE_DIRECTORY = "conf";
    public final static String CONFIGURATION_REGISTRY_PREFIX = "/_system/config/";
    public final static String GOVERNANCE_REGISTRY_PREFIX = "/_system/governance/";


}
