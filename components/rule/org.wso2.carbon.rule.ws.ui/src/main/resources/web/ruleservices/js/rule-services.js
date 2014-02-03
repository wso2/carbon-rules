function setRuleScriptType(type) {
    var ruleScriptKeyTR = document.getElementById("ruleScriptKeyTR");
    var ruleScriptSourceTR = document.getElementById("ruleScriptSourceTR");
    var rulesetCreationTR = document.getElementById("rulesetCreationTR");
    var rulesetCreationUploadTR = document.getElementById("rulesetCreationUploadTR");
    var ruleScriptUploadTR = document.getElementById("ruleScriptUploadTR");
    var ruleScriptURLTR = document.getElementById("ruleScriptURLTR");
    var scriptListTR = document.getElementById("ruleScriptList");
    if ('key' == type) {
        ruleScriptKeyTR.style.display = "";
        rulesetCreationTR.style.display = "";
        ruleScriptSourceTR.style.display = "none";
        ruleScriptUploadTR.style.display = "none";
        ruleScriptURLTR.style.display = "none";
        scriptListTR.style.display = "";
    }
    else if ('upload' == type) {
        ruleScriptSourceTR.style.display = "none";
        ruleScriptKeyTR.style.display = "none";
        rulesetCreationTR.style.display = "";
        ruleScriptUploadTR.style.display = "";
        ruleScriptURLTR.style.display = "none";
        scriptListTR.style.display = "";
    }
    else if ('url' == type) {
        ruleScriptSourceTR.style.display = "none";
        ruleScriptKeyTR.style.display = "none";
        rulesetCreationTR.style.display = "";
        ruleScriptURLTR.style.display = "";
        ruleScriptUploadTR.style.display = "none";
        scriptListTR.style.display = "";
    }
    else {
        ruleScriptSourceTR.style.display = "";
        rulesetCreationTR.style.display = "";
        ruleScriptKeyTR.style.display = "none";
        ruleScriptUploadTR.style.display = "none";
        ruleScriptURLTR.style.display = "none";
        scriptListTR.style.display = "none";
    }

    return true;
}

function showFactEditor(category, i) {

    var suffix = "index=" + i + "&category=" + category;
    var typeInput = document.getElementById(category + "Type" + i);
    var type = null;
    if (typeInput != null && typeInput != undefined) {
        type = typeInput.value;
    }
    if (type != undefined && type != null && type != "null" && type != "") {
        suffix += "&type=" + type;
    }
    var url = 'fact_slector-ajaxprocessor.jsp?' + suffix;

    var loadingContent = "<div id='workArea' style='overflow-x:hidden;'><div id='popupContent'><div class='ajax-loading-message'> <img src='../resources/images/ajax-loader.gif' align='top'/> <span>" + "Wating ..." + "</span> </div></div></div>";
    CARBON.showPopupDialog(loadingContent, ruleservicejsi18n["rule." + category + ".editor"], 200, false, null, 550);

    jQuery("#popupContent").load(url, null,
        function(res, status, t) {
            if (status != "success") {
                CARBON.showWarningDialog(ruleservicejsi18n["rule.facteditor.error"]);
            }
        });
    return false;
}

function showPropertyEditor() {

    var url = 'property_editor-ajaxprocessor.jsp';

    var loadingContent = "<div id='workArea' style='overflow-x:hidden;'><div id='popupContent'><div class='ajax-loading-message'> <img src='../resources/images/ajax-loader.gif' align='top'/> <span>" + "Wating ..." + "</span> </div></div></div>";
    CARBON.showPopupDialog(loadingContent, ruleservicejsi18n["property.editor"], 200, false, null, 550);

    jQuery("#popupContent").load(url, null,
        function(res, status, t) {
            if (status != "success") {
                CARBON.showWarningDialog(ruleservicejsi18n["property.editor.error"]);
            }
        });
    return false;
}

function addProperty() {

    var nsCount = document.getElementById("propertyCount");
    var i = nsCount.value;

    var currentCount = parseInt(i);

    currentCount = currentCount + 1;

    nsCount.value = currentCount;

    var nstable = document.getElementById("propertyTable");
    nstable.style.display = "";
    var nstbody = document.getElementById("propertyTBody");

    var nsRaw = document.createElement("tr");
    nsRaw.setAttribute("id", "propertyTR" + i);

    var prefixTD = document.createElement("td");
    prefixTD.appendChild(createNSEditorTextBox("name" + i, null));

    var uriTD = document.createElement("td");
    uriTD.appendChild(createNSEditorTextBox("value" + i, "longInput"));

    var actionTD = document.createElement("td");
    actionTD.appendChild(createPropertyDeleteLink(i));
    nsRaw.appendChild(prefixTD);
    nsRaw.appendChild(uriTD);
    nsRaw.appendChild(actionTD);
    nstbody.appendChild(nsRaw);
    return true;
}

