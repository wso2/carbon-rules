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
package org.wso2.carbon.rule.ws.ui.wizard;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.rule.common.RuleService;
import org.wso2.carbon.rule.common.config.RuleServiceHelper;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.ws.stub.wizard.RuleServiceAdminRuleServiceAdminException;
import org.wso2.carbon.rule.ws.stub.wizard.RuleServiceAdminStub;
import org.wso2.carbon.rule.ws.ui.RuleServiceClientException;
import org.wso2.carbon.ui.CarbonUIUtil;
import org.wso2.carbon.utils.ServerConstants;

import javax.activation.DataHandler;
import javax.servlet.http.HttpServletRequest;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * WS Client calling the rule service admin
 */
public class RuleServiceAdminClient {

    private static final Log log = LogFactory.getLog(RuleServiceAdminClient.class);

    private RuleServiceAdminStub ruleServiceAdminStub;
    public static final String RULE_SERVIE = "ruleservice";
    public static final String FACTS = "facts";
    public static final String SCRIPTS = "scripts";
    private static final OMFactory OM_FACTORY = OMAbstractFactory.getOMFactory();
    private static final OMNamespace NULL_NS = OM_FACTORY.createOMNamespace("", "");

    public RuleServiceAdminClient(ConfigurationContext ctx, String serverURL, String cookie)
            throws AxisFault {
        init(ctx, serverURL, cookie);
    }

    public RuleServiceAdminClient(javax.servlet.ServletContext servletContext,
                                  javax.servlet.http.HttpSession httpSession) throws Exception {
        ConfigurationContext ctx =
                (ConfigurationContext) servletContext.getAttribute(
                        CarbonConstants.CONFIGURATION_CONTEXT);
        String cookie = (String) httpSession.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
        String serverURL = CarbonUIUtil.getServerURL(servletContext, httpSession);
        init(ctx, serverURL, cookie);
    }

    private void init(ConfigurationContext ctx,
                      String serverURL,
                      String cookie) throws AxisFault {
        String serviceUploaderServiceEPR = serverURL + "RuleServiceAdmin";
        ruleServiceAdminStub = new RuleServiceAdminStub(ctx, serviceUploaderServiceEPR);
        ServiceClient client = ruleServiceAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        options.setTimeOutInMilliSeconds(10000);
    }

    /**
     * Retrieves the <code>RuleServiceDescription</code>
     *
     * @param request <code>HttpServletRequest</code>
     * @return <code>RuleServiceDescription</code>  instance containing information about
     *         rule service being edited or created.
     */
    public RuleService getRuleServiceDescription(
            HttpServletRequest request) {

        String serviceName = request.getParameter("serviceName");
        RuleService ruleService =
                (RuleService) request.getSession().getAttribute(RULE_SERVIE);
        if (ruleService != null) {
            if (serviceName != null && !"".equals(serviceName)) {
                String name = ruleService.getName();
                if (serviceName.equals(name)) {
                    return ruleService;
                }
            } else {
                return ruleService;
            }
        }
        if (serviceName != null && !"".equals(serviceName)) {
            ruleService = getRuleService(serviceName.trim());
        } else {
            ruleService = new RuleService();
            ruleService.setExtension(org.wso2.carbon.rule.common.util.Constants.RULE_SERVICE_FILE_EXTENSION);
            ruleService.setTargetNamespace(org.wso2.carbon.rule.common.util.Constants.RULE_CONF_NAMESPACE);
        }
        request.getSession().setAttribute(RULE_SERVIE, ruleService);
        return ruleService;
    }

