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
import org.wso2.carbon.rule.common.config.RuleHelper;

public class TestRule extends TestCase{

    public void testRule(){
        Rule rule = new Rule();

            String resourceType = "drl";
            String sourceType = "inline";
            String value ="package ruleTest;\n" +
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
        rule.setResourceType(resourceType);
        rule.setSourceType(sourceType);
        rule.setValue(value);
        rule.setDescription("test description");

        Rule resultRule = RuleHelper.getRule(rule.toOM());
        assertEquals(rule.getResourceType(), resultRule.getResourceType());
        assertEquals(rule.getSourceType(), resultRule.getSourceType());
        assertEquals(rule.getValue(), resultRule.getValue());
        assertEquals(rule.getDescription(), resultRule.getDescription());

    }
}