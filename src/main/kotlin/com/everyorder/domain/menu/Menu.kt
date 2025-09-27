package com.everyorder.domain.menu

import com.everyorder.domain.AuditingEntity
import com.everyorder.domain.store.Store
import com.everyorder.util.StringListConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Menu(

    @Column(nullable = false)
    var price: Int,

    var description: String,

    @Convert(converter = StringListConverter::class)
    @JoinColumn(columnDefinition = "json")
    var photoPath: List<String> = emptyList(),

    @JoinColumn(nullable = false, name = "store_id")
    @ManyToOne(fetch = FetchType.LAZY)
    val store: Store

) : AuditingEntity() {
}
