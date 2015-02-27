grails.project.work.dir = 'target'

grails.project.dependency.resolver = 'maven'
grails.project.dependency.resolution = {

  inherits 'global'
  log 'warn'

  repositories {
    mavenLocal()
    grailsCentral()
    mavenCentral()
  }

  dependencies {
    compile "org.codehaus.groovy.modules.http-builder:http-builder:0.7.1" //http://mvnrepository.com/artifact/org.codehaus.groovy.modules.http-builder/http-builder
    // needed for tomcat
    compile ("net.sf.ehcache:ehcache-core:2.6.10") {
      export = false
    }
  }

  plugins {
    // needed for testing
    build (":tomcat:8.0.20" ){ // 8.0.18 broken - https://jira.grails.org/browse/GPTOMCAT-29
      export = false
    }

    // needed for release
    build(":release:3.0.1",
          ":rest-client-builder:2.0.3") {
      export = false
    }

    // needed for userstore
    compile(':spring-security-core:2.0-RC4')
  }
}
