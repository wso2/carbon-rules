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

import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.ui.CarbonUIMessage;
import org.wso2.carbon.ui.transports.fileupload.AbstractFileUploadExecutor;
import org.wso2.carbon.utils.FileItemData;
import org.wso2.carbon.utils.ServerConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * The FileUploadExecutor for uploading facts and rule script
 */
public class FactsUploadExecutor extends AbstractFileUploadExecutor {

    private static final String[] ALLOWED_FILE_EXTENSIONS = new String[]{".jar"};

    public boolean execute(HttpServletRequest request, HttpServletResponse response)
            throws CarbonException, IOException {

        String webContext = (String) request.getAttribute(CarbonConstants.WEB_CONTEXT);
        String serverURL = (String) request.getAttribute(CarbonConstants.SERVER_URL);
        String cookie = (String) request.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
        Map<String, ArrayList<FileItemData>> fileItemsMap = getFileItemsMap();
        Map<String, ArrayList<String>> formFieldsMap = getFormFieldsMap();

        if (fileItemsMap == null || fileItemsMap.isEmpty()) {
            String msg = "File uploading failed. No files are specified";
            log.error(msg);
            CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.ERROR, request,
                    response, "../" + webContext + "/ruleservices/rule_service_wizard_step3.jsp");
            return false;
        }
        String ruleServiceName = formFieldsMap.get("ruleServiceName").get(0); //TODO validation
        RuleServiceAdminClient adminClient =
                new RuleServiceAdminClient(configurationContext, serverURL, cookie);
        String msg = "";
        String fileToRedirect = "";
        try {
            for (Object o : fileItemsMap.keySet()) {
                String fieldName = (String) o;
                if ("factFilename".equals(fieldName)) {
                    FileItemData fileItemData = fileItemsMap.get(fieldName).get(0);
                    String fileName = getFileName(fileItemData.getFileItem().getName());
                    checkServiceFileExtensionValidity(fileName, ALLOWED_FILE_EXTENSIONS);
                    if (fileName.endsWith(".jar")) {
                        String[] facts =
                                adminClient.uploadFacts(ruleServiceName,
                                        fileName, fileItemData.getDataHandler(), request);
                        ArrayList<String> factArchiveList =
                                (ArrayList<String>) request.getSession().getAttribute(RuleServiceAdminClient.FACTS);
                        if (factArchiveList == null) {
                            factArchiveList = new ArrayList<String>();
                        }
                        factArchiveList.add(fileName);
                        request.getSession().setAttribute(RuleServiceAdminClient.FACTS, factArchiveList);


                        String[] allFacts = ((String[]) request.getSession().getAttribute("allFacts"));
                        if (allFacts != null) {
                            if (allFacts.length > 0) {
                                HashSet<String> factSet = new HashSet<String>();
                                for (String fact : allFacts) {
                                    factSet.add(fact);
                                }
                                for (String fact : facts) {
                                    factSet.add(fact);
                                }
                                String[] mergedFacts = factSet.toArray(new String[factSet.size()]);
                                request.getSession().setAttribute("allFacts", mergedFacts);
                            }
                        } else {
                            HashSet<String> factSet = new HashSet<String>();
                            for (String fact : facts) {
                                factSet.add(fact);
                            }
                            String[] mergedFacts = factSet.toArray(new String[factSet.size()]);
                            request.getSession().setAttribute("allFacts", mergedFacts);
                        }
                    } else {
                        throw new CarbonException("File with extension " + fileName
                                + " is not supported!");
                    }

                    msg = "Facts file uploaded successfully.";
                    fileToRedirect = "/ruleservices/rule_service_wizard_step3.jsp";
                } else if ("ruleFilename".equals(fieldName)) {
                    FileItemData fileItemData = fileItemsMap.get(fieldName).get(0);
                    String fileName = getFileName(fileItemData.getFileItem().getName());
                    adminClient.uploadRuleFile(ruleServiceName,
                            fileName, fileItemData.getDataHandler(), request);
                    String[] ruleFileNames =
                            adminClient.getRuleFileList(adminClient.getRuleServiceDescription(request), request.getSession());
                    List<String> fileNames = new ArrayList<String>();
                    fileNames = Arrays.asList(ruleFileNames);//(ArrayList<String>) request.getSession().getAttribute("ruleScript"); // String filePath = (String) request.getSession().getAttribute("ruleScript");
                    Map<String, String> scriptList =
                            (Map<String, String>) request.getSession().getAttribute(RuleServiceAdminClient.SCRIPTS);
                    if (scriptList == null) {

                        scriptList = new HashMap<String, String>();
                    }
                    for (String filename : fileNames) {
                        scriptList.put(filename, "file");

                    }
                    request.getSession().setAttribute(RuleServiceAdminClient.SCRIPTS, scriptList);
                    msg = "Rule Script file uploaded successfully.";
                    fileToRedirect = "/ruleservices/rule_service_wizard_step2.jsp";
                }
            }
            response.setContentType("text/html; charset=utf-8");
            CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.INFO, request,
                    response, "../" + webContext + fileToRedirect);
            return true;
        } catch (java.lang.Exception e) {
            msg = "File upload failed. " + e.getMessage();
            log.error(msg);
            CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.ERROR, request,
                    response, "../" + webContext + fileToRedirect);
        }
        return false;
    }
}
