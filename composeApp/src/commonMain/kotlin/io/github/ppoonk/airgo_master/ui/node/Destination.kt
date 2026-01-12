package io.github.ppoonk.airgo_master.ui.node

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.ppoonk.ac.ui.component.ACDestination
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.node
import io.github.ppoonk.airgo_master.protocol_template
import io.github.ppoonk.airgo_master.ui.node.node.list.ListNodeScreen
import io.github.ppoonk.airgo_master.ui.node.protocolTemplate.list.ProtocolTemplateScreen
import org.jetbrains.compose.resources.StringResource

sealed class NodeDestination(
    override val title: StringResource,
    override val icon: ImageVector?,
    override val content: @Composable (() -> Unit)
) : ACDestination() {
    data object Node :
        NodeDestination(icon = null, title = Res.string.node, content = { ListNodeScreen() })

    data object NodeProtocolTemplate :
        NodeDestination(icon = null, title = Res.string.protocol_template, content = { ProtocolTemplateScreen() })

    companion object {
        val entries: List<NodeDestination> = listOf(Node, NodeProtocolTemplate)
    }

}