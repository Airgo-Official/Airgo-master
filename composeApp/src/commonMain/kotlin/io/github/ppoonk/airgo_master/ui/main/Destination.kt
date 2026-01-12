package io.github.ppoonk.airgo_master.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ppoonk.ac.ui.component.ACDestination
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.configuration
import io.github.ppoonk.airgo_master.node
import io.github.ppoonk.airgo_master.order
import io.github.ppoonk.airgo_master.store
import io.github.ppoonk.airgo_master.ticket
import io.github.ppoonk.airgo_master.ui.configuration.ConfigurationScreen
import io.github.ppoonk.airgo_master.ui.node.NodeScreen
import io.github.ppoonk.airgo_master.ui.store.StoreScreen
import io.github.ppoonk.airgo_master.ui.store.order.list.OrderScreen
import io.github.ppoonk.airgo_master.ui.user.ticket.TicketScreen
import io.github.ppoonk.airgo_master.ui.user.user.list.UserListScreen
import io.github.ppoonk.airgo_master.user
import org.jetbrains.compose.resources.StringResource

sealed class Destination(
    override val title: StringResource,
    override val icon: ImageVector,
    override val content: @Composable (() -> Unit)
) : ACDestination() {
    data object User :
        Destination(icon = ACIconDefault.User, title = Res.string.user, content = { UserListScreen() })

    data object Ticket :
        Destination(icon = ACIconDefault.Ticket, title = Res.string.ticket, content = { TicketScreen() })

    data object Node :
        Destination(icon = ACIconDefault.Sitemap, title = Res.string.node, content = { NodeScreen() })

    data object Store :
        Destination(icon = ACIconDefault.Cart, title = Res.string.store, content = { StoreScreen() })

    data object Order :
        Destination(icon = ACIconDefault.ClipboardList, title = Res.string.order, content = { OrderScreen() })

    data object Configuration :
        Destination(icon = ACIconDefault.Server, title = Res.string.configuration, content = { ConfigurationScreen() })

    companion object {
        val entries: List<Destination> = listOf(User, Ticket, Node, Store, Order, Configuration)
    }
}
