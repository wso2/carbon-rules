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
<%@ page import="org.wso2.carbon.rule.common.Rule" %>
<%@ page import="org.wso2.carbon.rule.common.RuleService" %>
<%@ page import="org.wso2.carbon.rule.common.RuleSet" %>
<%@ page import="org.wso2.carbon.rule.common.util.Constants" %>
<%@ page import="org.wso2.carbon.rule.ws.ui.wizard.RuleServiceAdminClient" %>
<%@ page import="org.wso2.carbon.rule.ws.ui.wizard.RuleServiceManagementHelper" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<script type="text/javascript" src="js/rule-services.js"></script>
<script type="text/javascript" src="js/ns-editor.js"></script>
<jsp:include page="../resources/resources-i18n-ajaxprocessor.jsp"/>
<script type="text/javascript" src="../yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../yui/build/container/container_core-min.js"></script>
<script type="text/javascript" src="../yui/build/yahoo/yahoo-min.js"></script>
<script type="text/javascript" src="../yui/build/event/event-min.js"></script>
<script type="text/javascript" src="../yui/build/connection/connection-min.js"></script>
<script type="text/javascript" src="../yui/build/menu/menu-min.js"></script>
<script type="text/javascript" src="../ajax/js/prototype.js"></script>
<script type="text/javascript" src="../resources/js/resource_util.js"></script>
<link rel="stylesheet" type="text/css" href="../resources/css/registry.css"/>
<%
    RuleServiceAdminClient ruleServiceAdminClient =
            new RuleServiceAdminClient(config.getServletContext(), session);
    RuleService serviceDescription =
            ruleServiceAdminClient.getRuleServiceDescription(request);
    RuleServiceManagementHelper.saveStep1(serviceDescription, request);
    String ruleValueZero = "";


    boolean isInLined = true;
    boolean isPath = false;
    boolean isRegistry = false;
    boolean isURL = false;


    Map<String, String> scriptList = (Map<String, String>) session.getAttribute(RuleServiceAdminClient.SCRIPTS);

    if (serviceDescription != null && scriptList == null) {
        RuleSet ruleSet = serviceDescription.getRuleSet();
        if (ruleSet != null) {
            scriptList = new HashMap<String, String>();
            if (ruleSet == null) {
                ruleSet = new RuleSet();
                serviceDescription.setRuleSet(ruleSet);
            }


            List<Rule> ruleList = ruleSet.getRules();
            String sourceType = ruleList.get(0).getSourceType();
            ruleValueZero = ruleList.get(0).getValue();
            for (Rule rule : ruleList) {
                String scriptValue = rule.getValue().toString();
                String scriptType = rule.getSourceType().toString();
                scriptList.put(scriptValue, scriptType);

            }
            if (!sourceType.equals("inline")) {
                isInLined = false;
                session.setAttribute(RuleServiceAdminClient.SCRIPTS, scriptList);

                if (sourceType.equals("file"))
                    isPath = true;
                else if (sourceType.equals("registry"))
                    isRegistry = true;
                else if (sourceType.equals("url"))
                    isURL = true;
                else
                    isInLined = true;
            }
        }


    }


    String ruleScriptListDisplay = "display:none;";
    String scriptListHeaderDisplay = "display:none;";
    if (scriptList != null && !scriptList.isEmpty()) {
        isInLined = false;
        ruleScriptListDisplay = "";
        scriptListHeaderDisplay = "";
        String tempType = "inline";

        List<String> tempPaths = new ArrayList<String>(scriptList.keySet());
        for (String scriptName : tempPaths) {
            String sourceType = scriptList.get(scriptName);
            if (!sourceType.equals("inline")) {
                tempType = sourceType;
                break;
            }


        }
        if (tempType.equals("file"))
            isPath = true;
        else if (tempType.equals("registry"))
            isRegistry = true;
        else if (tempType.equals("url"))
            isURL = true;
        else {
            isInLined = true;
            ruleScriptListDisplay = "display:none;";
            scriptListHeaderDisplay = "display:none;";

        }


    }
    ruleValueZero = isInLined ? ruleValueZero : "";

    String ruleSourceDisplay = isInLined ? "" : "display:none;";
    String ruleUploadDisplay = isPath ? "" : "display:none;";
    String ruleKeyDisplay = isRegistry ? "" : "display:none;";
    String ruleURLDisplay = isURL ? "" : "display:none;";
    String rulesetCreationDisplay = "";

