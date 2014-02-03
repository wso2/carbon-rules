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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>


<fmt:bundle basename="org.wso2.carbon.rule.ws.ui.i18n.Resources">
    <carbon:jsi18n
            resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.JSResources"
            request="<%=request%>" i18nObjectName="ruleservicejsi18n"/>
    <carbon:breadcrumb label="upload.ruleservice"
                       resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.Resources"
                       topPage="true" request="<%=request%>"/>
    <script type="text/javascript">
        function validate() {
            var fileName = document.ruleUpload.ruleFilename.value;
            if (fileName == '') {
                CARBON.showErrorDialog('<fmt:message key="select.rule.service"/>');
            } else if (fileName.lastIndexOf(".aar") == -1 && fileName.lastIndexOf(".rsl") == -1) {
                CARBON.showErrorDialog('<fmt:message key="select.rule.file"/>');
            } else {
                document.ruleUpload.submit();
            }
        }
    </script>

    <div id="middle">
        <h2><fmt:message key="upload.ruleservice"/></h2>

        <div id="workArea">
            <form method="post" name="ruleUpload" action="../../fileupload/rule"
                  enctype="multipart/form-data" target="_self">
                <table class="styledLeft">
                    <thead>
                    <tr>
                        <th colspan="2"><fmt:message key="upload.ruleservice"/> (.aar or .rsl)</th>
                    </tr>
                    </thead>
                    <tr>
                        <td class="formRow">
                            <table class="normal">
                                <tr>
                                    <td>
                                        <label><fmt:message key="path.to.ruleservice.config"/>
                                            (.aar or .rsl) :</label>
                                    </td>
                                    <td>
                                        <input type="file" id="ruleFilename" name="ruleFilename"
                                               size="75"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td class="buttonRow">
                            <input name="upload" type="button" class="button"
                                   value=" <fmt:message key="upload"/> "
                                   onclick="validate();"/>
                            <input type="button" class="button"
                                   onclick="location.href = '../service-mgt/index.jsp'"
                                   value=" <fmt:message key="cancel"/> "/>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</fmt:bundle>