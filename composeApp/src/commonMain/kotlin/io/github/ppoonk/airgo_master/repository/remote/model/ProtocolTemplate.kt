package io.github.ppoonk.airgo_master.repository.remote.model

import io.github.ppoonk.ac.utils.Logger
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ProtocolTemplate(
    val id: UInt,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val name: String,
    val inbounds: String,
) {
    @Serializable
    data class ProtocolOnlyType(
        val type: String
    )

    fun getProtocolType(): String {
        return inbounds.let {
            try {
                Json.decodeFromString<ProtocolOnlyType>(it).type
            } catch (e: Throwable) {
                Logger.error(Logger.JSON) { "getProtocolType, Failed to parse inbounds: ${e.message}" }
                ""
            }
        }
    }
}


@Serializable
data class CreateProtocolTemplateReq(
    val name: String = "",
    val inbounds: String = NodeConst.NODE_INBOUNDS,
)


@Serializable
data class DeleteProtocolTemplateReq(
    val id: UInt,
)

@Serializable
data class UpdateProtocolTemplateReq(
    val id: UInt = 0u,
    val name: String? = null,
    val inbounds: String? = null,
)

@Serializable
data class GetProtocolTemplateListReq(
    // 排序
    val order: SortOrder? = null,
    // 分页
    val pagination: Pagination = Pagination()
)



@Serializable
data class GetProtocolTemplateListRes(
    val total: Int,
    val list: List<ProtocolTemplate>
)
