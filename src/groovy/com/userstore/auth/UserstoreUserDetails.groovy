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

import org.springframework.security.core.GrantedAuthority
import grails.plugin.springsecurity.userdetails.GrailsUser

/**
 *
 * @author Allen
 */
class UserstoreUserDetails extends GrailsUser {

  final String first_name
  final String last_name
  final boolean is_email_verified

  UserstoreUserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
    boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
    String id, String firstName, String lastName, Boolean emailVerified) {

    super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities, id)
    first_name =  firstName
    last_name = lastName
    is_email_verified = emailVerified
  }

  String getFull_name() {
    String fullName = first_name
    if(fullName) {
      fullName += ' '
    }
    fullName += last_name
    return fullName
  }
}
