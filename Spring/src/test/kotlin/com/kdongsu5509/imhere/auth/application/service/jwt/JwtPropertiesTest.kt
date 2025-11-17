package com.kdongsu5509.imhere.auth.application.service.jwt

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "jwt.secret=testsecretkey",
        "jwt.accessExpirationMinutes=1",
        "jwt.refreshExpirationDays=1",
        "jwt.accessHeaderName=Authorization",
    ]
)
class JwtPropertiesTest {
    @Autowired
    private lateinit var jwtProperties: JwtProperties

    @Test
    @DisplayName("비밀번호를 잘 가져오는지 테스트")
    fun testGetSecret() {
        // given
        val secretInTestApplicationYaml = "testsecretkey"
        // when, then
        assertEquals(secretInTestApplicationYaml, jwtProperties.secret)
    }

    @Test
    @DisplayName("accessExpirationMinutes를 잘 가져오는지 테스트")
    fun testGetAccessExpirationMinutes() {
        // given
        val accessExpirationInTestApplicationYaml = 1L
        // when, then
        assertEquals(accessExpirationInTestApplicationYaml, jwtProperties.accessExpirationMinutes)
    }

    @Test
    @DisplayName("refreshExpirationDays를 잘 가져오는지 테스트")
    fun testGetRefreshExpirationDays() {
        // given
        val refreshExpirationInTestApplicationYaml = 1L
        // when, then
        assertEquals(refreshExpirationInTestApplicationYaml, jwtProperties.refreshExpirationDays)
    }

    @Test
    @DisplayName("accessHeaderName을 잘 가져오는지 테스트")
    fun testGetAccessHeaderName() {
        // given
        val accessHeaderNameInTestApplicationYaml = "Authorization"
        // when, then
        assertEquals(accessHeaderNameInTestApplicationYaml, jwtProperties.accessHeaderName)
    }
}