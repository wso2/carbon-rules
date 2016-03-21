/*
 * Copyright 2016 The Apache Software Foundation.
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
package org.wso2.carbon.rule.mediator;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.mediator.config.RuleMediatorConfig;
import org.wso2.carbon.rule.mediator.internal.config.RuleMediatorConfigHelper;

import javax.xml.stream.XMLStreamException;

/**
 * Testcase for creating check stateless session creation in rule mediator.
 */
public class RuleMediatorSessionCreationTestCase extends TestCase{
    public void testRuleEngine(){
        String EXPECTED = "stateless";
        try {
            OMElement ruleOMElement = AXIOMUtil.stringToOM("<brs:rule xmlns:brs=\"http://wso2.org/carbon/rules\">\n" +
                    "      <brs:source xpath=\"\"></brs:source>\n" +
                    "      <brs:target xpath=\"\" resultXpath=\"\" action=\"replace\"></brs:target>\n" +
                    "      <brs:ruleSet>\n" +
                    "         <brs:properties/>\n" +
                    "         <brs:rule resourceType=\"regular\" sourceType=\"inline\"/>\n" +
                    "      </brs:ruleSet>\n" +
                    "      <session xmlns=\"http://ws.apache.org/ns/synapse\" type=\"stateless\"/>\n" +
                    "      <brs:input/>\n" +
                    "      <brs:output/>\n" +
                    "   </brs:rule>");

            RuleMediatorConfig ruleMediatorConfig = RuleMediatorConfigHelper.getRuleMediatorConfig(ruleOMElement);
            Assert.assertEquals(ruleMediatorConfig.getSession().getAttributeValue(Constants.RULE_SESSION_TYPE_QNAME),EXPECTED);

        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
