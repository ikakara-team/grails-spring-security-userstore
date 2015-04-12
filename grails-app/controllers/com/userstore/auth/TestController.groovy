package com.userstore.auth

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class TestController {

  def springSecurityService
  def userDetailsService
  def grailsUrlMappingsHolder

  static int count_update = 0
  static int count_confirm = 0

  @Secured(['permitAll'])
  def index() {
    render "permitAll: ${springSecurityService.principal}"
  }

  @Secured(['ROLE_USER'])
  def user() {
    def username = springSecurityService.principal?.username
    def userId = springSecurityService.principal?.id
    def email = springSecurityService.principal?.email
    def roles = springSecurityService.principal?.authorities

    render "ROLE_USER: username:${username} id:${userId} email:${email} roles:${roles}"
  }

  def admin() {
    def username = springSecurityService.principal?.username
    def userId = springSecurityService.principal?.id
    def email = springSecurityService.principal?.email
    def roles = springSecurityService.principal?.authorities

    render "ROLE_ADMIN: username:${username} id:${userId} email:${email} roles:${roles}"
  }

  def username() {
    def user = userstoreDetailsService.loadUserByUsername(params.id)
    render "username: id:${params.id} user:${user}"
  }

  def update() {
    count_update++

    def userId = springSecurityService.principal?.id
    def email = "allen${count_update}@example.com"
    def password = 'blahblah'

    UserstoreDetailsService userstoreDetailsService = userDetailsService

    //def response = userstoreDetailsService.updatePassword(userId, password)
    def response = userstoreDetailsService.updateUser(userId, '', "first${count_update}", "last${count_update}", "allen${count_update}", email, 'https://hs.org:8443/this/is/test/url')
    render "update(${count_update}): id:${userId} email:${email} response:${response}"
  }

  def confirm() {
    count_confirm++

    def userId = springSecurityService.principal?.id
    def password = 'blahblah'

    UserstoreDetailsService userstoreDetailsService = userDetailsService

    def response = userstoreDetailsService.confirmPassword(userId, password)

    render "update(${count_confirm}): id:${userId} pwd:${password} response:${response}"
  }

  def logout() {
    UserstoreDetailsService userstoreDetailsService = userDetailsService

    userstoreDetailsService.logout(request, response)

    redirect action: 'admin'
  }

  @Secured(['permitAll'])
  def urlMappings() {
    render grailsUrlMappingsHolder.urlMappings
  }

}
