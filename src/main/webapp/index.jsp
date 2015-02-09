<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html >
  <head>
    <title>Status Manager</title>
    <meta charset="utf-8">
    <base href="<c:url value="/" />">

    <link rel="stylesheet" href="data/css/bootstrap.min.css">
    <link rel="stylesheet" href="data/css/bootstrap-theme.min.css">
    <link href="data/css/grid.css" rel="stylesheet">

    <script src="data/js/jquery-1.11.2.min.js"></script>
    <script src="data/js/bootstrap.min.js"></script>
    <script src="data/js/my.js"></script>
  </head>

  <body onload="loadPreData();">
    <div class="container">

      <div class="page-header">
        <h1>Project statistic</h1>
      </div>

      <div class="container">
        <div class="row">
          <div class="col-sm-6" style="background-color:white;">
            <h3>Json update info</h3>
            <div class="table-responsive">
              <table class="table table-bordered table-condensed nonfluid">
                <tr>
                  <th>Parameter</th>
                  <th>Value</th>
                </tr>
                <tr>
                  <td>Last build status</td>
                  <td><div id="lastBuildStatus"/></td>
                </tr>
                <tr>
                  <td>Duration update</td>
                  <td><div id="deltaUpdateTime"/></td>
                </tr>
                <tr>
                  <td>Last server start update datetime</td>
                  <td><div id="startUpdateTime"/></td>
                </tr>
                <tr>
                  <td>Last server end update datetime</td>
                  <td><div id="endUpdateTime"/></td>
                </tr>
              </table>

              <!--server info -->

              <p class="lead">Settings</p>

              <table class="table table-condensed nonfluid">
                <tr>
                  <td>Server crawler job status</td>
                  <td><div id="isJobStarted"></div></td>
                </tr>
                <tr>
                  <td>Server data crawler update</td>
                  <td><div id="secondsPeriodUpdate"/> sec.</td>
                </tr>

              </table>


            </div> <!-- table-responsive -->
          </div>

          <div class="col-sm-6" style="background-color:white;">
            <h3>Tasks stat</h3>
            <table class="table table-bordered table-condensed" id="tableStat">
              <!-- autocomplete table-->
            </table>

            <div id="lastCommitInfo"></div>

          </div> <!-- div col -->
        </div> <!-- div row -->
      </div> <!-- div container -->


      <div class="container nonfluid">
        <h3>Control panel</h3>
        <div class="row nonfluid">
          <div class="col-sm-2 nonfluid tohide" id="divStartJob" style="background-color:white;">

            <form onsubmit="postByUrl('<c:url value="/rest/api/start"/>')" >
              <button class="btn btn-success btn-default btn-xs" type="submit" >Start job</button>
            </form>
          </div>
          <div class="col-sm-2 nonfluid tohide" id="divStopJob" style="background-color:white;">

            <form onsubmit="postByUrl('<c:url value="/rest/api/stop"/>')" >
                <button class="btn btn-danger btn-default btn-xs" type="submit" >Stop job</button>
            </form>
          </div>

          <div class="col-sm-2 nonfluid" style="background-color:white;">
            <button type="button" class="btn btn-info btn-default btn-xs" onclick="loadJSON()">Update JSON (manual)</button>
          </div>

          <div class="col-sm-2 nonfluid" style="background-color:white;">

            <form onsubmit="postByUrl('<c:url value="/rest/api/manual" />')" >
              <button class="btn btn-info btn-default btn-xs" type="submit" >Manual update job</button>
            </form>
          </div>

          <div class="col-sm-2 nonfluid" style="background-color:white;">

            <button class="btn-default btn-xs"
                    onclick="window.open('<c:url value="/rest/api/result"/>',
                                        'testname',
                                        'width=640,height=480,resizable=yes,scrollbars=yes,status=yes')">
              view json
            </button>
          </div>

        </div>
      </div>

    </div>
  </body>
</html>