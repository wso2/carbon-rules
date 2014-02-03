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
package org.wso2.carbon.rule.ws.admin;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.axis2.description.Parameter;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.core.AbstractAdmin;
import org.wso2.carbon.rule.common.RuleService;
import org.wso2.carbon.rule.common.config.RuleServiceHelper;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.ws.admin.exception.RuleServiceAdminException;

import javax.activation.DataHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Admin service for rule service management
 */
@SuppressWarnings("unused")
public class RuleServiceAdmin extends AbstractAdmin {

    private final static RuleServiceAdminHandler RULE_FILE_HANDLER =
            new RuleServiceFileAdminHandler();
    private final static RuleServiceAdminHandler RULE_ARCHIVE_HANDLER =
            new RuleServiceArchiveAdminHandler();
    private static final Log log = LogFactory.getLog(RuleServiceAdmin.class);
    private static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();
    private static final OMNamespace NULL_NS = OM_FACTORY.createOMNamespace("", "");

    /**
     * Adds a rule service based on the given name and other information in the serviceXML
     *
     * @param fileExtension  rule service file extension
     * @param name           name of the service to be created
     * @param ruleServiceXML meta data required to create a rule service
     * @throws RuleServiceAdminException for any errors during service add operation
     */
    public void addRuleService(String fileExtension,
                               String name,
                               OMElement ruleServiceXML) throws RuleServiceAdminException {
        validateName(name);
        validateElement(ruleServiceXML);


        try {
            RuleService ruleService = RuleServiceHelper.getRuleService(ruleServiceXML);
            validateRuleServiceDescription(ruleService, ruleServiceXML);
            AxisConfiguration axisConfig = getAxisConfig();
            AxisService axisService = axisConfig.getService(name);
            if (axisService != null) {
                throw new RuleServiceAdminException("There is already a service " +
                        "with the given name : ");
            }
            RuleServiceAdminHandler adminHandler = getRuleServiceAdminHandler(fileExtension);
            adminHandler.saveRuleService(getAxisConfig(), axisService, ruleService);
        } catch (AxisFault axisFault) {
            throw new RuleServiceAdminException("Error while accessing " +
                    "the service with the name : " + name, axisFault);
        } catch (RuleConfigurationException e) {
            throw new RuleServiceAdminException("Can not parse the received xml");
        }
    }

    /**
     * Updates an existing service
     *
     * @param fileExtension  rule service file extension
     * @param name           name of the service to be updated
     * @param ruleServiceXML eta data required to update the rule service
     * @throws RuleServiceAdminException for any errors during service edit operation
     */
    public void editRuleService(String fileExtension,
                                String name,
                                OMElement ruleServiceXML) throws RuleServiceAdminException {
        validateName(name);
        validateElement(ruleServiceXML);

        try {
            RuleService ruleService = RuleServiceHelper.getRuleService(ruleServiceXML);
            validateRuleServiceDescription(ruleService, ruleServiceXML);
            AxisConfiguration axisConfig = getAxisConfig();
            AxisService axisService = axisConfig.getService(name);
            if (axisService == null) {
                // if there is no one add a new rule service
                addRuleService(fileExtension, name, ruleServiceXML);
                return;
            }

            // set the KEEP_SERVICE_HISTORY_PARAM to true in AxisService
            axisService.addParameter(CarbonConstants.KEEP_SERVICE_HISTORY_PARAM, "true");
            // set the KEEP_SERVICE_HISTORY_PARAM and PRESERVE_SERVICE_HISTORY_PARAM to
            //  true in AxisServiceGroup
            AxisServiceGroup axisServiceGroup = axisService.getAxisServiceGroup();
            axisServiceGroup.addParameter(CarbonConstants.KEEP_SERVICE_HISTORY_PARAM, "true");
            axisServiceGroup.addParameter(CarbonConstants.PRESERVE_SERVICE_HISTORY_PARAM, "true");

            RuleServiceAdminHandler adminHandler = getRuleServiceAdminHandler(fileExtension);
            adminHandler.saveRuleService(getAxisConfig(), axisService, ruleService);
        } catch (AxisFault axisFault) {
            throw new RuleServiceAdminException("Error while accessing " +
                    "the service with the name : " + name, axisFault);
        } catch (RuleConfigurationException e) {
            throw new RuleServiceAdminException("Error when parsing the rule xml");
        }

    }

