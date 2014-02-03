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
<%@ page import="org.wso2.carbon.rule.ws.ui.ns.NameSpacesRegistrar" %>
<%@ page import="org.wso2.carbon.rule.ws.ui.wizard.RuleServiceAdminClient" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="org.wso2.carbon.rule.common.*" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<script type="text/javascript" src="js/rule-services.js"></script>
<script type="text/javascript" src="js/ns-editor.js"></script>
<%
    RuleServiceAdminClient ruleServiceAdminClient =
            new RuleServiceAdminClient(config.getServletContext(), session);
    RuleService ruleService =
            ruleServiceAdminClient.getRuleServiceDescription(request);
    String opname = request.getParameter("opname");
//    boolean isNew = opname == null || "".equals(opname);
    if (opname == null) {
        opname = "";
    }
%>
<fmt:bundle basename="org.wso2.carbon.rule.ws.ui.i18n.Resources">
<carbon:jsi18n
        resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.JSResources"
        request="<%=request%>" i18nObjectName="ruleservicejsi18n"/>
<carbon:breadcrumb
        label="step5.msg"
        resourceBundle="org.wso2.carbon.rule.ws.ui.i18n.Resources"
        topPage="false"
        request="<%=request%>"/>
<style type="text/css">
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
 </style>
<script type="text/javascript">
    function validateStep5() {
        var serviceName = document.getElementById('operationName').value;
        if (serviceName == '') {
            CARBON.showWarningDialog('<fmt:message key="operation.name.empty"/>');
            return false;
        }
        var reWhiteSpace = new RegExp("^[a-zA-Z0-9_]+$");
        // Check for white space
        if (!reWhiteSpace.test(serviceName)) {
            CARBON.showWarningDialog('<fmt:message key="operation.name.validate"/>');
            return false;
        }
        var returnValue = validateFacts("inputFact");
        if (returnValue) {
            returnValue = validateFacts("outputFact");
        }
        return returnValue;
    }

</script>
<div id="middle">
<h2>
    <fmt:message key="step5.msg"/>
</h2>

<div id="workArea">
<form method="post" action="rule_service_wizard_step4.jsp" name="dataForm"
      onsubmit="return validateStep5();">
<table class="styledLeft">
<thead>
<tr>
    <th><fmt:message key="step5.msg"/></th>
</tr>
</thead>
<tr>
    <td class="formRaw">
        <table class="normal">
            <tr>
                <td><fmt:message key="name"/><font
                        color="red">*</font>
                </td>
                <td>
                    <input type="text" name="operationName" id="operationName"
                           value="<%=opname.trim()%>"/>
                </td>
            </tr>
        </table>
    </td>
</tr>
<%
    Operation operation = ruleService.getOperation(opname);

    List<Fact> facts;
    Input input = null;
    List<Fact> inputFacts = null;
    String inputWrapperName = "input";
    String inputNameSpace = null;
    Output output = null;
    List<Fact> outputFacts = null;
    String outputWrapperName = "output";
    String outputNameSpace = null;

    if(operation == null){
        inputFacts = new ArrayList<Fact>();
        outputFacts = new ArrayList<Fact>();
    }
    else {
        input = operation.getInput();
        if(input != null){
            inputFacts = input.getFacts();
            inputWrapperName = input.getWrapperElementName();
            inputNameSpace = input.getNameSpace();
        }

        output = operation.getOutput();
        if(output != null){
            inputFacts = output.getFacts();
            outputWrapperName = output.getWrapperElementName();
            outputNameSpace = output.getNameSpace();


        }

    }
    String inputTableStyle = inputFacts.isEmpty() ? "display:none;" : "";
    String outputTableStyle = outputFacts.isEmpty() ? "display:none;" : "";

%>


<tr>
    <td><h3 class="mediator"><fmt:message key="input"/></h3></td>
</tr>

<tr>
    <td class="formRaw">
        <table class="normal">
            <tr>
                <td><fmt:message key="wrapper"/>
                </td>
                <td>
                    <input type="text" name="inputWrapperName" id="inputWrapperName"
                           value="<%=inputWrapperName%>"/>
                </td>
                <td><fmt:message key="namespace"/>
                </td>
                <td>
                    <input type="text" name="inputNameSpace" id="inputNameSpace"
                           value="<%=inputNameSpace%>"/>
                </td>
            </tr>
        </table>
    </td>
</tr>




