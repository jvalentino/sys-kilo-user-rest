package com.github.jvalentino.kilo.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileDynamic
import org.springframework.data.annotation.Id
import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table

/**
 * The auth_user table in cassandra
 * @author john.valentino
 */
@CompileDynamic
@Table(value='auth_user')
class AuthUser {

    @Id
    @PrimaryKey('auth_user_id')
    @PrimaryKeyColumn(name = 'auth_user_id', ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    @Column(value = 'auth_user_id')
    UUID authUserId

    @Column
    @JsonIgnore
    String password

    @Column
    @JsonIgnore
    String salt

    @Column(value = 'first_name')
    String firstName

    @Column(value = 'last_name')
    String lastName

}
