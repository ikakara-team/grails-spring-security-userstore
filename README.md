# grails-spring-security-userstore

Description:
--------------
Save time integrating user registration and authentication with Spring Security Userstore Plugin -- a datastore (service)
for Spring Security Core (Authentication) and user information.  Simply customize the UI (sign-in, sign-up, forgot-password,
reset_password and emails) and you've got user registration and authentication for your application w/o provisioning a
database and smtp server.  You also get a nice dashboard for metrics/reporting.

Installation:
--------------

1. Sign-up w/ https://www.userstore.io/sign-up
  * Be sure to verify your email
2. Create App - https://www.userstore.io/welcome
3. UserStore Dashboard
  * Configure Widget:
    * Enter allowed hosts, such as localhost:8080, localhost:8443
  * Configure Mail Settings:
    * Enter SMTP settings
    * Be sure to enter "From Email" and "From Name" for each email template
4. grails-app/conf/BuildConfig.groovy:
```
  plugins {
...
    compile ':spring-security-core:2.0-RC4'
    compile ':spring-security-userstore:0.7.1'
...
  }
```

Initialization & Configuration:
--------------
Run the following from the command line:
```
>grails compile
>grails s2-init-userstore
```

Writes to grails-app/conf/Config.groovy:
```
// Required configuration:
grails.plugin.springsecurity.userstore.publishableKey = "USERSTORE_PUBLISHABLE_KEY"
grails.plugin.springsecurity.userstore.secretKey = "USERSTORE_SECRET_KEY"
```
```
// Optional configuration:
grails.plugin.springsecurity.userstore.defaultRoleOnSignin = "ROLE_NONE"
grails.plugin.springsecurity.userstore.initRoleOnSignup = "ROLE_USER"
grails.plugin.springsecurity.userstore.autoSigninOnSignup = true
grails.plugin.springsecurity.userstore.requireEmailVerifiedOnSignin = false
grails.plugin.springsecurity.userstore.defaultSettingsUrl = "/account-settings"
```

Add the following Spring Security settings:
```
// Allow access to sign-up, etc.
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
  '/':                              ['permitAll'],
  '/index':                         ['permitAll'],
  '/index.gsp':                     ['permitAll'],
  '/assets/**':                     ['permitAll'],
  '/**/js/**':                      ['permitAll'],
  '/**/css/**':                     ['permitAll'],
  '/**/images/**':                  ['permitAll'],
  '/**/favicon.ico':                ['permitAll'],
  '/sign-up':                       ['permitAll'],
]
// Use https
grails.plugin.springsecurity.secureChannel.definition = [
  '**':                   'REQUIRES_SECURE_CHANNEL',
]
```

Copies files to grails-app/views/login/:
* grails-app/views/login/denied.gsp
* grails-app/views/login/auth.gsp
* grails-app/views/login/forgot_password.gsp
* grails-app/views/login/reset_password.gsp
* grails-app/views/login/sign_up.gsp
* grails-app/views/login/verify_success.gsp
* grails-app/views/login/verify_fail.gsp
* grails-app/views/login/settings.gsp

These files are "templates"; you should customize them to suit your needs.

UserStore Customization:
-------------
UserStore Docs: https://www.userstore.io/docs

SecurityTagLib:
--------------
In addition to the current [taglib functionality](http://grails-plugins.github.io/grails-spring-security-core/guide/helperClasses.html#securityTagLib), you have access to the following:
```
<sec:loggedInUserInfo field="first_name"/>
<sec:loggedInUserInfo field="last_name"/>
<sec:loggedInUserInfo field="full_name"/>
<sec:loggedInUserInfo field="initials_name"/>
<sec:loggedInUserInfo field="email"/>
```

SpringSecurityService:
--------------
In addition to the current [service functionality](http://grails-plugins.github.io/grails-spring-security-core/guide/helperClasses.html#springSecurityService), you have access to the following:
```
springSecurityService.principal.first_name
springSecurityService.principal.last_name
springSecurityService.principal.full_name
springSecurityService.principal.initials_name
springSecurityService.principal.email
```

UserstoreDetailsService:
--------------
* ```void logout(HttpServletRequest request, HttpServletResponse response)```
* ```UserstoreUserDetails loadUserByUsername(String username, boolean loadRoles)```
* ```UserstoreUserDetails loadUserByEmail(String email, boolean loadRoles)```
* ```UserstoreUserDetails loadUserById(String uid, boolean loadRoles)```
* ```UserstoreUserDetails authToken2UserDetails(String authtoken)```
* ```responseData updateRoles(String id, String roles)```
* ```jsonData getUserById(String uid)```
* ```jsonData getUserByUsername(String username)```
* ```jsonData getUserByEmail(String email)```
* ```responseData verifyCode(String code)```
* ```Boolean confirmPassword(String uid, String password)```
* ```responseData updatePassword(String uid, String password)```
* ```responseData updateUser(String uid, String password, String first=null, String last=null, String username=null, String email=null, String verifyUrl=null)```

Example Usage:
--------------
```
class ExampleController {

  def userDetailsService

  def exampleAction(String email) {
    UserstoreDetailsService userstoreDetailsService = userDetailsService
    def user = userDetailsService.getUserByEmail(email)
  }
}
```

Copyright & License:
--------------
Copyright 2014-2015 Allen Arakaki.  All Rights Reserved.

```
Apache 2 License - http://www.apache.org/licenses/LICENSE-2.0
```

History:
--------------
```
0.8   - updateSettings; changePassword
0.7.1 - tweak verify pages
0.7   - logout
0.6.1 - confirmPassword; updatePassword; updateUser
0.5   - UserstoreUserDetails email
0.4   - loadUserByUsername
0.3.3 - support <sec:loggedInUserInfo field="initials_name"/>
0.2   - support <sec:loggedInUserInfo field="full_name"/>
0.1   - initial checkin
```