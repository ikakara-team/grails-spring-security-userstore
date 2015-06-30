/* Copyright 2014-2015 the original author or authors.
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
import groovy.util.logging.Slf4j

import grails.compiler.GrailsCompileStatic

import grails.plugin.springsecurity.SpringSecurityUtils

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.SaltSource
import org.springframework.security.authentication.encoding.PasswordEncoder
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsChecker
import org.springframework.security.core.userdetails.UserDetailsService

@Slf4j("LOG")
//@GrailsCompileStatic // can't do conf
class UserstoreAuthenticationProvider implements AuthenticationProvider {
  PasswordEncoder passwordEncoder
  SaltSource saltSource
  UserDetailsChecker preAuthenticationChecks
  UserDetailsChecker postAuthenticationChecks
  UserDetailsService userDetailsService

  Authentication authenticate(Authentication auth) throws AuthenticationException {
    UsernamePasswordAuthenticationToken upat = auth

    UserstoreDetailsService userstoreDetailsService = userDetailsService
    UserstoreUserDetails userDetails = userstoreDetailsService.authToken2UserDetails(upat.credentials)

    LOG.debug "AUTH: $upat  CRED: $upat.credentials USER: $userDetails"

    preAuthenticationChecks.check userDetails
    additionalAuthenticationChecks userDetails
    postAuthenticationChecks.check userDetails

    def result = new UsernamePasswordAuthenticationToken(userDetails, upat.credentials, userDetails.authorities)
    result.details = upat.details

    return result
  }

  protected void additionalAuthenticationChecks(UserstoreUserDetails userDetails) throws AuthenticationException {
    def conf = SpringSecurityUtils.securityConfig
    if(conf.userstore.requireEmailVerifiedOnSignin && !userDetails.is_email_verified) {
      throw new EmailNotVerifiedException('Sorry, your email has not been verfied.  Please verify your email.', userDetails)
    }
  }

  boolean supports(Class<? extends Object> authenticationClass) {
    UsernamePasswordAuthenticationToken.isAssignableFrom authenticationClass
  }
}
