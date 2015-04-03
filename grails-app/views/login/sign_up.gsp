<%@ page import = "grails.plugin.springsecurity.SpringSecurityUtils" %>
<html>
  <head>
    <meta name='layout' content=''/>
    <title>Sign Up</title>
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

  <body id="signup" class="auth-page">
    <h3>Sign Up</h3>

    <div class="content">
      <div id="signup-container">
        <noscript>Sign-up requires Javascript.  Please enable Javascript in your browser.</noscript>
      </div>
    </div>
    <script src="https://api.userstore.io/1/js/userstore.js"></script>
    <script>
      UserStore.setPublishableKey('${SpringSecurityUtils?.securityConfig.userstore.publishableKey}');
      window.onload = function() {
      UserStore.renderSignUp({
      successURL: '${request.contextPath}/sign-up-callback', // optional
      container: 'signup-container',
      options: {
      fields: 'first_name,last_name,email,password',
      renderBootstrapClasses: true
      }
      });
      }
    </script>

  </body>
</html>
