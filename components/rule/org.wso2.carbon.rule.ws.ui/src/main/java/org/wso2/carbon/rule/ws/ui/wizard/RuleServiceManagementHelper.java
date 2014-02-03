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

import org.wso2.carbon.rule.common.*;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.ws.ui.ns.NameSpacesInformation;
import org.wso2.carbon.rule.ws.ui.ns.NameSpacesInformationRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper class to use various requirements such as saving each step of the rule service creation wizard
 */
public class RuleServiceManagementHelper {

    private static String EMPTY_STRING = "";

    /**
     * Saves the data gathered in the step 1 of the rule service wizard
     * <ul>
     * <li>Service Name</li>
     * <li>Service Description</li>
     * </ul>
     *
     * @param ruleService <code>RuleServiceDescription</code>  containing information
     *                    about the rule service being created or edited
     * @param request     <code>HttpServletRequest</code>
     */
    public static void saveStep1(RuleService ruleService,
                                 HttpServletRequest request) {

        String stepID = request.getParameter("stepID");
        if (!"step1".equals(stepID)) {
            return;
        }

        String serviceName = request.getParameter("ruleServiceName");
        if (serviceName != null && !"".equals(serviceName.trim())) {
            String name = ruleService.getName();
            if (name == null || "".equals(name)) {
                ruleService.setEditable(true);
            }
            ruleService.setName(serviceName);
        }

        String ruleServiceTNS = request.getParameter("ruleServiceTNS");
        if (ruleServiceTNS != null && !"".equals(ruleServiceTNS)) {
            ruleService.setTargetNamespace(ruleServiceTNS.trim());
        }

        String description = request.getParameter("description");
        if (description != null && !"".equals(description.trim())) {
            ruleService.setDescription(description.trim());
        }

        String serviceScope = request.getParameter("ruleServiceScope");
        if (serviceScope != null && !"".equals(serviceScope)) {
            ruleService.setScope(serviceScope.trim());

        }

        String generateServicesXML = request.getParameter("generateServiceXML");
        if (generateServicesXML != null) {
            ruleService.setContainsServicesXML(true);
        } else {
            ruleService.setContainsServicesXML(false);
        }
        ruleService.setExtension("aar");
    }

    /**
     * Saves the data gathered in the step 2 of the rule service wizard
     * (i.e rule set )
     *
     * @param ruleService <code>RuleSetDescription</code> containing information
     *                    about rule set
     * @param request     <code>HttpServletRequest</code>
     */
    public static void saveStep2(RuleService ruleService,
                                 HttpServletRequest request) {

            String stepID = request.getParameter("stepID");
        if (!"step2".equals(stepID)) {
            return;
        }

        RuleSet ruleSet = ruleService.getRuleSet();
        if (ruleSet == null) {
            ruleSet = new RuleSet();
            ruleService.setRuleSet(ruleSet);
        }

        List<String> scriptNameList = new ArrayList<String>();
        String ruleScriptType = request.getParameter("ruleSourceType");
        String ruleResourceType = request.getParameter("ruleResouceType");
        Map<String, String> scriptList =
                (Map<String, String>) request.getSession().getAttribute(RuleServiceAdminClient.SCRIPTS);
        //TODO add method ruleset.clear() to clear all existing rules

        String inlinedSource = request.getParameter("ruleSourceInlined");
        if ("inlined".equals(ruleScriptType) && inlinedSource != null && !"".equals(inlinedSource.trim())) {
            ruleSet.clearRules();
            Rule rule = new Rule();
            ruleSet.addRule(rule);
            rule.setSourceType(Constants.RULE_SOURCE_TYPE_INLINE);
            rule.setResourceType(ruleResourceType);
            rule.setValue(inlinedSource.trim());
        } else if ("key".equals(ruleScriptType) || "url".equals(ruleScriptType) || "upload".equals(ruleResourceType)) {
            ruleSet.clearRules();

            List<String> paths = null;//= new ArrayList<String>();
            if (scriptList != null && !scriptList.isEmpty()) {
                paths = new ArrayList<String>(scriptList.keySet());
            }

            if (paths != null && !paths.isEmpty()) {
                for (String scriptName : paths) {
                    String sourceType = scriptList.get(scriptName);
                    Rule rule = new Rule();
                    rule.setSourceType(sourceType);
                    rule.setResourceType(ruleResourceType);
                    rule.setValue(scriptName.trim());
                    ruleSet.addRule(rule);


                }
            }
        }

    }

