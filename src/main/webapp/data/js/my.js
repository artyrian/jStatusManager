var autoupdateJson = 120 * 1000;
var assignees = [];
var priorities = [];
var  tableReady = false;


function loadPreData() {
    assigneesReady = false;
    var urlAssignees = "rest/api/assignees";
    $.getJSON( urlAssignees, {
        format: "json"
    })
        .done(function(data) {
            assignees = data;
            assigneesReady = true;
        });

    prioritiesReady = false;
    var urlPriorities = "rest/api/priorities";
    $.getJSON( urlPriorities, {
        format: "json"
    })
        .done(function(data) {
            priorities = data;
            prioritiesReady = true;
        });

    var urlSettings = "rest/api/settings";
    $.getJSON( urlSettings, {
        format: "json"
    })
        .done(function(data) {
            var htmlCode = '<span class="label label-default">unknown</span>';
            if (data.isJobStarted == "true") {
                htmlCode = '<span class="label label-success">Started</span>';
                hide('divStartJob');
                show('divStopJob');
            } else {
                htmlCode = '<span class="label label-danger">Stopped</span>';
                show('divStartJob');
                hide('divStopJob');
            }
            setData("isJobStarted", htmlCode);
            setData("secondsPeriodUpdate", data.secondsPeriodUpdate);
        });

    function checkFlags() {
        if (assignees.length > 0 && priorities.length > 0) {
            drawTable();
        } else {
            setTimeout(checkFlags, 1000);
        }
    }

    setTimeout(checkFlags, 100);
    start('rest/api/streamdata');

    //function updateData() {
    //    loadJSON();
    //}

    //setInterval(updateData, autoupdateJson);

}

function drawTable() {
    innerTable = "<tr><th>Name</th><th>Major</th><th>Critical</th><th>All</th></tr>";

    for (var k = 0; k < assignees.length; k++) {
        name = (assignees[k] != '__all') ? assignees[k] : "<b>Total</b>";
        trLine = '<tr><td>' + name +'</td>';
        for (var i = 0; i < priorities.length; i++) {
            tdId = assignees[k] + "Count" + priorities[i];
            trLine += '<td><div id="' + tdId +'"></div></td>';
        }
        trLine += '</tr>';
        innerTable += trLine + '</tr>';
    }

    setData("tableStat", innerTable);
    tableReady = true;
}

function hide(divName) {
    //If the HIDE class exists then remove it, But first hide DIV
    if ($("#" + divName).hasClass('tohide')) {
        $("#" + divName).hide();
        $(divName).hide();
    }
}

function show(divName) {
    $("#" + divName).show();
}

function loadJSON() {
    var flickerAPI = "rest/api/result";
    $.getJSON( flickerAPI, {
        format: "json"
    })
    .done(function(data) {
        setJsonData(data);
    });
};


function setJsonData(data) {
    setStatusBuild(data.lastBuildStatus);

    setData("deltaUpdateTime", data.deltaUpdateTime / 1000 + " sec");
    setData("startUpdateTime", new Date(data.startUpdateTime).toLocaleString());
    setData("endUpdateTime", new Date(data.endUpdateTime).toLocaleString());


    setData("lastCommitInfo", "Last commit by <b>" + data.lastCommitUser + "</b> " +
    " [" + data.lastCommitMessage + "]. " + new Date(data.lastCommitDate).toLocaleString());

    for (var k = 0; k < priorities.length; k++) {
        currentPriority = priorities[k];
        curPriorityArray = data.openIssues[currentPriority];
        for (var i=0; i < assignees.length; i++) {
            key = assignees[i] + "Count" + currentPriority;
            value = curPriorityArray[assignees[i]];
            setData(key, value);
        }
    }
}


function setStatusBuild(value) {
    var htmlCode;

    switch (value) {
        case "SUCCESS":
            htmlCode = '<span class="label label-success">SUCCESS</span>';
            break;
        case "FAILURE":
            htmlCode = '<span class="label label-danger">FAILURE</span>';
            break;
        case "UNSTABLE":
            htmlCode = '<span class="label label-warning">UNSTABLE</span>';
            break;
        default:
            htmlCode = '<span class="label label-default">UNKNOWN</span>';
            break;
    }

    setData("lastBuildStatus", htmlCode);
}

function setData(elemId, value) {
    document.getElementById(elemId).innerHTML = value;
}

function postByUrl(url, data) {
    $.ajax({
        type: "POST",
        url: url,
        data: data,
        success: function() {
            location.reload();
        },
        dataType: null
    });
}

function getByUrl(url, data) {
    simpleAjax("GET", url, data, null, null);
}

function simpleAjax(type, url, data, fn, dataType) {
    $.ajax({
        type: type,
        url: url,
        data: data,
        success: fn,
        dataType: dataType
    });
}

function start(url) {
    var eventSource = new EventSource(url);

    eventSource.onerror = function(e) {
        if (this.readyState == EventSource.CONNECTING) {
            //console.log("Соединение порвалось, пересоединяемся...");
            console.log("Connection is closed. Reconnection.");
        } else {
            console.log("Error; state: " + this.readyState);
        }
    };

    eventSource.onmessage = function(e) {
        if (tableReady) {
            loadJSON();
        } else {
            function waitTable() {
                if (tableReady) {
                    loadJSON();
                } else {
                    setTimeout(waitTable, 500);
                }
            }

            setTimeout(waitTable, 1000);
        }

        //setJsonData(JSON.parse(e.data));
    };
}