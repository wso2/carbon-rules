/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.rule.ws.receiver;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.service.Lifecycle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.rule.common.exception.RuleRuntimeException;
import org.wso2.carbon.rule.common.util.Constants;
import org.wso2.carbon.rule.kernel.engine.RuleSession;


public class RuleSessionLifecycle implements Lifecycle {

    private static Log log = LogFactory.getLog(RuleSessionLifecycle.class);

    @Override
    public void init(ServiceContext serviceContext) throws AxisFault {
    }

    @Override
    public void destroy(ServiceContext serviceContext) {
        if (log.isDebugEnabled()) {
            log.debug("Destroying rule session bound to transport session");
        }
        //get stored rule session
        RuleSession ruleSession = (RuleSession) serviceContext.getProperty(Constants.RULE_SESSION_OBJECT);

        //dispose it, if exists
        if (ruleSession != null) {
            try {
                ruleSession.destroy();
                serviceContext.removeProperty(Constants.RULE_SESSION_OBJECT);

            } catch (RuleRuntimeException e) {
                log.error("Error occurred while destroying rule session", e);
            }
        }
    }
}
