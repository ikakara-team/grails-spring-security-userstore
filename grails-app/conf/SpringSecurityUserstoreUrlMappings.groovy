class SpringSecurityUserstoreUrlMappings {

  static mappings = {
    "/sign-in"(view:"/login/auth") { }
    "/sign-up"(view:"/login/sign_up") { }
    "/sign-up-callback"(controller: "signup", action:"callback")
    "/forgot-password"(view:"/login/forgot_password") { }
    "/reset-password"(view:"/login/reset_password") { }
    "/verify"(controller: "signup", action:"verify") { }
    // spring-security-core controllers
    "/login/$action?"(controller: "login")
    "/logout/$action?"(controller: "logout")
  }
}
