package com.github.jvalentino.kilo.rest

import com.github.jvalentino.kilo.dto.CountDto
import com.github.jvalentino.kilo.dto.ListDto
import com.github.jvalentino.kilo.dto.UserDto
import com.github.jvalentino.kilo.entity.AuthUser
import com.github.jvalentino.kilo.eventing.UserProducer
import com.github.jvalentino.kilo.repo.AuthUserRepo
import com.github.jvalentino.kilo.util.BaseIntg
import com.github.jvalentino.kilo.util.DateGenerator
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MvcResult

import static org.mockito.Mockito.verify
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class UserRestIntgTest extends BaseIntg {

    @MockBean
    AuthUserRepo authUserRepo

    @MockBean
    UserProducer userProducer

    @Captor
    ArgumentCaptor<UserDto> userProducerCaptor

    def setup() {
        GroovyMock(DateGenerator, global:true)
    }

    void "Test /user/count"() {
        given:
        org.mockito.Mockito.when(authUserRepo.count()).thenReturn(1L)

        when:
        MvcResult response = mvc.perform(
                get("/user/count").header('X-Auth-Token', '123'))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        CountDto result = toObject(response, CountDto)
        result.value == 1L
    }

    void "test /user/login"() {
        given:
        AuthUser user = new AuthUser()
        user.with {
            authUserId = UUID.fromString('b9ccf7dd-a1ef-4b86-9087-88a72e144aea')
            firstName = 'alpha'
            lastName = 'bravo'
            salt = '$1$9DrjDNgl'
            password = '$1$9DrjDNgl$Bdl2Dq3DKMRYwiGeJWdCj.'
            email = 'charlie'
        }

        org.mockito.Mockito.when(authUserRepo.findAdminUser('charlie')).thenReturn([user])

        and:
        UserDto input = new UserDto()
        input.with {
            email = 'charlie'
            password = '37e098f0-b78d-4a48-adf1-e6c2568d4ea1'
        }

        when:
        MvcResult response = mvc.perform(
                post("/user/login")
                        .header('X-Auth-Token', '123')
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJson(input)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        AuthUser result = toObject(response, AuthUser)
        result.email == 'charlie'
        result.salt == null
        result.password == null
        result.firstName == 'alpha'
        result.lastName == 'bravo'
        result.authUserId.toString() == 'b9ccf7dd-a1ef-4b86-9087-88a72e144aea'
    }

    void "test /user/list"() {
        given:
        AuthUser user = new AuthUser()
        user.with {
            authUserId = UUID.fromString('b9ccf7dd-a1ef-4b86-9087-88a72e144aea')
            firstName = 'alpha'
            lastName = 'bravo'
            salt = '$1$9DrjDNgl'
            password = '$1$9DrjDNgl$Bdl2Dq3DKMRYwiGeJWdCj.'
            email = 'charlie'
        }

        org.mockito.Mockito.when(authUserRepo.findAllById([user.authUserId])).thenReturn([user])

        and:
        ListDto input = new ListDto()
        input.values = ['b9ccf7dd-a1ef-4b86-9087-88a72e144aea']

        when:
        MvcResult response = mvc.perform(
                post("/user/list")
                        .header('X-Auth-Token', '123')
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJson(input)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        List<AuthUser> results = toObject(response, List<AuthUser>)
        results.size() == 1
        results.first().email == 'charlie'

    }

    void "Test /user/new"() {
        given:
        UserDto input = new UserDto()
        input.with {
            firstName = 'alpha'
            lastName = 'bravo'
            email = 'charlie'
            password = 'delta'
        }

        when:
        MvcResult response = mvc.perform(
                post("/user/new")
                        .header('X-Auth-Token', '123')
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJson(input)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()

        then:
        verify(userProducer).produce(userProducerCaptor.capture())
        UserDto sent = userProducerCaptor.getValue()
        sent.firstName == 'alpha'
        sent.lastName == 'bravo'
        sent.email == 'charlie'
        sent.password != 'delta'
        sent.salt != null
        sent.uuid != null
    }

}
