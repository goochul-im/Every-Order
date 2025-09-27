package com.everyorder.domain.sales

import com.everyorder.domain.AuditingEntity
import com.everyorder.domain.store.Store
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Sales(

    @Column(nullable = true, name = "day_sales")
    var daySales: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    val store: Store

) : AuditingEntity() {
}
