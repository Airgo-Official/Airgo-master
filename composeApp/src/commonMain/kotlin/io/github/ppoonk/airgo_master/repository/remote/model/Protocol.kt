package io.github.ppoonk.airgo_master.repository.remote.model

import androidx.compose.runtime.Composable
import io.github.ppoonk.ac.utils.Logger
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.unknown_protocol
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource

@Serializable
data class Protocol(
    val createdAt: Instant,
    val updatedAt: Instant?,
    val id: UInt,
    val name: String,
    val nodeId: UInt,
    val templateId: UInt?,
    val status: Int,
    val address: String,
    val inbounds: String,
    val port: Int
) {

    @Serializable
    data class ProtocolOnlyType(
        val type: String
    )

    @Composable
    fun getProtocolType(): String {
        return try {
            val json = Json { ignoreUnknownKeys = true } // 忽略未知字段
            json.decodeFromString<ProtocolOnlyType>(this.inbounds).type
        } catch (e: Throwable) {
            // TODO (JSON) getProtocolType, Failed to parse inbounds: Encountered an unknown key 'tls' at offset 2 at path: $
            Logger.error(Logger.JSON) { "getProtocolType, Failed to parse inbounds: ${e.message}" }
            stringResource(Res.string.unknown_protocol)
        }
    }
}


@Serializable
data class CreateProtocolReq(
    val name: String,
    val nodeId: UInt,
    val templateId: UInt?,
    val status: Int,
    val address: String,
    val inbounds: String,
    val port: Int
)


@Serializable
data class DeleteProtocolReq(
    val id: UInt,
)

@Serializable
data class UpdateProtocolReq(
    val id: UInt = 0u,
    val name: String? = null,
    val templateId: UInt? = null,
    val status: Int? = null,
    val address: String? = null,
    val inbounds: String? = null,
    val port: Int? = null
)


@Serializable
data class GetProtocolListReq(
    val search: SearchProtocol? = null,
    // 过滤
    val filter: FilterProtocol? = null,
    // 排序
    val order: SortOrder? = null,
    //  分页
    val pagination: Pagination = Pagination(),
)

@Serializable
data class GetProtocolListRes(
    val total: Int,
    val list: List<Protocol>
)

@Serializable
data class SearchProtocol(
    val nodeId: UInt? = null,
    val productId: UInt? = null,
)

@Serializable
data class FilterProtocol(
    val status: Int? = null,
    var createdAtStart: Instant? = null,
    var createdAtEnd: Instant? = null,
)


object ProtocolConst {
    enum class ProtocolType(val value: String) {
        VLESS("vless")
    }

}