    /**
     * Retrieves names of all existing rule services
     *
     * @return a array of existing rule services' names
     * @throws RuleServiceAdminException for any errors during service list operation
     */
    public String[] getAllRuleServices() throws RuleServiceAdminException {
        final List<String> serviceList = new ArrayList<String>();
        final Map<String, AxisService> map = getAxisConfig().getServices();
        Set<String> set = map.keySet();
        for (String serviceName : set) {
            try {
                AxisService axisService = getAxisConfig().getService(serviceName);
                Parameter parameter = axisService.getParameter("serviceType");
                if (parameter != null) {
                    if (Constants.RULE_SERVICE_TYPE.equals(parameter.getValue().toString())) {
                        serviceList.add(serviceName);
                    }
                }
            } catch (AxisFault axisFault) {
                throw new RuleServiceAdminException("Error while accessing " +
                        "the service with the name : " + serviceName, axisFault);
            }
        }
        return serviceList.toArray(new String[serviceList.size()]);
    }


    /**
     * Uploads facts
     *
     * @param serviceName name of the service facts belongs
     * @param fileName    fact file name
     * @param dataHandler DataHandler representing facts in binary
     * @return A string array contains names of the uploaded facts
     * @throws RuleServiceAdminException for any errors during fact uploading
     */
    public String[] uploadFacts(String serviceName,
                                String fileName,
                                DataHandler dataHandler) throws RuleServiceAdminException {
        RuleServiceAdminHandler adminHandler = getRuleServiceAdminHandler(
                Constants.RULE_SERVICE_ARCHIVE_EXTENSION);
        return adminHandler.uploadFacts(getAxisConfig(), serviceName, fileName, dataHandler);
    }

    /**
     * Upload a ruleset as a file
     *
     * @param serviceName name of the service rule script belongs
     * @param fileName    rule file name
     * @param dataHandler DataHandler representing rule script in binary
     * @throws RuleServiceAdminException for any errors during rule script uploading
     */
    public void uploadRuleFile(String serviceName,
                               String fileName,
                               DataHandler dataHandler) throws RuleServiceAdminException {
        RuleServiceAdminHandler adminHandler = getRuleServiceAdminHandler(
                Constants.RULE_SERVICE_ARCHIVE_EXTENSION);
        adminHandler.uploadRuleFile(getAxisConfig(), serviceName, fileName, dataHandler);
    }

    /**
     * Gets all facts belong to the service with the given name
     *
     * @param fileExtension rule service file extension
     * @param serviceName   name of the service
     * @return A string array of the facts' names
     * @throws RuleServiceAdminException for any errors during facts retrieving
     */
    public String[] getAllFacts(String fileExtension,
                                String serviceName) throws RuleServiceAdminException {
        RuleServiceAdminHandler adminHandler = getRuleServiceAdminHandler(fileExtension);
        return adminHandler.getAllFacts(getAxisConfig(), serviceName);
    }

