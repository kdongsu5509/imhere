//package com.kdongsu5509.imhere.auth.adapter.out.jjwt
//
//import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKey
//import com.kdongsu5509.imhere.auth.adapter.out.dto.OIDCPublicKeyResponse
//import com.kdongsu5509.imhere.auth.application.port.out.JwtVerficationPort
//import com.kdongsu5509.imhere.auth.application.port.out.LoadPublicKeyPort
//import com.kdongsu5509.imhere.auth.application.service.oidc.TestJwtBuilder
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.InjectMocks
//import org.mockito.Mock
//import org.mockito.junit.jupiter.MockitoExtension
//import java.security.KeyFactory
//import java.security.spec.RSAPublicKeySpec
//import java.util.*
//
//@ExtendWith(MockitoExtension::class)
//class JjwtParserAdapterTest {
//
//    @Mock
//    private lateinit var loadPublicKeyPort: LoadPublicKeyPort
//
//    @Mock
//    private lateinit var kakaoOIDCProperties: KakaoOIDCProperties
//
//    @Mock
//    private lateinit var jwtVerficationPort: JwtVerficationPort
//
//    @InjectMocks
//    private lateinit var jjwtParserAdapter: JjwtParserAdapter
//
//    @Test
//    @DisplayName("idToken를 받으면 헤더를 검증 후 payload를 반환한다.")
//    fun parse() {
//        //given
//        val idToken = TestJwtBuilder.buildValidIdToken()
//
//        //when
//
//
//        //then
//    }
//
//    private fun createMockPublicKeyResponse(): OIDCPublicKeyResponse {
//        val keyPair = TestJwtBuilder.keyPair
//        val publicKey = keyPair.public
//        val keyFactory = KeyFactory.getInstance("RSA")
//        val keySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)
//
//        val modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(keySpec.modulus.toByteArray())
//        val exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(keySpec.publicExponent.toByteArray())
//
//        val oidcPublicKey = OIDCPublicKey(
//            kid = TestJwtBuilder.KAKAO_HEADER_KID,
//            kty = "RSA",
//            alg = "RS256",
//            use = "sig",
//            n = modulus,
//            e = exponent
//        )
//
//        return OIDCPublicKeyResponse(keys = listOf(oidcPublicKey))
//    }
//}