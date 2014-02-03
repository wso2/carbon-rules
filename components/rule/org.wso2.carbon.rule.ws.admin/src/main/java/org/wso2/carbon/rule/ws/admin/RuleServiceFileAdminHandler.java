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

import org.apache.axiom.om.OMElement;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.engine.AxisConfiguration;
import org.wso2.carbon.rule.common.RuleService;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.ws.admin.exception.RuleServiceAdminException;
import org.wso2.carbon.utils.FileManipulator;

import javax.activation.DataHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler for executing rule service management operations for rule services
 * based on the rule service files (.rsl)
 */
public class RuleServiceFileAdminHandler extends AbstractRuleServiceAdminHandler {

    public void saveRuleService(AxisConfiguration axisConfiguration, AxisService axisService,
                                RuleService ruleService)
            throws RuleServiceAdminException {

        String serviceName = ruleService.getName();
        Paths paths = createTempRuleServiceFile(axisConfiguration, serviceName);

        File ruleFile = new File(paths.getWorkingDirPath());
        try {
            OutputStream os = new FileOutputStream(ruleFile);
            ruleService.toOM().serialize(os);
        } catch (Exception e) {
            throw new RuleServiceAdminException("Cannot write rule services XML", e);
        }

        try {
            FileManipulator.copyFile(new File(paths.getWorkingDirPath()),
                    new File(paths.getServicePath()));
        } catch (IOException e) {
            throw new RuleServiceAdminException("Error coping content of the  file : " +
                    paths.getWorkingDirPath() + ". to the target file : " +
                    paths.getServicePath());
        }
//        saveToRegistry(paths, serviceName);
        cleanUp(paths);
    }

    public OMElement getRuleService(AxisConfiguration axisConfiguration, String name) throws RuleServiceAdminException {
        Paths paths = createTempRuleServiceFile(axisConfiguration, name);
        File ruleFile = new File(paths.getWorkingDirPath());
        return createXML(name, ruleFile);
    }

    public String[] uploadFacts(AxisConfiguration axisConfiguration,
                                String serviceName,
                                String fileName,
                                DataHandler dataHandler) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    public void uploadRuleFile(AxisConfiguration axisConfiguration,
                               String serviceName,
                               String fileName,
                               DataHandler dataHandler) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    public String[] getAllFacts(AxisConfiguration axisConfiguration,
                                String serviceName) {
        final List<String> facts = new ArrayList<String>();
        return facts.toArray(new String[facts.size()]);
    }

    public String[] getFactArchiveList(AxisConfiguration axisConfiguration, String serviceName)  {
        final List<String> factArchives = new ArrayList<String>();
        return factArchives.toArray(new String[factArchives.size()]);
    }

    public void deleteFactArchive(AxisConfiguration axisConfiguration, String serviceName, String fileName) throws RuleServiceAdminException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String[] getRuleFileList(AxisConfiguration axisConfiguration, String serviceName, String fileName) throws RuleServiceAdminException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteRuleFile(AxisConfiguration axisConfiguration, String serviceName, String fileName) throws RuleServiceAdminException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Paths createTempRuleServiceFile(AxisConfiguration axisConfig,
                                            String serviceName) throws RuleServiceAdminException {

        String servicesDir = createServiceRepository(axisConfig);
        String servicePath = getServicePath(axisConfig, serviceName);
        String suffix = File.separator + serviceName + "." +
                Constants.RULE_SERVICE_FILE_EXTENSION;
        if (servicePath == null) {
            servicePath = servicesDir + suffix;
        }

        String targetDirectory = getTempDir() + suffix;
        File sourceFile = new File(servicePath);
        File rlsFile = new File(targetDirectory);
        File absoluteFile = rlsFile.getAbsoluteFile();
        if (absoluteFile.exists()) {
            absoluteFile.delete();
        }
        try {
            rlsFile.createNewFile();
        } catch (IOException e) {
            throw new RuleServiceAdminException("Error creating a new file : " + rlsFile);
        }
        if (sourceFile.exists()) {
            try {
                FileManipulator.copyFile(sourceFile, absoluteFile);
            } catch (IOException e) {
                throw new RuleServiceAdminException("Error coping content of the  file : " +
                        sourceFile + ". to the target file : " + rlsFile);
            }
        }
        return new Paths(servicePath, targetDirectory);
    }
}
