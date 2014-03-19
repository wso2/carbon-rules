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

package org.wso2.carbon.rule.common;

import junit.framework.TestCase;
import org.wso2.carbon.rule.common.config.RuleServiceHelper;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;

import java.util.HashMap;
import java.util.Map;

public class TestRuleService extends TestCase{


    public void testRuleService(){

        RuleService rulService = new RuleService();
        rulService.setName("TestRuleService");
        rulService.setTargetNamespace("http://com.test/testRuleService");
        rulService.setScope("in-out");
        rulService.setDescription("This Rule service is used to test the rule service functionalities ");
        rulService.setContainsServicesXML(false);
        rulService.setExtension("br");
        rulService.setEditable(false);

        // Add ruleset
        RuleSet ruleSet = new RuleSet();

        // Add rule
        Rule rule = new Rule();
        rule.setResourceType("drl");
        rule.setSourceType("inline");
        String value =  "package ruleTest;\n" +
            "\n" +
            "import org.wso2.carbon.rule.common.Test;\n" +
            "\n" +
            "rule SampleTest\n" +
            "\n" +
            "when\n" +
            "$test : Test( passed == true)\n" +
            "\n" +
            "then\n" +
            "$test.setMessage(\" Test Passed \");\n" +
            "\n" ;
        rule.setValue(value);
        ruleSet.addRule(rule);

        // Add property Map
        Map<String, String> propertyMAP = new HashMap<String, String>();
        propertyMAP.put("path", "banking.drl");
        propertyMAP.put("property2", "sampleProperty");

        ruleSet.setProperties(propertyMAP);

       rulService.setRuleSet(ruleSet);

        // Add rule operation
        Operation operation = new Operation();
                operation.setName("testOperation");

                // Set input object
                Input input = new Input();
                input.setWrapperElementName("sampleTestInput");
                input.setNameSpace("http://com.test");

                Map<String, String> nsMAP = new HashMap<String, String>();
                nsMAP.put("ns1", "http://com.testNS1");
                nsMAP.put("ns2", "http://com.testNS2");
                nsMAP.put("ns3", "http://com.testNS3");

                // Create Fact object
                Fact inputFact = new Fact();
                inputFact.setElementName("sampleInputFact");
                inputFact.setNamespace("http://com.testInput");
                inputFact.setType("sample.Test.Input");
                inputFact.setXpath("http://com.test.sampleInput.xml");

                inputFact.setPrefixToNamespaceMap(nsMAP);
                input.addFact(inputFact);
                operation.setInput(input);

                // Set output Object
                Output output = new Output();
                output.setWrapperElementName("sampleTestOutput");
                output.setNameSpace("http://com.test");

                // Create Fact object
                Fact outputFact = new Fact();
                outputFact.setElementName("sampleOutputFact");
                outputFact.setNamespace("http://com.testOutput");
                outputFact.setType("sample.Test.Output");
                outputFact.setXpath("http://com.test.sampleOutput.xml");
                outputFact.setPrefixToNamespaceMap(nsMAP);
                output.addFact(outputFact);
                operation.setOutput(output);
         rulService.addOperation(operation);

        try {
            RuleService resultRuleService = RuleServiceHelper.getRuleService(rulService.toOM());
            assertEquals(rulService.getName(), resultRuleService.getName());
            assertEquals(rulService.getTargetNamespace(), resultRuleService.getTargetNamespace());
            assertEquals(rulService.getScope(), resultRuleService.getScope());
        } catch (RuleConfigurationException e) {
            e.printStackTrace();
        }


    }

}
