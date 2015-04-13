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

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import groovy.transform.TypeCheckingMode
import grails.compiler.GrailsCompileStatic

import static groovyx.net.http.ContentType.URLENC

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.logout.LogoutHandler

//@GrailsCompileStatic // can't do conf
class UserstoreDetailsService implements GrailsUserDetailsService {

  static transactional = false

  /**
   * Some Spring Security classes (e.g. RoleHierarchyVoter) expect at least
   * one role, so we give a user with no granted roles this one which gets
   * past that restriction but doesn't grant anything.
   */
  public static final List NO_ROLES = [new SimpleGrantedAuthority(SpringSecurityUtils.NO_ROLE)]

  List<LogoutHandler> logoutHandlers

  void logout(HttpServletRequest request, HttpServletResponse response) {
    Authentication auth = SecurityContextHolder.context.authentication
    if (auth) {
      logoutHandlers.each  { handler->
        log.debug "logout: ${handler.class}"
        handler.logout(request, response, auth)
      }
    }
  }

  UserstoreUserDetails loadUserByUsername(String username, boolean loadRoles = true)
  throws UsernameNotFoundException {
    log.debug "loadUserByUsername - loading user ..."
    def user = getUserByUsername(username)
    return userStore2UserDetails(user)
  }

  UserstoreUserDetails loadUserByEmail(String email, boolean loadRoles = true)
  throws UsernameNotFoundException {
    log.debug "loadUserByEmail - loading user ..."
    def user = getUserByEmail(email)
    return userStore2UserDetails(user)
  }

  UserstoreUserDetails loadUserById(String id, boolean loadRoles = true)
  throws UsernameNotFoundException {
    log.debug "loadUserById - loading user ..."
    def user = getUserById(id)
    return userStore2UserDetails(user)
  }

  UserstoreUserDetails authToken2UserDetails(String authtoken) throws UsernameNotFoundException {
    def authuser = authToken2AuthUser(authtoken)
    if (!authuser) {
      throw new UsernameNotFoundException('User not found', authtoken)
    }

    def user = authuser?.data

    return userStore2UserDetails(user)
  }

  private authToken2AuthUser(String token) {
    def response

    def conf = SpringSecurityUtils.securityConfig
    def tokenClient = UserstoreInstance.tokenClient(conf.userstore.secretKey)

    try { // expect an exception from a 404 response:
      response = tokenClient.get(path: token, headers: ["User-Agent": "grails-spring-security-userstore"])

      log.debug "authToken2AuthUser success: ${response?.statusLine} ${response?.allHeaders}"

    } catch( ex ) { // The exception is used for flow control but has access to the response as well:
      log.error "${token} ${ex.message}"
    }

    return response?.data
  }

  private UserstoreUserDetails userStore2UserDetails(Map user, boolean loadRoles = true) {
    if(!user) {
      return null
    }

    def authorities

    def conf = SpringSecurityUtils.securityConfig

    if(loadRoles) {
      if(user.roles) {
        authorities = user.roles?.collect { new SimpleGrantedAuthority(it) }
      } else if(conf.userstore.defaultRoleOnSignin) {
        authorities = [new SimpleGrantedAuthority(conf.userstore.defaultRoleOnSignin)]
      } else {
        authorities = []
      }

    } else {
      authorities = []
    }

    log.debug "userStore2UserDetails - user: ${user} authorities: ${authorities}"

    UserstoreUserDetails userDetails = new UserstoreUserDetails(
      user.username,
      '',             // password
      user.is_active, // user.enabled,          "Sorry, your account is disabled"
      true,           // !user.accountExpired,  "Sorry, your account is expired."
      true,           // !user.passwordExpired, "Sorry, your password has expired."
      true,           // !user.accountLocked,   "Sorry, your account is locked."
      authorities,
      user.id,
      user.first_name,
      user.last_name,
      user.email,
      user.is_email_verified)

    return userDetails
  }

  // https://www.userstore.io/docs/rest-api/users#update-user
  def updateRoles(String id, String roles) { // comma delimited roles
    def response

    def conf = SpringSecurityUtils.securityConfig
    def userClient = UserstoreInstance.userClient(conf.userstore.secretKey)

    try { // expect an exception from a 404 response:
      response = userClient.put(path: id,
        headers: ["User-Agent": "grails-spring-security-userstore"],
        requestContentType: URLENC,
        body: [roles: roles])

      log.debug "updateRoles success: ${response?.statusLine} ${response?.allHeaders}"

    } catch( ex ) { // The exception is used for flow control but has access to the response as well:
      log.error "${id} ${ex.message}"
    }

    return response?.data
  }

