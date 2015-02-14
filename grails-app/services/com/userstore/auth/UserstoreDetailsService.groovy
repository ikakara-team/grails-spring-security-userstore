/* Copyright 2014 Allen Arakaki.  All Rights Reserved.
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

import static groovyx.net.http.ContentType.URLENC

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 *
 * @author Allen
 */
class UserstoreDetailsService implements GrailsUserDetailsService {
  /**
   * Some Spring Security classes (e.g. RoleHierarchyVoter) expect at least
   * one role, so we give a user with no granted roles this one which gets
   * past that restriction but doesn't grant anything.
   */
  public static final List NO_ROLES = [new SimpleGrantedAuthority(SpringSecurityUtils.NO_ROLE)]

  UserstoreUserDetails loadUserByUsername(String username, boolean loadRoles)
  throws UsernameNotFoundException {
    log.debug "UserstoreDetailsService - loading user ..."
    return loadUserByUsername(username)
  }

  UserstoreUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return null
  }

  UserstoreUserDetails authToken2UserDetails(String authtoken) throws UsernameNotFoundException {
    def authuser = authToken2AuthUser(authtoken)
    if (!authuser) {
      throw new UsernameNotFoundException('User not found', authtoken)
    }

    def user = authuser?.data
    def authorities

    def conf = SpringSecurityUtils.securityConfig

    if(user.roles) {
      authorities = user.roles?.collect { new SimpleGrantedAuthority(it) }
    } else if(conf.userstore.defaultRoleOnSignin) {
      authorities = [new SimpleGrantedAuthority(conf.userstore.defaultRoleOnSignin)]
    } else {
      authorities = []
    }

    log.debug "authToken2UserDetails - user: ${user} authorities: ${authorities}"

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
      user.is_email_verified)

    return userDetails
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

  // commad delimited roles
  def updateRoles(String id, String roles) {
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

  def getUser(String uid) {
    def response

    def conf = SpringSecurityUtils.securityConfig
    def userClient = UserstoreInstance.userClient(conf.userstore.secretKey)

    try { // expect an exception from a 404 response:
      response = userClient.get(path: uid,
        headers: ["User-Agent": "grails-spring-security-userstore"])

      log.debug "getUser success: ${response?.statusLine} ${response?.allHeaders}"

    } catch( ex ) { // The exception is used for flow control but has access to the response as well:
      log.error "${uid} ${ex.message}"
    }

    return response?.data
  }

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
}