function createPropertyDeleteLink(i) {
    // Create the element:
    var factDeleteLink = document.createElement('a');
    // Set some properties:
    factDeleteLink.setAttribute("href", "#");
    factDeleteLink.style.paddingLeft = '40px';
    factDeleteLink.className = "delete-icon-link";
    factDeleteLink.appendChild(document.createTextNode(ruleservicejsi18n["rule.action.delete"]));
    factDeleteLink.onclick = function () {
        deletePropertyRaw(i)
    };
    return factDeleteLink;
}

function deletePropertyRaw(i) {
    CARBON.showConfirmationDialog(ruleservicejsi18n["ns.editor.delete.confirmation"], function() {
        var propRow = document.getElementById("propertyTR" + i);
        if (propRow != undefined && propRow != null) {
            var parentTBody = propRow.parentNode;
            if (parentTBody != undefined && parentTBody != null) {
                parentTBody.removeChild(propRow);
            }
        }
    });
}

function saveProperties() {

    var nsCount = document.getElementById("propertyCount");
    var count = parseInt(nsCount.value);
    var referenceString = "";
    for (var i = 0; i < count; i++) {
        var nameTD = "name" + i;
        var name = document.getElementById(nameTD);
        var valueTD = "value" + i;
        var value = document.getElementById(valueTD);
        if (name != null && name != undefined && value != null && value != undefined) {
            var nameValue = name.value;
            var valueText = value.value;
            if (nameValue != undefined && valueText != undefined && valueText != "") {
                referenceString += "&" + nameTD + "=" + nameValue + "&" + valueTD + "=" + valueText;
            }
        }
    }
    var url = 'property_save-ajaxprocessor.jsp?propertyCount=' + count + referenceString;
    jQuery.post(url, ({}),
        function(data, status) {
            if (status != "success") {
                CARBON.showWarningDialog(ruleservicejsi18n["property.save.error"]);
            }
        });
    hideEditor();
    CARBON.closeWindow();
    return false;
}

function deleteOperation(name, i) {
    var opCount = document.getElementById("opCount");
    opCount.value = parseInt(opCount.value) - 1;
    CARBON.showConfirmationDialog(ruleservicejsi18n["operation.delete.confirmation"], function() {
        deleteRaw('operation', i);
        var suffix = "index=" + i + "&opname=" + name;
        var url = 'op_delete-ajaxprocessor.jsp?' + suffix;
        jQuery.get(url, ({}),
            function(data, status) {
                if (status != "success") {
                    CARBON.showWarningDialog(ruleservicejsi18n['error.occurred']);
                    return false;
                }
            });
    });

    return false;
}

function deleteFactArchives(name) {
    CARBON.showConfirmationDialog(ruleservicejsi18n["operation.delete.confirmation"], function() {
        // deleteFactArchiveRaw(name);
        var suffix = "factArchiveName=" + name;
        var url = 'fact_archive_delete-ajaxprocessor.jsp?' + suffix;
        jQuery.get(url, ({}),
            function(data, status) {
                location.reload();
                if (status != "success") {
                    CARBON.showWarningDialog(ruleservicejsi18n['error.occurred']);
                    return false;
                }
            });
    });

    return false;
}

function deleteRuleFile(name) {
    CARBON.showConfirmationDialog(ruleservicejsi18n["operation.delete.confirmation"], function() {
        // deleteFactArchiveRaw(name);
        var suffix = "ruleFileName=" + name;
        var url = 'rule_file_delete-ajaxprocessor.jsp?' + suffix;
        jQuery.get(url, ({}),
            function(data, status) {
                location.reload();
                if (status != "success") {
                    CARBON.showWarningDialog(ruleservicejsi18n['error.occurred']);
                    return false;
                }
            });
    });

    return false;
}

function deleteAllRuleFiles(){
      var url = 'rule_delete_all_files-ajaxprocessor.jsp';
        jQuery.get(url, ({}),
            function(data, status) {
                if (status != "success") {
                    CARBON.showWarningDialog(ruleservicejsi18n['error.occurred']);
                    return false;
                }
            });
    return false;
}

function getSelectedValue(id) {
    var variableType = document.getElementById(id);
    var variableType_indexstr = null;
    var variableType_value = null;
    if (variableType != null) {
        variableType_indexstr = variableType.selectedIndex;
        if (variableType_indexstr != null) {
            variableType_value = variableType.options[variableType_indexstr].value;
        }
    }
    return variableType_value;
}

