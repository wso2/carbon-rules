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
<%@ page import="org.wso2.carbon.rule.ws.ui.wizard.RuleServiceManagementHelper" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.wso2.carbon.rule.common.RuleService" %>
<%@ page import="org.wso2.carbon.rule.common.Operation" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<script type="text/javascript" src="js/rule-services.js"></script>
<%
    RuleServiceAdminClient ruleServiceAdminClient =
            new RuleServiceAdminClient(config.getServletContext(), session);
    RuleService ruleService =
            ruleServiceAdminClient.getRuleServiceDescription(request);
    RuleServiceManagementHelper.saveStep5(ruleService, request);

    Iterator<Operation> iterator = ruleService.getOperations().iterator();
%>

<fmt:bundle basename="org.wso2.carbon.rule.ws.ui.i18n.Resources">
    <carbon:jsi18n
            resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.JSResources"
            request="<%=request%>" i18nObjectName="ruleservicejsi18n"/>
    <carbon:breadcrumb
            label="step4.msg"
            resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.Resources"
            topPage="false"
            request="<%=request%>"/>
    <script type="text/javascript">
        function validate() {
            var opCount = 0;
            if (document.getElementById('opCount') != null) {
                opCount = parseInt(document.getElementById('opCount').value);
            }
            if (opCount <= 0) {
                CARBON.showErrorDialog('<fmt:message key="operations.empty"/>');
                return false;
            }
            document.dataForm.submit();
            return true;
        }
    </script>

    <div id="middle">
        <h2>
            <fmt:message key="step4.msg"/>
        </h2>

        <div id="workArea">

            <form method="post" action="rule_service_wizard_end.jsp" name="dataForm">
                <table class="styledLeft">
                    <thead>
                    <tr>
                        <th><fmt:message key="step4.msg"/></th>
                    </tr>
                    </thead>
                    <tr>
                        <td class="formRaw">
                            <table class="styledLeft" id="operationtable">
                                <%
                                    if (ruleService.getOperations().size() > 0) {
                                %>
                                <thead>
                                <tr>
                                    <th width="20%"><fmt:message key="name"/></th>
                                    <th width="60%"><fmt:message key="actions"/></th>
                                </tr>
                                </thead>
                                <tbody id="operationbody">
                                <% int j = 0;
                                    while (iterator.hasNext()) {
                                        Operation operation = iterator.next();
                                        if (operation != null) {
                                            String name = operation.getName();
                                %>
                                <tr id="operationRaw<%=j%>">
                                    <td><input name="operationName" id="operationName"
                                               value="<%=name%>"
                                               type="text" disabled="disabled"/></td>
                                    <td>
                                            <%--<input type="hidden" id="<%=name%>" name="opname" value="<%=name%>"/>--%>
                                        <a class="edit-icon-link"
                                           href="rule_service_wizard_step5.jsp?opname=<%=name%>"><fmt:message
                                                key="edit"/></a>
                                        <a class="delete-icon-link"
                                           onclick="deleteOperation('<%=name%>','<%=j%>');"
                                           href="#"><fmt:message
                                                key="delete"/></a>
                                    </td>
                                </tr>
                                <%
                                        }
                                        j++;
                                    }
                                %>
                                <input type="hidden" name="opCount" id="opCount" value="<%=j%>"/>
                                <%
                                    }
                                %>
                                <tr>
                                    <td colspan="2">
                                        <a class="add-icon-link"
                                           href="rule_service_wizard_step5.jsp"><fmt:message
                                                key="add.new.operation"/></a>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="buttonRow">
                                        <input type="hidden" id="stepID" name="stepID"
                                               value="step4"/>
                                        <input class="button" type="button"
                                               value="< <fmt:message key="back"/>"
                                               onclick="location.href = 'rule_service_wizard_step3.jsp'"/>
                                        <input class="button" type="button" onclick="validate()"
                                               value="<fmt:message key="finish"/>"/>
                                        <input class="button" type="button"
                                               value="<fmt:message key="cancel"/>"
                                               onclick="location.href = 'cancel_handler.jsp'"/>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </td>
                </table>
            </form>
        </div>
    </div>
</fmt:bundle>
