package io.github.ppoonk.airgo_master.ui.node.node.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.diffObject
import io.github.ppoonk.ac.utils.mergeObject
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNodeScreen() {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val vm = sharedVM.nodeVM
    val widget by sharedVM.nodeVM.editNodeWidget.collectAsState()
    val currentNode by sharedVM.nodeVM.currentNode.collectAsState()


    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(
                        when (widget.editType) {
                            EditType.CREATE -> "创建节点"
                            EditType.UPDATE -> "编辑节点"
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
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(paddingValues).imePadding().padding(horizontal = 16.dp)
        ) {
            item {
                Text("名称")
                ACTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.name,
                    onValueChange = { vm.nodeName(it) },
                    isError = widget.nameError.isNotEmpty(),
                    supportingText = { Text(widget.nameError) },
                )
            }
            item {
                Text("状态")
                Switch(
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                    checked = widget.status == Status.ENABLE,
                    onCheckedChange = { vm.nodeStatus(it) }
                )
            }
            item {
                Text("config")
                ACTextField(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    value = widget.config,
                    onValueChange = { vm.nodeConfig(it) },
                    isError = widget.configError.isNotEmpty(),
                    supportingText = { Text(widget.configError) },
                    singleLine = false,
                    maxLines = 16
                )
            }
        }

    }
}
