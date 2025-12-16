package io.github.ppoonk.airgo_master.sharedViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.github.ppoonk.ac.utils.Logger
import io.github.ppoonk.ac.utils.Result
import io.github.ppoonk.ac.utils.StringUtils
import io.github.ppoonk.ac.utils.ValidationResult
import io.github.ppoonk.ac.utils.ValidationUtils
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.component.BaseSearchWidget
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.SearchType
import io.github.ppoonk.airgo_master.navigation.Routes
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.BusinessCode
import io.github.ppoonk.airgo_master.repository.remote.model.CreateUserReq
import io.github.ppoonk.airgo_master.repository.remote.model.FilterUser
import io.github.ppoonk.airgo_master.repository.remote.model.GetUserListReq
import io.github.ppoonk.airgo_master.repository.remote.model.RoleConst
import io.github.ppoonk.airgo_master.repository.remote.model.SearchUser
import io.github.ppoonk.airgo_master.repository.remote.model.SignInReq
import io.github.ppoonk.airgo_master.repository.remote.model.SignInRes
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateUserReq
import io.github.ppoonk.airgo_master.repository.remote.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class UserVM() : ViewModel() {
    // 当前选择的用户
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // 当前登录的用户
    private val _signedInUser = MutableStateFlow<User?>(null)
    val signedInUser: StateFlow<User?> = _signedInUser


    // 登录
    private val _signInWidget = MutableStateFlow(SignInWidget())
    val signInWidget: StateFlow<SignInWidget> = _signInWidget

    fun signInEmail(v: String): Unit {
        val err = when (val r = ValidationUtils.validateEmail(v)) {
            is ValidationResult.Failure -> if (v.isEmpty()) "" else r.error
            is ValidationResult.Success -> ""
        }
        _signInWidget.value = _signInWidget.value.copy(email = v, emailError = err)
    }

    fun signInPassword(v: String): Unit {
        val err = when (val r = ValidationUtils.validatePassword(v)) {
            is ValidationResult.Failure -> if (v.isEmpty()) "" else r.error
            is ValidationResult.Success -> ""
        }
        _signInWidget.value = _signInWidget.value.copy(password = v, passwordError = err)
    }

    fun signInShowPassword(): Unit {
        _signInWidget.value =
            _signInWidget.value.copy(showPassword = !_signInWidget.value.showPassword)
    }

    fun signInShowMore(): Unit {
        _signInWidget.value = _signInWidget.value.copy(showMore = !_signInWidget.value.showMore)
    }

    suspend fun doSignInReq(): Result<SignInRes> {
        return viewModelScope.async {
            Repository.remote.signIn(_signInWidget.value.toSignInReq())
                .onSuccess {
                    if (it.code == 0) {
                        it.data?.user?.let { u ->
                            _signedInUser.value = u
                            Repository.local.setSignedUser(u)
                        }
                        it.data?.token?.let { t ->
                            Repository.local.setToken(t)
                        }
                    }
                }
        }.await()
    }

    suspend fun checkSigned(): Routes {
        var r: Routes = Routes.SignIn
        val token: String? = Repository.local.getToken()
        // 无 token
        if (token != null) {
            // 有 token，尝试获取登录用户信息
            Repository.remote.getUserInfo()
                .onSuccess {
                    it.data?.let { user ->
                        _signedInUser.value = user
                        Repository.local.setSignedUser(user)
                    }
                    // token 有效，跳转到主页面
                    r = Routes.Main
                }
        }
        return r
    }


    // 获取用户列表请求
    private val _getUserListReq = MutableStateFlow(GetUserListReq())

    fun refreshUpdateUserListReq(v: BaseSearchWidget): Unit {
        with(v) {
            var req = GetUserListReq()

            // search 参数
            search.isNotEmpty().let {
                val r1 = when (searchType) {
                    SearchType.ID -> SearchUser(id = search.toUIntOrNull())
                    SearchType.NAME -> SearchUser(email = search)
                }
                req = req.copy(search = r1)
            }

            // filter 参数
            val r2 = FilterUser(
                status = if (status == Status.ALL) null else status.ordinal,
                createdAtStart = datePickerStart,
                createdAtEnd = datePickerEnd
            )
            req = req.copy(filter = r2)
            _getUserListReq.value = req
        }
    }


    // 用户列表
    @OptIn(ExperimentalCoroutinesApi::class)
    val userList: Flow<PagingData<User>> = _getUserListReq.flatMapLatest {
        Repository.newPagingSource { p ->
            when (val res = Repository.remote.getUserList(
                it.copy(
                    pagination = it.pagination.copy(
                        page = p.first,
                        pageSize = p.second
                    )
                )
            )) {
                is Result.Error -> Pair(emptyList(), 0)
                is Result.Success -> Pair(res.data!!.list, res.data!!.total)
            }
        }.flow.cachedIn(viewModelScope).flowOn(Dispatchers.IO)
    }


    private val _editUserWidget = MutableStateFlow(EditUserWidget())
    val editUserWidget: StateFlow<EditUserWidget> = _editUserWidget
    fun initEditUser(editType: EditType, current: User? = null): Unit {
        when (editType) {
            EditType.CREATE -> {
                _editUserWidget.value = EditUserWidget(editType = editType)
            }

            EditType.UPDATE -> {
                _currentUser.value?.let {
                    _editUserWidget.value = EditUserWidget(
                        id = it.id,
                        email = it.email,
                        uuid = it.uuid,
                        role = RoleConst.valueOf(it.role),
                        status = Status.entries[it.status],
                        avatar = it.avatar ?: "",
                        oldUpdateUserReq = UpdateUserReq(
                            id = it.id,
                            email = it.email,
                            uuid = it.uuid,
                            role = it.role,
                            status = it.status,
                            avatar = it.avatar,
                        ),
                        editType = editType
                    )
                }


            }
        }


    }

    fun refreshEmail(v: String): Unit {
        val err = when (val r = ValidationUtils.validateEmail(v)) {
            is ValidationResult.Failure -> if (v.isEmpty()) "" else r.error
            is ValidationResult.Success -> ""
        }
        _editUserWidget.value = _editUserWidget.value.copy(email = v, emailError = err)
    }

    fun refreshPassword(v: String): Unit {
        val err = when (val r = ValidationUtils.validatePassword(v)) {
            is ValidationResult.Failure -> if (v.isEmpty()) "" else r.error
            is ValidationResult.Success -> ""
        }
        _editUserWidget.value =
            _editUserWidget.value.copy(password = v, passwordError = err)
    }

    fun refreshStatus(v: Boolean): Unit {
        _editUserWidget.value =
            _editUserWidget.value.copy(status = if (v) Status.ENABLE else Status.DISABLE)
    }

    fun refreshRole(v: RoleConst): Unit {
        _editUserWidget.value = _editUserWidget.value.copy(
            role = v,
            roleExpanded = !_editUserWidget.value.roleExpanded
        )
    }

    fun refreshRoleExpanded(): Unit {
        _editUserWidget.value =
            _editUserWidget.value.copy(roleExpanded = !_editUserWidget.value.roleExpanded)
    }

    fun refreshAvatar(v: String): Unit {
        _editUserWidget.value = _editUserWidget.value.copy(avatar = v)
    }

    fun refreshUUID(v: String): Unit {
        _editUserWidget.value = _editUserWidget.value.copy(uuid = v)
    }

}


