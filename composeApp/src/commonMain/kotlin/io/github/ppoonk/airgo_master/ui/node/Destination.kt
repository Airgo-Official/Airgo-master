package io.github.ppoonk.airgo_master.ui.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ppoonk.ac.ui.component.ACDestination
import io.github.ppoonk.airgo_master.ui.node.node.list.ListNodeScreen
import io.github.ppoonk.airgo_master.ui.node.protocolTemplate.list.ProtocolTemplateScreen

sealed class NodeDestination(
    override val title: String,
    override val icon: ImageVector?,
    override val content: @Composable (() -> Unit)
) : ACDestination() {
    data object Node :
        NodeDestination(icon = null, title = "节点服务器", content = { ListNodeScreen() })

    data object NodeProtocolTemplate :
        NodeDestination(icon = null, title = "节点协议模板", content = { ProtocolTemplateScreen() })

    companion object {
        val entries: List<NodeDestination> = listOf(Node, NodeProtocolTemplate)
    }

}