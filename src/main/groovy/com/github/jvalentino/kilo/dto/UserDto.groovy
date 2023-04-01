package com.github.jvalentino.kilo.dto

import groovy.transform.CompileDynamic

/**
 * The information needed to login
 * @author john.valentino
 */
@CompileDynamic
class UserDto {

    String uuid
    String email
    String password
    String salt
    String firstName
    String lastName

}
