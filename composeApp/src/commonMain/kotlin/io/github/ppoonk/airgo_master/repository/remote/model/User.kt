package io.github.ppoonk.airgo_master.repository.remote.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.operate
import io.github.ppoonk.airgo_master.user_avatar
import io.github.ppoonk.airgo_master.user_created_at
import io.github.ppoonk.airgo_master.user_email
import io.github.ppoonk.airgo_master.user_id
import io.github.ppoonk.airgo_master.user_role
import io.github.ppoonk.airgo_master.user_role_admin
import io.github.ppoonk.airgo_master.user_role_normal
import io.github.ppoonk.airgo_master.user_status
import io.github.ppoonk.airgo_master.user_uuid
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant


@Serializable
data class User(
    @Contextual
    val createdAt: Instant,
    @Contextual
    val updatedAt: Instant?,

    val id: UInt,
    val email: String,
    val status: Int,
    val avatar: String?,
    val uuid: String,
    val role: String,
)


@Serializable
data class SignInReq(
    val email: String,
    val password: String
)


@Serializable
data class SignInRes(
    val user: User,
    val token: String,
)


@Serializable
data class CreateUserReq(
    val email: String,
    val password: String,
    val uuid: String,
    val role: String,
    val status: Int,
    val avatar: String,
)


@Serializable
data class DeleteUserReq(
    val id: UInt,
)

@Serializable
data class UpdateUserReq(
    val id: UInt = 0u,
    val email: String? = null,
    val password: String? = null,
    val uuid: String? = null,
    val role: String? = null,
    val status: Int? = null,
    val avatar: String? = null,
)

@Serializable
data class GetUserListReq(
    // 查询
    val search: SearchUser? = null,
    // 过滤
    val filter: FilterUser? = null,
    // 排序
    val order: SortOrder? = null,
    // 分页
    val pagination: Pagination = Pagination()
)

@Serializable
data class GetUserListRes(
    val total: Int,
    val list: List<User>
)


@Serializable
data class SearchUser(
    val id: UInt? = null,
    val email: String? = null,
)

@Serializable
data class FilterUser(
    val status: Int? = null,
    @Contextual
    val createdAtStart: Instant? = null,
    @Contextual
    val createdAtEnd: Instant? = null,
)


enum class RoleConst {
    ADMIN,
    NORMAL;

    @Composable
    fun i18n(): String {
        return when (this) {
            ADMIN -> stringResource(Res.string.user_role_admin)
            NORMAL -> stringResource(Res.string.user_role_normal)
        }
    }

}


enum class UserTableColumn(
    val text: StringResource,
    val width: Dp
) {
    ID(Res.string.user_id, 100.dp),
    EMAIL(Res.string.user_email, 200.dp),
    STATUS(Res.string.user_status, 100.dp),
    ROLE(Res.string.user_role, 100.dp),
    UUID(Res.string.user_uuid, 400.dp),
    CREATED_AT(Res.string.user_created_at, 150.dp),
    AVATAR(Res.string.user_avatar, 300.dp),

    OPERATE(Res.string.operate, 150.dp);


    companion object {
        fun totalWidth(): Int {
            return UserTableColumn.entries.sumOf { it.width.value.toInt() }
        }
    }

}