    /**
     * Saves the information gathered in the step 5 of the rule service wizard
     * * <ul>
     * <li>Operation Name</li>
     * <li>Facts</li>
     * <li>Results</li>
     * </ul>
     *
     * @param ruleService <code>RuleServiceDescription</code>  containing information
     *                    about the rule service being created or edited
     * @param request     <code>HttpServletRequest</code>
     */
    public static void saveStep5(RuleService ruleService,
                                 HttpServletRequest request) {

        String stepID = request.getParameter("stepID");
        if (!"step5".equals(stepID)) {
            return;
        }

        // Clear rule script session
        request.getSession().removeAttribute("ruleScript");

        String operationName = request.getParameter("operationName");
        Operation operation = ruleService.getOperation(operationName);
        NameSpacesInformationRepository repository =
                (NameSpacesInformationRepository) request.getSession().getAttribute(
                        NameSpacesInformationRepository.NAMESPACES_INFORMATION_REPOSITORY);

        NameSpacesInformation information = null;
        String ownerID = request.getParameter("opName");
        if (ownerID == null || "".equals(ownerID)) {
            ownerID = "default";
        }
        if (operation == null) {
            operation = new Operation();
            operation.setName(operationName);
            ruleService.addOperation(operation);
        } else {
            operation.getInput().getFacts().clear();
            operation.getOutput().getFacts().clear();
        }

        Input input = operation.getInput();
        String inputWrapperName = request.getParameter("inputWrapperName");
        String inputNameSpace = request.getParameter("inputNameSpace");
        if (input == null) {
            input = new Input();
            operation.setInput(input);

        }
        if (inputWrapperName != null && !"".equals(inputWrapperName)) {
            input.setWrapperElementName(inputWrapperName);

        }
        if (inputNameSpace != null && !"".equals(inputNameSpace)) {
            input.setNameSpace(inputNameSpace);

        }

        String inputFactCounter = request.getParameter("inputFactCount");

        if (inputFactCounter != null && !"".equals(inputFactCounter)) {
            int inputFactCount = 0;
            try {
                inputFactCount = Integer.parseInt(inputFactCounter.trim());

                for (int i = 0; i < inputFactCount; i++) {
                    String name = request.getParameter("inputFactName" + i);
                    String type = request.getParameter("inputFactType" + i);
                    String nameSpace = request.getParameter("inputFactNameSpace" + i);
                    String xPath = request.getParameter("inputFactXPath" + i);
                    String id = "inputFactValue" + i;

                    if (type != null && !"".equals(type)) {
                        Fact fact = new Fact();
                        input.addFact(fact);
                        fact.setType(type.trim());

                        if (name != null && !"".equals(name)) {
                            fact.setElementName(name.trim());
                        }
                        if (nameSpace != null && !"".equals(nameSpace)) {
                            fact.setNamespace(nameSpace.trim());
                        }
                        if (xPath != null && !"".equals(xPath)) {
                            fact.setXpath(xPath.trim());
                        }
                        if (repository != null) {

                            information = repository.getNameSpacesInformation(ownerID, id);
                            if (information != null) {
                                fact.setPrefixToNamespaceMap(information.getNameSpaces());
                            }
                        }
                    }

                }
            } catch (NumberFormatException ignored) {
            }

        }


        Output output = operation.getOutput();
        String outputWrapperName = request.getParameter("outputWrapperName");
        String outputNameSpace = request.getParameter("outputNameSpace");
        if (output == null) {
            output = new Output();
            operation.setOutput(output);
        }
        if (outputWrapperName != null && !"".equals(outputWrapperName)) {
            output.setWrapperElementName(outputWrapperName);

        }
        if (outputNameSpace != null && !"".equals(outputNameSpace)) {
            output.setNameSpace(outputNameSpace);

        }

        String outputFactCounter = request.getParameter("outputFactCount");
        if (outputFactCounter != null && !"".equals(outputFactCounter)) {
            int outputFactCount = 0;
            try {
                outputFactCount = Integer.parseInt(outputFactCounter.trim());

                for (int i = 0; i < outputFactCount; i++) {
                    String name = request.getParameter("outputFactName" + i);
                    String type = request.getParameter("outputFactType" + i);
                    String nameSpace = request.getParameter("outputFactNameSpace" + i);
                    String xPath = request.getParameter("outputFactXPath" + i);
                    String id = "outputFactValue" + i;

                    if (type != null && !"".equals(type)) {
                        Fact fact = new Fact();
                        output.addFact(fact);
                        fact.setType(type.trim());

                        if (name != null && !"".equals(name)) {
                            fact.setElementName(name.trim());
                        }
                        if (nameSpace != null && !"".equals(nameSpace)) {
                            fact.setNamespace(nameSpace.trim());
                        }
                        if (xPath != null && !"".equals(xPath)) {
                            fact.setXpath(xPath.trim());
                        }
                        if (repository != null) {

                            information = repository.getNameSpacesInformation(ownerID, id);
                            if (information != null) {
                                fact.setPrefixToNamespaceMap(information.getNameSpaces());
                            }
                        }
                    }

                }
            } catch (NumberFormatException ignored) {
            }

        }
    }
}
