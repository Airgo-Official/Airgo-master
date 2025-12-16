package io.github.ppoonk.airgo_master.ui.user.user.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.StringUtils
import io.github.ppoonk.ac.utils.diffObject
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.RoleConst
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen() {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val vm = sharedVM.userVM
    val widget by sharedVM.userVM.editUserWidget.collectAsState()

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(
                        text = when (widget.editType) {
                            EditType.CREATE -> "创建用户"
                            EditType.UPDATE -> "编辑用户"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            navController.popBackStack()
                        }
                    }) {
                        ACIconSmall(ACIconDefault.AngleLeft, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                when (widget.editType) {
                                    EditType.CREATE -> {
                                        Repository.remote.createUser(widget.toCreateUserReq())
                                            .onFailure {
                                                sharedVM.dialogVM.openDialog(
                                                    title = { Text(it.code.toString()) },
                                                    text = { Text(it.message) }
                                                )
                                            }
                                            .onSuccess {
                                                navController.popBackStack()
                                            }
                                    }

                                    EditType.UPDATE -> {
                                        val req =
                                            diffObject(
                                                widget.oldUpdateUserReq,
                                                widget.toUpdateUserReq()
                                            )
                                        req?.let { r ->
                                            Repository.remote.updateUser(r.copy(widget.oldUpdateUserReq.id))
                                                .onFailure {
                                                    sharedVM.dialogVM.openDialog(
                                                        title = { Text(it.code.toString()) },
                                                        text = { Text(it.message) }
                                                    )
                                                }
                                                .onSuccess {
                                                    // TODO 刷新用户详情
                                                    navController.popBackStack()
                                                }
                                        }
                                    }
                                }
                            }
                        },
                        enabled = when (widget.editType) {
                            EditType.CREATE -> widget.createIsValid
                            EditType.UPDATE -> widget.updateIsValid
                        }
                    ) {
                        ACIconSmall(ACIconDefault.Check, null)
                    }
                },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(paddingValues).imePadding().padding(horizontal = 16.dp)
        ) {
            if (widget.editType == EditType.CREATE) {
                item {
                    Text("邮箱")
                    ACTextField(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                        value = widget.email,
                        onValueChange = { vm.refreshEmail(it) },
                        isError = widget.emailError.isNotEmpty(),
                        supportingText = { Text(widget.emailError) }
                    )
                }
            }

            item {
                Text("密码")
                ACTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 0.dp),
                    value = widget.password,
                    onValueChange = { vm.refreshPassword(it) },
                    isError = widget.passwordError.isNotEmpty(),
                    supportingText = { Text(widget.passwordError) },
                    trailingIcon = {
                        IconButton(onClick = { vm.refreshPassword(StringUtils.newRandomPassword()) }) {
                            ACIconSmall(ACIconDefault.Sync, null)
                        }
                    }
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                ) {
                    Text("状态")
                    Switch(
                        checked = widget.status == Status.ENABLE,
                        onCheckedChange = { vm.refreshStatus(it) },
                    )
                }
            }

            item {
                Text("角色")
                ExposedDropdownMenuBox(
                    expanded = widget.roleExpanded,
                    onExpandedChange = { vm.refreshRoleExpanded() },
                ) {
                    ACTextField(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = widget.role.name,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false
                    )
                    DropdownMenu(
                        expanded = widget.roleExpanded,
                        onDismissRequest = { vm.refreshRoleExpanded() }
                    ) {
                        RoleConst.entries.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r.name) },
                                onClick = { vm.refreshRole(r) }
                            )
                        }
                    }
                }
            }
            item {
                Text("头像")
                ACTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.avatar,
                    onValueChange = { vm.refreshAvatar(it) },
                )
            }
            item {
                Text("UUID")
                ACTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    value = widget.uuid,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { vm.refreshUUID(StringUtils.newUUID()) }) {
                            ACIconSmall(ACIconDefault.Sync, null)
                        }
                    }
                )
            }
        }
    }
}
