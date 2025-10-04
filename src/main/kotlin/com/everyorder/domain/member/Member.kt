package com.everyorder.domain.member

import com.everyorder.domain.AuditingEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import net.datafaker.Faker


@Entity
class Member(

    @Column(nullable = false, name = "social_id")
    val socialId: String,

    @Column(nullable = false, name = "social_type")
    @Enumerated(value = EnumType.STRING)
    val socialType: SocialType,

    @Column(nullable = true, name = "email")
    var email: String?,

    @Column(nullable = false, name = "nickname")
    var nickname: String,

    @Column(nullable = true, name = "profile_image_url")
    var profileImageUrl: String?,

    @Column(nullable = false, name = "role")
    @Enumerated(value = EnumType.STRING)
    var role: Role,

) : AuditingEntity() {
    fun update(nickname: String, profileImageUrl: String?) {
        this.nickname = nickname
        this.profileImageUrl = profileImageUrl
    }

    companion object {

        // data-faker 사용법 https://g.co/gemini/share/051321b71987
        const val FAKE_SOCIAL_ID : String = "fakemember"

        fun createFakeMember(): Member {
            return Member(
                FAKE_SOCIAL_ID,
                SocialType.GOOGLE,
                Faker().internet().emailAddress(),
                Faker().name().fullName(),
                "fake-image",
                Role.USER,
            )
        }
    }
}

enum class SocialType {
    GOOGLE,
    KAKAO,
}

enum class Role {
    USER,
    ADMIN
}
