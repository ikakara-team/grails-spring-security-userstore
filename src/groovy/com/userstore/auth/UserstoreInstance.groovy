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

import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*

import grails.plugin.springsecurity.SpringSecurityUtils

/**
 *
 * @author Allen
 */
class UserstoreInstance {
  private static final String URL_USER = "https://api.userstore.io/1/users/"
  private static final String URL_TOKEN = "https://api.userstore.io/1/tokens/"
  private static final String URL_AUTH = "https://api.userstore.io/1/auth/"

  static public def tokenClient(String key) {
    def client = new RESTClient(URL_TOKEN)
    //client.ignoreSSLIssues()
    // below doesn't work:
    //client.auth.basic('secretkey-15a03ec8490bcdc24d2457a3e5d57a64', 'blahblah')
    // http://stackoverflow.com/questions/19456670/why-is-httpbuilder-basic-auth-not-working
    //
    // http://stackoverflow.com/questions/6588256/using-groovy-http-builder-in-preemptive-mode
    client.setHeaders('Authorization':"Basic " + "${key}:".bytes.encodeBase64().toString())

    return client
  }

  static public def userClient(String key) {
    def client = new RESTClient(URL_USER)
    //client.ignoreSSLIssues()
    client.setHeaders('Authorization':"Basic " + "${key}:".bytes.encodeBase64().toString())

    return client
  }

  static public def authClient(String key) {
    def client = new RESTClient(URL_AUTH)
    //client.ignoreSSLIssues()
    client.setHeaders('Authorization':"Basic " + "${key}:".bytes.encodeBase64().toString())

    return client
  }
}