<tr>
    <td class="formRaw">
        <h3 class="mediator"><fmt:message key="facts"/></h3>

        <div style="margin-top:0px;">
            <table id="inputFacttable" style="<%=inputTableStyle%>;"
                   class="styledInner">
                <thead>
                <tr>
                    <th width="10%"><fmt:message key="th.type"/></th>
                    <th width="10%"><fmt:message key="th.selector"/></th>
                    <th width="10%"><fmt:message key="th.name"/></th>
                    <th><fmt:message key="namespaceeditor"/></th>
                    <th><fmt:message key="actions"/></th>
                </tr>
                <tbody id="inputFacttbody">
                <%
                    int k = 0;
                    for (Fact inputProperty : inputFacts) {
                        if (inputProperty != null) {
                            String type = inputProperty.getType();
                            String name = inputProperty.getElementName();
                            if (name == null) {
                                name = "";
                            }
                %>
                <tr id="inputFactRaw<%=k%>">
                    <td>
                        <input class="longInput" name="inputFactType<%=k%>"
                               id="inputFactType<%=k%>" value="<%=type%>"
                               type="text"/>
                    </td>
                    <td><a href="#factEditorLink" class="fact-selector-icon-link"
                           style="padding-left:40px"
                           onclick="showFactEditor('inputFact','<%=k%>')"><fmt:message
                            key="fact.type"/></a></td>

                    <td>
                        <input class="longInput" id="inputFactName<%=k%>"
                               name="inputFactName<%=k%>"
                               type="text" value="<%=name%>"/>
                    </td>
                    <td id="factNsEditorButtonTD<%=k%>">
                        <a href="#nsEditorLink" class="nseditor-icon-link"
                           style="padding-left:40px"
                           onclick="showNameSpaceEditor('inputFactValue<%=k%>','<%=opname.trim()%>')"><fmt:message
                                key="namespaces"/></a>
                    </td>
                    <td><a href="#" href="#" class="delete-icon-link" style="padding-left:40px"
                           onclick="deleteFact('inputFact','<%=k%>')"><fmt:message
                            key="delete"/></a></td>
                </tr>
                <% }
                    k++;
                } %>
                <input type="hidden" name="inputFactCount" id="inputFactCount"
                       value="<%=k%>"/>
                </tbody>
                </thead>
            </table>
        </div>
    </td>
</tr>






<tr>
    <td>
        <div style="margin-top:0px;">
            <a name="addfactLink"></a>
            <a class="add-icon-link"
               href="#addfactLink"
               onclick="addFact('inputFact','<%=opname%>')">
                <fmt:message key="add.fact"/></a>
        </div>
    </td>
</tr>



<% // Add output %>




<tr>
    <td><h3 class="mediator"><fmt:message key="output"/></h3></td>
</tr>





<tr>
    <td class="formRaw">
        <table class="normal">
            <tr>
                <td><fmt:message key="wrapper"/>
                </td>
                <td>
                    <input type="text" name="wrapperName" id="wrapperName"
                           value="<%=outputWrapperName%>"/>
                </td>
                <td> <fmt:message key="namespace"/>
                </td>
                <td>
                    <input type="text" name="outputNameSpace" id="outputNameSpace"
                           value="<%=outputNameSpace%>"/>
                </td>
            </tr>
        </table>
    </td>
</tr>





<tr>
    <td class="formRaw">
        <div style="margin-top:0px;">
            <table id="outputFacttable" style="<%=outputTableStyle%>;"
                   class="styledInner">
                <thead>
                <tr>
                    <th width="10%"><fmt:message key="th.type"/></th>
                    <th width="10%"><fmt:message key="th.selector"/></th>
                    <th width="10%"><fmt:message key="th.name"/></th>
                    <th><fmt:message key="namespaceeditor"/></th>
                    <th><fmt:message key="actions"/></th>
                </tr>
                <tbody id="outputFacttbody">
                <%
                    int j = 0;
                    for (Fact inputProperty : outputFacts) {
                        if (inputProperty != null) {
                            String type = inputProperty.getType();
                            String name = inputProperty.getElementName();
                            if (name == null) {
                                name = "";
                            }
                %>
                <tr id="outputRaw<%=j%>">
                    <td>
                        <input class="longInput" name="outputFactType<%=j%>"
                               id="outputFactType<%=j%>" value="<%=type%>"
                               type="text"/>
                    </td>
                    <td><a href="#factEditorLink" class="fact-selector-icon-link"
                           style="padding-left:40px"
                           onclick="showFactEditor('outputFact','<%=j%>')"><fmt:message
                            key="fact.type"/></a></td>


                    <td>
                        <input class="longInput" id="outputFactName<%=j%>"
                               name="outputFactName<%=j%>"
                               type="text" value="<%=name%>"/>
                    </td>
                    <td id="factNsEditorButtonTD<%=k%>">
                        <a href="#nsEditorLink" class="nseditor-icon-link"
                           style="padding-left:40px"
                           onclick="showNameSpaceEditor('outputFactValue<%=j%>','<%=opname%>')"><fmt:message
                                key="namespaces"/></a>
                    </td>
                    <td><a href="#" href="#" class="delete-icon-link" style="padding-left:40px"
                           onclick="deleteFact('outputFact','<%=j%>')"><fmt:message
                            key="delete"/></a></td>
                </tr>
                <% }
                    j++;
                } %>
                <input type="hidden" name="outputFactCount" id="outputFactCount"
                       value="<%=j%>"/>
                </tbody>
                </thead>
            </table>
        </div>
    </td>
</tr>






<tr>
    <td>
        <div style="margin-top:0px;">
            <a name="addoutputLink"></a>
            <a class="add-icon-link"
               href="#addoutputLink"
               onclick="addFact('outputFact','<%=opname%>')">
                <fmt:message key="add.fact"/></a>
        </div>
    </td>
</tr>







<tr>
    <td class="buttonRow">
        <input type="hidden" id="stepID" name="stepID" value="step5"/>
        <input class="button" type="submit"
               value="<fmt:message key="add"/>"/>
        <input class="button" type="button" value="<fmt:message key="cancel"/>"
               onclick="location.href = 'rule_service_wizard_step4.jsp'"/>
    </td>
</tr>





</table>
</form>
</div>

<a name="factEditorLink"></a>

<div id="factEditor" style="display:none;"></div>

<a name="nsEditorLink"></a>

<div id="nsEditor" style="display:none;"></div>

</div>
</fmt:bundle>
