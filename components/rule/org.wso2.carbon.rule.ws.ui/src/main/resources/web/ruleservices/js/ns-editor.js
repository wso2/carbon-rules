var DEFAULT_DIV_ID = 'nsEditor';

//private method
function show_ns_editor(dID, id, opName) {

    var url = 'ns_editor-ajaxprocessor.jsp?currentID=' + id + '&divID=' + dID + '&opName=' + opName;

    var loadingContent = "<div id='workArea' style='overflow-x:hidden;'><div id='popupContent'><div class='ajax-loading-message'> <img src='../resources/images/ajax-loader.gif' align='top'/> <span>" + ruleservicejsi18n["ns.editor.waiting.text"] + "</span> </div></div></div>";
    CARBON.showPopupDialog(loadingContent, ruleservicejsi18n["ns.editor.title"], 300, false, null, 550);

    jQuery("#popupContent").load(url, null,
            function(res, status, t) {
                if (status != "success") {
                    CARBON.showWarningDialog(ruleservicejsi18n["ns.editor.load.error"]);
                }
            });


    return false;
}
function showNameSpaceEditor(id, opName) {
    return show_ns_editor('nsEditor', id, opName);
}

function addNameSpace() {

    if (!isValidNameSpaces()) {
        return false;
    }

    var nsCount = document.getElementById("nsCount");
    var i = nsCount.value;

    var currentCount = parseInt(i);

    currentCount = currentCount + 1;

    nsCount.value = currentCount;

    var nstable = document.getElementById("nsTable");
    nstable.style.display = "";
    var nstbody = document.getElementById("nsTBody");

    var nsRaw = document.createElement("tr");
    nsRaw.setAttribute("id", "nsTR" + i);

    var prefixTD = document.createElement("td");
    prefixTD.appendChild(createNSEditorTextBox("prefix" + i, null));

    var uriTD = document.createElement("td");
    uriTD.appendChild(createNSEditorTextBox("uri" + i, "longInput"));

    var actionTD = document.createElement("td");
    actionTD.appendChild(createNSDeleteLink(i));
    nsRaw.appendChild(prefixTD);
    nsRaw.appendChild(uriTD);
    nsRaw.appendChild(actionTD);
    nstbody.appendChild(nsRaw);
    return true;
}

function createNSDeleteLink(i) {
    // Create the element:
    var factDeleteLink = document.createElement('a');
    // Set some properties:
    factDeleteLink.setAttribute("href", "#");
    factDeleteLink.style.paddingLeft = '40px';
    factDeleteLink.className = "delete-icon-link";
    factDeleteLink.appendChild(document.createTextNode(ruleservicejsi18n["rule.action.delete"]));
    factDeleteLink.onclick = function () {
        deleteNSRaw(i)
    };
    return factDeleteLink;
}
function deleteNSRaw(i) {
    CARBON.showConfirmationDialog(ruleservicejsi18n["ns.editor.delete.confirmation"], function() {
        var propRow = document.getElementById("nsTR" + i);
        if (propRow != undefined && propRow != null) {
            var parentTBody = propRow.parentNode;
            if (parentTBody != undefined && parentTBody != null) {
                parentTBody.removeChild(propRow);
            }
        }
    });
}
function createNSEditorTextBox(id, classOfTextBox) {
    // Create the element:
    var input = document.createElement('input');

    // Set some properties:
    input.setAttribute("type", 'text');
    input.setAttribute("id", id);
    input.setAttribute("name", id);
    if (classOfTextBox != null) {
        input.className = classOfTextBox;
    }
    else{
         input.style['width'] = "100px";
    }
    return input;
}

function isValidNameSpaces() {

    var nsCount = document.getElementById("nsCount");
    var i = nsCount.value;

    var currentCount = parseInt(i);

    if (currentCount >= 1) {
        for (var k = 0; k < currentCount; k++) {
            var prefix = document.getElementById("prefix" + k);
            if (prefix != null && prefix != undefined) {
                if (prefix.value == "") {
                    CARBON.showWarningDialog(ruleservicejsi18n["ns.prefix.empty"]);
                    return false;
                }
            }
            var uri = document.getElementById("uri" + k);
            if (uri != null && uri != undefined) {
                if (uri.value == "") {
                    CARBON.showWarningDialog(ruleservicejsi18n["ns.uri.empty"]);
                    return false;
                }
            }
        }
    }
    return true;
}

function saveNameSpace(divID, id, linkID, opName) {

    if (!isValidNameSpaces()) {
        return false;
    }

    if (linkID != undefined && linkID != null && linkID != "null" && linkID != "") {
        document.getElementById(linkID).style.display = "";
    }

    var nsCount = document.getElementById("nsCount");
    var count = parseInt(nsCount.value);
    var referenceString = "";
    for (var i = 0; i < count; i++) {
        var prefixID = "prefix" + i;
        var prefix = document.getElementById(prefixID);
        var uriID = "uri" + i;
        var uri = document.getElementById(uriID);
        if (prefix != null && prefix != undefined && uri != null && uri != undefined) {
            var prefixValue = prefix.value;
            var uriValue = uri.value;
            if (prefixValue != undefined && uriValue != undefined && uriValue != "") {
                referenceString += "&" + prefixID + "=" + prefixValue + "&" + uriID + "=" + uriValue;
            }
        }
    }
    var url = 'ns_save-ajaxprocessor.jsp?currentID=' + id + '&opName=' + opName + "&nsCount=" + count + referenceString;
    jQuery.post(url, ({}),
            function(data, status) {
                if (status != "success") {
                    CARBON.showWarningDialog(ruleservicejsi18n["ns.editor.load.error"]);
                }
            });
    hideEditor();
    CARBON.closeWindow();
    return false;
}

function hideEditor() {
    CARBON.closeWindow();
}
