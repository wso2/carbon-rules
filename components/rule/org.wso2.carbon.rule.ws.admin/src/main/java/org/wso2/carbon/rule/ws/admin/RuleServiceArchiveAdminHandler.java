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
import org.wso2.carbon.rule.ws.admin.exception.RuleServiceAdminException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.common.RuleService;
import org.wso2.carbon.utils.ArchiveManipulator;
import org.wso2.carbon.utils.FileManipulator;

import javax.activation.DataHandler;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler for executing rule service management operations for rule services
 * based on the rule service archive files
 */
public class RuleServiceArchiveAdminHandler extends AbstractRuleServiceAdminHandler {

    public void saveRuleService(AxisConfiguration axisConfiguration,
                                AxisService axisService,
                                RuleService ruleService) throws RuleServiceAdminException {

        Paths paths = createTempRuleServiceFile(axisConfiguration,
                ruleService.getName());

        File metaINF = new File(paths.getWorkingDirPath() + File.separator + "META-INF");
        if (!metaINF.exists()) {
            metaINF.mkdirs();
        }
        // save services.xml
        File file = new File(paths.getWorkingDirPath() + File.separator + "META-INF" +
                File.separator + "services.xml");
        File serviceXMLFile = file.getAbsoluteFile();

//        if (ruleServiceDescription.isContainsServicesXML()) {
//            try {
//                OMElement parent = null;
//                if (serviceXMLFile.exists()) {
//                    BufferedInputStream inputStream =
//                            new BufferedInputStream(new FileInputStream(serviceXMLFile));
//                    parent = OMElementHelper.getInstance().toOM(inputStream);
//                    parent.build();
//                    inputStream.close();
//                    serviceXMLFile.delete();
//                }
//                serviceXMLFile.createNewFile();
//
//                OutputStream os = new FileOutputStream(file);
//                OMElement servicesXML =
//                        ServiceDescriptionSerializer.serializeToServiceXML(
//                                ruleServiceDescription, parent, AXIOM_XPATH_SERIALIZER);
//                servicesXML.build();
//                servicesXML.serialize(os);
//            } catch (Exception e) {
//                throw new RuleServiceManagementException("Cannot write services XML", e, log);
//            }
//
//        } else {
        if (serviceXMLFile.exists()) {
            serviceXMLFile.delete();
        }
//        }

        File ruleFile = new File(paths.getWorkingDirPath() + File.separator + "META-INF" +
                File.separator + Constants.RULE_SERVICE_FILE_NAME);
        File absoluteFile = ruleFile.getAbsoluteFile();
        if (absoluteFile.exists()) {
            absoluteFile.delete();
        }
        try {
            absoluteFile.createNewFile();
        } catch (IOException e) {
            throw new RuleServiceAdminException("Error creating a rule service file : " +
                    absoluteFile);
        }

        OutputStream os = null;
        try {
            os = new FileOutputStream(ruleFile);
            ruleService.toOM().serialize(os);
            os.flush();
        } catch (Exception e) {
            throw new RuleServiceAdminException("Cannot write to the rule service file : " +
                    ruleFile, e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("Can not close the out put stream");
                }
            }
        }

