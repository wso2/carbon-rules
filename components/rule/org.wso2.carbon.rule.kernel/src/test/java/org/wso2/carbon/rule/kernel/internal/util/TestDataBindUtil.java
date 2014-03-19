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

package org.wso2.carbon.rule.kernel.internal.util;

import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.apache.axiom.om.OMElement;
import junit.framework.TestCase;

import javax.xml.namespace.QName;


public class TestDataBindUtil extends TestCase {

    public void testGetOMElement1() {

        OMElement omElement = DataBindUtil.toOM("Test String", new QName("http://com.test/rule", "custom"));
        System.out.println("OMElement ==> " + omElement.toString());
    }

    public void testGetOMElement2() {

        PlaceOrder placeOrder = new PlaceOrder();
        placeOrder.setSymbol("IBM");
        placeOrder.setQuantity(10);
        placeOrder.setPrice(20.34);
        OMElement omElement = DataBindUtil.toOM(placeOrder, new QName("http://com.test/rule", "custom"));
        System.out.println("OMElement ==> " + omElement.toString());
    }

    public void testGetValue() {

        OMElement omElement = DataBindUtil.toOM("Test String", new QName("http://com.test/rule", "custom"));
        try {
            Object result = DataBindUtil.getValue(omElement, String.class);
            assertEquals(result, "Test String");
        } catch (RuleRuntimeException e) {
            e.printStackTrace();
        }

    }

    public void testGetValue2() {

        PlaceOrder placeOrder = new PlaceOrder();
        placeOrder.setSymbol("IBM");
        placeOrder.setQuantity(10);
        placeOrder.setPrice(20.34);
        OMElement omElement = DataBindUtil.toOM(placeOrder, new QName("http://com.test/rule", "custom"));
        try {
            PlaceOrder result = (PlaceOrder) DataBindUtil.getValue(omElement, PlaceOrder.class);
            assertEquals(result.getSymbol(), "IBM");
            assertEquals(result.getQuantity(), 10);
            assertEquals(result.getPrice(), 20.34);
        } catch (RuleRuntimeException e) {
            e.printStackTrace();
        }

    }
    
}
