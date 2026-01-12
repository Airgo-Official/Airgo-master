package io.github.ppoonk.airgo_master.repository.remote.model


import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.node_config
import io.github.ppoonk.airgo_master.node_created_at
import io.github.ppoonk.airgo_master.node_id
import io.github.ppoonk.airgo_master.node_name
import io.github.ppoonk.airgo_master.node_status
import io.github.ppoonk.airgo_master.operate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Instant

/**
 * 节点
 */
@Serializable
data class Node(
    @Contextual
    val createdAt: Instant,
    @Contextual
    val updatedAt: Instant?,
    val id: UInt,
    val name: String,
    val status: Int,
    val config: String,
)


/**
 * 创建节点, 请求
 */
@Serializable
data class CreateNodeReq(
    val name: String,
    val status: Int,
    val config: String,
)

/**
 * 删除节点, 请求
 */
@Serializable
data class DeleteNodeReq(
    val id: UInt,
)

/**
 * 更新节点, 请求
 */

@Serializable
data class UpdateNodeReq(
    val id: UInt = 0u,
    val name: String? = null,
    val status: Int? = null,
    val config: String? = null,
)


/**
 * 获取节点列表, 请求
 */
@Serializable
data class GetNodeListReq(
    // 查询
    val search: SearchNode? = null,
    // 过滤
    val filter: FilterNode? = null,
    // 排序
    val order: SortOrder? = null,
    // 分页
    val pagination: Pagination = Pagination()
)

@Serializable
data class SearchNode(
    val id: UInt? = null,
    val name: String? = null,
)

@Serializable
data class FilterNode(
    val status: Int? = null,
    @Contextual
    var createdAtStart: Instant? = null,
    @Contextual
    var createdAtEnd: Instant? = null,
)


@Serializable
data class GetNodeListRes(
    val total: Int,
    val list: List<Node>
)

object NodeConst {
    const val config = """
{
  "certificate": {},
  "dns": {},
  "endpoints": {},
  "experimental": {},
  "log": {
    "disabled": false,
    "level": "error",
    "output": "box.log",
    "timestamp": true
  },
  "ntp": {},
  "outbounds": {
    "type": "direct",
    "tag": "direct-out"
  },
  "route": {}
}
    """


    const val NODE_INBOUNDS: String = """[
    {
      "tag": "tun-in",
      "type": "tun",
      "address": [
        "172.19.0.0/30",
        "fdfe:dcba:9876::0/126"
      ],
      "stack": "system",
      "auto_route": true,
      "strict_route": true,
      "platform": {
        "http_proxy": {
          "enabled": true,
          "server": "127.0.0.1",
          "server_port": 7890
        }
      }
    },
    {
      "tag": "mixed-in",
      "type": "mixed",
      "listen": "127.0.0.1",
      "listen_port": 7890
    }
  ]"""

}

enum class NodeTableColumn(
    val text: StringResource,
    val width: Dp
) {
    ID(Res.string.node_id, 100.dp),

    NAME(Res.string.node_name, 300.dp),

    STATUS(Res.string.node_status, 100.dp),

//    CONFIG(Res.string.node_config, 50.dp),

    CREATED_AT(Res.string.node_created_at, 150.dp),

    OPERATE(Res.string.operate, 150.dp);


    companion object {
        fun totalWidth(): Int {
            return NodeTableColumn.entries.sumOf { it.width.value.toInt() }
        }
    }

}

