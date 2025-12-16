package io.github.ppoonk.airgo_master.ui.configuration.push.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import io.github.ppoonk.ac.ui.component.ACButtonError
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.PushType
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.sharedViewModel.ConfigurationVM
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPushScreen() {
    val sharedVM = LocalSharedVM.current
    val widget by sharedVM.configurationVM.pushWidget.collectAsState()
    val vm = sharedVM.configurationVM

    Scaffold(
        topBar = { EditPushTopBar(widget.editType, sharedVM.configurationVM) }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.padding(paddingValues).imePadding().padding(horizontal = 16.dp)
        ) {
            item {
                Text("名称")
                ACTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.name,
                    isError = widget.nameError.isNotEmpty(),
                    supportingText = { Text(widget.nameError) },
                    onValueChange = { vm.refreshPushName(it) },
                )
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text("状态")
                    Switch(
                        checked = widget.status == Status.ENABLE,
                        onCheckedChange = { vm.refreshPushStatus(it) }
                    )
                }
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text("类型")
                    ExposedDropdownMenuBox(
                        expanded = widget.pushTypeExpanded,
                        onExpandedChange = { vm.refreshPushTypeExpanded() },
                    ) {
                        ACTextField(
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            value = widget.pushType.name,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                        )
                        ExposedDropdownMenu(
                            expanded = widget.pushTypeExpanded,
                            onDismissRequest = { },
                        ) {
                            PushType.entries.forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p.name) },
                                    onClick = { vm.refreshPushType(p) }
                                )
                            }
                        }
                    }
                }
            }
            item {
                when (widget.pushType) {
                    PushType.TG_BOT -> {
                        Text("botToken")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.tgBotConfig.botToken,
                            onValueChange = { vm.refreshTgBotConfig(widget.tgBotConfig.copy(botToken = it)) }
                        )
                        Text("proxyURL")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            value = widget.tgBotConfig.proxyURL,
                            onValueChange = { vm.refreshTgBotConfig(widget.tgBotConfig.copy(proxyURL = it)) }
                        )
                    }

                    PushType.EMAIL -> {
                        Text("host")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.emailConfig.host,
                            onValueChange = { vm.refreshEmailConfig(widget.emailConfig.copy(host = it)) }
                        )
                        Text("username")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                            value = widget.emailConfig.username,
                            onValueChange = { vm.refreshEmailConfig(widget.emailConfig.copy(username = it)) }
                        )
                        Text("password")
                        ACTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            value = widget.emailConfig.password,
                            onValueChange = { vm.refreshEmailConfig(widget.emailConfig.copy(password = it)) }
                        )
                    }
                }
            }

        }


    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPushTopBar(
    editType: EditType,
    vm: ConfigurationVM
) {
    val sharedVM = LocalSharedVM.current // TODO 优化 sharedVM
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current

    ACTopAppBar(
        title = {
            Text(
                when (editType) {
                    EditType.CREATE -> "新建消息推送"
                    EditType.UPDATE -> "编辑消息推送"
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
            when (editType) {
                EditType.CREATE -> {
                    // 新建
                    IconButton(onClick = {
                        scope.launch {
                            Repository.remote.updatePushList(vm.updatePushReq())
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
                    }) { ACIconSmall(ACIconDefault.Check, null) }
                }

                EditType.UPDATE -> {
                    // 更新
                    IconButton(onClick = {
                        scope.launch {
                            Repository.remote.updatePushList(vm.updatePushReq())
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
                    }) { ACIconSmall(ACIconDefault.Check, null) }
                    // 删除
                    IconButton(onClick = {
                        sharedVM.dialogVM.openDialog(
                            title = { Text("提示") },
                            text = { Text("删除后数据无法恢复，确认删除吗?") }
                        ) {
                            ACButtonError(
                                onClick = {
                                    scope.launch {
                                        Repository.remote.updatePushList(vm.deletePushReq())
                                            .onFailure {
                                                sharedVM.dialogVM.openDialog(
                                                    title = { Text(it.code.toString()) },
                                                    text = { Text(it.message) }
                                                )
                                            }
                                            .onSuccess {
                                                sharedVM.dialogVM.closeDialog()
                                                navController.popBackStack()
                                            }
                                    }
                                },
                            ) {
                                Text("确认")
                            }
                        }
                    }) { ACIconSmall(ACIconDefault.Trash, null) }
                }
            }
        }
    )
}
