<%@ page import = "grails.plugin.springsecurity.SpringSecurityUtils" %>
<html>
  <head>
    <meta name='layout' content=''/>
    <title>Reset Password</title>
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

  <body id="reset-password" class="auth-page">
    <h3>Reset Password</h3>

    <div class="content">
      <div id="reset-password-container">
        <noscript>Resetting your password requires Javascript.  Please enable Javascript in your browser.</noscript>
      </div>
    </div>
    <script src="https://api.userstore.io/1/js/userstore.js"></script>
    <script>
      UserStore.setPublishableKey('${SpringSecurityUtils?.securityConfig.userstore.publishableKey}');
      window.onload = function() {
      UserStore.renderResetPassword({
      container: 'reset-password-container', // required
      errorURL: '${request.contextPath}/forgot-password', // required
      successURL: '${request.contextPath}/reset-password', // optional
      messages: { // optional
      placeholders: {
      password: 'Password',
      passwordConfirm: 'Retype Password'
      },
      labels: {
      submitButton: 'Reset Password'
      }
      },
      options: { // optional
      renderBootstrapClasses: true
      }
      });
      }
    </script>

  </body>
</html>
