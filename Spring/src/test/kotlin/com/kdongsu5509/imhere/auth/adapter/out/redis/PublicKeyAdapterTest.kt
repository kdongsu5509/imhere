package com.kdongsu5509.imhere.auth.adapter.out.redis

import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey
import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
import com.kdongsu5509.imhere.auth.adapter.out.jjwt.KakaoOIDCProperties
import com.kdongsu5509.imhere.auth.application.port.out.CachePort
import com.kdongsu5509.imhere.auth.application.service.oidc.TestJwtBuilder
import com.kdongsu5509.imhere.common.exception.implementation.auth.KakaoOIDCKeyFetchFailFromRedisException
import com.kdongsu5509.imhere.common.exception.implementation.auth.KakaoOIDCPublicKeyNotFoundException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.util.*

@ExtendWith(MockitoExtension::class)
class PublicKeyAdapterTest {

    @Mock
    private lateinit var cachePort: CachePort

    @Mock
    private lateinit var kakaoOIDCProperties: KakaoOIDCProperties

    @InjectMocks
    private lateinit var publicKeyAdapter: PublicKeyAdapter

    @Test
    @DisplayName("KID을 입력 받으면 OIDC 퍼블릭 키를 제공한다")
    fun success() {
        //given
        val testKID = TestJwtBuilder.KAKAO_HEADER_KID
        val testCacheKey = "tempCacheKey"
        val mockPublicKeySet = createMockPublicKeySet()
        `when`(kakaoOIDCProperties.cacheKey).thenReturn(testCacheKey)
        `when`(cachePort.find(testCacheKey)).thenReturn(
            mockPublicKeySet
        )

        //when
        val publicKey = publicKeyAdapter.loadPublicKey(testKID)

        //then
        Assertions.assertThat(publicKey).isNotNull
    }

    @Test
    @DisplayName("없는 KID을 입력 받으면 `KakaoOIDCPublicKeyNotFoundException` 을 야기한다")
    fun fail_no_exist_kid() {
        //given
        val testKID = TestJwtBuilder.KAKAO_HEADER_KID
        val testCacheKey = "tempCacheKey"
        val mockPublicKeySet = createMockPublicKeySet()
        `when`(kakaoOIDCProperties.cacheKey).thenReturn(testCacheKey)
        `when`(cachePort.find(testCacheKey)).thenReturn(
            mockPublicKeySet
        )

        //when, then
        Assertions.assertThatThrownBy {
            publicKeyAdapter.loadPublicKey(testKID)
        }.isExactlyInstanceOf(KakaoOIDCPublicKeyNotFoundException::class.java)
    }

    @Test
    @DisplayName("없는 레디스 키를 입력하면 KakaoOIDCKeyFetchFailFromRedisException가 발생한다")
    fun fail_no_exist_redis_key() {
        //given
        val notExistKID = "notExistKID"
        val noExistRedisKey = "NOT_EXIST_IN_REDIS"
        `when`(kakaoOIDCProperties.cacheKey).thenReturn(noExistRedisKey)
        `when`(cachePort.find(noExistRedisKey)).thenReturn(
            null
        )

        //when, then
        Assertions.assertThatThrownBy {
            publicKeyAdapter.loadPublicKey(notExistKID)
        }.isExactlyInstanceOf(KakaoOIDCKeyFetchFailFromRedisException::class.java)
    }

    private fun createMockPublicKeySet(): OIDCPublicKeyResponse {
        val keyPair = TestJwtBuilder.keyPair
        val publicKey = keyPair.public
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)

        val modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(keySpec.modulus.toByteArray())
        val exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(keySpec.publicExponent.toByteArray())

        val oidcPublicKey = OIDCPublicKey(
            kid = TestJwtBuilder.KAKAO_HEADER_KID,
            kty = "RSA",
            alg = "RS256",
            use = "sig",
            n = modulus,
            e = exponent
        )

        return OIDCPublicKeyResponse(keys = listOf(oidcPublicKey))
    }

}