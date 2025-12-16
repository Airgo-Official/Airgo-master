package io.github.ppoonk.airgo_master.ui.store

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ppoonk.ac.ui.component.ACDestination
import io.github.ppoonk.airgo_master.ui.store.coupon.list.CouponScreen
import io.github.ppoonk.airgo_master.ui.store.order.list.OrderScreen
import io.github.ppoonk.airgo_master.ui.store.product.list.ProductScreen

sealed class StoreDestination(
    override val title: String,
    override val icon: ImageVector?,
    override val content: @Composable (() -> Unit)
) : ACDestination() {
    data object Order :
        StoreDestination(icon = null, title = "订单", content = { OrderScreen() })

    data object Product :
        StoreDestination(icon = null, title = "商品", content = { ProductScreen() })

    data object Coupon :
        StoreDestination(icon = null, title = "优惠券", content = { CouponScreen() })

    companion object {
        val entries: List<StoreDestination> = listOf(Order, Product, Coupon)
    }

}