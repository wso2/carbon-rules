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

package org.wso2.carbon.rule.ws.deploy;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.deployment.*;
import org.apache.axis2.deployment.repository.util.DeploymentFileData;
import org.apache.axis2.description.*;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.ServiceLifeCycle;
import org.apache.axis2.util.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.rule.common.Input;
import org.wso2.carbon.rule.common.Operation;
import org.wso2.carbon.rule.common.Output;
import org.wso2.carbon.rule.common.RuleService;
import org.wso2.carbon.rule.common.config.HelperUtil;
import org.wso2.carbon.rule.common.config.RuleServiceHelper;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.kernel.engine.RuleEngine;
import org.wso2.carbon.rule.ws.internal.schema.SchemaBuilder;
import org.wso2.carbon.rule.ws.receiver.RuleMessageReceiver;
import org.wso2.carbon.utils.ServerConstants;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * this is the deployer extension to axis2 of rule deployer
 * rule service deployer. This reads either the .aar or .rsl files under the ruleservices folder and create AxisServices
 * from that.
 */
public class RuleServiceDeployer extends AbstractDeployer {

    private static Log log = LogFactory.getLog(RuleServiceDeployer.class);

    /**
     * configuration context of ther server
     */
    private ConfigurationContext configurationContext;

    private Map<String, String> fileNameToServiceNameMap;

    public void init(ConfigurationContext configurationContext) {
        this.configurationContext = configurationContext;
        this.fileNameToServiceNameMap = new HashMap<String, String>();
    }

    @Override
    /**
     * this method reads the rule service file (either in .rsl format or .aar format and creates the corresponding axis
     * service from that.
     */
    public void deploy(DeploymentFileData deploymentFileData) throws DeploymentException {

        PrivilegedCarbonContext.getThreadLocalCarbonContext().setUsername(CarbonConstants.REGISTRY_SYSTEM_USERNAME);
        AxisConfiguration axisConfiguration = this.configurationContext.getAxisConfiguration();
        File file = deploymentFileData.getFile();

        InputStream rslFileInputStream = null;

        try {

            ClassLoader serviceClassLoader = axisConfiguration.getServiceClassLoader();
            // if the file is a archive file (.aar) we need to create a class loader from that and use it.
            if (deploymentFileData.getName().endsWith(Constants.RULE_SERVICE_ARCHIVE_EXTENSION)) {
                deploymentFileData.setClassLoader(file.isDirectory(),
                        axisConfiguration.getServiceClassLoader(),
                        (File) axisConfiguration.getParameterValue(
                                org.apache.axis2.Constants.Configuration.ARTIFACTS_TEMP_DIR), true);
                serviceClassLoader = deploymentFileData.getClassLoader();
                rslFileInputStream = serviceClassLoader.getResourceAsStream("META-INF/" + Constants.RULE_SERVICE_FILE_NAME);

                if (rslFileInputStream == null){
                    throw new DeploymentException(
                            " service.rsl file can not be found in the META-INF folder of "
                                                                    + file.getAbsoluteFile());
                }

            } else if (deploymentFileData.getName().endsWith(Constants.RULE_SERVICE_FILE_EXTENSION)) {
                rslFileInputStream = new FileInputStream(file);
            } else {
                throw new DeploymentException("File with unknown extension type " +
                        deploymentFileData.getFile().getName());
            }

            AxisService axisService = createService(rslFileInputStream, serviceClassLoader, deploymentFileData);

            AxisServiceGroup axisServiceGroup = new AxisServiceGroup(axisConfiguration);
            axisServiceGroup.setServiceGroupName(axisService.getName());
            axisServiceGroup.setServiceGroupClassLoader(serviceClassLoader);
            axisServiceGroup.addParameter(CarbonConstants.FORCE_EXISTING_SERVICE_INIT, true);

            // set the service type
            axisService.addParameter(new Parameter(ServerConstants.SERVICE_TYPE, Constants.RULE_SERVICE_TYPE));
            axisService.addParameter(new Parameter(Constants.RULE_SERVICE_PATH,
                deploymentFileData.getFile().getAbsolutePath()));

            axisService.setParent(axisServiceGroup);

            AxisOperation axisOperation;
            for (Iterator<AxisOperation> iter = axisService.getOperations(); iter.hasNext();){
               axisOperation = iter.next();
               axisConfiguration.getPhasesInfo().setOperationPhases(axisOperation);
            }

            ArrayList<AxisService> axisServices = new ArrayList<AxisService>();
            axisServices.add(axisService);

            DeploymentEngine.addServiceGroup(
                    axisServiceGroup,
                    axisServices,
                    deploymentFileData.getFile().toURI().toURL(),
                    deploymentFileData,
                    axisConfiguration);

            this.fileNameToServiceNameMap.put(file.getAbsolutePath(), axisServiceGroup.getServiceGroupName());

        } catch (FileNotFoundException e) {
            throw new DeploymentException("Can not read the file ", e);
        } catch (AxisFault axisFault) {
            log.error("Can not create the rule engine ", axisFault);
            throw new DeploymentException("Can not add the axis2 service to configuration", axisFault);
        } catch (RuleConfigurationException e) {
            log.error("Can not create the rule engine ", e);
            throw new DeploymentException("Can not create the rule engine ", e);
        } catch (MalformedURLException e) {
            throw new DeploymentException("Invalid deployment file data location", e);
        } finally {
            if (rslFileInputStream != null) {
                try {
                    rslFileInputStream.close();
                } catch (IOException e) {
                    log.error("Can not close the rule service file input stream ");
                }
            }
        }
    }

