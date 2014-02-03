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

package org.wso2.carbon.rule.backend.drools;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.wso2.carbon.rule.common.RuleSet;
import org.wso2.carbon.rule.common.Rule;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntime;
import org.wso2.carbon.rule.kernel.backend.Session;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import samples.userguide.PlaceOrder;
import samples.userguide.OrderAccept;
import samples.userguide.OrderReject;

public class TestDroolsBackendRuntimeFactory extends TestCase {

    public void testOrderProcessor(){
        DroolsBackendRuntimeFactory droolsBackendRuntimeFactory = new DroolsBackendRuntimeFactory();
        RuleBackendRuntime ruleBackendRuntime = droolsBackendRuntimeFactory.getRuleBackendRuntime(new HashMap(), this.getClass().getClassLoader());

        RuleSet ruleSet = new RuleSet();

        List<Rule> rules = new ArrayList<Rule>();
        Rule rule = new Rule();
        rule.setSourceType(Constants.RULE_SOURCE_TYPE_INLINE);
        rule.setResourceType(Constants.RULE_RESOURCE_TYPE_REGULAR);



        String ruleSource = "package OrderApproval;\n" +
                "\n" +
                "import samples.userguide.OrderAccept;\n" +
                "import samples.userguide.OrderReject;\n" +
                "import samples.userguide.PlaceOrder;\n" +
                "\n" +
                "rule \"Order Approval Rule\" dialect \"mvel\" no-loop true salience 4\n" +
                "\n" +
                "when\n" +
                "$placeOrder : PlaceOrder( ( symbol == \"IBM\" && quantity > 10 ) || ( symbol == \"SUN\" && price > 100 ) || ( symbol == \"MSFT\" && price > 50 && quantity < 200 ) )\n" +
                "then\n" +
                "\n" +
                "OrderAccept orderAccept = new OrderAccept();\n" +
                "orderAccept.setMessage(\"Accepted order for: \"+ $placeOrder.quantity + \" stocks of \"+\n" +
                "$placeOrder.symbol +\" at$ \" + $placeOrder.price);\n" +
                "insertLogical(orderAccept);\n" +
                "\n" +
                "end\n" +
                "\n" +
                "rule \"IBM Order Deny Rule\" dialect \"mvel\" no-loop true salience 3\n" +
                "\n" +
                "when\n" +
                "not ( OrderAccept())\n" +
                "$placeOrder : PlaceOrder( symbol == \"IBM\" )\n" +
                "then\n" +
                "retract($placeOrder);\n" +
                "OrderReject orderReject = new OrderReject();\n" +
                "orderReject.setReason(\"An Order for stocks of IBM is accepted only if the number of stocks is higher than 10.\");\n" +
                "insertLogical(orderReject);\n" +
                "end\n" +
                "\n" +
                "rule \"SUN Order Deny Rule\" dialect \"mvel\" no-loop true salience 2\n" +
                "when\n" +
                "not ( OrderAccept())\n" +
                "$placeOrder : PlaceOrder( symbol == \"SUN\" )\n" +
                "then\n" +
                "retract($placeOrder);\n" +
                "OrderReject orderReject = new OrderReject();\n" +
                "orderReject.setReason(\"An Order for stocks of SUN is accepted only if the stock price is higher than 100 $.\");\n" +
                "insertLogical(orderReject);\n" +
                "end\n" +
                "\n" +
                "rule \"MSFT Order Deny Rule\" dialect \"mvel\" no-loop true salience 1\n" +
                "when\n" +
                "not ( OrderAccept())\n" +
                "$placeOrder : PlaceOrder( symbol == \"MSFT\" )\n" +
                "then\n" +
                "retract($placeOrder);\n" +
                "OrderReject orderReject = new OrderReject();\n" +
                "orderReject.setReason(\"An Order for stocks of MSFT is accepted only if the stock price is higher than 50 $ and the number of stocks is lower than 200.\");\n" +
                "insertLogical(orderReject);\n" +
                "end";


        rule.setValue(ruleSource);
        rules.add(rule);

        ruleSet.setRules(rules);

        try {
            ruleBackendRuntime.addRuleSet(ruleSet);

            //inserting the facts
            Session session = ruleBackendRuntime.createSession(Constants.RULE_STATEFUL_SESSION);

            List facts = new ArrayList();

            PlaceOrder placeOrder = new PlaceOrder();
            placeOrder.setSymbol("IBM");
            placeOrder.setPrice(20);
            placeOrder.setQuantity(20);

            facts.add(placeOrder);
            List resultFacts = session.execute(facts);

            for (Object result : resultFacts){
               if (result instanceof OrderAccept){
                   System.out.println("Accepted order ==> " + ((OrderAccept)result).getMessage());
               } else if (result instanceof OrderReject){
                   System.out.println("Rejected order ==> " + ((OrderReject)result).getReason());
               }
            }

        } catch (RuleConfigurationException e) {
            e.printStackTrace();
        } catch (RuleRuntimeException e) {
            e.printStackTrace();
        }


    }
}
