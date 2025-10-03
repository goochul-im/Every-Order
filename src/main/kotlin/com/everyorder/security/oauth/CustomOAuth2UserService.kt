package com.everyorder.security.oauth

import com.everyorder.domain.member.Member
import com.everyorder.domain.member.MemberRepository
import com.everyorder.domain.member.Role
import com.everyorder.domain.member.SocialType
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomOAuth2UserService(
    private val memberRepository: MemberRepository
) : DefaultOAuth2UserService() {

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val socialType = SocialType.valueOf(userRequest.clientRegistration.registrationId.uppercase())
        val userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(socialType, oAuth2User.attributes)

        val member = memberRepository.findBySocialTypeAndSocialId(socialType, userInfo.socialId)
            .map {
                it.update(userInfo.nickname, userInfo.profileImageUrl)
                it
            }
            .orElseGet { 
                memberRepository.save(
                    Member(
                        socialId = userInfo.socialId,
                        socialType = userInfo.socialType,
                        email = userInfo.email,
                        nickname = userInfo.nickname,
                        profileImageUrl = userInfo.profileImageUrl,
                        role = Role.USER,
                    )
                )
            }
        
        return CustomUserDetails(member, oAuth2User.attributes)
    }
}
