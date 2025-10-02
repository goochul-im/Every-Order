package com.everyorder.security.oauth

import com.everyorder.domain.member.SocialType
import java.util.*

object OAuth2UserInfoFactory {
    fun getOAuth2UserInfo(socialType: SocialType, attributes: Map<String, Any>): OAuth2UserInfo {
        return when (socialType) {
            SocialType.GOOGLE -> GoogleUserInfo(attributes)
            SocialType.KAKAO -> KakaoUserInfo(attributes)
        }
    }
}
