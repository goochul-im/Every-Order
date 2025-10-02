package com.everyorder.security.oauth

import com.everyorder.domain.member.SocialType

abstract class OAuth2UserInfo(
    val attributes: Map<String, Any>
) {
    abstract val socialId: String
    abstract val socialType: SocialType
    abstract val nickname: String
    abstract val email: String?
    abstract val profileImageUrl: String?
}

class GoogleUserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override val socialId: String
        get() = attributes["sub"] as String
    override val socialType: SocialType
        get() = SocialType.GOOGLE
    override val nickname: String
        get() = attributes["name"] as String
    override val email: String?
        get() = attributes["email"] as? String
    override val profileImageUrl: String?
        get() = attributes["picture"] as? String
}

class KakaoUserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    private val kakaoAccount = attributes["kakao_account"] as Map<String, Any>
    private val profile = kakaoAccount["profile"] as Map<String, Any>

    override val socialId: String
        get() = attributes["id"].toString()
    override val socialType: SocialType
        get() = SocialType.KAKAO
    override val nickname: String
        get() = profile["nickname"] as String
    override val email: String?
        get() = kakaoAccount["email"] as? String
    override val profileImageUrl: String?
        get() = profile["profile_image_url"] as? String
}
