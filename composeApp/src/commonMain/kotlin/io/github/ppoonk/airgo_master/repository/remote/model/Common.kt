package io.github.ppoonk.airgo_master.repository.remote.model

import androidx.compose.runtime.Composable
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.all
import io.github.ppoonk.airgo_master.disable
import io.github.ppoonk.airgo_master.enable
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource


/**
 * 排序
 */
@Serializable
data class SortOrder(
    val orderBy: String = OrderConst.BY_ID, // 排序字段，默认 id
    val orderDirection: String = OrderConst.DESC, // 排序方向，默认降序
)

/**
 * 分页
 */
@Serializable
data class Pagination(
    val page: Int = PageConst.PAGE, // 分页页码
    val pageSize: Int = PageConst.PAGE_SIZE// 分页大小
)

object OrderConst {
    const val ASC = "asc"
    const val DESC = "desc"

    const val BY_ID = "id"
}


object PageConst {
    const val PAGE: Int = 1
    const val PAGE_SIZE: Int = 20
}

/**
 * 状态
 */
enum class Status {
    DISABLE,
    ENABLE,
    ALL;

    @Composable
    fun i18n(): String {
        return when (this) {
            DISABLE -> stringResource(Res.string.disable)
            ENABLE -> stringResource(Res.string.enable)
            ALL -> stringResource(Res.string.all)
        }
    }
}
