import grails.plugin.springsecurity.ReflectionUtils
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.web.authentication.RequestHolderAuthenticationFilter

class SpringSecurityUserstoreGrailsPlugin {
  // the plugin version
  def version = "0.1"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "2.0.0 > *"
  List loadAfter = ['springSecurityCore']

  //def packaging = "binary"
  //Map dependsOn = ['springSecurityCore': '2.0-RC4> *']

  // resources that are excluded from plugin packaging
  def pluginExcludes = [
        "grails-app/views/error.gsp",
        "grails-app/views/index.gsp",
        "grails-app/controllers/com/userstore/auth/TestController.groovy"
    //'src/docs/**',
  ]

  // TODO Fill in these fields
  def title = "Spring Security Userstore Plugin" // Headline display name of the plugin
  def author = "Allen Arakaki"
  def authorEmail = ""
  def description = '''
Save time integrating user registration and authentication with Spring Security Userstore Plugin -- a datastore (service)
for Spring Security Core (Authentication) and user information.  Simply customize the UI (sign-in, sign-up, forgot-password,
reset_password and emails) and you've got user registration and authentication for your application w/o provisioning a
database and smtp server.  You also get a nice dashboard for metrics/reporting.
'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugin/spring-security-userstore"

  // Extra (optional) plugin metadata

  // License: one of 'APACHE', 'GPL2', 'GPL3'
  def license = "APACHE"

  // Details of company behind the plugin (if there is one)
  //    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

  // Any additional developers beyond the author specified above.
  //    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

  // Location of the plugin's issue tracker.
  //    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

  // Online location of the plugin's browseable source code.
  //    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

  //def observe = ["springSecurityCore"]

  def doWithWebDescriptor = { xml ->
    // TODO Implement additions to web.xml (optional), this event occurs before
  }

  def doWithSpring = {
    // TODO Implement runtime spring config (optional)
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
    //
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
    daoAuthenticationProvider(com.userstore.auth.UserstoreAuthenticationProvider) {
      passwordEncoder = ref('passwordEncoder')
      saltSource = ref('saltSource')
      preAuthenticationChecks = ref('preAuthenticationChecks')
      postAuthenticationChecks = ref('postAuthenticationChecks')
      userDetailsService = ref('userDetailsService')
    }

    println '... finished configuring Spring Security Userstore'
  }

  def doWithDynamicMethods = { ctx ->
    // TODO Implement registering dynamic methods to classes (optional)
  }

  def doWithApplicationContext = { ctx ->
    // TODO Implement post initialization spring config (optional)
  }

  def onChange = { event ->
    // TODO Implement code that is executed when any artefact that this plugin is
    // watching is modified and reloaded. The event contains: event.source,
    // event.application, event.manager, event.ctx, and event.plugin.
  }

  def onConfigChange = { event ->
    // TODO Implement code that is executed when the project configuration changes.
    // The event is the same as for 'onChange'.
  }

  def onShutdown = { event ->
    // TODO Implement code that is executed when the application shuts down (optional)
  }
}
