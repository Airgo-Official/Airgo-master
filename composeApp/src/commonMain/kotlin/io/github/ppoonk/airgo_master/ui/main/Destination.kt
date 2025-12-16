package io.github.ppoonk.airgo_master.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ppoonk.ac.ui.component.ACDestination
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.airgo_master.ui.configuration.SettingsScreen
import io.github.ppoonk.airgo_master.ui.home.HomeScreen
import io.github.ppoonk.airgo_master.ui.node.NodeScreen
import io.github.ppoonk.airgo_master.ui.store.StoreScreen
import io.github.ppoonk.airgo_master.ui.store.order.list.OrderScreen
import io.github.ppoonk.airgo_master.ui.user.ticket.TicketScreen
import io.github.ppoonk.airgo_master.ui.user.user.list.UserScreen

sealed class Destination(
    override val title: String,
    override val icon: ImageVector,
    override val content: @Composable (() -> Unit)
) : ACDestination() {
    data object Home :
        Destination(icon = ACIconDefault.Home, title = "主页", content = { HomeScreen() })

    data object User :
        Destination(icon = ACIconDefault.User, title = "用户", content = { UserScreen() })

    data object Ticket :
        Destination(icon = ACIconDefault.Ticket, title = "工单", content = { TicketScreen() })

    data object Node :
        Destination(icon = ACIconDefault.Sitemap, title = "节点", content = { NodeScreen() })

    data object Store :
        Destination(icon = ACIconDefault.Cart, title = "商店", content = { StoreScreen() })

    data object Order :
        Destination(icon = ACIconDefault.ClipboardList, title = "订单", content = { OrderScreen() })

    data object Settings :
        Destination(icon = ACIconDefault.Server, title = "设置", content = { SettingsScreen() })

    companion object {
        val entries: List<Destination> = listOf(Home, User, Ticket, Node, Store, Order, Settings)
    }
}

//

//
//Coupon(
//index = 10,
//icon = ACIconDefault.Gift,
//title = "优惠券",
//content = { CouponScreen() }
//),
