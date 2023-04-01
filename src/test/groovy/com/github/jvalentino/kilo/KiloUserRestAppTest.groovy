package com.github.jvalentino.kilo


import org.springframework.boot.SpringApplication
import spock.lang.Specification

class KiloUserRestAppTest extends Specification {

    def setup() {
        GroovyMock(SpringApplication, global:true)
    }

    def "test main"() {
        when:
        KiloUserRestApp.main(null)

        then:
        1 * SpringApplication.run(KiloUserRestApp, null)
    }

}
