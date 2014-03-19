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

import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatelessKnowledgeSession;
import org.wso2.carbon.rule.kernel.backend.Session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DroolsStatelessSession implements Session {

    private StatelessKnowledgeSession statelessKnowledgeSession;

    public DroolsStatelessSession(StatelessKnowledgeSession statelessKnowledgeSession) {
        this.statelessKnowledgeSession = statelessKnowledgeSession;
    }

    public List execute(List facts) {

        final List<Command> cmds = new ArrayList<Command>();
        for (Object fact : facts) {
            cmds.add(CommandFactory.newInsert(fact));
        }

        final ExecutionResults executionResults = this.statelessKnowledgeSession.execute(
                CommandFactory.newBatchExecution(cmds));

        final Collection<String> results = executionResults.getIdentifiers();
        final List tobeReturn = new ArrayList();
        for (String name : results) {
            if (name != null) {
                tobeReturn.add(executionResults.getValue(name));
            }
        }
        return tobeReturn;
    }

    public void destroy() {

    }

    
}
