package com.github.jvalentino.kilo.service

import com.github.jvalentino.kilo.dto.UserDto
import com.github.jvalentino.kilo.entity.AuthUser
import com.github.jvalentino.kilo.eventing.UserProducer
import com.github.jvalentino.kilo.repo.AuthUserRepo
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.apache.commons.codec.digest.Md5Crypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.apache.commons.codec.digest.B64

/**
 * A general service used for dealing with users
 * @author john.valentino
 */
@CompileDynamic
@Service
@Slf4j
class UserService {

    @Autowired
    AuthUserRepo authUserRepo

    @Autowired
    UserProducer userProducer

    List<AuthUser> findUsers(List<String> ids) {
        List<UUID> list = []
        for (String id : ids) {
            list.add(UUID.fromString(id))
        }
        authUserRepo.findAllById(list)
    }

    int countCurrentUsers() {
        authUserRepo.count()
    }

    AuthUser isValidUser(String email, String password) {
        log.info("Checking to see if ${email} is a valid user with its given password...")
        List<AuthUser> users = authUserRepo.findAdminUser(email)

        if (users.size() == 0) {
            log.info("${email} doesn't have any email matches, so not valid")
            return null
        }

        AuthUser user = users.first()
        String expected = Md5Crypt.md5Crypt(password.bytes, user.salt)

        if (expected == user.password) {
            log.info("Email ${email} gave a password that matches the salted MD5 hash")
            return user
        }

        log.info("Email ${email} gave a passowrd that doesn't match the salted MD5 hash")
        null
    }

    UserDto generateUser(UserDto input) {
        UserDto output = new UserDto()
        String randomSalt = this.generateSalt()
        output.with {
            uuid = UUID.randomUUID().toString()
            email = input.email
            firstName = input.firstName
            lastName = input.lastName
            salt = randomSalt
            password = Md5Crypt.md5Crypt(input.password.bytes, randomSalt)
        }

        userProducer.produce(output)

        input
    }

    String generateSalt() {
        '$1$' + B64.getRandomSalt(8)
    }

}
