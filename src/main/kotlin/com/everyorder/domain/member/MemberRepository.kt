package com.everyorder.domain.member

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface MemberRepository : JpaRepository<Member, Long>, KotlinJdslJpqlExecutor {
    fun findBySocialTypeAndSocialId(socialType: SocialType, socialId: String): Optional<Member>
}
