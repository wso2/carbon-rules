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
<%@ page import="org.wso2.carbon.rule.ws.ui.wizard.RuleServiceAdminClient" %>
<%@ page import="org.wso2.carbon.rule.ws.ui.wizard.RuleServiceManagementHelper" %>
<%@ page import="org.wso2.carbon.rule.common.RuleService" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<script type="text/javascript" src="js/rule-services.js"></script>
<%
    //    String[] classes = (String[]) session.getAttribute(RuleServiceAdminClient.FACTS);
    RuleServiceAdminClient ruleServiceAdminClient =
            new RuleServiceAdminClient(config.getServletContext(), session);
    RuleService ruleService =
            ruleServiceAdminClient.getRuleServiceDescription(request);

    RuleServiceManagementHelper.saveStep2(ruleService, request);
    String[] classes = ruleServiceAdminClient.getAllFacts(ruleService, session);
    ArrayList<String> factArchiveList = (ArrayList<String>) session.getAttribute(RuleServiceAdminClient.FACTS);
    if(factArchiveList == null){
        factArchiveList  = ruleServiceAdminClient.getFactArchiveList(ruleService,session);
        session.setAttribute(RuleServiceAdminClient.FACTS, factArchiveList);
    }

    int classesCount = 0;
    if (classes != null && classes.length != 0 && classes.length > 1) {
        classesCount = classes.length; // Check the reason for (classes.length - 1)
    }
    boolean factAdded = classesCount != 0;
%>

<fmt:bundle basename="org.wso2.carbon.rule.ws.ui.i18n.Resources">
    <carbon:jsi18n
            resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.JSResources"
            request="<%=request%>" i18nObjectName="ruleservicejsi18n"/>
    <carbon:breadcrumb label="upload.ruleservice.facts"
                       resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.Resources"
                       topPage="true" request="<%=request%>"/>

    <script type="text/javascript">
        function validate() {
            var fileName = document.factUpload.factFilename.value;
            if (fileName == '') {
                CARBON.showErrorDialog('<fmt:message key="select.fact"/>');
            } else if (fileName.lastIndexOf(".jar") == -1) {
                CARBON.showErrorDialog('<fmt:message key="select.fact.file"/>');
            } else {
                document.factUpload.submit();
            }
        }
    </script>

    <div id="middle">
        <h2><fmt:message key="upload.ruleservice.facts"/></h2>

        <div id="workArea">

            <table class="styledLeft">
                <thead>
                <tr>
                    <th colspan="2"><fmt:message key="upload.ruleservice.facts"/> (.jar)</th>
                </tr>
                </thead>
                <tr>
                    <td class="formRow">
                        <form method="post" name="factUpload"
                              action="../../fileupload/facts"
                              enctype="multipart/form-data" target="_self" style="margin-bottom: 0">
                            <table class="normal">
                                <tr>
                                    <td>
                                        <label><fmt:message key="path.to.ruleservice.facts"/>
                                            (.jar) :</label>
                                    </td>
                                    <td>
                                        <input type="file" id="factFilename" name="factFilename"
                                               size="75"/>
                                    </td>
                                    <td class="buttonRow">
                                        <input type="hidden"
                                               value="<%=ruleService.getName()%>"
                                               name="ruleServiceName"/>
                                        <input name="upload" type="button" class="button"
                                               value=" <fmt:message key="upload"/> "
                                               onclick="validate();"/>
                                    </td>
                                </tr>
                            </table>
                        </form>
                    </td>
                </tr>
                <tr>

                    <td>
                        <table id="factArchiveListTable" class="normal">
                                <%
                                    if(factArchiveList != null && !factArchiveList.isEmpty()) {
                                        for(String factArchieName : factArchiveList){

                                %>
                            <tr id="<%=factArchieName%>">
                            <td>
                                    <label><%=factArchieName%></label>

                                </td>
                                <td><a href="#" href="#" class="delete-icon-link" style="padding-left:40px"
                                       onclick="deleteFactArchives('<%=factArchieName%>')"><fmt:message
                                        key="delete"/></a></td>
                                <% } } %>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                <% if (factAdded) {
                %>
                    <td class="formRow">
                        <table class="normal">
                            <tr>
                                <td>
                                    <label><fmt:message key="uploaded.facts"/> <%=classesCount%>
                                    </label>
                                </td>
                                    <%--<td>--%>
                                    <%--<% String prefix = "";--%>
                                    <%--for (String fact : classes) {--%>
                                    <%--if (fact != null && !"".equals(fact)) {--%>
                                    <%--%><%=prefix + fact%><%--%>
                                    <%--prefix = ",";--%>
                                    <%--}--%>
                                    <%--}--%>
                                    <%--%>--%>
                                    <%--</td>--%>
                            </tr>
                        </table>
                    </td>
                </tr>

                <% } %>
                <tr>
                    <td class="buttonRow">
                        <form method="post" action="rule_service_wizard_step4.jsp" name="dataForm">
                            <input type="hidden" id="stepID" name="stepID" value="step3"/>
                            <input class="button" type="button" value="< <fmt:message key="back"/>"
                                   onclick="location.href = 'rule_service_wizard_step2.jsp'"/>
                            <input class="button" type="submit"
                                   value="<fmt:message key="next"/> >"/>
                            <input class="button" type="button" value="<fmt:message key="cancel"/>"
                                   onclick="location.href = 'cancel_handler.jsp'"/>
                        </form>
                    </td>
                </tr>
            </table>

        </div>
    </div>
</fmt:bundle>