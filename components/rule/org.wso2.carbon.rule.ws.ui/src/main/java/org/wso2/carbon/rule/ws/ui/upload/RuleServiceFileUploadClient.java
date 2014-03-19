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

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.rule.ws.ui.RuleServiceClientException;
import org.wso2.carbon.rule.ws.stub.fileupload.RuleServiceFileUploadAdminStub;
import org.wso2.carbon.ui.CarbonUIUtil;
import org.wso2.carbon.utils.ServerConstants;

import javax.activation.DataHandler;

/**
 * File uploader for uploading rule service archives (.aar)
 */
public class RuleServiceFileUploadClient {

    private static final Log log = LogFactory.getLog(RuleServiceFileUploadClient.class);

    private RuleServiceFileUploadAdminStub serviceUploaderStub;

    public RuleServiceFileUploadClient(ConfigurationContext ctx, String serverURL, String cookie)
            throws AxisFault {
        init(ctx, serverURL, cookie);
    }

    public RuleServiceFileUploadClient(javax.servlet.ServletContext servletContext,
                                       javax.servlet.http.HttpSession httpSession) throws Exception {
        ConfigurationContext ctx =
                (ConfigurationContext) servletContext.getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
        String cookie = (String) httpSession.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
        String serverURL = CarbonUIUtil.getServerURL(servletContext, httpSession);
        init(ctx, serverURL, cookie);
    }

    private void init(ConfigurationContext ctx, String serverURL, String cookie) throws AxisFault {
        String serviceUploaderServiceEPR = serverURL + "RuleServiceFileUploadAdmin";
        serviceUploaderStub = new RuleServiceFileUploadAdminStub(ctx, serviceUploaderServiceEPR);
        ServiceClient client = serviceUploaderStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
        options.setProperty(Constants.Configuration.ENABLE_MTOM, Constants.VALUE_TRUE);
        options.setTimeOutInMilliSeconds(10000);
    }

    public void uploadService(String fileName, DataHandler dataHandler) {
        try {
            serviceUploaderStub.uploadService(fileName, dataHandler);
        } catch (Exception e) {
            throw new RuleServiceClientException("Error uploading rule archive : " + fileName, e);
        }
    }
}
