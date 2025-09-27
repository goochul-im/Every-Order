package com.everyorder.domain.member

import com.everyorder.domain.AuditingEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
class Member(

    @Column(nullable = false, name = "social_id")
    val socialId: String,

    @Column(nullable = false, name = "social_type")
    @Enumerated(value = EnumType.STRING)
    val socialType: SocialType

) : AuditingEntity() {

}

enum class SocialType {
    GOOGLE,
    KAKAO
}
