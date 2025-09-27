package com.everyorder.domain.store

import com.everyorder.domain.AuditingEntity
import com.everyorder.domain.member.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Store(

    @Column(nullable = false,name="store_name")
    var storeName : String,

    @JoinColumn(name="owner_id", nullable =false)
    @ManyToOne(fetch = FetchType.LAZY)
    val owner: Member

) : AuditingEntity() {
}
