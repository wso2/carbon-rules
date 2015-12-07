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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.drools.KnowledgeBase;
import org.drools.builder.*;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.wso2.carbon.rule.backend.util.RuleSetLoader;
import org.wso2.carbon.rule.common.Rule;
import org.wso2.carbon.rule.common.RuleSet;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntime;
import org.wso2.carbon.rule.kernel.backend.Session;

import java.io.InputStream;
import java.util.Collection;

public class DroolsBackendRuntime implements RuleBackendRuntime {

    private static Log log = LogFactory.getLog(DroolsBackendRuntime.class);
    private KnowledgeBase knowledgeBase;
    private KnowledgeBuilder knowledgeBuilder;
    private ClassLoader classLoader;

    public DroolsBackendRuntime(KnowledgeBase knowledgeBase,
                                KnowledgeBuilder knowledgeBuilder, ClassLoader classLoader) {
        this.knowledgeBase = knowledgeBase;
        this.knowledgeBuilder = knowledgeBuilder;
        this.classLoader = classLoader;
    }

    public void addRuleSet(RuleSet ruleSet) throws RuleConfigurationException {

        for (Rule rule : ruleSet.getRules()) {
            InputStream ruleInputStream = RuleSetLoader.getRuleSetAsStream(rule, classLoader);

            if (rule.getResourceType().equals(Constants.RULE_RESOURCE_TYPE_REGULAR)) {

                //check the file type before adding it to knowledge builder for "file" source type
                if (rule.getSourceType().equalsIgnoreCase(Constants.RULE_SOURCE_TYPE_FILE) && (rule.getValue().lastIndexOf(".drl") < 0) ) {
                    throw new RuleConfigurationException(
                            "Error in rule service configuration : Select \"dtable\" as Resource Type for decision tables "
                                                                        + "or attached file is not supported by rule engine");
                } else {
                    this.knowledgeBuilder.add(ResourceFactory.newInputStreamResource(ruleInputStream), ResourceType.DRL);
                }

            } else if (rule.getResourceType().equals(Constants.RULE_RESOURCE_TYPE_DTABLE)) {

                //check the file type before adding it to knowledge builder for "file" source type
                if (rule.getSourceType().equalsIgnoreCase(Constants.RULE_SOURCE_TYPE_FILE) &&
                                                                            rule.getValue().lastIndexOf(".xls") < 0 &&
                                                                            rule.getValue().lastIndexOf(".csv") < 0) {
                    throw new RuleConfigurationException(
                            "Error in rule service configuration : Select \"regular\" as Resource Type for regular rules "
                                                                        + "or attached file is not supported by rule engine");
                } else {
                    DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();

                    //check whether the decision tables is base on .xsl file of .csv format (inline input or .csv file input)
                    if (rule.getSourceType().equalsIgnoreCase(Constants.RULE_SOURCE_TYPE_INLINE) ||
                                                                    rule.getValue().lastIndexOf(".csv") > 0) {
                        //decision table in .xls format
                        dtconf.setInputType(DecisionTableInputType.CSV);
                    } else {
                        //decision table in .xls format
                        dtconf.setInputType(DecisionTableInputType.XLS);
                    }
                    this.knowledgeBuilder.add(ResourceFactory.newInputStreamResource(ruleInputStream),
                                              ResourceType.DTABLE, dtconf);
                }

            }

            if (this.knowledgeBuilder.hasErrors()) {
                throw new RuleConfigurationException("Error during creating rule set: " +
                        this.knowledgeBuilder.getErrors());
            }

            Collection<KnowledgePackage> pkgs = this.knowledgeBuilder.getKnowledgePackages();
            this.knowledgeBase.addKnowledgePackages(pkgs);
        }

    }

    public Session createSession(int type) throws RuleRuntimeException {

        Session sesson;
        if (type == Constants.RULE_STATEFUL_SESSION) {

            StatefulKnowledgeSession ruleSession =
                    this.knowledgeBase.newStatefulKnowledgeSession();

            if (ruleSession == null) {
                throw new RuleRuntimeException("The created stateful rule session is null");
            }
            sesson = new DroolsStatefulSession(ruleSession);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Using stateless rule session");
            }
            // create stateless rule session
            /**
                Due to drools have deprecated knowledgeBase usage, some classes have already unsupported,
                Until BRS adapt new concept we create stateful knowledge session and use it in stateless manner
                (dispose at the end of usage) therefore here we create stateful knowledge session from knowledgebase

            StatelessKnowledgeSession ruleSession = knowledgeBase.newStatelessKnowledgeSession();
            */

            StatefulKnowledgeSession ruleSession = knowledgeBase.newStatefulKnowledgeSession();

            if (ruleSession == null) {
                throw new RuleRuntimeException("The created stateless rule session is null");
            }
            sesson = new DroolsStatelessSession(ruleSession);
        }
        return sesson;
    }


}