    /**
     * Uploads facts
     *
     * @param serviceName name of the service facts belongs
     * @param fileName    fact file name
     * @param dataHandler DataHandler representing facts in binary
     * @param request     <code>HttpServletRequest</code>
     * @return A string array contains names of the uploaded facts
     */
    public String[] uploadFacts(String serviceName,
                                String fileName,
                                DataHandler dataHandler, HttpServletRequest request) {
        try {
            String[] strings =
                    ruleServiceAdminStub.uploadFacts(serviceName, fileName, dataHandler);
            RuleService ruleService = getRuleServiceDescription(request);
            if (ruleService != null) {
                ruleService.setExtension(org.wso2.carbon.rule.common.util.Constants.RULE_SERVICE_ARCHIVE_EXTENSION);
            }
            return strings;
        } catch (Exception e) {
            throw new RuleServiceClientException("Error uploading facts : " + fileName);
        }
    }

    /**
     * Upload a ruleset as a file
     *
     * @param serviceName name of the service rule script belongs
     * @param fileName    rule file name
     * @param dataHandler DataHandler representing rule script in binary
     * @param request     <code>HttpServletRequest</code>
     */
    public void uploadRuleFile(String serviceName,
                               String fileName,
                               DataHandler dataHandler,
                               HttpServletRequest request) {

        try {
            ruleServiceAdminStub.uploadRuleFile(serviceName,
                    fileName, dataHandler);
            RuleService ruleService = getRuleServiceDescription(request);
            if (ruleService != null) {
                ruleService.setExtension(org.wso2.carbon.rule.common.util.Constants.RULE_SERVICE_ARCHIVE_EXTENSION);
//                RuleServiceExtensionDescription extensionDescription =
//                        (RuleServiceExtensionDescription) ruleService.getServiceExtensionDescription();
//                RuleSetDescription ruleSetDescription =
//                        extensionDescription.getRuleSetDescription();
//                if (ruleSetDescription != null) {
//                    ruleSetDescription.setPath(fileName);
//                    ruleSetDescription.setKey("");
//                    ruleSetDescription.setRuleSource(null); //remove rule source
//                }
            }
        } catch (Exception e) {
            throw new RuleServiceClientException("Error uploading rule script : " + fileName);
        }

    }

       public String[] getRuleFileList(RuleService ruleService,
                                javax.servlet.http.HttpSession session){
           String serviceName = ruleService.getName();
           String fname = "temp";
           try {
               String[] ruleFileList = ruleServiceAdminStub.getRuleFileList(serviceName,fname);
               return ruleFileList;
           } catch (Exception e) {
           throw new RuleServiceClientException("Error getting all rule files for rule service : " +
                        serviceName);

           }
       }

    public void deleteRuleFile(RuleService ruleService, String fileName, javax.servlet.http.HttpSession session){
        String serviceName = ruleService.getName();
           try {
               ruleServiceAdminStub.deleteRuleFile(serviceName,fileName);
           } catch (Exception e) {
           throw new RuleServiceClientException("Error getting all rule files for rule service : " +
                        serviceName);

           }
    }

    public void deleteAllRuleFiles( RuleService ruleService,  javax.servlet.http.HttpSession session){
         String serviceName = ruleService.getName();
        try {
               String[] ruleFileList = getRuleFileList(ruleService,session);
               if(ruleFileList.length >0){
                   for(String filename : ruleFileList){
                       deleteRuleFile(ruleService, filename, session);
                   }

               }
           } catch (Exception e) {
           throw new RuleServiceClientException("Error getting all rule files for rule service : " +
                        serviceName);

           }


    }

