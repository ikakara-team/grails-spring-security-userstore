security {
  userstore {
    secretKey = "SECRET_KEY"
    publishableKey = "PUBLISHABLE_KEY"
    defaultRoleOnSignin = "ROLE_NONE"
    initRoleOnSignup = "ROLE_USER"
    autoSigninOnSignup = true
    requireEmailVerifiedOnSignin = false
  }
}