  // https://www.userstore.io/docs/rest-api/users#get-user
  def getUserById(String uid) {
    def response

    def conf = SpringSecurityUtils.securityConfig
    def userClient = UserstoreInstance.userClient(conf.userstore.secretKey)

    try { // expect an exception from a 404 response:
      response = userClient.get(path: uid,
        headers: ["User-Agent": "grails-spring-security-userstore"])

      log.debug "getUserById success: ${response?.statusLine} ${response?.allHeaders}"

    } catch( ex ) { // The exception is used for flow control but has access to the response as well:
      log.error "${uid} ${ex.message}"
    }

    return response?.data
  }

  // https://api.userstore.io/users?username=USERNAME
  def getUserByUsername(String username) {
    def response

    def conf = SpringSecurityUtils.securityConfig
    def userClient = UserstoreInstance.userClient(conf.userstore.secretKey)

    try { // expect an exception from a 404 response:
      response = userClient.get(query: [username: username],
        headers: ["User-Agent": "grails-spring-security-userstore"])

      log.debug "getUserByUsername success: ${response?.statusLine} ${response?.allHeaders}"

    } catch( ex ) { // The exception is used for flow control but has access to the response as well:
      log.error "${username} ${ex.message}"
    }

    return response?.data
  }

  // https://api.userstore.io/users?email=EMAIL
  def getUserByEmail(String email) {
    def response

    def conf = SpringSecurityUtils.securityConfig
    def userClient = UserstoreInstance.userClient(conf.userstore.secretKey)

    try { // expect an exception from a 404 response:
      response = userClient.get(query: [email: email],
        headers: ["User-Agent": "grails-spring-security-userstore"])

      log.debug "getUserByEmail success: ${response?.statusLine} ${response?.allHeaders}"

    } catch( ex ) { // The exception is used for flow control but has access to the response as well:
      log.error "${email} ${ex.message}"
    }

    return response?.data
  }

  // https://www.userstore.io/docs/rest-api/authentication#verify-account
  def verifyCode(String code) {
    def response

    def conf = SpringSecurityUtils.securityConfig
    def authClient = UserstoreInstance.authClient(conf.userstore.secretKey)

    try { // expect an exception from a 404 response:
      response = authClient.post( path: 'verify',
        headers: ["User-Agent": "grails-spring-security-userstore"],
        requestContentType: URLENC,
        body: [code: code])

      log.debug "verifyCode success: ${response?.statusLine} ${response?.allHeaders}"

    } catch( ex ) { // The exception is used for flow control but has access to the response as well:
      log.error "${code} ${ex.message}"
    }

    return response?.data
  }

  // https://www.userstore.io/docs/rest-api/authentication#confirm-password
  Boolean confirmPassword(String uid, String password) {
    def response

    def conf = SpringSecurityUtils.securityConfig
    def authClient = UserstoreInstance.authClient(conf.userstore.secretKey)

    try { // expect an exception from a 404 response:
      response = authClient.post( path: 'confirm',
        headers: ["User-Agent": "grails-spring-security-userstore"],
        requestContentType: URLENC,
        body: [id: uid, password: password])

      log.debug "confirmPassword success: ${response?.statusLine} ${response?.allHeaders}"

    } catch( ex ) { // The exception is used for flow control but has access to the response as well:
      log.error "${code} ${ex.message}"
    }

    log.debug "confirmPassword response.data: ${response?.data}"

    return response?.data?.confirmed
  }

  def updatePassword(String uid, String password) {
    return updateUser(uid, password)
  }

  //https://www.userstore.io/docs/rest-api/users#update-user
  def updateUser(String uid, String password, String first=null, String last=null, String username=null, String email=null, String verifyUrl=null) {
    def response

    def queryMap = [:]
    def bodyMap = [:]

    if(first) {
      bodyMap['first_name'] = first
    }
    if(last) {
      bodyMap['last_name'] = last
    }
    if(username) {
      bodyMap['username'] = username
    }
    if(password) {
      bodyMap['password'] = password
    }
    if(email) {
      bodyMap['email'] = email
      if(verifyUrl) {
        queryMap['verify_user'] = true
        queryMap['verify_url'] = verifyUrl
      }
    }

    if(!uid || !bodyMap) {
      return null
    }

    log.debug("updateUser - passes the input check ...")

    def conf = SpringSecurityUtils.securityConfig
    def userClient = UserstoreInstance.userClient(conf.userstore.secretKey)

    try { // expect an exception from a 404 response:
      response = userClient.put(path: uid,
        query: queryMap,
        headers: ["User-Agent": "grails-spring-security-userstore"],
        requestContentType: URLENC,
        body: bodyMap)

      log.debug "updateUser success: ${response?.statusLine} ${response?.allHeaders}"

    } catch( ex ) { // The exception is used for flow control but has access to the response as well:
      log.error "${uid} ${ex.message}"
    }

    return response?.data
  }

}