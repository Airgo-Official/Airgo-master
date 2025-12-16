package io.github.ppoonk.airgo_master.ui.node.protocol.edit

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.diffObject
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.create_protocol
import io.github.ppoonk.airgo_master.protocol_address
import io.github.ppoonk.airgo_master.protocol_bind_template
import io.github.ppoonk.airgo_master.protocol_inbounds
import io.github.ppoonk.airgo_master.protocol_name
import io.github.ppoonk.airgo_master.protocol_no_template
import io.github.ppoonk.airgo_master.protocol_port
import io.github.ppoonk.airgo_master.repository.Repository
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProtocolScreen() {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val vm = sharedVM.nodeVM
    val widget by vm.editProtocolWidget.collectAsState()
    val protocolTemplateList = vm.protocolTemplateList.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(
                        when (widget.editType) {
                            EditType.CREATE -> stringResource(Res.string.create_protocol)
                            EditType.UPDATE -> "更新协议"
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
                                        Repository.remote.createProtocol(widget.toCreateProtocolReq()) // TODO node_id 没有赋值
                                            .onFailure {
                                                sharedVM.dialogVM.openDialog(
                                                    title = { Text(it.code.toString()) },
                                                    text = { Text(it.message) }
                                                )
                                            }.onSuccess {
                                                // TODO 刷新数据
                                                navController.popBackStack()
                                            }
                                    }

                                    EditType.UPDATE -> {
                                        // 只有新修改时才更新
                                        diffObject(
                                            widget.oldUpdateProtocolReq,
                                            widget.toUpdateProtocolReq(),
                                        )?.let { r ->
                                            val req =
                                                r.copy(id = widget.oldUpdateProtocolReq.id) // id 在 diffObject 中被过滤掉
                                            Repository.remote.updateProtocol(req)
                                                .onFailure {
                                                    sharedVM.dialogVM.openDialog(
                                                        title = { Text(it.code.toString()) },
                                                        text = { Text(it.message) }
                                                    )
                                                }
                                                .onSuccess {
                                                    // TODO 刷新数据
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
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(paddingValues).imePadding().padding(horizontal = 16.dp)
        ) {

            item {
                Text(stringResource(Res.string.protocol_name))
                ACTextField(
                    value = widget.name,
                    onValueChange = { vm.protocolName(it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                )
            }
            item {
                Text(stringResource(Res.string.protocol_address))
                ACTextField(
                    value = widget.address,
                    onValueChange = { vm.protocolAddress(it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                )
            }
            item {
                Text(stringResource(Res.string.protocol_port))
                ACTextField(
                    value = widget.port.toString(),
                    onValueChange = { vm.protocolPort(it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                )
            }
            item {
                Text(stringResource(Res.string.protocol_bind_template))
                ExposedDropdownMenuBox(
                    expanded = widget.expandedBind,
                    onExpandedChange = { vm.expandedBind() },
                ) {
                    ACTextField(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = widget.selectedTemplateName,
                        onValueChange = {},
                        readOnly = true,
                    )
                    ExposedDropdownMenu(
                        expanded = widget.expandedBind,
                        onDismissRequest = { vm.expandedBind() },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.protocol_no_template)) },
                            onClick = { vm.protocolNoTemp() }
                        )

                        protocolTemplateList.itemSnapshotList.items.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.name) },
                                onClick = { vm.protocolTemp(t) }
                            )
                        }
                    }
                }
            }
            item {
                Text(stringResource(Res.string.protocol_inbounds))
                ACTextField(
                    value = widget.inbounds,
                    onValueChange = { vm.protocolInbounds(it) },
                    enabled = widget.templateId == null,
                    isError = widget.inboundsError.isNotEmpty(),
                    supportingText = { Text(widget.inboundsError) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    singleLine = false,
                    maxLines = 16,
                )
            }
        }
    }
}