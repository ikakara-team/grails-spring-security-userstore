/* Copyright 2014-2015 Allen Arakaki.  All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.userstore.auth

import groovy.transform.TypeCheckingMode
import grails.compiler.GrailsCompileStatic

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.savedrequest.DefaultSavedRequest

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
//@GrailsCompileStatic // can't do conf
class SignupController {
  static allowedMethods = [ updateSettings: "PUT", changePassword: "POST" ]

  def springSecurityService
  def authenticationManager
  def userDetailsService

  def verify(String code) {

    if(code) {
      UserstoreDetailsService userstoreDetailsService = userDetailsService

      def resp = userstoreDetailsService.verifyCode(code)
      if(resp?.id) {
        def user = userstoreDetailsService.getUserById(resp.id)
        if(user) {
          render view: "/login/verify_success", model: [user: user]
          return
        }
      }
    }

    render view: "/login/verify_fail"
  }

  def callback(String token) {

    UserstoreUserDetails uud
    boolean bNoException = false

    def conf = SpringSecurityUtils.securityConfig

    if(token) {
      try {
        UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(token, token, UserstoreDetailsService.NO_ROLES)
        Authentication authenticatedUser = authenticationManager.authenticate(upat)
        uud = authenticatedUser.principal
        bNoException = true

        //log.debug "USER: $authenticatedUser UUD: $uud"
      } catch (EmailNotVerifiedException enve) {
        uud = enve.userDetails
        log.error enve.message
      } catch (e) {
        log.error e.message
        render e.message
        return
      }

      def authorities = uud?.authorities // get default authorities

      if(conf.userstore.initRoleOnSignup) {
        // initialize authorities
        authorities = conf.userstore.initRoleOnSignup.split(',').collect { new SimpleGrantedAuthority(it) }

        UserstoreDetailsService userstoreDetailsService = userDetailsService
        def resp = userstoreDetailsService.updateRoles(uud.id, conf.userstore.initRoleOnSignup)

        //log.debug "RESPONSE: $resp"
      }

      if(bNoException && conf.userstore.autoSigninOnSignup) {
        // auto signin
        UserstoreUserDetails userDetails = new UserstoreUserDetails(
          uud.username,
          '',                         // password
          uud.enabled,                // user.enabled,          "Sorry, your account is disabled"
          uud.accountNonExpired,      // !user.accountExpired,  "Sorry, your account is expired."
          uud.credentialsNonExpired,  // !user.passwordExpired, "Sorry, your password has expired."
          uud.accountNonLocked,       // !user.accountLocked,   "Sorry, your account is locked."
          authorities,
          uud.id,
          uud.first_name ?: '',
          uud.last_name ?: '',
          uud.email ?: '',
          uud.is_email_verified)

        def upat = new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.authorities)
        //upat.details = authenticatedUser.details

        SecurityContextHolder.context.authentication = upat
      }
    }

    // return to original request
    DefaultSavedRequest request = session['SPRING_SECURITY_SAVED_REQUEST']
    if(request) {
      redirect url: request.requestURL
    } else {
      redirect uri: conf.successHandler.defaultTargetUrl ?: '/'
    }
  }

  // this requires a user to be authenticated
  def settings() {
    if(!springSecurityService.isLoggedIn()) {
      redirect uri: "/sign-in"
      return
    }

    def userId    = springSecurityService.principal?.id

    UserstoreDetailsService userstoreDetailsService = userDetailsService
    def auth = userstoreDetailsService.getUserById(userId)

    render view: '/login/settings', model:[auth: auth, form: params.form]
  }

  // this requires a user to be authenticated
  def updateSettings() {
    withForm {
      // good request
    }.invalidToken {
      // bad request
      return
    }

    if(!springSecurityService.isLoggedIn()) {
      redirect uri: "/sign-in"
      return
    }

    def userId    = springSecurityService.principal?.id

    UserstoreDetailsService userstoreDetailsService = userDetailsService

    def password = params.password
    def first_name = getChanged(params.first_name, params.prev_first_name)
    def last_name = getChanged(params.last_name, params.prev_last_name)
    def username = getChanged(params.username, params.prev_username)
    def email = getChanged(params.email, params.prev_email)

    if(password && (first_name || last_name || username || email)) {
      def auth = userstoreDetailsService.confirmPassword(userId, password)
      if(auth) {
        if(username) {
          // verify that the username not already taken
          auth = userstoreDetailsService.getUserByUsername(username)
          if(auth) {
            flash.message = g.message(code: "springSecurityUserstore.account.settings.username.failed", args: [username])
            redirect uri: "${SpringSecurityUtils.securityConfig.userstore.defaultSettingsUrl}", params:[form: 'settings']
            return
          }
        }
        if(email) {
          // verify that the email not already taken
          auth = userstoreDetailsService.getUserByEmail(email)
          if(auth) {
            flash.message = g.message(code: "springSecurityUserstore.account.settings.email.failed", args: [email])
            redirect uri: "${SpringSecurityUtils.securityConfig.userstore.defaultSettingsUrl}", params:[form: 'settings']
            return
          }
        }

        auth = userstoreDetailsService.updateUser(userId, '', first_name, last_name, username, email, "${grailsApplication.config.grails.secureServerURL}/verify")
        if(auth?.updated_at) {
          if(email && !auth.is_email_verified) {
            // email changed, we need to logout
            flash.message = g.message(code: "springSecurityUserstore.account.settings.email.success")
            userstoreDetailsService.logout(request, response)
          } else {
            if(!username) {
              username = springSecurityService.principal?.username
            }
            springSecurityService.reauthenticate(username)
            flash.message = g.message(code: "springSecurityUserstore.account.settings.updated", args: [auth?.updated_at])
          }
        } else {
          flash.message = g.message(code: "springSecurityUserstore.account.settings.failed")
        }
      } else {
        flash.message = g.message(code: "springSecurityUserstore.account.password.confirm")
      }
    } else {
      flash.message = g.message(code: "springSecurityUserstore.account.settings.invalid")
    }

    redirect uri: "${SpringSecurityUtils.securityConfig.userstore.defaultSettingsUrl}", params:[form: 'settings']
  }

  // this requires a user to be authenticated
  def changePassword() {
    withForm {
      // good request
    }.invalidToken {
      // bad request
      return
    }

    if(!springSecurityService.isLoggedIn()) {
      redirect uri: "/sign-in"
      return
    }

    def userId    = springSecurityService.principal?.id

    UserstoreDetailsService userstoreDetailsService = userDetailsService

    def password = params.password
    def newPassword = params.newPassword
    def confirmPassword = params.confirmPassword

    if(password && newPassword && confirmPassword && (newPassword == confirmPassword)) {
      def auth = userstoreDetailsService.confirmPassword(userId, password)
      if(auth) {
        auth = userstoreDetailsService.updatePassword(userId, newPassword)
        if(auth?.updated_at) {
          flash.message = g.message(code: "springSecurityUserstore.account.password.updated", args: [auth?.updated_at])
        } else {
          flash.message = g.message(code: "springSecurityUserstore.account.password.failed")
        }
      } else {
        flash.message = g.message(code: "springSecurityUserstore.account.password.confirm")
      }
    } else {
      flash.message = g.message(code: "springSecurityUserstore.account.password.invalid")
    }

    redirect (uri: "${SpringSecurityUtils.securityConfig.userstore.defaultSettingsUrl}", params:[form: 'password'])
  }

  protected String getChanged(String newinput, String oldinput) {
    if(newinput == oldinput) {
      return null
    }
    return newinput
  }

}
