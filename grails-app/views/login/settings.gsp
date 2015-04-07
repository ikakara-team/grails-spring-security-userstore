<%@ page import = "grails.plugin.springsecurity.SpringSecurityUtils" %>
<html>
  <head>
    <meta name='layout' content=''/>
    <title>Account Settings</title>
    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">

    <!-- Optional theme -->
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
    <style type='text/css' media='screen'>
      body {
      padding: 50px;
      font: 14px "Lucida Grande", Helvetica, Arial, sans-serif;
      }
      a {
      color: #00b7ff;
      }
    </style>
  </head>
  <body id="account-settings" class="auth-page">
    <h3>Account Settings</h3>

    <div class="content">
      <div id="account-settings-container">
        <div class="box-content">
          <g:form name="accountForm" class="form-horizontal" useToken="true"  uri="${SpringSecurityUtils.securityConfig.userstore.defaultSettingsUrl}" method="PUT" >
            <fieldset>
              <legend>Change Details</legend>
              <g:if test="${form == 'settings' && flash.message}">
                <div class="message" role="status">${flash.message}</div>
              </g:if>
              <div class="form-group">
                <label class="col-sm-3 control-label">First Name</label>
                <div class="col-sm-5">
                  <input type="hidden" name="prev_first_name" value="${auth?.first_name}" />
                  <input type="text" class="form-control" name="first_name" value="${auth?.first_name}" />
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Last Name</label>
                <div class="col-sm-5">
                  <input type="hidden" name="prev_last_name" value="${auth?.last_name}" />
                  <input type="text" class="form-control" name="last_name" value="${auth?.last_name}" />
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Username</label>
                <div class="col-sm-5">
                  <input type="hidden" name="prev_username" value="${auth?.username}" />
                  <input type="text" class="form-control" name="username" value="${auth?.username}" />
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Email address</label>
                <div class="col-sm-5">
                  <input type="hidden" name="prev_email" value="${auth?.email}" />
                  <input type="text" class="form-control" name="email" value="${auth?.email}" />
                  <span style="font-size: .75em;">**<g:message code="springSecurityUserstore.account.settings.warning" /></span>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Confirm Password</label>
                <div class="col-sm-5">
                  <input type="password" class="form-control" name="password" />
                </div>
              </div>
            </fieldset>
            <div class="form-group">
              <div class="col-sm-9 col-sm-offset-3">
                <button type="submit" class="btn btn-primary">Submit</button>
              </div>
            </div>
          </g:form>
        </div>

        <div class="box-content">
          <g:form name="passwordForm" class="form-horizontal" useToken="true"  uri="${SpringSecurityUtils.securityConfig.userstore.defaultSettingsUrl}" method="POST" >
            <fieldset>
              <legend>Change Password</legend>
              <g:if test="${form == 'password' && flash.message}">
                <div class="message" role="status">${flash.message}</div>
              </g:if>
              <div class="form-group">
                <label class="col-sm-3 control-label">Existing Password</label>
                <div class="col-sm-5">
                  <input type="password" class="form-control" name="password" />
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">New Password</label>
                <div class="col-sm-5">
                  <input type="password" class="form-control" name="newPassword" />
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-3 control-label">Retype password</label>
                <div class="col-sm-5">
                  <input type="password" class="form-control" name="confirmPassword" />
                </div>
              </div>
            </fieldset>
            <div class="form-group">
              <div class="col-sm-9 col-sm-offset-3">
                <button type="submit" class="btn btn-primary">Submit</button>
              </div>
            </div>
          </g:form>
        </div>
      </div>
    </div>

  </body>
</html>
