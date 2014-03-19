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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.rule.backend.util.RuleSetLoader;
import org.wso2.carbon.rule.common.Rule;
import org.wso2.carbon.rule.common.RuleSet;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.kernel.backend.DefaultPropertiesProvider;
import org.wso2.carbon.rule.kernel.backend.RuleBackendRuntime;
import org.wso2.carbon.rule.kernel.backend.Session;

import javax.rules.*;
import javax.rules.admin.*;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class JSR94BackendRuntime implements RuleBackendRuntime {

    private static Log log = LogFactory.getLog(JSR94BackendRuntime.class);

    /**
     * To register/unregister rule execution sets and to access rule service providers
     */
    private RuleAdministrator ruleAdministrator;

    /**
     * The access point for runtime execution of RuleExecutionSets *
     */
    private RuleRuntime ruleRuntime;

    /**
     * Creates rule execution sets from local resources
     */
    private LocalRuleExecutionSetProvider localRuleExecutionSetProvider;

    /**
     * Provides any default properties required by the underling rule engine
     */
    private DefaultPropertiesProvider defaultPropertiesProvider;

    /**
     * To be used as a class loader by the rule engine
     */
    private ClassLoader classLoader;

    private String ruleExecutionSetURI;

    public JSR94BackendRuntime(RuleAdministrator ruleAdministrator,
                               RuleRuntime ruleRuntime,
                               LocalRuleExecutionSetProvider localRuleExecutionSetProvider,
                               ClassLoader classLoader) {
        this.ruleAdministrator = ruleAdministrator;
        this.ruleRuntime = ruleRuntime;
        this.localRuleExecutionSetProvider = localRuleExecutionSetProvider;
        this.classLoader = classLoader;
    }

    /**
     * Adds the rule set and returns the bind URI for the rule set. The bind URI should be used to
     * create a session associated with this rule set
     *
     */
    public void addRuleSet(RuleSet ruleSet) throws RuleConfigurationException {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.putAll(ruleSet.getProperties());
        if (defaultPropertiesProvider != null) {
            Map<String, Object> map =
                    defaultPropertiesProvider.getRuleExecutionSetCreationDefaultProperties(classLoader);
            properties.putAll(map);
        }

        for (Rule rule : ruleSet.getRules()) {

            Map<String, Object> ruleProperties = new HashMap<String, Object>();
            ruleProperties.putAll(properties);
            if (rule.getResourceType().equals(Constants.RULE_RESOURCE_TYPE_REGULAR)) {
                ruleProperties.put(Constants.RULE_SOURCE, Constants.RULE_SOURCE_DRL);
            } else if (rule.getResourceType().equals(Constants.RULE_RESOURCE_TYPE_DTABLE)){
                 ruleProperties.put(Constants.RULE_SOURCE, "javax.rules.admin.RuleExecutionSet.source.decisiontable");
            }

            InputStream ruleInputStream = RuleSetLoader.getRuleSetAsStream(rule, classLoader);

            if (ruleInputStream == null) {
                throw new RuleConfigurationException(" The input stream form rule script is null.");
            }
            String bindURI =
                    registerRuleExecutionSet(ruleInputStream, ruleProperties, ruleSet.getBindURI(), ruleSet.getProperties());
            if (ruleSet.getBindURI() == null) {
                ruleSet.setBindURI(bindURI);
            }
        }

    }


    public Session createSession(int type) throws RuleRuntimeException {
        boolean stateful = (type == Constants.RULE_STATEFUL_SESSION);
        try {

            //TODO: check whether this is useful
            final Map<String, Object> properties = new HashMap<String, Object>();

            if (stateful) {
                if (log.isDebugEnabled()) {
                    log.debug("Using stateful rule session ");
                }

                // create state full rule session and sets inputs
                StatefulRuleSession ruleSession =
                        (StatefulRuleSession) ruleRuntime.createRuleSession(this.ruleExecutionSetURI,
                                properties,
                                RuleRuntime.STATEFUL_SESSION_TYPE);

                if (ruleSession == null) {
                    throw new RuleRuntimeException("The created stateful rule session is null");
                }
                return new JSR94StatefulSession(ruleSession);

            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Using stateless rule session");
                }
                // create stateless rule session
                StatelessRuleSession ruleSession =
                        (StatelessRuleSession) ruleRuntime.createRuleSession(
                                this.ruleExecutionSetURI, properties, RuleRuntime.STATELESS_SESSION_TYPE);

                if (ruleSession == null) {
                    throw new RuleRuntimeException("The created stateless rule session is null");
                }
                return new JSR94StatelessSession(ruleSession);

            }
        } catch (
                RuleSessionTypeUnsupportedException e) {
            throw new RuleRuntimeException("Error was occurred when creating " +
                    (stateful ? " StateFul" : "StateLess") + " Session", e);
        } catch (
                RuleSessionCreateException e) {
            throw new RuleRuntimeException("Error was occurred when creating " +
                    (stateful ? " StateFul" : "StateLess") + " Session", e);
        } catch (
                RemoteException e) {
            throw new RuleRuntimeException("Error was occurred when creating " +
                    (stateful ? " StateFul" : "StateLess") + " Session", e);
        } catch (
                RuleExecutionSetNotFoundException e) {
            throw new RuleRuntimeException("Error was occurred when creating " +
                    (stateful ? " StateFul" : "StateLess") + " Session", e);
        } catch (Exception e) {
            throw new RuleRuntimeException("Unknown Error was when executing rules using " +
                    (stateful ? " StateFul" : "StateLess") + " Session", e);
        }
    }

    /**
     * Removed the rule execution set runtime from the rule runtime based on the information
     * in the given rule set description
     * @param ruleSet - remove the rules of this rule set
     * @throws RuleConfigurationException - if there is a problem with the configuration
     */
    public void removeRuleSet(RuleSet ruleSet) throws RuleConfigurationException {
        String bindUri = ruleSet.getBindURI();
        try {
            // Removes the old rule sets , if there are any
            if (log.isDebugEnabled()) {
                log.debug("Removing the rule execution set" +
                        " that has been bind to Uri " + bindUri);
            }
            ruleAdministrator.deregisterRuleExecutionSet(bindUri, ruleSet.getDeregistrationProperties());

        } catch (RuleExecutionSetDeregistrationException e) {
            throw new RuleConfigurationException("Error was occurred when tying to unregistered " +
                    "the RuleExecutionSet which has Uri " + bindUri, e);
        } catch (RemoteException e) {
            throw new RuleConfigurationException("Error was occurred when trying to unregistered " +
                    "the RuleExecutionSet which has Uri " + bindUri, e);
        }
    }

    /**
     * TODO
     */
    public void destroy() {
        //TODO
    }

    /**
     * Helper method to register a rule execution set in the local rule set execution provider
     *
     * @param in Rule set as an input stream
     * @param properties  properties to be used in the registration process.
     * @param bindUri     the URI to be associated with the rule set
     * @param registrationProperties registration properties
     * @return the URI associated with the rule set
     * @throws RuleConfigurationException - if there is a problem with the configuration
     */

    private String registerRuleExecutionSet(InputStream in,
                                            Map<String, Object> properties,
                                            String bindUri,
                                            Map<String, String> registrationProperties)
            throws RuleConfigurationException {
        try {
            //Create the rule execution set
            RuleExecutionSet ruleExecutionSet =
                    localRuleExecutionSetProvider.createRuleExecutionSet(in, properties);
            if (ruleExecutionSet == null) {
                throw new RuleConfigurationException("The newly created rule execution set is null ");
            }
            bindUri = ruleExecutionSet.getName();
            ruleAdministrator.registerRuleExecutionSet(bindUri, ruleExecutionSet, registrationProperties);
            this.ruleExecutionSetURI = ruleExecutionSet.getName();
            return bindUri;
        } catch (RuleExecutionSetCreateException e) {
            throw new RuleConfigurationException("Error was occurred when creating" +
                    " the RuleExecutionSet", e);
        } catch (IOException e) {
            throw new RuleConfigurationException("Error was occurred when getting an input stream" +
                    " from provided rule script", e);
        } catch (RuleExecutionSetRegisterException e) {
            throw new RuleConfigurationException("Error was occurred when trying to registering " +
                    "the RuleExecutionSet with Uri " + bindUri, e);
        } catch (Exception e) {
            throw new RuleConfigurationException("Unknown Error was occurred when trying to " +
                    "registering the RuleExecutionSet with Uri " + bindUri, e);
        }
    }

    public void setDefaultPropertiesProvider(
            DefaultPropertiesProvider defaultPropertiesProvider) {
        this.defaultPropertiesProvider = defaultPropertiesProvider;
    }
}
