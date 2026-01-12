package io.github.ppoonk.airgo_master.ui.node.node.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.ui.component.AutoSizeFade
import io.github.ppoonk.ac.utils.diffObject
import io.github.ppoonk.ac.utils.mergeObject
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.node_config
import io.github.ppoonk.airgo_master.node_create
import io.github.ppoonk.airgo_master.node_name
import io.github.ppoonk.airgo_master.node_status
import io.github.ppoonk.airgo_master.node_update
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.sharedViewModel.EditNodeWidget
import io.github.ppoonk.airgo_master.sharedViewModel.SharedVM
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNodeScreen() {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val widget by sharedVM.nodeVM.editNodeWidget.collectAsState()
    val currentNode by sharedVM.nodeVM.currentNode.collectAsState()


    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(
                        when (widget.editType) {
                            EditType.CREATE -> stringResource(Res.string.node_create)
                            EditType.UPDATE -> stringResource(Res.string.node_update)
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
                                        Repository.remote.createNode(widget.toCreateNodeReq())
                                            .onFailure {
                                                sharedVM.dialogVM.openDialog(
                                                    title = { Text(it.code.toString()) },
                                                    text = { Text(it.message) }
                                                )
                                            }
                                            .onSuccess {
//                                TODO 刷新节点数据
                                            }
                                    }

                                    EditType.UPDATE -> {
                                        // 只有新修改时才更新
                                        diffObject(
                                            widget.oldUpdateNodeReq,
                                            widget.toUpdateNodeReq()
                                        )?.let { r ->
                                            val req = r.copy(id = widget.oldUpdateNodeReq.id)
                                            Repository.remote.updateNode(req) // id 在 diffObject 中被过滤掉，需要重新赋值
                                                .onFailure {
                                                    sharedVM.dialogVM.openDialog(
                                                        text = { Text(it.message) }
                                                    )
                                                }
                                                .onSuccess {
                                                    // 刷新当前节点
                                                    val newNode = mergeObject(currentNode!!, req)
                                                    sharedVM.nodeVM.refreshCurrentNode(newNode)
                                                    // 返回页面
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

        AutoSizeFade(
            compact = {
                compact(
                    Modifier.padding(paddingValues).padding(horizontal = 16.dp).imePadding(),
                    sharedVM,
                    widget,
                )
            },
            medium = {
                compact(
                    Modifier.padding(paddingValues).padding(horizontal = 16.dp),
                    sharedVM,
                    widget,
                )
            },
            expanded = {
                expanded(
                    Modifier.padding(paddingValues).padding(horizontal = 16.dp),
                    sharedVM,
                    widget,
                )
            },
        )

    }
}

@Composable
private fun compact(
    modifier: Modifier,
    sharedVM: SharedVM,
    widget: EditNodeWidget,
): Unit {
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {

        item {
            Text(stringResource(Res.string.node_name))
            TextField(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                value = widget.name,
                onValueChange = { sharedVM.nodeVM.editNodeWidgetName(it) },
                isError = widget.nameError.isNotEmpty(),
                supportingText = { Text(widget.nameError) },
            )
        }
        item {
            Text(stringResource(Res.string.node_status))
            Switch(
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                checked = widget.status == Status.ENABLE,
                onCheckedChange = { sharedVM.nodeVM.editNodeWidgetStatus(it) }
            )
        }
        item {
            Text(stringResource(Res.string.node_config))
            TextField(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                value = widget.config,
                onValueChange = { sharedVM.nodeVM.editNodeWidgetConfig(it) },
                isError = widget.configError.isNotEmpty(),
                supportingText = { Text(widget.configError) },
                singleLine = false,
                maxLines = 16
            )
        }
    }
}

@Composable
private fun expanded(
    modifier: Modifier,
    sharedVM: SharedVM,
    widget: EditNodeWidget,
): Unit {
    Row(modifier = modifier) {
        LazyColumn(modifier = Modifier.padding(end = 16.dp).weight(1f)) {
            item {
                Text(stringResource(Res.string.node_name))
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.name,
                    onValueChange = { sharedVM.nodeVM.editNodeWidgetName(it) },
                    isError = widget.nameError.isNotEmpty(),
                    supportingText = { Text(widget.nameError) },
                )
            }
            item {
                Text(stringResource(Res.string.node_status))
                Switch(
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    checked = widget.status == Status.ENABLE,
                    onCheckedChange = { sharedVM.nodeVM.editNodeWidgetStatus(it) }
                )
            }
        }
        LazyColumn(modifier = Modifier.padding(end = 16.dp).weight(1f)) {
            item {
                Text(stringResource(Res.string.node_config))
                TextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.config,
                    onValueChange = { sharedVM.nodeVM.editNodeWidgetConfig(it) },
                    isError = widget.configError.isNotEmpty(),
                    supportingText = { Text(widget.configError) },
                    singleLine = false,
                    maxLines = 16
                )
            }
        }
    }

}