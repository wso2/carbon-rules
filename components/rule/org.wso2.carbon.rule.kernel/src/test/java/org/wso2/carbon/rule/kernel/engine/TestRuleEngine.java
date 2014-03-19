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

package org.wso2.carbon.rule.kernel.engine;

import junit.framework.TestCase;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.common.Input;
import org.wso2.carbon.rule.common.Fact;
import org.wso2.carbon.rule.common.Output;
import org.wso2.carbon.rule.common.RuleSet;
import org.wso2.carbon.rule.kernel.config.RuleEngineConfigService;
import org.wso2.carbon.rule.kernel.config.RuleEngineProvider;
import org.wso2.carbon.rule.kernel.config.RuleEngineConfig;
import org.wso2.carbon.rule.kernel.internal.config.CarbonRuleEngineConfigService;
import org.wso2.carbon.rule.kernel.internal.ds.RuleValueHolder;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.ArrayList;

public class TestRuleEngine extends TestCase {

    public void testRuleEngine(){

        try {

            RuleSet ruleSet = new RuleSet();

            RuleEngineProvider ruleEngineProvider = new RuleEngineProvider();
            ruleEngineProvider.setClassName("org.wso2.carbon.rule.kernel.engine.DummyRuleBackendRuntimeFactory");

            RuleEngineConfig ruleEngineConfig = new RuleEngineConfig();
            ruleEngineConfig.setRuleEngineProvider(ruleEngineProvider);
            RuleEngineConfigService ruleEngineConfigService = new CarbonRuleEngineConfigService(ruleEngineConfig);

            RuleValueHolder.getInstance().registerRuleEngineConfigService(ruleEngineConfigService);


            RuleEngine ruleEngine = new RuleEngine(ruleSet, null);
            RuleSession ruleSession = ruleEngine.createSession(Constants.RULE_STATEFUL_SESSION);

            Input input = new Input();
            input.setWrapperElementName("http://test.com/rule/wrapper");
            input.setWrapperElementName("wrapper");

            List<Fact> facts = new ArrayList<Fact>();
            Fact fact = new Fact();
            fact.setType("org.wso2.carbon.rule.kernel.engine.PlaceOrder");
            fact.setElementName("placeorder");
            fact.setNamespace("http://test.com/rule/sample");
            fact.setTypeClass(PlaceOrder.class);
            facts.add(fact);

            input.setFacts(facts);

            Output output = new Output();
            output.setWrapperElementName("http://test.com/rule/wrapper/out");
            output.setNameSpace("wrapperOut");

            output.setFacts(facts);



            OMFactory omFactory = OMAbstractFactory.getOMFactory();
            OMElement inputWrapper = omFactory.createOMElement(new QName("http://test.com/rule/wrapper","wrapper"));
            OMElement placeOrderElement = omFactory.createOMElement(new QName("http://test.com/rule/sample","placeorder"));
            OMElement symbolElement = omFactory.createOMElement(new QName("http://test.com/rule/sample","symbol"));
            symbolElement.setText("IBM");
            OMElement quantityElement = omFactory.createOMElement(new QName("http://test.com/rule/sample","quantity"));
            quantityElement.setText("20");
            OMElement priceElement = omFactory.createOMElement(new QName("http://test.com/rule/sample","price"));
            priceElement.setText("12.34");

            placeOrderElement.addChild(symbolElement);
            placeOrderElement.addChild(quantityElement);
            placeOrderElement.addChild(priceElement);

            inputWrapper.addChild(placeOrderElement);

            OMElement omElement = ruleSession.execute(inputWrapper, input, output);

            System.out.println("OMElement ==> " + omElement.toString());
            
        } catch (RuleConfigurationException e) {
            e.printStackTrace();
        } catch (RuleRuntimeException e) {
            e.printStackTrace();
        }


    }


}
