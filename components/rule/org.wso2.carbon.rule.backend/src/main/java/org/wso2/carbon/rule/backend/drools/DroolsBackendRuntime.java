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
import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.wso2.carbon.rule.backend.util.RuleSetLoader;
import org.wso2.carbon.rule.common.Rule;
import org.wso2.carbon.rule.common.RuleSet;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntime;
import org.wso2.carbon.rule.kernel.backend.Session;

import java.io.File;
import java.io.FileInputStream;

public class DroolsBackendRuntime implements RuleBackendRuntime {

    private static Log log = LogFactory.getLog(DroolsBackendRuntime.class);
    private KieServices kieServices;
    private KieFileSystem kieFileSystem;
    private ClassLoader classLoader;
    private KieBase kbase;

    public DroolsBackendRuntime(KieServices kieServices, KieFileSystem kieFileSystem, ClassLoader classLoader) {
        this.kieServices = kieServices;
        this.kieFileSystem = kieFileSystem;
        this.classLoader = classLoader;
    }

    public void addRuleSet(RuleSet ruleSet) throws RuleConfigurationException {

        for (Rule rule : ruleSet.getRules()) {
            FileInputStream ruleInputStream = RuleSetLoader.getRuleSetAsFileStream(rule, classLoader);

            if (rule.getResourceType().equals(Constants.RULE_RESOURCE_TYPE_REGULAR)) {
                //check the file type before adding it to knowledge builder for "file" source type
                if (rule.getSourceType().equalsIgnoreCase(Constants.RULE_SOURCE_TYPE_FILE) && (rule.getValue().lastIndexOf(".drl") < 0) ) {
                    throw new RuleConfigurationException(
                            "Error in rule service configuration : Select \"dtable\" as Resource Type for decision tables "
                                                                        + "or attached file is not supported by rule engine");
                } else {
                    this.kieFileSystem.write("src/main/resources/org/wso2/"+ rule.hashCode() + ".drl",
                            this.kieServices.getResources().newInputStreamResource(ruleInputStream));
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
                    this.kieFileSystem.write("src/main/resources/org/wso2/"+ rule.hashCode() + ".drl",
                            this.kieServices.getResources().newInputStreamResource(ruleInputStream));
                }

            }
        }

        ReleaseId releaseId = this.kieServices.newReleaseId("org.wso2", "carbon-rule", "1.0.0");
        this.kieFileSystem.generateAndWritePomXML(releaseId);

        // Now resources are built and stored into an internal repository
        try {
            this.kieServices.newKieBuilder(kieFileSystem).buildAll(ExecutableModelProject.class);
        } catch (Exception e) {
           log.warn("Error while building the knowledge base" + e.getMessage());
        }
        kieServices.newKieBuilder(kieFileSystem).buildAll(ExecutableModelProject.class);

        // You can get a KieContainer with the ReleaseId
        KieContainer kcontainer = this.kieServices.newKieContainer(releaseId);

        // KieContainer can create a KieBase
        this.kbase = kcontainer.getKieBase();

    }

    public Session createSession(int type) throws RuleRuntimeException {
        Session session;
        KieSession ruleSession = kbase.newKieSession();

        if (ruleSession == null) {
            throw new RuleRuntimeException("The created stateful rule session is null");
        }
        session = new DroolsStatefulSession(ruleSession);
        return session;
    }


}
