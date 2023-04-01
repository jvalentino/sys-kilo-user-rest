package com.github.jvalentino.kilo.service

import org.apache.commons.codec.digest.Md5Crypt
import spock.lang.Specification
import spock.lang.Subject

class UserServiceTest extends Specification {

    @Subject
    UserService subject

    def setup() {
        subject = new UserService()
    }

    void "Test generate default user info"() {
        given:
        String plaintextPassword = '37e098f0-b78d-4a48-adf1-e6c2568d4ea1'
        String randomSalt = subject.generateSalt()

        when:
        String password = Md5Crypt.md5Crypt(plaintextPassword.bytes, randomSalt)

        then:
        println password
        println randomSalt
    }
}
