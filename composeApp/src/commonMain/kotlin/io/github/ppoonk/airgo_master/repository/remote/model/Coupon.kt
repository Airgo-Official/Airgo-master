package io.github.ppoonk.airgo_master.repository.remote.model


import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.coupon_code
import io.github.ppoonk.airgo_master.coupon_created_at
import io.github.ppoonk.airgo_master.coupon_discount
import io.github.ppoonk.airgo_master.coupon_id
import io.github.ppoonk.airgo_master.coupon_min_order_amount
import io.github.ppoonk.airgo_master.coupon_name
import io.github.ppoonk.airgo_master.coupon_status
import io.github.ppoonk.airgo_master.coupon_type
import io.github.ppoonk.airgo_master.coupon_type_discount_rate
import io.github.ppoonk.airgo_master.coupon_type_threshold
import io.github.ppoonk.airgo_master.node_created_at
import io.github.ppoonk.airgo_master.node_id
import io.github.ppoonk.airgo_master.node_name
import io.github.ppoonk.airgo_master.node_status
import io.github.ppoonk.airgo_master.operate
import io.github.ppoonk.airgo_master.user_role_admin
import kotlinx.serialization.Contextual
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Serializable
data class Coupon(
    val id: UInt,
    @Contextual
    val createdAt: Instant,
    @Contextual
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
    @Contextual
    val createdAtStart: Instant? = null,
    @Contextual
    val createdAtEnd: Instant? = null,
    val status: Int? = null,
    val category: String? = null
)

enum class CouponType() {
    THRESHOLD,// 满减券
    DISCOUNT_RATE,  // 折扣券
    ;

    @Composable
    fun i18n(): String {
       return when(this){
            THRESHOLD ->  stringResource(Res.string.coupon_type_threshold)
            DISCOUNT_RATE ->  stringResource(Res.string.coupon_type_discount_rate)
        }

    }
}


enum class CouponTableColumn(
    val text: StringResource,
    val width: Dp
) {
    ID(Res.string.coupon_id, 100.dp),

    NAME(Res.string.coupon_name, 200.dp),

    STATUS(Res.string.coupon_status, 100.dp),
    COUPON_CODE(Res.string.coupon_code, 200.dp),
    COUPON_TYPE(Res.string.coupon_type, 200.dp),
    DISCOUNT(Res.string.coupon_discount, 100.dp),
    MIN_ORDER_AMOUNT(Res.string.coupon_min_order_amount, 200.dp),

    CREATED_AT(Res.string.coupon_created_at, 150.dp),

    OPERATE(Res.string.operate, 150.dp);
}