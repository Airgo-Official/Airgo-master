package io.github.ppoonk.airgo_master.repository.remote.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class User(
    val createdAt: Instant,
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
    val createdAtStart: Instant? = null,
    val createdAtEnd: Instant? = null,
)


enum class RoleConst {
    ADMIN,
    NORMAL
}
