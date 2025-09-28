package com.everyorder.domain.sales

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface SalesRepository : JpaRepository<Sales, Long>, KotlinJdslJpqlExecutor {
}
