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

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import groovyx.net.http.RESTClient

/**
 *
 * @author Allen
 */
@Slf4j("LOG")
@CompileStatic
class UserstoreInstance {
  private static final String URL_USER = "https://api.userstore.io/1/users/"
  private static final String URL_TOKEN = "https://api.userstore.io/1/tokens/"
  private static final String URL_AUTH = "https://api.userstore.io/1/auth/"

  static RESTClient tokenClient(String key) {
    client(key, URL_TOKEN)
  }

  static RESTClient userClient(String key) {
    client(key, URL_USER)
  }

  static RESTClient authClient(String key) {
    client(key, URL_AUTH)
  }

  private static RESTClient client(String key, String url) {
    def client = new RESTClient(url)
    //client.ignoreSSLIssues()
    // below doesn't work:
    //client.auth.basic('secretkey-15a03ec8490bcdc24d2457a3e5d57a64', 'blahblah')
    // http://stackoverflow.com/questions/19456670/why-is-httpbuilder-basic-auth-not-working
    //
    // http://stackoverflow.com/questions/6588256/using-groovy-http-builder-in-preemptive-mode
    client.setHeaders(Authorization: "Basic ${(key + ':').bytes.encodeBase64()}")

    return client
  }
}
