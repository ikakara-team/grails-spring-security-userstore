import grails.util.GrailsNameUtils
import groovy.text.SimpleTemplateEngine

includeTargets << grailsScript("Init")
includeTargets << grailsScript('_GrailsBootstrap')

overwriteAll = false
pluginViewsDir = "$springSecurityUserstorePluginDir/grails-app/views"
appGrailsAppDir = "$basedir/grails-app"

USAGE = """
	Usage: grails s2-init-userstore\n\

  Updates grails-app/conf/Config.groovy for the Spring Security Userstore plugin

	Example: grails s2-init-userstore
"""

target(s2InitUserstore: 'Updates grails-app/conf/Config.groovy for the Spring Security Userstore plugin') {
  depends(checkVersion, configureProxy, packageApp, classpath)

  updateConfig()
  copyFiles()

  printMessage """
*******************************************************
* Your grails-app/conf/Config.groovy has been updated *
* with security settings; please verify that the      *
* values are correct.                                 *
*******************************************************
"""
}

private void updateConfig() {
  def configFile = new File(appGrailsAppDir, 'conf/Config.groovy')
  if (!configFile.exists()) {
    return
  }

  configFile.withWriterAppend { BufferedWriter writer ->
    writer.writeLine "// Added by the Spring Security Userstore plugin:"
    writer.writeLine "grails.plugin.springsecurity.userstore.publishableKey = 'ENTER USERSTORE PUBLISHABLE KEY HERE'"
    writer.writeLine "grails.plugin.springsecurity.userstore.secretKey = 'ENTER USERSTORE SECRET KEY HERE'"
    writer.newLine()
  }
}

private void copyFiles() {
  ant.mkdir dir: "$appGrailsAppDir/views/login"
  copyFile "$pluginViewsDir/login/denied.gsp", "$appGrailsAppDir/views/login/denied.gsp"
  copyFile "$pluginViewsDir/login/auth.gsp", "$appGrailsAppDir/views/login/auth.gsp"
  copyFile "$pluginViewsDir/login/forgot_password.gsp", "$appGrailsAppDir/views/login/forgot_password.gsp"
  copyFile "$pluginViewsDir/login/reset_password.gsp", "$appGrailsAppDir/views/login/reset_password.gsp"
  copyFile "$pluginViewsDir/login/sign_up.gsp", "$appGrailsAppDir/views/login/sign_up.gsp"
}

okToWrite = { String dest ->

  def file = new File(dest)
  if (overwriteAll || !file.exists()) {
    return true
  }

  String propertyName = "file.overwrite.$file.name"
  ant.input(addProperty: propertyName, message: "$dest exists, ok to overwrite?",
    validargs: 'y,n,a', defaultvalue: 'y')

  if (ant.antProject.properties."$propertyName" == 'n') {
    return false
  }

  if (ant.antProject.properties."$propertyName" == 'a') {
    overwriteAll = true
  }

  true
}

copyFile = { String from, String to ->
  if (!okToWrite(to)) {
    return
  }

  ant.copy file: from, tofile: to, overwrite: true
}

printMessage = { String message -> event('StatusUpdate', [message]) }
errorMessage = { String message -> event('StatusError', [message]) }

setDefaultTarget 's2InitUserstore'