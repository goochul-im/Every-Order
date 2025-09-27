package com.everyorder.domain.order

import com.everyorder.domain.AuditingEntity
import com.everyorder.domain.menu.Menu
import com.everyorder.domain.sales.Sales
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Order(

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    var orderStatus : OrderStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id")
    val sales: Sales

) : AuditingEntity() {
}

@Entity
class OrderMenu(

    @Column(nullable = false)
    var quantity :Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    val order: Order,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    val menu: Menu

) : AuditingEntity()


enum class OrderStatus{
    취소,
    대기중,
    준비중,
    완료
}
