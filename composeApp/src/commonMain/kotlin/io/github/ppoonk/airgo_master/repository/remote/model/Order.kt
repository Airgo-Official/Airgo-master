package io.github.ppoonk.airgo_master.repository.remote.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: UInt,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val orderNo: String,
    val outOrderNo: String?,
    val userId: UInt,
    val status: String,
    val originalAmount: Double,
    val totalAmount: Double,
    val paymentAmount: Double,
    val discountAmount: Double,
    val notes: String?,
    val source: String,
    val paymentType: String?,
    val paymentId: UInt?,
    val orderId: UInt,
    val orderName: String
)

@Serializable
data class GetOrderListReq(
    val search: SearchOrderParams? = null,
    val filter: FilterOrderParams? = null,
    val order: SortOrder? = null,
    val pagination: Pagination = Pagination(),
)

@Serializable
data class SearchOrderParams(
    val orderNo: String? = null,
    val userId: UInt? = null,
)

@Serializable
data class FilterOrderParams(
    val createdAtStart: Instant? = null,
    val createdAtEnd: Instant? = null,
    val status: Instant? = null, 
    val paymentType: String? = null,
)