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

import org.drools.runtime.StatefulKnowledgeSession;
import org.wso2.carbon.rule.kernel.backend.Session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DroolsStatelessSession implements Session {

    /*
        NOTE:
        -------
        we have to wrap stateful knowledge session due to drools has deprecated knowledgebase concept but still provide
        knowledge base to legacy implementations. But some classes required (eg: CommandFactory) is no longer supported
        we cannot use drools statelessknowledge sessions. Since the statelessknowledge session is developed wrapping
        statefulKnowledge session,we can implement DroolsStatelessSession by wrapping StatefulKnowledgeSession until we
        adapt new design with KIE

        private StatelessKnowledgeSession statelessKnowledgeSession;
    */
    private StatefulKnowledgeSession statefulKnowledgeSession;

    public DroolsStatelessSession(StatefulKnowledgeSession knowledgeSession) {
        this.statefulKnowledgeSession = knowledgeSession;
    }

    public List execute(List facts) {

        for (Object fact : facts) {
            this.statefulKnowledgeSession.insert(fact);
        }
        this.statefulKnowledgeSession.fireAllRules();

        Iterator results = this.statefulKnowledgeSession.getObjects().iterator();
        List<Object> tobeReturn = new ArrayList<Object>();
        while (results.hasNext()) {
            Object result = results.next();
            if (result != null) {
                tobeReturn.add(result);
            }
        }

        //Destroy/Dispose stateful knowledge session since we use it for stateless usage
        destroy();

        return tobeReturn;
    }

    public void destroy() {
        statefulKnowledgeSession.dispose();
    }

    
}