%>
<fmt:bundle basename="org.wso2.carbon.rule.ws.ui.i18n.Resources">
<carbon:jsi18n
        resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.JSResources"
        request="<%=request%>" i18nObjectName="ruleservicejsi18n"/>
<carbon:breadcrumb
        label="step2.msg"
        resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.Resources"
        topPage="false"
        request="<%=request%>"/>
<style type="text/css">
    .scriptListNameWrapper {
        width: 700px;
        overflow: hidden;
    }

    a.fact-selector-icon-link {
        background-image: url("../rule_service/images/facts-selector.gif");
        background-position: left top;
        background-repeat: no-repeat;
        float: left;
        height: 17px;
        line-height: 17px;
        margin-bottom: 3px;
        margin-left: 10px;
        margin-top: 5px;
        padding-left: 20px;
        position: relative;
        white-space: nowrap;
    }

    .bordered-table {
        border: 1px solid #CCCCCC;
    }

    td.hiddenMargin {
        border: none !important;
    }
</style>
<script type="text/javascript">
    function validateRuleFileUpload() {
        var fileName = document.ruleScriptUpload.ruleFilename.value;
        if (fileName == '') {
            CARBON.showErrorDialog('<fmt:message key="select.rule.script"/>');
        } else if (fileName.lastIndexOf(".drl") == -1) {
            CARBON.showErrorDialog('<fmt:message key="select.valid.rule.script"/>');
        } else {
            document.ruleScriptUpload.submit();
        }
    }

    function validate() {
        var value;
        if (document.getElementById('ruleScriptTypeinlined').checked) {
            document.getElementById('ruleSourceType').value = "inlined";
            value = document.getElementById("ruleSourceInlined").value;
            value = value.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
            if (value == '') {
                CARBON.showErrorDialog('<fmt:message key="inlined.script.empty"/>');
                return false;
            }
            deleteAllRuleFiles();


        } else {
            document.getElementById('ruleSourceType').value = "key";
            var ruleScriptCount = document.getElementById("ruleScriptCount");
            var ruleScriptHiddenCount = document.getElementById("ruleScriptHiddenCount");
            var j = ruleScriptHiddenCount.value;
            var i = ruleScriptCount.value;
            if (i == 0 || j >= i) {
                CARBON.showErrorDialog('<fmt:message key="upload.script.empty"/>');
                return false;
                //TODO add proper message

            }


        }
        document.dataForm.submit();
        return true;
    }

    function onRegistryResourceSelect() {
        var ruleSourceKey = document.getElementById("ruleSourceKey").value;
        if (ruleSourceKey != undefined && ruleSourceKey != null) {
            document.getElementById("registryResourcePath").value = ruleSourceKey;
            var index = ruleSourceKey.indexOf("/_system/governance/");
            var configIndex = ruleSourceKey.indexOf("/_system/config/");
            if (index >= 0) {
                document.getElementById("ruleSourceKey").value = "gov:" +
                        ruleSourceKey.substring("/_system/governance/".length);
            } else if (configIndex >= 0) {
                document.getElementById("ruleSourceKey").value = "conf:" +
                        ruleSourceKey.substring("/_system/config/".length);
            }


        }
    }

</script>

<div id="middle">
<h2>
    <h2><fmt:message key="step2.msg"/></h2>
</h2>

