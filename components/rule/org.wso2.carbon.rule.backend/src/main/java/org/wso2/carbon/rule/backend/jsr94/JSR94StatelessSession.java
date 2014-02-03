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

package org.wso2.carbon.rule.backend.jsr94;

import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.wso2.carbon.rule.kernel.backend.Session;

import javax.rules.InvalidRuleSessionException;
import javax.rules.StatelessRuleSession;
import java.rmi.RemoteException;
import java.util.List;

public class JSR94StatelessSession implements Session {

    /**
     * JSR94 StatelessSession
     */
    private StatelessRuleSession statelessRuleSession;

    public JSR94StatelessSession(StatelessRuleSession statelessRuleSession) {
        this.statelessRuleSession = statelessRuleSession;
    }

    @Override
    public List execute(List facts) throws RuleRuntimeException {
        try {
            return statelessRuleSession.executeRules(facts);
        } catch (InvalidRuleSessionException e) {
            throw new RuleRuntimeException("Error was occurred when executing StateLess Session", e);
        } catch (RemoteException e) {
            throw new RuleRuntimeException("Error was occurred when executing StateLess Session",e);
        }
    }

    @Override
    public void destroy() throws RuleRuntimeException {
        try {
            statelessRuleSession.release();
        } catch (RemoteException e) {
            throw new RuleRuntimeException("Error was occurred when executing StateLess Session", e);
        } catch (InvalidRuleSessionException e) {
            throw new RuleRuntimeException("Error was occurred when executing StateLess Session", e);
        }
    }
}
