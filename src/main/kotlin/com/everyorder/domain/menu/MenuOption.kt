package com.everyorder.domain.menu

import com.everyorder.domain.AuditingEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class MenuOption(

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    var price: Int,

    @Column(nullable = false)
    var description: String,

    @JoinColumn(nullable = false, name = "menu_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val menu: Menu

) : AuditingEntity() {
}