<div id="workArea">
<table class="styledLeft">
    <thead>
    <tr>
        <th><fmt:message key="step2.msg"/></th>
    </tr>
    </thead>
    <tr>
        <td class="formRaw">
            <table class="normal">
                <tr>
                    <td><fmt:message key="rule.script.as"/><font color="red">*</font>
                    </td>
                    <td>
                        <%
                            if (isInLined) {
                        %>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeinlined"
                               value="inlined"
                               onclick="setRuleScriptType('inlined');"
                               checked="checked"/>
                        <fmt:message key="inlined"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypekey"
                               value="key"
                               onclick="setRuleScriptType('key');"/>
                        <fmt:message key="reg.key"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeUpload"
                               value="upload"
                               onclick="setRuleScriptType('upload');"/>
                        <fmt:message key="reg.upload"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeurl"
                               value="url"
                               onclick="setRuleScriptType('url');"/>
                        <fmt:message key="reg.url"/>
                        <% } else if (isRegistry) { %>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeinlined"
                               value="inlined"
                               onclick="setRuleScriptType('inlined');"/>
                        <fmt:message key="inlined"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypekey"
                               value="key"
                               onclick="setRuleScriptType('key');"
                               checked="checked"/>
                        <fmt:message key="reg.key"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeUpload"
                               value="upload"
                               onclick="setRuleScriptType('upload');"/>
                        <fmt:message key="reg.upload"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeurl"
                               value="url"
                               onclick="setRuleScriptType('url');"/>
                        <fmt:message key="reg.url"/>


                        <% } else if (isPath) { %>

                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeinlined"
                               value="inlined"
                               onclick="setRuleScriptType('inlined');"/>
                        <fmt:message key="inlined"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypekey"
                               value="key"
                               onclick="setRuleScriptType('key');"/>
                        <fmt:message key="reg.key"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeUpload"
                               value="upload"
                               onclick="setRuleScriptType('upload');"
                               checked="checked"/>
                        <fmt:message key="reg.upload"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeurl"
                               value="url"
                               onclick="setRuleScriptType('url');"/>
                        <fmt:message key="reg.url"/>


                        <%} else { %>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeinlined"
                               value="inlined"
                               onclick="setRuleScriptType('inlined');"/>
                        <fmt:message key="inlined"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypekey"
                               value="key"
                               onclick="setRuleScriptType('key');"/>
                        <fmt:message key="reg.key"/>
                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeUpload"
                               value="upload"
                               onclick="setRuleScriptType('upload');"/>
                        <fmt:message key="reg.upload"/>

                        <input type="radio" name="ruleScriptType"
                               id="ruleScriptTypeurl"
                               value="url"
                               onclick="setRuleScriptType('url');"
                               checked="checked"/>
                        <fmt:message key="reg.url"/>


                        <%} %>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>

<table class="styledLeft bordered-table">
<form method="post" name="ruleScriptUpload"
      action="../../fileupload/facts"
      enctype="multipart/form-data" target="_self">
    <tr id="ruleScriptUploadTR" style="<%=ruleUploadDisplay%>">
        <td class="hiddenMargin">
            <table class="normal">
                <tr>
                    <td><fmt:message key="rule.source.upload"/></td>
                    <td>
                        <input type="file" id="ruleFilename"
                               name="ruleFilename"
                               size="75"/>
                    </td>
                    <td class="buttonRow">
                        <input type="hidden"
                               value="<%=serviceDescription.getName()%>"
                               name="ruleServiceName"/>
                        <input name="upload" type="button"
                               class="button"
                               value="<fmt:message key="upload"/> "
                               onclick="validateRuleFileUpload();"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>

