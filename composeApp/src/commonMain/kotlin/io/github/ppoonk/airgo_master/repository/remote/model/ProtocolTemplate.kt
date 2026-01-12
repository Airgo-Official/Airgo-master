package io.github.ppoonk.airgo_master.repository.remote.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.utils.Logger
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.operate
import io.github.ppoonk.airgo_master.protocol_template_created_at
import io.github.ppoonk.airgo_master.protocol_template_id
import io.github.ppoonk.airgo_master.protocol_template_inbounds
import io.github.ppoonk.airgo_master.protocol_template_name
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Instant

@Serializable
data class ProtocolTemplate(
    val id: UInt,
    @Contextual
    val createdAt: Instant,
    @Contextual
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


enum class ProtocolTemplateTableColumn(
    val text: StringResource,
    val width: Dp
) {
    ID(Res.string.protocol_template_id, 100.dp),

    NAME(Res.string.protocol_template_name, 300.dp),

    CREATED_AT(Res.string.protocol_template_created_at, 150.dp),
//    INBOUNDS(Res.string.protocol_template_inbounds, 200.dp),
    OPERATE(Res.string.operate, 150.dp);
}