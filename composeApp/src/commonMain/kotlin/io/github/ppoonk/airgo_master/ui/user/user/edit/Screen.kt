package io.github.ppoonk.airgo_master.ui.user.user.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.StringUtils
import io.github.ppoonk.ac.utils.diffObject
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.RoleConst
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.user_avatar
import io.github.ppoonk.airgo_master.user_create
import io.github.ppoonk.airgo_master.user_email
import io.github.ppoonk.airgo_master.user_password
import io.github.ppoonk.airgo_master.user_role
import io.github.ppoonk.airgo_master.user_status
import io.github.ppoonk.airgo_master.user_update
import io.github.ppoonk.airgo_master.user_uuid
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

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
                            EditType.CREATE -> stringResource(Res.string.user_create)
                            EditType.UPDATE -> stringResource(Res.string.user_update)
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
            modifier = Modifier.padding(paddingValues).imePadding().padding(horizontal = 16.dp).widthIn(max= 600.dp)
        ) {
            if (widget.editType == EditType.CREATE) {
                item {
                    Text(stringResource(Res.string.user_email))
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                        value = widget.email,
                        onValueChange = { vm.editUserWidgetEmail(it) },
                        isError = widget.emailError.isNotEmpty(),
                        supportingText = { Text(widget.emailError) }
                    )
                }
            }

            item {
                Text(stringResource(Res.string.user_password))
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 0.dp),
                    value = widget.password,
                    onValueChange = { vm.editUserWidgetPassword(it) },
                    isError = widget.passwordError.isNotEmpty(),
                    supportingText = { Text(widget.passwordError) },
                    trailingIcon = {
                        IconButton(onClick = { vm.editUserWidgetPassword(StringUtils.newRandomPassword()) }) {
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
                    Text(stringResource(Res.string.user_status))
                    Switch(
                        checked = widget.status == Status.ENABLE,
                        onCheckedChange = { vm.editUserWidgetStatus(it) },
                    )
                }
            }

            item {
                Text(stringResource(Res.string.user_role))
                ExposedDropdownMenuBox(
                    expanded = widget.expandRole,
                    onExpandedChange = { vm.editUserWidgetExpandRole() },
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = widget.role.i18n(),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false
                    )
                    DropdownMenu(
                        expanded = widget.expandRole,
                        onDismissRequest = { vm.editUserWidgetExpandRole() }
                    ) {
                        RoleConst.entries.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r.i18n()) },
                                onClick = { vm.editUserWidgetRole(r) }
                            )
                        }
                    }
                }
            }
            item {
                Text(stringResource(Res.string.user_avatar))
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.avatar,
                    onValueChange = { vm.editUserWidgetAvatar(it) },
                )
            }
            item {
                Text(stringResource(Res.string.user_uuid))
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.uuid,
                    onValueChange = { vm.editUserWidgetUUID(it) },
                    trailingIcon = {
                        IconButton(onClick = { vm.editUserWidgetUUID(StringUtils.newUUID()) }) {
                            ACIconSmall(ACIconDefault.Sync, null)
                        }
                    }
                )
            }
        }
    }
}
