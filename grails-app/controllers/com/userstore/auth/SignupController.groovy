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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.savedrequest.DefaultSavedRequest

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.annotation.Secured

@Secured(['permitAll'])
class SignupController {

  def authenticationManager
  def userDetailsService

  def verify(String code) {

    if(code) {
      UserstoreDetailsService userstoreDetailsService = userDetailsService

      def resp = userstoreDetailsService.verifyCode(code)
      if(resp?.id) {
        def user = userstoreDetailsService.getUser(resp.id)
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
}
