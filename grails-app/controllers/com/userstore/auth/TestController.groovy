package com.userstore.auth

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class TestController {

  def springSecurityService
  def userstoreDetailsService

  @Secured(['permitAll'])
  def index() {
    render "permitAll: ${springSecurityService.principal}"
  }

  @Secured(['ROLE_USER'])
  def user() {
    def username = springSecurityService.principal?.username;
    def userId = springSecurityService.principal?.id;
    def email = springSecurityService.principal?.email;
    def roles = springSecurityService.principal?.authorities;

    render "ROLE_USER: username:${username} id:${userId} email:${email} roles:${roles}"
  }

  def admin() {
    def username = springSecurityService.principal?.username;
    def userId = springSecurityService.principal?.id;
    def email = springSecurityService.principal?.email;
    def roles = springSecurityService.principal?.authorities;

    render "ROLE_ADMIN: username:${username} id:${userId} email:${email} roles:${roles}"
  }

  def username() {
    def user = userstoreDetailsService.loadUserByUsername(params.id)
    render "ROLE_ADMIN: id:${params.id} user:${user}"
  }
}
