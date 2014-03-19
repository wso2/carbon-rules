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

import org.apache.axis2.AxisFault;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.core.AbstractAdmin;
import org.wso2.carbon.utils.CarbonUtils;

import javax.activation.DataHandler;
import java.io.File;
import java.io.FileOutputStream;

/**
 * File Uploader copied from the DataService file Uploader - need to reuse it if possible
 */
public class RuleServiceFileUploadAdmin extends AbstractAdmin {

    private static final Log log = LogFactory.getLog(RuleServiceFileUploadAdmin.class);

    public String uploadService(String fileName, DataHandler dataHandler)
            throws Exception {
        try {
            AxisConfiguration axisConfig = getAxisConfig();
            String repo = axisConfig
                    .getRepository().getPath();

            if (CarbonUtils.isURL(repo)) {
                throw new AxisFault(
                        "Uploading services to URL repo is not supported ");
            }
            // Composing the proper location to copy the artifact
            String servicesDir = repo + File.separator + "ruleservices";
            writeToFileSystem(servicesDir, fileName, dataHandler);

        } catch (Exception e) {
            String msg = "Error occurred while uploading the service "
                    + fileName;
            log.error(msg, e);
            throw new Exception("Failed to upload the service archive "
                    + fileName, e);
        }
        return "successful";
    }

    private void writeToFileSystem(String path, String fileName, DataHandler dataHandler)
            throws Exception {
        final File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        final File destFile = new File(path, fileName);
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        final FileOutputStream fos = new FileOutputStream(destFile);
        dataHandler.writeTo(fos);
        fos.flush();
        fos.close();
    }
}