    /**
     * creates the axis service by reading the .rsl file for the service.
     * @param rslFileInputStream  - input stream for the .rsl file
     * @param serviceClassLoader  - class loader of the service
     * @param deploymentFileData  - deploy artifact file
     * @return     - corresponding axis service for this rule archive.
     * @throws RuleConfigurationException - if there is a problem with the configuration
     * @throws AxisFault - if the service can not be created sucessfully
     */
    private AxisService createService(InputStream rslFileInputStream,
                                      ClassLoader serviceClassLoader,
                                      DeploymentFileData deploymentFileData)
                                             throws RuleConfigurationException, AxisFault {
        // get the om element for the rule service
        OMElement ruleServiceElement = getRuleServiceConfiguration(rslFileInputStream);

        // get the rule service details from the om element
        RuleService ruleService = RuleServiceHelper.getRuleService(ruleServiceElement);

        processDefaultValues(ruleService, serviceClassLoader);

        AxisService axisService = new AxisService(ruleService.getName());
        axisService.setTargetNamespace(ruleService.getTargetNamespace());
        axisService.setClassLoader(serviceClassLoader);
        axisService.setScope(ruleService.getScope());

        //populate the services.xml data to axis service if available.
        if (deploymentFileData.getName().endsWith(Constants.RULE_SERVICE_ARCHIVE_EXTENSION)) {
            populateServiceXMLData(deploymentFileData.getFile().getAbsolutePath(), axisService);
        }

        SchemaBuilder schemaBuilder = new SchemaBuilder(axisService.getClassLoader(), axisService);

        for (Operation operation : ruleService.getOperations()) {
            Input input = operation.getInput();
            Output output = operation.getOutput();

            output.populateClassTypes();
            
            schemaBuilder.addOperation(operation);

            AxisOperation axisOperation =
                    axisService.getOperation(new QName(ruleService.getTargetNamespace(), operation.getName()));

            if (axisOperation == null){
                  axisOperation =
                          new InOutAxisOperation(new QName(ruleService.getTargetNamespace(), operation.getName()));
                  axisService.addOperation(axisOperation);
            }

            RuleEngine ruleEngine =
                    new RuleEngine(ruleService.getRuleSet(), serviceClassLoader);
            RuleMessageReceiver ruleMessageReceiver =
                    new RuleMessageReceiver(ruleEngine, input, output);
            axisOperation.setMessageReceiver(ruleMessageReceiver);

            AxisMessage intAxisMessage = axisOperation.getMessage(WSDL2Constants.MESSAGE_LABEL_IN);
            intAxisMessage.setName(operation.getName() + "RequestMessage");
            intAxisMessage.setElementQName(input.getQName());

            AxisMessage outAxisMessage = axisOperation.getMessage(WSDL2Constants.MESSAGE_LABEL_OUT);
            outAxisMessage.setName(operation.getName() + "ResponseMessage");
            outAxisMessage.setElementQName(output.getQName());


        }

        axisService.addSchema(schemaBuilder.getSchemaList());
        return axisService;

    }

