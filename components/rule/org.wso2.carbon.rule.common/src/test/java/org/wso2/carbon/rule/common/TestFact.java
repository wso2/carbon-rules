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
import org.wso2.carbon.rule.common.config.FactHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * test case for Fact class parsing and serializing
 */
public class TestFact extends TestCase{

    public void testFact(){
        Fact fact = new Fact();
        fact.setElementName("sampleFact");
        fact.setNamespace("http://com.test");
        fact.setType("sample.Test");
        fact.setXpath("http://com.test.sample.xml");

        Map<String, String> nsMAP = new HashMap<String, String>();
        nsMAP.put("ns1", "http://com.testNS1");
        nsMAP.put("ns2", "http://com.testNS2");
        nsMAP.put("ns3", "http://com.testNS3");
        fact.setPrefixToNamespaceMap(nsMAP);

        Fact resultFact = FactHelper.getFact(fact.toOM());
        assertEquals(fact.getElementName(), resultFact.getElementName());
        assertEquals(fact.getNamespace(), resultFact.getNamespace());
        Map nsResultMap = resultFact.getPrefixToNamespaceMap();
        assertEquals(nsMAP.get("ns1"), nsResultMap.get("ns1"));
        assertEquals(nsMAP.get("ns2"), nsResultMap.get("ns2"));
        assertEquals(nsMAP.get("ns3"), nsResultMap.get("ns3"));

    }
}
