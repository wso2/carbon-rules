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

package org.wso2.carbon.rule.ws.receiver;

import org.apache.axis2.receivers.AbstractInOutMessageReceiver;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.AxisFault;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPEnvelope;
import org.wso2.carbon.rule.kernel.engine.RuleSession;
import org.wso2.carbon.rule.kernel.engine.RuleEngine;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.wso2.carbon.rule.common.Input;
import org.wso2.carbon.rule.common.Output;

/**
 * message receiver is the class which plugs the rule engine to the Axis2 kernel. After invoking the handlers Axis2 kernal invokes the
 * message receiver which does the proccessing. At the deployment time we set the message receiver to each and every operation.
 */
public class RuleMessageReceiver extends AbstractInOutMessageReceiver {

    private RuleEngine ruleEngine;
    private Input input;
    private Output output;

    public RuleMessageReceiver(RuleEngine ruleEngine, Input input, Output output) {
        this.ruleEngine = ruleEngine;
        this.input = input;
        this.output = output;
    }

    /**
     * this is the method invoke by the axis2 engine
     * @param inMessageContext   - input message context from which we can get the in comming OMElement
     * @param outMessageContext  - output message context to which we can set the out going soap message.
     * @throws AxisFault  - throws if some problem happens with the processing.
     */
    public void invokeBusinessLogic(MessageContext inMessageContext,
                                    MessageContext outMessageContext) throws AxisFault {
        OMElement inputOMElement = inMessageContext.getEnvelope().getBody().getFirstElement();

        SOAPFactory soapFactory = getSOAPFactory(inMessageContext);
        SOAPEnvelope soapEnvelope = soapFactory.getDefaultEnvelope();
        RuleSession ruleSession;
        try {
            ruleSession = getRuleSession(inMessageContext);
            soapEnvelope.getBody().addChild(ruleSession.execute(inputOMElement, this.input, this.output));
            outMessageContext.setEnvelope(soapEnvelope);
        } catch (RuleRuntimeException e) {
            log.error("Can not create the rule session", e);
            throw new AxisFault("Can not create the rule session", e);
        }

    }

    /**
     * we handles the rule sessions with the axis2 sessions. Axis2 manages sessions using the service context. Therefore we can store the rule session
     * on the service context as well.
     * @param inMessageContext  - in comming message context. From which we can obtain the service context object.
     * @return  - rule session to use
     * @throws RuleRuntimeException - if there is a problem of getting the rule session.
     */
    private RuleSession getRuleSession(MessageContext inMessageContext) throws RuleRuntimeException {
        ServiceContext serviceContext = inMessageContext.getServiceContext();
        RuleSession ruleSession =
                (RuleSession) serviceContext.getProperty(Constants.RULE_SESSION_OBJECT);
        if (ruleSession == null){
            ruleSession = this.ruleEngine.createSession(Constants.RULE_STATEFUL_SESSION);

            // put a configuration to invalidate the session or keep the session countinued
            serviceContext.setProperty(Constants.RULE_SESSION_OBJECT, ruleSession);
        }

        return ruleSession;
        
    }
}
