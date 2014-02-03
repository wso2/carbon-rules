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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<%

    RuleServiceAdminClient ruleServiceAdminClient = new RuleServiceAdminClient(config.getServletContext(), session);
    RuleService ruleService =
            ruleServiceAdminClient.getRuleServiceDescription(request);


    String serviceName = "";
    String description = "";
    String serviceScope = "";
    //serviceName = (ruleService.getName() == null) ? "" : ruleService.getName();
       // description = (ruleService.getDescription() == null) ? "" : ruleService.getDescription();
      if(!ruleService.isEditable()){
        serviceName = (ruleService.getName() == null) ? "" : ruleService.getName();
        description = (ruleService.getDescription() == null) ? "" : ruleService.getDescription();
        serviceScope = (ruleService.getScope() == null) ? "" : ruleService.getScope();
    }
%>

<fmt:bundle basename="org.wso2.carbon.rule.ws.ui.i18n.Resources">
    <carbon:jsi18n
            resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.JSResources"
            request="<%=request%>" i18nObjectName="ruleservicejsi18n"/>
    <carbon:breadcrumb
            label="step1.msg"
            resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.Resources"
            topPage="false"
            request="<%=request%>"/>
    <script type="text/javascript">
        function validateStep1() {
            var serviceName = document.getElementById('ruleServiceName').value;
            if (serviceName == '') {
                CARBON.showWarningDialog('<fmt:message key="service.name.empty"/>');
                return false;
            }
            var reWhiteSpace = new RegExp("^[a-zA-Z0-9_]+$");
            // Check for white space
            if (!reWhiteSpace.test(serviceName)) {
                CARBON.showWarningDialog('<fmt:message key="service.name.validate"/>');
                return false;
            }
            return true;
        }
    </script>

    <div id="middle">
        <h2>
            <h2><fmt:message key="step1.msg"/></h2>
        </h2>

        <div id="workArea">
            <form method="post" action="rule_service_wizard_step2.jsp" name="dataForm"
                  onsubmit="return validateStep1();">
                <table class="styledLeft">
                    <thead>
                    <tr>
                        <th><fmt:message key="step1.msg"/></th>
                    </tr>
                    </thead>
                    <tr>
                        <td class="formRaw">
                            <table class="normal">
                                <tr>
                                    <td><fmt:message key="service.name"/><font color="red">*</font>
                                    </td>
                                    <td><% if (!ruleService.isEditable()) { %>
                                        <input class="longInput" type="text" name="ruleServiceName"
                                               id="ruleServiceName"
                                               value="<%=serviceName%>" readonly="readonly"/>
                                        <input type="hidden" id="ruleServiceNameHidden"
                                               name="ruleServiceNameHidden"
                                               value="<%=serviceName%>"/>
                                        <% } else { %>
                                        <input class="longInput" type="text" name="ruleServiceName"
                                               id="ruleServiceName"
                                               value="<%=serviceName%>"/>
                                        <% } %>
                                    </td>
                                </tr>
                                <tr>
                                    <td><fmt:message key="service.tns"/>
                                    </td>
                                    <td>
                                        <input class="longInput" type="text" name="ruleServiceTNS"
                                               id="ruleServiceTNS"
                                               value="<%=ruleService.getTargetNamespace()%>"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td><fmt:message key="service.description"/></td>
                                    <td><textarea cols="40" rows="5" name="description"><%=description%></textarea></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td class="formRaw">
                            <table class="normal">
                                <td><fmt:message key="service.scope"/>
                                </td>
                                <td>
                                    <select id="ruleServiceScope" name="ruleServiceScope"
                                           >
                                         <%
                            if ("tpSession".equals(serviceScope)) {
                        %>
                                        <option id="request" value="request" >
                                            <fmt:message key="service.scope.request"/>
                                        </option>
                                        <option id="application" value="application">
                                            <fmt:message key="service.scope.application"/>
                                        </option>
                                        <option id="soapSession" value="soapSession">
                                            <fmt:message key="service.scope.soapS"/>
                                        </option>
                                        <option id="tpSession" value="tpSession" selected="true">
                                            <fmt:message key="service.scope.tps"/>
                                        </option>
                                           <% } else if("application".equals(serviceScope)){

                                          %>
                                         <option id="request" value="request" >
                                            <fmt:message key="service.scope.request"/>
                                        </option>
                                        <option id="application" value="application" selected="true">
                                            <fmt:message key="service.scope.application"/>
                                        </option>
                                        <option id="soapSession" value="soapSession">
                                            <fmt:message key="service.scope.soapS"/>
                                        </option>
                                        <option id="tpSession" value="tpSession">
                                            <fmt:message key="service.scope.tps"/>
                                        </option>
                                           <% } else if("soapSession".equals(serviceScope)){
                                           %>
                                         <option id="request" value="request" >
                                            <fmt:message key="service.scope.request"/>
                                        </option>
                                        <option id="application" value="application">
                                            <fmt:message key="service.scope.application"/>
                                        </option>
                                        <option id="soapSession" value="soapSession" selected="true">
                                            <fmt:message key="service.scope.soapS"/>
                                        </option>
                                        <option id="tpSession" value="tpSession">
                                            <fmt:message key="service.scope.tps"/>
                                        </option>
                                           <% } else {
                                           %>
                                         <option id="request" value="request" selected="true">
                                            <fmt:message key="service.scope.request"/>
                                        </option>
                                        <option id="application" value="application">
                                            <fmt:message key="service.scope.application"/>
                                        </option>
                                        <option id="soapSession" value="soapSession">
                                            <fmt:message key="service.scope.soapS"/>
                                        </option>
                                        <option id="tpSession" value="tpSession" >
                                            <fmt:message key="service.scope.tps"/>
                                        </option>
                                         <% }
                                           %>

                                    </select>

                    </select>
                                </td>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td class="buttonRow">
                            <input type="hidden" id="stepID" name="stepID" value="step1"/>
                            <input class="button" type="submit"
                                   value="<fmt:message key="next"/> >"/>
                            <input class="button" type="button" value="<fmt:message key="cancel"/>"
                                   onclick="location.href = 'cancel_handler.jsp'"/>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</fmt:bundle>
