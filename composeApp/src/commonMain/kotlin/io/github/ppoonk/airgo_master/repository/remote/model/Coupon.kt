package io.github.ppoonk.airgo_master.repository.remote.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Coupon(
    val id: UInt,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val name: String,
    val status: Int,
    val couponCode: String,
    val couponType: String,
    val discount: Double,
    val minOrderAmount: Double,
)

@Serializable
data class CreateCouponReq(
    val name: String,
    val status: Int,
    val couponCode: String,
    val couponType: String,
    val discount: Double,
    val minOrderAmount: Double,
    val productIdList: List<UInt>? = null
)

@Serializable
data class DeleteCouponReq(
    val id: UInt
)

@Serializable
data class UpdateCouponReq(
    val id: UInt = 0u,
    val name: String? = null,
    val status: Int? = null,
    val couponCode: String? = null,
    val couponType: String? = null,
    val discount: Double? = null,
    val minOrderAmount: Double? = null,
    val productIdList: List<UInt>? = null
)

@Serializable
data class GetCouponListReq(
    val search: SearchCoupon? = null,
    val filter: FilterCoupon? = null,
    val order: SortOrder? = null,
    val pagination: Pagination = Pagination(),
)

@Serializable
data class GetCouponListRes(
    val total: Int,
    val list: List<Coupon>
)


@Serializable
data class SearchCoupon(
    val id: UInt? = null,
    val name: String? = null,
    val couponCode: String? = null,
)

@Serializable
data class FilterCoupon(
    val createdAtStart: Instant? = null,
    val createdAtEnd: Instant? = null,
    val status: Int? = null,
    val category: String? = null
)

enum class CouponType() {
    THRESHOLD,// 满减券
    DISCOUNT_RATE,  // 折扣
}
