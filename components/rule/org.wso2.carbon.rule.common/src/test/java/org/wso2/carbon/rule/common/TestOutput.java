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
import org.wso2.carbon.rule.common.config.OutputHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestOutput extends TestCase{

    public void testOutput(){

        Output output = new Output();
        output.setWrapperElementName("sampleTestOutput");
        output.setNameSpace("http://com.test");

        // Create Fact object
        Fact outputFact = new Fact();
        outputFact.setElementName("sampleOutputFact");
        outputFact.setNamespace("http://com.testOutput");
        outputFact.setType("sample.Test.Output");
        outputFact.setXpath("http://com.test.sampleOutput.xml");

        Map<String, String> nsMAP = new HashMap<String, String>();
        nsMAP.put("ns1", "http://com.testNS1");
        nsMAP.put("ns2", "http://com.testNS2");
        nsMAP.put("ns3", "http://com.testNS3");
        outputFact.setPrefixToNamespaceMap(nsMAP);
        output.addFact(outputFact);

        Output resultOutput = OutputHelper.getOutput(output.toOM());
        List<Fact> outputFacts = output.getFacts();
        List<Fact> resultFacts = resultOutput.getFacts();
        assertEquals(output.getWrapperElementName(), resultOutput.getWrapperElementName());
        assertEquals(output.getNameSpace(), resultOutput.getNameSpace());
        assertNotNull(outputFact);
        assertNotNull(resultFacts);
        assertEquals(outputFacts.size(), resultFacts.size());

    }
}
