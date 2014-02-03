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

import org.wso2.carbon.rule.common.exception.RuleRuntimeException;

import java.util.List;

/**
 * This interface abstracts the rule session concept. Backend can create a session according to the type requested.
 * Then session can execute the different fact sets.
 */
public interface Session {

    /**
     * apply the rules to the given sent of facts and send the result facts back.
     * @param facts  - list of facts given as POJOs to be executed against backend runtime
     * @return   - list of facts after applying the rules
     * @throws RuleRuntimeException - if there is a problem occurs
     */
    public List execute(List facts) throws RuleRuntimeException;

    /**
     *
     * @throws RuleRuntimeException - if there is a problem occurs
     */
    public void destroy() throws RuleRuntimeException;
}
