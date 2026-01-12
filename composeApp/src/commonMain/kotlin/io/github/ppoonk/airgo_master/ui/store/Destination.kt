package io.github.ppoonk.airgo_master.ui.store

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ppoonk.ac.ui.component.ACDestination
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.coupon
import io.github.ppoonk.airgo_master.order
import io.github.ppoonk.airgo_master.product
import io.github.ppoonk.airgo_master.ui.store.coupon.list.CouponScreen
import io.github.ppoonk.airgo_master.ui.store.order.list.OrderScreen
import io.github.ppoonk.airgo_master.ui.store.product.list.ProductListScreen
import org.jetbrains.compose.resources.StringResource

sealed class StoreDestination(
    override val title: StringResource,
    override val icon: ImageVector?,
    override val content: @Composable (() -> Unit)
) : ACDestination() {
    data object Order :
        StoreDestination(icon = null, title = Res.string.order, content = { OrderScreen() })

    data object Product :
        StoreDestination(icon = null, title = Res.string.product, content = { ProductListScreen() })

    data object Coupon :
        StoreDestination(icon = null, title = Res.string.coupon, content = { CouponScreen() })

    companion object {
        val entries: List<StoreDestination> = listOf(Order, Product, Coupon)
    }

}