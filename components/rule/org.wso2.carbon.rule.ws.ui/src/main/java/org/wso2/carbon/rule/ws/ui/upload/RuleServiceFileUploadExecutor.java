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
package org.wso2.carbon.rule.ws.ui.upload;

import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.CarbonException;
import org.wso2.carbon.ui.CarbonUIMessage;
import org.wso2.carbon.ui.transports.fileupload.AbstractFileUploadExecutor;
import org.wso2.carbon.utils.FileItemData;
import org.wso2.carbon.utils.ServerConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * The FileUploadExecutor for uploading rule service archives (.aar)
 */
public class RuleServiceFileUploadExecutor extends AbstractFileUploadExecutor {

    private static final String[] ALLOWED_FILE_EXTENSIONS = new String[]{".aar", ".rsl"};

    public boolean execute(HttpServletRequest request, HttpServletResponse response)
            throws CarbonException, IOException {
        String webContext = (String) request.getAttribute(CarbonConstants.WEB_CONTEXT);
        String serverURL = (String) request.getAttribute(CarbonConstants.SERVER_URL);
        String cookie = (String) request.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
        Map<String, ArrayList<FileItemData>> fileItemsMap = getFileItemsMap();
        if (fileItemsMap == null || fileItemsMap.isEmpty()) {
            String msg = "File uploading failed. No files are specified";
            log.error(msg);
            CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.ERROR, request,
                    response, "../" + webContext + "/ruleservices/ruleserviceupload.jsp");
            return false;
        }

        RuleServiceFileUploadClient serviceUploaderClient =
                new RuleServiceFileUploadClient(configurationContext, serverURL, cookie);
        String msg;
        try {
            for (Object o : fileItemsMap.keySet()) {
                String fieldName = (String) o;
                FileItemData fileItemData = fileItemsMap.get(fieldName).get(0);
                String fileName = getFileName(fileItemData.getFileItem().getName());
                checkServiceFileExtensionValidity(fileName, ALLOWED_FILE_EXTENSIONS);

                if (fileName.endsWith(".aar") || fileName.endsWith(".rsl")) {
                    serviceUploaderClient.uploadService(fileName, fileItemData.getDataHandler());
                } else {
                    throw new CarbonException("File with extension " + fileName
                            + " is not supported!. Supported extensions are : .aar and .rsl");
                }
            }
            response.setContentType("text/html; charset=utf-8");
            msg = "Rule Service archive file uploaded successfully.";
            CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.INFO, request,
                    response, "../" + webContext + "/service-mgt/index.jsp");
            return true;
        } catch (java.lang.Exception e) {
            msg = "File upload failed. " + e.getMessage();
            log.error(msg);
            CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.ERROR, request,
                    response, "../" + webContext + "/ruleservices/ruleserviceupload.jsp");
        }
        return false;
    }
}