    /**
     * Gets all facts belong to the service with the given name
     *
     * @param ruleService information about the service
     * @param session                HttpSession
     * @return A string array of the facts' names
     */
    public String[] getAllFacts(RuleService ruleService,
                                javax.servlet.http.HttpSession session) {
        String serviceName = ruleService.getName();
//        String[] classes = (String[]) session.getAttribute("allFacts");
//        if (classes != null && classes.length > 1) { // message class already there: need the length to be > 1
//            return classes;
//        } else {
//            try {
//                String[] facts = ruleServiceAdminStub.getAllFacts(
//                        ruleService.getExtension(), serviceName);
//                session.setAttribute(serviceName.trim(), facts);
//                return facts;
//            } catch (Exception e) {
//                throw new RuleServiceClientException("Error getting all facts for rule service : " +
//                        serviceName);
//            }
//        }
        try {
                String[] facts = ruleServiceAdminStub.getAllFacts(
                        ruleService.getExtension(), serviceName);
                session.setAttribute(serviceName.trim(), facts);
                return facts;
            } catch (Exception e) {
                throw new RuleServiceClientException("Error getting all facts for rule service : " +
                        serviceName);
            }
    }

/**
     * Gets all facts belong to the service with the given name
     *
     * @param ruleService information about the service
     * @param session                HttpSession
     * @return A string array of the factArchives names
     */
    public ArrayList<String> getFactArchiveList(RuleService ruleService,
                                javax.servlet.http.HttpSession session) {
        String serviceName = ruleService.getName();
//        String[] archiveList = (String[]) session.getAttribute("allArchives");
//        if (archiveList != null && archiveList.length > 1) { // message class already there: need the length to be > 1
//            return archiveList;
//        } else {
            try {
                String[] archiveList = ruleServiceAdminStub.getFactArchiveList(serviceName);
                ArrayList<String> archiveArrayList= new ArrayList<String>();
                if(archiveList != null)  {
                for(String fileName : archiveList){
                    archiveArrayList.add(fileName);
                }
                }
                session.setAttribute("allArchives", archiveList);
                return archiveArrayList;
            } catch (Exception e) {
                throw new RuleServiceClientException("Error getting all facts archives for rule service : " +
                        serviceName);
            }
      //  }
    }

  /**
     * Delete fact archives that are not needed
     *
     * @param @param session                HttpSession
     * @param ruleService       The name of the service that fact archives are belonged
     * @param fileName          archive file name
     */
    public void deleteFactArchive(RuleService ruleService, String fileName,
                                javax.servlet.http.HttpSession session) {
        String serviceName = ruleService.getName();
        try {
            ruleServiceAdminStub.deleteFactArchive(serviceName,fileName);
        } catch (Exception e) {
           throw new RuleServiceClientException("Error deleting fact archive : " + fileName +"  rule service : " +
                        serviceName);
        }

    }


    /**
     * Save the rule service based on the information in the given
     * <code>RuleServiceDescription </code>
     *
     * @param ruleService <code>RuleServiceDescription </code>
     */
    public void saveService(RuleService ruleService) {
        String serviceName = ruleService.getName();
        OMElement result = ruleService.toOM();

        if (!ruleService.isEditable()) {
            try {
                ruleServiceAdminStub.editRuleService(ruleService.getExtension(),
                        serviceName, result);
            } catch (Exception e) {
                throw new RuleServiceClientException("Error editing rule service : " + serviceName);
            }
        } else {
            try {
                ruleServiceAdminStub.addRuleService(ruleService.getExtension(),
                        serviceName, result);
            } catch (Exception e) {
                throw new RuleServiceClientException("Error adding a new rule service : " + serviceName);
            }
        }
    }

    /**
     * retrieves the rule service for the given service name
     *
     * @param name name of the rule service
     * @return <code>RuleServiceDescription </code> representing the information of the rule service
     */
    public RuleService getRuleService(String name) {
        try {
            OMElement omElement = ruleServiceAdminStub.getRuleService(name).getExtraElement();
            RuleService ruleService = RuleServiceHelper.getRuleService(omElement);
            ruleService.setEditable(false);
            return ruleService;
        } catch (RemoteException e) {
            throw new RuleServiceClientException("Error retrieving rule service from name : " + name);
        } catch (RuleServiceAdminRuleServiceAdminException e) {
            throw new RuleServiceClientException("Error accessing stub rule service from name : " + name);
        } catch (RuleConfigurationException e) {
            throw new RuleServiceClientException("Error creating configuration rule service from name : " + name);
        }
    }
}
