package com.kdongsu5509.imhere

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
@Import(TestFirebaseConfig::class)
class ImhereApplicationTests {

    @Test
    fun contextLoads() {
    }

}
