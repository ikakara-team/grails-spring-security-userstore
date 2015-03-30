package com.userstore.auth

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class TestController {

  def springSecurityService
  def userstoreDetailsService

  @Secured(['permitAll'])
  def index() {
    def username = springSecurityService.getPrincipal()?.username;
    def userId = springSecurityService.getPrincipal()?.id;
    def roles = springSecurityService.getPrincipal()?.authorities;

    render "permitAll: username:${username} id:${userId} roles:${roles}"
  }

  @Secured(['ROLE_USER'])
  def user() {
    def username = springSecurityService.getPrincipal()?.username;
    def userId = springSecurityService.getPrincipal()?.id;
    def roles = springSecurityService.getPrincipal()?.authorities;

    render "ROLE_USER: username:${username} id:${userId} roles:${roles}"
  }

  def admin() {
    def username = springSecurityService.getPrincipal()?.username;
    def userId = springSecurityService.getPrincipal()?.id;
    def roles = springSecurityService.getPrincipal()?.authorities;

    render "ROLE_ADMIN: username:${username} id:${userId} roles:${roles}"
  }

  def username() {
    def user = userstoreDetailsService.loadUserByUsername(params.id)
    render "ROLE_ADMIN: id:${params.id} user:${user}"
  }
}
