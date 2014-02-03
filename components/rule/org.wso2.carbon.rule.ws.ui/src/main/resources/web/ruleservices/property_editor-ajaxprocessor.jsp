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
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="org.wso2.carbon.rule.common.RuleService" %>
<%@ page import="org.wso2.carbon.rule.common.RuleSet" %>
<%@ page import="org.wso2.carbon.rule.ws.ui.wizard.RuleServiceAdminClient" %>
<%--<%@ page import="org.wso2.carbon.rulecep.commons.descriptions.PropertyDescription" %>
<%@ page import="org.wso2.carbon.rulecep.commons.descriptions.rule.RuleSetDescription" %>
<%@ page import="org.wso2.carbon.rulecep.commons.descriptions.rule.service.RuleServiceExtensionDescription" %>
<%@ page import="org.wso2.carbon.rulecep.commons.descriptions.service.ServiceDescription" %>--%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<script type="text/javascript" src="js/rule-services.js"></script>
<script type="text/javascript" src="js/ns-editor.js"></script>
<%
    RuleServiceAdminClient ruleServiceAdminClient =
            new RuleServiceAdminClient(config.getServletContext(), session);
/*    ServiceDescription serviceDescription =
            ruleServiceAdminClient.getRuleServiceDescription(request);
    RuleServiceExtensionDescription extensionDescription =
            (RuleServiceExtensionDescription) serviceDescription.getServiceExtensionDescription();
    RuleSetDescription ruleSetDescription = extensionDescription.getRuleSetDescription();
    List<PropertyDescription> properties = ruleSetDescription.getCreationProperties();*/
    RuleService ruleService = ruleServiceAdminClient.getRuleServiceDescription(request);
    RuleSet ruleSet = ruleService.getRuleSet();
    Map<String, String> properties = new HashMap<String, String>();
    if(ruleSet != null){
        properties = ruleSet.getProperties();
    }
    // Set standard HTTP/1.1 no-cache headers.
    response.setHeader("Cache-Control", "no-store, max-age=0, no-cache, must-revalidate");
    // Set IE extended HTTP/1.1 no-cache headers.
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    // Set standard HTTP/1.0 no-cache header.
    response.setHeader("Pragma", "no-cache");

%>

<fmt:bundle basename="org.wso2.carbon.rule.ws.ui.i18n.Resources">
    <carbon:jsi18n
            resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.JSResources"
            request="<%=request%>" i18nObjectName="ruleservicejsi18n"/>

    <div id="propertyEditorContent" style="margin-top:10px;">
        <table class="styledLeft noBorders" cellspacing="0" cellpadding="0" border="0">

            <tbody>
            <tr>
                <td>
                    <div style="margin-top:10px;">
                        <table border="0" cellpadding="0" cellspacing="0" width="600"
                               id="propertyTable"
                               class="styledInner">
                            <thead>
                            <tr>
                                <th width="25%"><fmt:message key="th.name"/></th>
                                <th width="50%"><fmt:message key="th.value"/></th>
                                <th><fmt:message key="actions"/></th>
                            </tr>
                            </thead>

                            <tbody id="propertyTBody">
                            <%

                                int i = 0;
                                if (!properties.isEmpty() && properties != null) {
                                    for (String attName : properties.keySet()) {
                                        if (attName == null) {
                                            continue;
                                        }
                                        String name = attName;
                                        if (name == null || "".equals(name)) {
                                            continue;
                                        }
                                        String value = properties.get(attName);
                                        if (value == null || "".equals(value)) {
                                            continue;
                                        }

                            %>
                            <tr id="propertyTR<%=i%>">
                                <td align="left">
                                    <input type="text" name="name<%=i%>" id="name<%=i%>"
                                           value="<%=name%>"/>
                                </td>
                                <td>
                                    <input id="value<%=i%>" class="longInput" name="value<%=i%>"
                                           type="text" value="<%=value%>"/>

                                </td>
                                <td><a href="#" class="delete-icon-link"
                                       style="padding-left:40px"
                                       onclick="deletePropertyRaw('<%=i%>')"><fmt:message
                                        key="delete"/></a>
                                </td>
                            </tr>
                            <% i++;

                            }
                            }%>
                            <input type="hidden" name="propertyCount" id="propertyCount"
                                   value="<%=i%>"/>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="buttonRow" colspan="3">
                    <input id="addPropertyButton" class="button"
                           name="addPropertyButton"
                           type="button"
                           onclick="addProperty();"
                           href="#"
                           value="<fmt:message key="add"/>"/>
                    <input id="savePropertyButton" class="button" name="savePropertyButton"
                           type="button"
                           onclick="saveProperties(); return false;"
                           href="#"
                           value="<fmt:message key="save"/>"/>
                    <input id="cancelNSButton" class="button" name="cancelNSButton" type="button"
                           onclick="hideEditor(); return false;"
                           href="#"
                           value="<fmt:message key="cancel"/>"/>

                </td>
            </tr>
            </tbody>
        </table>
    </div>
</fmt:bundle>
