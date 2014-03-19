<!--
~ Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
~
~ WSO2 Inc. licenses this file to you under the Apache License,
~ Version 2.0 (the "License"); you may not use this file except
~ in compliance with the License.
~ You may obtain a copy of the License at
~
~ http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing,
~ software distributed under the License is distributed on an
~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
~ KIND, either express or implied. See the License for the
~ specific language governing permissions and limitations
~ under the License.
-->
<%@ page import="org.wso2.carbon.rule.ws.ui.ns.NameSpacesInformationRepository" %>
<%@ page import="org.wso2.carbon.rule.ws.ui.wizard.RuleServiceAdminClient" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.rule.common.RuleService" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<%
    try {
        RuleServiceAdminClient ruleServiceAdminClient =
                new RuleServiceAdminClient(config.getServletContext(), session);
        RuleService ruleService =
                ruleServiceAdminClient.getRuleServiceDescription(request);
        ruleServiceAdminClient.saveService(ruleService);
        String msg = "Service was saved successfully : Service Name : " +
                ruleService.getName();
        CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.INFO, request,
                response, "../service-mgt/index.jsp");

        session.removeAttribute(RuleServiceAdminClient.RULE_SERVIE);
        session.removeAttribute(RuleServiceAdminClient.FACTS);
        session.removeAttribute(RuleServiceAdminClient.SCRIPTS);
        session.removeAttribute(NameSpacesInformationRepository.NAMESPACES_INFORMATION_REPOSITORY);
    } catch (Exception e) {
        String msg = "Error saving rule service : " + e.getMessage();
        CarbonUIMessage.sendCarbonUIMessage(msg, CarbonUIMessage.ERROR, request,
                response, "../admin/error.jsp");
    } finally {
        session.removeAttribute(RuleServiceAdminClient.RULE_SERVIE);
        session.removeAttribute(RuleServiceAdminClient.FACTS);
        session.removeAttribute(NameSpacesInformationRepository.NAMESPACES_INFORMATION_REPOSITORY);
    }
%>

<script type="text/javascript">
    location.href = "../service-mgt/index.jsp?region=region1&item=services_list_menu";
</script>