    /**
     * retrieves the rule service for the given service name
     *
     * @param serviceName name of the rule service
     * @return <code>OMElement </code> representing the information of the rule service
     * @throws org.apache.axis2.AxisFault for any errors during accessing service
     */
    public OMElement getRuleService(String serviceName) throws RuleServiceAdminException {
        validateName(serviceName);
        AxisConfiguration axisConfig = getAxisConfig();
        try {
            AxisService axisService = axisConfig.getService(serviceName);
            if (axisService == null) {
                // this may be a faulty server handle it here.
                ConfigurationContext configurationContext =
                        MessageContext.getCurrentMessageContext().getConfigurationContext();
                Map<String, AxisService> faultyServicesMap =
                        (Map<String, AxisService>) configurationContext.
                                getPropertyNonReplicable(CarbonConstants.FAULTY_SERVICES_MAP);
                if (faultyServicesMap != null) {
                    for (String key : faultyServicesMap.keySet()) {
                        axisService = faultyServicesMap.get(key);
                        if (axisService.getName().equals(serviceName)) {
                            axisService.addParameter(Constants.RULE_SERVICE_PATH, key);
                            break;
                        }
                    }
                }
                if (axisService == null) {
                    throw new RuleServiceAdminException("There is no a service " +
                            "with the given name : " + serviceName);
                }
            }
            String path = (String) axisService.getParameterValue(Constants.RULE_SERVICE_PATH);
            String fileExtension = path.substring(path.lastIndexOf(".") + 1);
            RuleServiceAdminHandler adminHandler = getRuleServiceAdminHandler(fileExtension);
            OMElement omElement =
                    adminHandler.getRuleService(getAxisConfig(), serviceName);
            omElement.addAttribute(
                    OM_FACTORY.createOMAttribute(
                            Constants.ATT_EXTENSION, NULL_NS, fileExtension));
            return omElement;
        } catch (AxisFault axisFault) {
            throw new RuleServiceAdminException("Error while accessing " +
                    "the service with the name : " + serviceName, axisFault);
        }
    }

    private static void validateElement(OMElement element) throws RuleServiceAdminException {
        if (element == null) {
            throw new RuleServiceAdminException("Rule Service" +
                    "Description OMElement can not be found.");
        }
    }


    private static void validateName(String name) throws RuleServiceAdminException {
        if (name == null || "".equals(name)) {
            throw new RuleServiceAdminException("Name is null or empty");
        }
    }

    private static void validateRuleServiceDescription(RuleService ruleService,
                                                       OMElement xml) throws RuleServiceAdminException {
        if (ruleService == null) {
            throw new RuleServiceAdminException("RuleService Description can not be " +
                    "created from : " + xml);
        }
    }

    private RuleServiceAdminHandler getRuleServiceAdminHandler(String fileExtension)
            throws RuleServiceAdminException {

        if (Constants.RULE_SERVICE_FILE_EXTENSION.equals(fileExtension)) {
            return RULE_FILE_HANDLER;
        } else if (Constants.RULE_SERVICE_ARCHIVE_EXTENSION.equals(fileExtension)) {
            return RULE_ARCHIVE_HANDLER;
        } else {
            throw new RuleServiceAdminException("Invalid file extension : " + fileExtension);
        }
    }

    public String[] getFactArchiveList(String serviceName) throws RuleServiceAdminException {
        AxisConfiguration axisConfig = getAxisConfig();
        RuleServiceAdminHandler adminHandler = getRuleServiceAdminHandler(
                Constants.RULE_SERVICE_ARCHIVE_EXTENSION);
        return adminHandler.getFactArchiveList(axisConfig, serviceName);
    }

    public void deleteFactArchive(String serviceName, String fileName) throws RuleServiceAdminException {
        AxisConfiguration axisConfig = getAxisConfig();
        RuleServiceAdminHandler adminHandler = getRuleServiceAdminHandler(
                Constants.RULE_SERVICE_ARCHIVE_EXTENSION);
        adminHandler.deleteFactArchive(axisConfig, serviceName, fileName);
    }

    public String[] getRuleFileList(String serviceName, String fileName) throws RuleServiceAdminException {
        AxisConfiguration axisConfig = getAxisConfig();
        RuleServiceAdminHandler adminHandler = getRuleServiceAdminHandler(Constants.RULE_SERVICE_ARCHIVE_EXTENSION);
        return adminHandler.getRuleFileList(axisConfig, serviceName, fileName);
    }

    public void deleteRuleFile(String serviceName, String fileName) throws RuleServiceAdminException {
        AxisConfiguration axisConfig = getAxisConfig();
        RuleServiceAdminHandler adminHandler = getRuleServiceAdminHandler(Constants.RULE_SERVICE_ARCHIVE_EXTENSION);
        adminHandler.deleteRuleFile(axisConfig, serviceName, fileName);
    }
}
