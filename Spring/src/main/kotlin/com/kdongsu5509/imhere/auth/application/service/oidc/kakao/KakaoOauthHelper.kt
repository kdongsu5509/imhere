package com.kdongsu5509.imhere.auth.application.service.oidc.kakao

//class KakaoOauthHelper {
//    // 7. ID 토큰에서 OauthInfo 추출
//    fun getOauthInfoByIdToken(idToken: String?): OauthInfo {
//        val oidcDecodePayload: OIDCDecodePayload = getOIDCDecodePayload(idToken) // ID 토큰 디코딩
//        return OauthInfo.builder() // OIDC 페이로드에서 제공자 및 사용자 고유 ID 추출
//            .provider(OauthProvider.KAKAO)
//            .oid(oidcDecodePayload.sub) // sub 클레임이 사용자 고유 ID
//            .build()
//    }
//
//    // 8. 카카오 계정 연결 해제 (Unlink)
//    fun unlink(oid: String?) {
//        val kakaoAdminKey: String = oauthProperties.getKakaoAdminKey() // 카카오 관리자 키 설정에서 가져오기
//        val unlinkKaKaoTarget: UnlinkKaKaoTarget = UnlinkKaKaoTarget.from(oid) // 연결 해제할 사용자 고유 ID (oid)
//        val header = "KakaoAK $kakaoAdminKey" // 관리자 키를 사용한 인증 헤더 생성
//        kakaoInfoClient.unlinkUser(header, unlinkKaKaoTarget) // FeignClient를 통해 연결 해제 API 호출
//    }
//}