        ArchiveManipulator archiveManipulator = new ArchiveManipulator();
        try {
            String servicePath = paths.getServicePath();
            if (!servicePath.endsWith(Constants.RULE_SERVICE_ARCHIVE_EXTENSION)) {
                File serviceFile = new File(servicePath);
                File absoluteServiceFile = serviceFile.getAbsoluteFile();
                if (absoluteServiceFile.exists()) {
                    absoluteServiceFile.delete();
                }
                servicePath = servicePath.substring(0, servicePath.lastIndexOf(".") + 1) +
                        Constants.RULE_SERVICE_ARCHIVE_EXTENSION;
            }
            archiveManipulator.archiveDir(servicePath, paths.getWorkingDirPath());
        } catch (IOException e) {
            throw new RuleServiceAdminException("Error creating a archive a rule service ", e);
        }
//        saveToRegistry(paths, ruleServiceDescription.getName());
        cleanUp(paths);
    }

    public OMElement getRuleService(AxisConfiguration axisConfiguration, String name)
            throws RuleServiceAdminException {

        Paths paths = createTempRuleServiceFile(axisConfiguration, name);
        File ruleFile = new File(paths.getWorkingDirPath() + File.separator + "META-INF" +
                File.separator + Constants.RULE_SERVICE_FILE_NAME);
        OMElement result = createXML(name, ruleFile);
        File servicesXML = new File(paths.getWorkingDirPath() + File.separator + "META-INF" +
                File.separator + "services.xml");
        if (servicesXML.exists()) {
            result.addAttribute(
                    OM_FACTORY.createOMAttribute(
                            Constants.ATT_GENERATE_SERVICES_XML.getLocalPart(),
                            NULL_NS, String.valueOf(true)));
        }
        return result;
    }

    public String[] uploadFacts(AxisConfiguration axisConfiguration,
                                String serviceName,
                                String fileName,
                                DataHandler dataHandler) throws RuleServiceAdminException {

        Paths paths = createTempRuleServiceFile(axisConfiguration, serviceName);

        File lib = new File(paths.getWorkingDirPath() + File.separator + "lib");
        if (!lib.exists()) {
            lib.mkdirs();
        }

        File factFile = new File(lib, fileName);
        File absoluteFile = factFile.getAbsoluteFile();

        if (absoluteFile.exists()) {
            absoluteFile.delete();
        }
        try {
            absoluteFile.createNewFile();
            final FileOutputStream fos = new FileOutputStream(factFile);
            dataHandler.writeTo(fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuleServiceAdminException("Cannot write facts", e);
        }
        ArchiveManipulator archiveManipulator = new ArchiveManipulator();
        try {
            String[] strings = archiveManipulator.check(factFile.getAbsolutePath());
            List<String> list = filterClasses(strings);
            return list.toArray(new String[list.size()]);
        } catch (IOException e) {
            throw new RuleServiceAdminException("Cannot extractPayload classes from the fact" +
                    " file", e);
        }
    }

    public void uploadRuleFile(AxisConfiguration axisConfiguration,
                               String serviceName,
                               String fileName,
                               DataHandler dataHandler) throws RuleServiceAdminException {

        Paths paths = createTempRuleServiceFile(axisConfiguration, serviceName);

        File conf = new File(paths.getWorkingDirPath() + File.separator + "conf");
        if (!conf.exists()) {
            conf.mkdirs();
        }

        File factFile = new File(conf, fileName);
        File absoluteFile = factFile.getAbsoluteFile();

        if (absoluteFile.exists()) {
            absoluteFile.delete();
        }
        try {
            absoluteFile.createNewFile();
            final FileOutputStream fos = new FileOutputStream(factFile);
            dataHandler.writeTo(fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuleServiceAdminException("Cannot write Rule File", e);
        }
    }

    public String[] getAllFacts(AxisConfiguration axisConfiguration,
                                String serviceName) throws RuleServiceAdminException {
        final List<String> facts = new ArrayList<String>();

        Paths paths = createTempRuleServiceFile(axisConfiguration, serviceName);
        File lib = new File(paths.getWorkingDirPath() + File.separator + "lib");
        if (lib.exists()) {
            File[] jars = FileManipulator.getMatchingFiles(
                    lib.getAbsolutePath(), null, ".jar");
            for (File file : jars) {
                try {
                    String[] strings =
                            new ArchiveManipulator().check(file.getAbsolutePath());
                    facts.addAll(filterClasses(strings));
                } catch (IOException e) {
                    throw new RuleServiceAdminException("Cannot extractPayload classes from " +
                            "the fact file", e);
                }
            }
        }
        return facts.toArray(new String[facts.size()]);
    }

    public String[] getFactArchiveList(AxisConfiguration axisConfiguration, String serviceName) throws RuleServiceAdminException {
        final List<String> factArchives = new ArrayList<String>();
        Paths paths = createTempRuleServiceFile(axisConfiguration, serviceName);
        File lib = new File(paths.getWorkingDirPath() + File.separator + "lib");
        if (lib.exists()) {
            File[] jars = FileManipulator.getMatchingFiles(
                    lib.getAbsolutePath(), null, ".jar");
            for (File file : jars) {
                factArchives.add(file.getName());

            }
        }
        return factArchives.toArray(new String[factArchives.size()]);
    }

    public void deleteFactArchive(AxisConfiguration axisConfiguration, String serviceName, String fileName) throws RuleServiceAdminException {
        Paths paths = createTempRuleServiceFile(axisConfiguration, serviceName);
        File lib = new File(paths.getWorkingDirPath() + File.separator + "lib");

        File factFile = new File(lib, fileName);
        File absoluteFile = factFile.getAbsoluteFile();

        if (absoluteFile.exists()) {
            absoluteFile.delete();
        }
    }

    @Override
    public String[] getRuleFileList(AxisConfiguration axisConfiguration, String serviceName, String fileName) throws RuleServiceAdminException {
        final List<String> ruleFiles = new ArrayList<String>();
        Paths paths = createTempRuleServiceFile(axisConfiguration, serviceName);
        File conf = new File(paths.getWorkingDirPath() + File.separator + "conf");
        if (conf.exists()) {
            File[] drls = FileManipulator.getMatchingFiles(
                    conf.getAbsolutePath(), null, ".drl");
            for (File file : drls) {
                ruleFiles.add(file.getName());

            }
        }
        return ruleFiles.toArray(new String[ruleFiles.size()]);
    }

    @Override
    public void deleteRuleFile(AxisConfiguration axisConfiguration, String serviceName, String fileName) throws RuleServiceAdminException {
        Paths paths = createTempRuleServiceFile(axisConfiguration, serviceName);

        File conf = new File(paths.getWorkingDirPath() + File.separator + "conf");
        File factFile = new File(conf, fileName);
        File absoluteFile = factFile.getAbsoluteFile();

        absoluteFile.delete();
    }

    private Paths createTempRuleServiceFile(AxisConfiguration axisConfig,
                                            String serviceName) throws RuleServiceAdminException {
        String servicesDir = createServiceRepository(axisConfig);
        String servicePath = getServicePath(axisConfig, serviceName);
        if (servicePath == null || "".equals(servicePath)) {
            servicePath = servicesDir + File.separator + serviceName + "." +
                    Constants.RULE_SERVICE_ARCHIVE_EXTENSION;
        }

        File sourceFile = new File(servicePath);
        String targetDirectory = getTempDir() + File.separator + serviceName + "." +
                Constants.RULE_SERVICE_ARCHIVE_EXTENSION;
        File targetDirPath = new File(targetDirectory);
      if(!targetDirPath.exists()){

        if (sourceFile.exists() &&
                servicePath.endsWith(Constants.RULE_SERVICE_ARCHIVE_EXTENSION)) {
            ArchiveManipulator manipulator = new ArchiveManipulator();
            try {
                manipulator.extractFromStream(new FileInputStream(sourceFile), targetDirectory);
            } catch (IOException e) {
                throw new RuleServiceAdminException(
                        "Error extracting files from a source:  " + sourceFile +
                                " into destination : " + targetDirectory);
            }
        } else {
            new File(targetDirectory).mkdirs();
        }
       }
        return new Paths(servicePath, targetDirectory);
    }

    private List<String> filterClasses(String[] strings) throws RuleServiceAdminException {
        if (strings == null) {
            return new ArrayList<String>();
        }
        final List<String> classes = new ArrayList<String>();
        for (String s : strings) {
            if (s != null && s.endsWith(".class")) {
                classes.add(getClassNameFromResourceName(s));
            }
        }
        return classes;
    }

    private String getClassNameFromResourceName(String resourceName) throws RuleServiceAdminException {
        if (!resourceName.endsWith(".class")) {
            throw new RuleServiceAdminException("The resource name doesn't refer to" +
                    " a class file");
        }
        return resourceName.substring(0, resourceName.length() - 6).replace('/', '.');
    }
}