function addFact(category, opName) {

    if (!validateFacts(category)) {
        return false;
    }

    var factCount = document.getElementById(category + "Count");
    var i = factCount.value;

    var currentCount = parseInt(i);
    currentCount = currentCount + 1;

    factCount.value = currentCount;

    var facttable = document.getElementById(category + "table");
    facttable.style.display = "";
    var facttbody = document.getElementById(category + "tbody");

    var factRaw = document.createElement("tr");
    factRaw.setAttribute("id", category + "Raw" + i);

    var factSelectorTD = document.createElement("td");
    factSelectorTD.appendChild(createFactEditorLLink(category, i));
    var nameTD = document.createElement("td");
    nameTD.appendChild(createFactTextBox(category + "Name" + i));
    var factTypeTD = document.createElement("td");
    factTypeTD.appendChild(createFactTextBox(category + "Type" + i));
    var deleteTD = document.createElement("td");
    deleteTD.appendChild(createFactDeleteLink(category, i));

    var factNameSpace = document.createElement("td");
    factNameSpace.appendChild(createFactTextBox(category + "NameSpace" + i));

    factRaw.appendChild(factTypeTD);
    factRaw.appendChild(factSelectorTD);
    factRaw.appendChild(nameTD);
    factRaw.appendChild(factNameSpace);
    factRaw.appendChild(deleteTD);
    facttbody.appendChild(factRaw);
    return true;
}

function createNSEditorLink(category, i, opName) {
    // Create the element:
    var factHref = document.createElement('a');

    // Set some properties:
    factHref.setAttribute("href", "#nsEditorLink");
    factHref.className = "nseditor-icon-link";
    factHref.appendChild(document.createTextNode(ruleservicejsi18n["rule.namespaces"]));
    factHref.style.paddingLeft = '40px';
    factHref.onclick = function () {
        showNameSpaceEditor(category + 'Value' + i, opName);
    };
    return factHref;
}

function createFactEditorLLink(category, i) {
    // Create the element:
    var factHref = document.createElement('a');

    // Set some properties:
    factHref.setAttribute("href", "#factEditorLink");
    factHref.style.paddingLeft = '40px';
    factHref.className = "fact-selector-icon-link";
    factHref.appendChild(document.createTextNode(ruleservicejsi18n["fact.type"]));
    factHref.onclick = function () {
        showFactEditor(category, i)
    };
    return factHref;
}
function createFactDeleteLink(category, i) {
    // Create the element:
    var factDeleteLink = document.createElement('a');

    // Set some properties:
    factDeleteLink.setAttribute("href", "#");
    factDeleteLink.style.paddingLeft = '40px';
    factDeleteLink.className = "delete-icon-link";
    factDeleteLink.appendChild(document.createTextNode(ruleservicejsi18n["rule.action.delete"]));
    factDeleteLink.onclick = function () {
        deleteFact(category, i)
    };
    return factDeleteLink;
}

function createFactTextBox(id) {
    // Create the element:
    var factInput = document.createElement('input');

    // Set some properties:
    factInput.setAttribute("type", 'text');
    factInput.setAttribute("id", id);
    factInput.setAttribute("name", id);
    factInput.className = "longInput";
    return factInput;
}

function deleteFact(category, i) {
    CARBON.showConfirmationDialog(ruleservicejsi18n[ "fact.delete.confirmation"], function() {
        deleteRaw(category, i)
    });
}

function deleteRaw(category, i) {
    var propRow = document.getElementById(category + "Raw" + i);
    if (propRow != undefined && propRow != null) {
        var parentTBody = propRow.parentNode;
        if (parentTBody != undefined && parentTBody != null) {
            parentTBody.removeChild(propRow);
            if (!isContainRaw(parentTBody)) {
                var factTable = document.getElementById(category + "table");
                factTable.style.display = "none";
            }
        }
    }
}


function isContainRaw(tbody) {
    if (tbody.childNodes == null || tbody.childNodes.length == 0) {
        return false;
    } else {
        for (var i = 0; i < tbody.childNodes.length; i++) {
            var child = tbody.childNodes[i];
            if (child != undefined && child != null) {
                if (child.nodeName == "tr" || child.nodeName == "TR") {
                    return true;
                }
            }
        }
    }
    return false;
}

function validateFacts(category) {

    var nsCount = document.getElementById(category + "Count");
    var i = nsCount.value;

    var currentCount = parseInt(i);

    if (currentCount >= 1) {
        for (var k = 0; k < currentCount; k++) {
            var propRow = document.getElementById(category + "Raw" + k);
            if (propRow != null && propRow != undefined) {
                var type = document.getElementById(category + "Type" + k);
                if (type != null && type != undefined) {
                    if (type.value == "") {
                        CARBON.showWarningDialog(ruleservicejsi18n["invalid." + category]);
                        return false;
                    }
                }
            }
        }
    }
    return true;
}

