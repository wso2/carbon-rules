<!--
 ~ Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 ~
 ~ WSO2 Inc. licenses this file to you under the Apache License,
 ~ Version 2.0 (the "License"); you may not use this file except
 ~ in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~    http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 -->
<%@ page import="org.wso2.carbon.rule.ws.ui.wizard.RuleServiceAdminClient" %>
<%@ page import="org.wso2.carbon.rule.common.RuleService" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<%
    RuleServiceAdminClient ruleServiceAdminClient =
            new RuleServiceAdminClient(config.getServletContext(), session);
    RuleService ruleService = ruleServiceAdminClient.getRuleServiceDescription(request);
    String scriptValue = request.getParameter("scriptValue");
    String scriptSourceType = request.getParameter("scriptSourceType");

    if(scriptValue != null && !scriptValue.equals("")){
        Map<String, String> scriptList = (Map<String, String>) session.getAttribute(RuleServiceAdminClient.SCRIPTS);
        if(scriptList == null){
            scriptList = new HashMap<String, String>();
        }
        scriptList.put(scriptValue, scriptSourceType);
        session.setAttribute(RuleServiceAdminClient.SCRIPTS, scriptList);


}

%>