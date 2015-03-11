import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.web.authentication.RequestHolderAuthenticationFilter

import com.userstore.auth.UserstoreAuthenticationProvider

class SpringSecurityUserstoreGrailsPlugin {
  def version = "0.2"
  def grailsVersion = "2.0.0 > *"
  List loadAfter = ['springSecurityCore']
  //def packaging = "binary"
  def pluginExcludes = [
    "grails-app/controllers/com/userstore/auth/TestController.groovy",
    "grails-app/i18n/*",
    "web-app/**/*"
    //'src/docs/**',
  ]
  def title = "Spring Security Userstore Plugin"
  def author = "Allen Arakaki"
  def authorEmail = ""
  def description = '''
Save time integrating user registration and authentication with Spring Security Userstore Plugin -- a datastore (service)
for Spring Security Core (Authentication) and user information.  Simply customize the UI (sign-in, sign-up, forgot-password,
reset_password and emails) and you've got user registration and authentication for your application w/o provisioning a
database and smtp server.  You also get a nice dashboard for metrics/reporting.
'''
  def documentation = "http://grails.org/plugin/spring-security-userstore"
  def license = "APACHE"
  def issueManagement = [url: 'https://github.com/ikakara-team/grails-spring-security-userstore/issues']
  def scm = [url: 'https://github.com/ikakara-team/grails-spring-security-userstore']

  def doWithSpring = {
    def conf = SpringSecurityUtils.securityConfig
    if (!conf || !conf.active) {
      println 'ERROR: There is no Spring Security configuration'
      println 'ERROR: Stop configuring Spring Security Userstore'
      return
    }

    println 'Configuring Spring Security Userstore ...'

    SpringSecurityUtils.loadSecondaryConfig 'DefaultUserstoreSecurityConfig'
    // have to get again after overlaying DefaultUserstoreSecurityConfig
    conf = SpringSecurityUtils.securityConfig

    if(!conf.userstore.secretKey || !conf.userstore.publishableKey) {
      println 'ERROR: userstore.secretKey and userstore.publishableKey are required'
      println 'ERROR: Stop configuring Spring Security Userstore'
      return
    }

    //println "secretKey:${conf.userstore.secretKey} publishableKey:${conf.userstore.publishableKey}"

    userDetailsService(com.userstore.auth.UserstoreDetailsService)

    // we need to override the usernameParameter and passwordParameter
    authenticationProcessingFilter(RequestHolderAuthenticationFilter) {
      authenticationManager = ref('authenticationManager')
      sessionAuthenticationStrategy = ref('sessionAuthenticationStrategy')
      authenticationSuccessHandler = ref('authenticationSuccessHandler')
      authenticationFailureHandler = ref('authenticationFailureHandler')
      rememberMeServices = ref('rememberMeServices')
      authenticationDetailsSource = ref('authenticationDetailsSource')
      requiresAuthenticationRequestMatcher = ref('filterProcessUrlRequestMatcher')
      usernameParameter = 'token'
      passwordParameter = 'token'
      continueChainBeforeSuccessfulAuthentication = conf.apf.continueChainBeforeSuccessfulAuthentication // false
      allowSessionCreation = conf.apf.allowSessionCreation // true
      postOnly = conf.apf.postOnly // true
      storeLastUsername = conf.apf.storeLastUsername // false
    }

    // custom authentication
    daoAuthenticationProvider(UserstoreAuthenticationProvider) {
      passwordEncoder = ref('passwordEncoder')
      saltSource = ref('saltSource')
      preAuthenticationChecks = ref('preAuthenticationChecks')
      postAuthenticationChecks = ref('postAuthenticationChecks')
      userDetailsService = ref('userDetailsService')
    }

    println '... finished configuring Spring Security Userstore'
  }
}
