package com.github.jvalentino.kilo.repo

import com.github.jvalentino.kilo.entity.AuthUser
import groovy.transform.CompileDynamic
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

/**
 * Repo class for the auth_user table
 * @author john.valentino
 */
@CompileDynamic
@Repository
interface AuthUserRepo extends CassandraRepository<AuthUser, UUID>  {

    @Query('select * from auth_user where email = ?0')
    List<AuthUser> findAdminUser(String email)

}
