package com.userstore.auth

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_ADMIN'])
class TestController {

    def index() { render "hello world" }
}