    /**
     * after reading the Rule servie from the configuration file we need to set some default values for some optional elements
     * and also keep a class instance in order to avoid any futrue class loading issues.
     * @param ruleService  - Rule configuration details
     * @param classLoader - classloder to be used to load the fact classes
     * @throws RuleConfigurationException - if there is a problem witht the configuration
     */
    private void processDefaultValues(RuleService ruleService, ClassLoader classLoader)
                                                        throws RuleConfigurationException {

        if (ruleService.getTargetNamespace() == null){
            ruleService.setTargetNamespace(Constants.RULE_CONF_NAMESPACE);
        }

        // set the class instances in facts to be used in data binding and shema generation
        for (Operation operation : ruleService.getOperations()){
            HelperUtil.processFactDefaultValues(operation.getInput().getFacts(), classLoader);
            HelperUtil.processFactDefaultValues(operation.getOutput().getFacts(), classLoader);

            // set the input and out put wrapper names
            Input input = operation.getInput();
            if ((input.getWrapperElementName() == null) || (input.getWrapperElementName().trim().equals(""))){
                input.setWrapperElementName(operation.getName());
            }

            if ((input.getNameSpace() == null) || (input.getNameSpace().trim().equals(""))){
               input.setNameSpace(ruleService.getTargetNamespace());
            }

            Output output = operation.getOutput();
            if ((output.getWrapperElementName() == null) || (output.getWrapperElementName().trim().equals(""))){
                output.setWrapperElementName(operation.getName() + "Response");
            }

            if ((output.getNameSpace() == null) || (output.getNameSpace().trim().equals("")) ){
                output.setNameSpace(ruleService.getTargetNamespace());
            }
        }
    }


    public void setDirectory(String s) {

    }

    public void setExtension(String s) {

    }

    private OMElement getRuleServiceConfiguration(InputStream inputStream) throws AxisFault {

        try {
            StAXOMBuilder stAXOMBuilder = new StAXOMBuilder(inputStream);
            return stAXOMBuilder.getDocumentElement();
        } catch (XMLStreamException e) {
            throw new AxisFault("can not read the input stream");
        }

    }

    @Override
    public void undeploy(String s) throws DeploymentException {

        //remove this service from the AxisConfiguration
        File file = new File(s);
        AxisConfiguration axisConfiguration = this.configurationContext.getAxisConfiguration();
        AxisServiceGroup serviceGroup;
        try {
            serviceGroup =
                    axisConfiguration.removeServiceGroup(this.fileNameToServiceNameMap.get(file.getAbsolutePath()));
             if (serviceGroup != null) {
                for (Iterator services = serviceGroup.getServices(); services.hasNext();) {
                AxisService axisService = (AxisService) services.next();
                ServiceLifeCycle serviceLifeCycle = axisService.getServiceLifeCycle();
                if (serviceLifeCycle != null) {
                    serviceLifeCycle.shutDown(this.configurationContext, axisService);
                }
            }
                this.configurationContext.removeServiceGroupContext(serviceGroup);

            } else {
                axisConfiguration.removeFaultyService(this.fileNameToServiceNameMap.get(file.getAbsolutePath()));
            }
        } catch (AxisFault axisFault) {
            throw new DeploymentException("Can not undeploy the file " + s);
        }
        super.undeploy(s);   
    }

    private void populateServiceXMLData(String zipFileName,
                                        AxisService axisService) throws RuleConfigurationException {
        ZipInputStream zin = null;
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(zipFileName);
            zin = new ZipInputStream(fin);
            ZipEntry entry;
            while ((entry = zin.getNextEntry()) != null) {
                if (entry.getName().equalsIgnoreCase(DeploymentConstants.SERVICES_XML)) {
                    OMElement omElement = (OMElement) XMLUtils.toOM(zin, true);
                    ServiceBuilder serviceBuilder = new ServiceBuilder(this.configurationContext, axisService);
                    serviceBuilder.populateService(omElement);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuleConfigurationException("Cannot find the file : " + zipFileName, e);
        } catch (IOException e) {
            throw new RuleConfigurationException("IO error reading the file : " + zipFileName, e);
        } catch (XMLStreamException e) {
            throw new RuleConfigurationException("Can not read the xml stream ", e);
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException ignored) {

                }
            }
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
