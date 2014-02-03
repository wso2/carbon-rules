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

public class DroolsStatefulSession implements Session {

    private StatefulKnowledgeSession statefulKnowledgeSession;

    public DroolsStatefulSession(StatefulKnowledgeSession statefulKnowledgeSession) {
        this.statefulKnowledgeSession = statefulKnowledgeSession;
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
        return tobeReturn;
    }

    public void destroy() {

    }
}