data class EditUserWidget(
    val id: UInt = 0u,
    val email: String = "",
    val password: String = "",
    val uuid: String = StringUtils.newUUID(),
    val role: RoleConst = RoleConst.NORMAL,
    val status: Status = Status.ENABLE,
    val avatar: String = "",

    val emailError: String = "",
    val passwordError: String = "",
    val roleExpanded: Boolean = false,

    val oldUpdateUserReq: UpdateUserReq = UpdateUserReq(),

    val editType: EditType = EditType.CREATE,

    ) {
    val createIsValid: Boolean
        get() = email.isNotEmpty() &&
                password.isNotEmpty() &&
                uuid.isNotEmpty() &&
                emailError.isEmpty() &&
                passwordError.isEmpty()
    val updateIsValid: Boolean
        get() = id > 0u &&
                (email.isEmpty() || emailError.isEmpty()) &&
                (password.isEmpty() || passwordError.isEmpty())


    fun toCreateUserReq(): CreateUserReq {
        return CreateUserReq(
            email = this.email,
            password = this.password,
            uuid = this.uuid,
            role = this.role.name,
            status = this.status.ordinal,
            avatar = this.avatar,
        )
    }

    fun toUpdateUserReq(): UpdateUserReq {
        return UpdateUserReq(
            id = this.id,
            email = this.email,
            password = this.password,
            uuid = this.uuid,
            role = this.role.name,
            status = this.status.ordinal,
            avatar = this.avatar,
        )
    }
}

data class SignInWidget(
    val email: String = "",
    val password: String = "",
    val emailError: String = "",
    val passwordError: String = "",

    val showPassword: Boolean = false,
    val showMore: Boolean = false,
) {
    val isValid: Boolean
        get() = email.isNotEmpty() &&
                password.isNotEmpty() &&
                emailError.isEmpty() &&
                passwordError.isEmpty()

    fun toSignInReq(): SignInReq {
        return SignInReq(
            email = email,
            password = password
        )
    }
}