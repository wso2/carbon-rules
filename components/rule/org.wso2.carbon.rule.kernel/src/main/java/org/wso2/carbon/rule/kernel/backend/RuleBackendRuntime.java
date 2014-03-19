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

package org.wso2.carbon.rule.kernel.backend;

import org.wso2.carbon.rule.common.RuleSet;
import org.wso2.carbon.rule.common.exception.RuleConfigurationException;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;

/**
 * interface to separate the Back end real rule implementation from the xml processing layer
 * which is in the kernel module.
 */
public interface RuleBackendRuntime {

    /**
     * Adds the rule set to back end runtime. Here the rule is depends on the real rule implementation.
     * @param ruleSet - rule set to be added to runtime
     * @throws RuleConfigurationException - if there are problems with the configuaration
     */
    public void addRuleSet(RuleSet ruleSet) throws RuleConfigurationException;

    /**
     * Some rules engines support different sessions types. For an example State ful and state less. We abstract this
     * concept with session interface.
     * @param type - whether it is a stateful or stateless sesstion
     * @return  - session created
     * @throws RuleRuntimeException - if could not create the session
     */
    public Session createSession(int type) throws RuleRuntimeException;
}
