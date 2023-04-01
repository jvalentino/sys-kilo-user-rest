package com.github.jvalentino.kilo.rest

import com.github.jvalentino.kilo.dto.CountDto
import com.github.jvalentino.kilo.dto.ListDto
import com.github.jvalentino.kilo.dto.UserDto
import com.github.jvalentino.kilo.entity.AuthUser
import com.github.jvalentino.kilo.service.UserService
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

/**
 * The general rest endpoint for accessing all user related things.
 * @author john.valentino
 */
@CompileDynamic
@Slf4j
@RestController
class UserRest {

    @Autowired
    UserService userService

    @GetMapping('/user/count')
    @CircuitBreaker(name = 'UserCount')
    CountDto performCount() {
        CountDto result = new CountDto()
        result.with {
            value = userService.countCurrentUsers()
        }
        result
    }

    @PostMapping('/user/login')
    @CircuitBreaker(name = 'UserLogin')
    AuthUser login(@RequestBody UserDto input, HttpServletResponse response) {
        AuthUser user = userService.isValidUser(input.email, input.password)

        if (user == null) {
            response.status = 401
        }
        user
    }

    @PostMapping('/user/list')
    @CircuitBreaker(name = 'UserList')
    List<AuthUser> listUsers(@RequestBody ListDto input) {
        userService.findUsers(input.values)
    }

    @PostMapping('/user/new')
    @CircuitBreaker(name = 'UserGenerate')
    UserDto generateUser(@RequestBody UserDto input) {
        userService.generateUser(input)
    }

}