function addRuleScript(scriptType, ruleSourceType) {

    if (!validateRule(scriptType)) {
        return false;
    }

    var ruleScriptValue = document.getElementById(scriptType);
    var ruleScript = ruleScriptValue.value;
    var suffix = "scriptValue=" + ruleScript + "&scriptSourceType=" + ruleSourceType;
    var url = 'rule_script_add-ajaxprocessor.jsp?' + suffix;
    jQuery.get(url, ({}),
        function(data, status) {
            if (status != "success") {
                CARBON.showWarningDialog(ruleservicejsi18n['error.occurred']);
                return false;
            }
        });

    var scriptListTH = document.getElementById("ruleScriptHeader");
    scriptListTH.style.display = "";


    ruleScriptValue.value = "";
    var ruleScriptCount = document.getElementById("ruleScriptCount");
    var i = ruleScriptCount.value;
    var currentCount = parseInt(i);
    currentCount = currentCount + 1;

    ruleScriptCount.value = currentCount;

    var ruleScriptList = document.getElementById("ruleScriptList");
    ruleScriptList.style.display = "";

    var ruleScriptListTBody = document.getElementById("ruleScriptListTBody");

    var scriptRaw = document.createElement("tr");
    scriptRaw.setAttribute("id", "ruleScriptListRaw" + i);
    var typeTD = document.createElement("td");
    typeTD.appendChild(createScriptLabel(ruleSourceType))

    var nameTD = document.createElement("td");
    var nameTDWrapper = document.createElement("div");
    nameTD.appendChild(createScriptInput("ruleScriptName" + i, ruleScript));
    nameTD.appendChild(nameTDWrapper);

    nameTDWrapper.className = "scriptListNameWrapper";
    nameTDWrapper.appendChild(createScriptInput("ruleScriptSource" + i, ruleSourceType));
    nameTDWrapper.appendChild(createScriptLabel(ruleScript));
    var deleteTD = document.createElement("td");
    deleteTD.style.width = '200px';
    deleteTD.appendChild(createRuleDeleteLink("ruleScriptList", i, ruleSourceType, ruleScript));

    scriptRaw.appendChild(typeTD);
    scriptRaw.appendChild(nameTD);
    scriptRaw.appendChild(deleteTD);
    ruleScriptListTBody.appendChild(scriptRaw);
    return true;

}

function validateRule(scriptType) {
    var ruleScript = document.getElementById(scriptType);
    if (ruleScript.value == "") {
        //TODO  Add proper message
        CARBON.showWarningDialog(ruleservicejsi18n["invalid." + scriptType]);
        return false;

    }
    return true;
}

function createRuleDeleteLink(category, i, ruleSourceType, ruleScript) {

    var factDeleteLink = document.createElement('a');
    // Set some properties:
    factDeleteLink.setAttribute("href", "#");
    factDeleteLink.style.paddingLeft = '40px';
    factDeleteLink.className = "delete-icon-link";
    factDeleteLink.appendChild(document.createTextNode(ruleservicejsi18n["rule.action.delete"]));

    jQuery(factDeleteLink).click(function() {
        deleteScript(category, i, ruleSourceType, ruleScript);
    });
    return factDeleteLink;
}

function createScriptInput(id, ruleScript) {
    // Create the element:
    var ruleName = document.createElement('input');

    // Set some properties:
    ruleName.setAttribute("type", 'hidden');
    ruleName.setAttribute("id", id);
    ruleName.setAttribute("name", id);
    ruleName.setAttribute("value", ruleScript);
    return ruleName;
}


function createScriptLabel(ruleScript) {
    var ruleLabel = document.createElement('label');
    ruleLabel.innerHTML = ruleScript;
    return ruleLabel;

}


function deleteScript(category, i, sourceType, scriptName) {
    CARBON.showConfirmationDialog(ruleservicejsi18n[ "fact.delete.confirmation"], function() {
        deleteScriptRaw(category, i, sourceType, scriptName)
    });
}

function deleteScriptRaw(category, i, sourceType, scriptName) {
    var ruleScriptCount = document.getElementById("ruleScriptHiddenCount");
    var j = ruleScriptCount.value;
    var currentCount = parseInt(j);
    currentCount = currentCount + 1;
    ruleScriptCount.value = currentCount;

    var propRow = document.getElementById(category + "Raw" + i);

    var suffix = "ruleFileName=" + scriptName + "&sourceType=" + sourceType;
    var url = 'rule_file_delete-ajaxprocessor.jsp?' + suffix;
    jQuery.get(url, ({}),
        function(data, status) {
            if (status != "success") {
                CARBON.showWarningDialog(ruleservicejsi18n['error.occurred']);
                return false;
            }
        });

    if (propRow != undefined && propRow != null) {
        var parentTBody = propRow.parentNode;
        if (parentTBody != undefined && parentTBody != null) {
            parentTBody.removeChild(propRow);
            if (!isContainRaw(parentTBody)) {
                var scriptListTH = document.getElementById("ruleScriptHeader");
                scriptListTH.style.display = "none";
            }
        }
    }
}


