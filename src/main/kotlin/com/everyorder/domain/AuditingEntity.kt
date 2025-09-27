package com.everyorder.domain

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime

@EntityListeners(value = [AuditingEntityListener::class])
@MappedSuperclass
abstract class AuditingEntity : AuditingEntityId() {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt : LocalDateTime? = null
        protected set

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
        protected set
}

@EntityListeners(value = [AuditingEntityListener::class])
@MappedSuperclass
abstract class AuditingEntityId : Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set
}

/**
 * 왜 필드들에 null 값을 넣어두나? -> JPA 생명주기에 따라 JPA가 데이터베이스에 presist 하는 시점에 저장되기 때문에
 * protected set 인 이유는? -> JPA 구현체가 리플렉션이 아닌 접근자 메서드를 사용하려 할 때 private이면 문제가 접근이 불가능할수도 있어서
 */