</form>
<form method="post" action="rule_service_wizard_step3.jsp" name="dataForm">
    <tr id="ruleScriptKeyTR" style="<%=ruleKeyDisplay%>">
        <td class="hiddenMargin">
            <table class="normal">
                <tr>
                    <td><fmt:message key="rule.source.key"/></td>
                    <td>
                        <input class="longInput" type="text" name="ruleSourceKey"
                               id="ruleSourceKey"/>
                    </td>
                    <td>
                        <a href="#registryBrowserLink"
                           class="registry-picker-icon-link"
                           onclick="showResourceTree('ruleSourceKey',onRegistryResourceSelect,'/_system/config')"><fmt:message
                                key="registry.config"/></a>
                    </td>
                    <td>
                        <a href="#registryBrowserLink"
                           class="registry-picker-icon-link"
                           onclick="showResourceTree('ruleSourceKey',onRegistryResourceSelect,'/_system/governance')"><fmt:message
                                key="registry.governance"/></a>
                    </td>
                    <td class="buttonRow">
                        <input name="upload" type="button"
                               class="button"
                               value="<fmt:message key="add"/> "
                               onclick="addRuleScript('ruleSourceKey', 'registry');"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr id="ruleScriptURLTR" style="<%=ruleURLDisplay%>">
        <td class="hiddenMargin">
            <table class="normal">
                <tr>
                    <td><fmt:message key="rule.source.url"/></td>
                    <td>
                        <input type="text" name="ruleSourceURL"
                               id="ruleSourceURL" size="75"/>
                    </td>
                    <td class="buttonRow">
                        <input name="upload" type="button"
                               class="button"
                               value="<fmt:message key="add"/> "
                               onclick="addRuleScript('ruleSourceURL','url');"/>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr id="ruleScriptSourceTR" style="<%=ruleSourceDisplay%>">
        <td class="hiddenMargin">
            <table class="normal">
                <tr>
                    <td><fmt:message key="rule.source.inlined"/></td>
                    <td><textarea cols="80" rows="15"
                                  name="ruleSourceInlined"
                                  id="ruleSourceInlined"><%=ruleValueZero%>
                    </textarea></td>
                </tr>
            </table>
        </td>
    </tr>

    <tr id="rulesetCreationTR" style="<%=rulesetCreationDisplay%>">
        <td class="hiddenMargin">
            <table class="normal">
                <tr>
                    <td><label><fmt:message
                            key="rule.resource.type"/></label></td>
                    <td>
                        <select id="ruleResouceTypeID" name="ruleResouceType">
                            <option id="regularID" value="<%=Constants.RULE_RESOURCE_TYPE_REGULAR%>">
                                <fmt:message key="rule.resource.type.regular"/>
                            </option>
                            <option id="dtableID" value="<%=Constants.RULE_RESOURCE_TYPE_DTABLE%>">
                                <fmt:message key="rule.resource.type.dtable"/>
                            </option>
                        </select>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr id="ruleScriptList" style="<%=ruleScriptListDisplay%>">
        <td class="hiddenMargin">
            <table class="normal">
                <tr>
                    <td>
                        <table id="ruleScriptListTable" class="styledInner" style="width:120%">
                            <thead>
                            <tr id="ruleScriptHeader" style="<%=scriptListHeaderDisplay%>">
                                <th width="10%">Script Type</th>
                                <th width="80%">Source</th>
                                <th width="10%">Action</th>
                            </tr>
                            </thead>

                            <tbody id="ruleScriptListTBody">

                            <%
                                int i = 0;
                                List<String> paths = null;
                                if (scriptList != null && !scriptList.isEmpty()) {
                                    paths = new ArrayList<String>(scriptList.keySet());
                                }

                                if (paths != null && !paths.isEmpty()) {
                                    for (String scriptName : paths) {
                                        String sourceType = scriptList.get(scriptName);
                                        if (!sourceType.equals("inline")) {

                            %>
                            <tr id="ruleScriptListRaw<%=i%>">
                                <td>
                                    <%=sourceType%>
                                </td>
                                <td>
                                    <div class="scriptListNameWrapper">
                                        <label><%=scriptName%>
                                        </label>
                                    </div>
                                    <input type="hidden" name="ruleScriptName<%=i%>" id="ruleScriptName<%=i%>"
                                           value="<%=scriptName%>"/>
                                    <input type="hidden" name="ruleScriptSource<%=i%>" id="ruleScriptSource<%=i%>"
                                           value="<%=sourceType%>"/>
                                </td>

                                <td><a href="#" href="#" class="delete-icon-link" style="padding-left:40px"
                                       onclick="deleteScript('ruleScriptList','<%=i%>','<%=sourceType%>','<%=scriptName%>')"><fmt:message
                                        key="delete"/></a></td>
                            </tr>
                            <%
                                            i++;
                                        }
                                    }

                                } %>
                            <input type="hidden" name="ruleScriptCount" id="ruleScriptCount"
                                   value="<%=i%>"/>
                            <input type="hidden" name="ruleScriptHiddenCount" id="ruleScriptHiddenCount"
                                   value="<%=0%>"/>
                            </tbody>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td>
            <table class="normal">
                <tr>
                    <td class="buttonRow">
                        <input type="hidden" id="stepID" name="stepID" value="step2"/>
                        <input type="hidden" id="ruleSourceType" name="ruleSourceType" value="inline"/>
                        <input type="hidden" id="registryResourcePath" name="registryResourcePath" value="">
                        <input type="hidden" id="ruleResourceURL" name="ruleResourceURL" value="">
                        <input class="button" type="button" value="< <fmt:message key="back"/>"
                               onclick="location.href = 'rule_service_wizard_step1.jsp'"/>
                        <input class="button" type="button" onclick="validate()"
                               value="<fmt:message key="next"/> >"/>
                        <input class="button" type="button" value="<fmt:message key="cancel"/>"
                               onclick="location.href = 'cancel_handler.jsp'"/>

                    </td>
                </tr>
            </table>
        </td>
    </tr>
</form>
</table>


</div>
<a name="propertyEditorLink"></a>

<div id="propertyEditor" style="display:none;"></div>

<a name="registryBrowserLink"></a>

<div id="registryBrowser" style="display:none;"></div>

</div>

</fmt:bundle>
