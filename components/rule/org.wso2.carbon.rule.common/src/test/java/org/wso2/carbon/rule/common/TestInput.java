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
import org.wso2.carbon.rule.common.config.InputHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestInput extends TestCase{

    public void testInput(){
        Input input = new Input();
        input.setWrapperElementName("sampleTestInput");
        input.setNameSpace("http://com.test");

        // Create Fact object
        Fact inputFact = new Fact();
        inputFact.setElementName("sampleInputFact");
        inputFact.setNamespace("http://com.testInput");
        inputFact.setType("sample.Test.Input");
        inputFact.setXpath("http://com.test.sampleInput.xml");

        Map<String, String> nsMAP = new HashMap<String, String>();
        nsMAP.put("ns1", "http://com.testNS1");
        nsMAP.put("ns2", "http://com.testNS2");
        nsMAP.put("ns3", "http://com.testNS3");
        inputFact.setPrefixToNamespaceMap(nsMAP);
        input.addFact(inputFact);

        Input resultInput = InputHelper.getInput(input.toOM());
        List<Fact> inputFacts = input.getFacts();
        List<Fact> resultFacts = resultInput.getFacts();
        assertEquals(input.getWrapperElementName(), resultInput.getWrapperElementName());
        assertEquals(input.getNameSpace(), resultInput.getNameSpace());
        assertNotNull(inputFacts);
        assertNotNull(resultFacts);
        assertEquals(inputFacts.size(), resultFacts.size());

    }
}
