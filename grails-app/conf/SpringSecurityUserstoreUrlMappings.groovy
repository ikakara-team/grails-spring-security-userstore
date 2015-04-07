import grails.plugin.springsecurity.SpringSecurityUtils

class SpringSecurityUserstoreUrlMappings {

  static mappings = {
    "/sign-in"(view:"/login/auth") { }
    "/sign-up"(view:"/login/sign_up") { }
    "/sign-up-callback"(controller: "signup", action:"callback")
    "/forgot-password"(view:"/login/forgot_password") { }
    "/reset-password"(view:"/login/reset_password") { }
    "/verify"(controller: "signup", action:"verify") { }
    "${SpringSecurityUtils.securityConfig.userstore.defaultSettingsUrl}"(controller: "signup", parseRequest: true) {
      action = [GET: "settings", PUT: "updateSettings", POST: "changePassword"]
    }
    // spring-security-core controllers
    "/login/$action?"(controller: "login")
    "/logout/$action?"(controller: "logout")
  }
}
