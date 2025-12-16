package io.github.ppoonk.airgo_master.ui.node.protocolTemplate.edit

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACLabelPrimary
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.diffObject
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.repository.Repository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProtocolTemplateScreen() {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val vm = sharedVM.nodeVM
    val widget by vm.editProtocolTemplateWidget.collectAsState()

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(
                        when (widget.editType) {
                            EditType.CREATE -> "创建协议模板"
                            EditType.UPDATE -> "编辑协议模板"
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
                                        Repository.remote.createProtocolTemplate(widget.toCreateProtocolTemplateReq())
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
                                        // 只有新修改时才更新
                                        diffObject(
                                            old = widget.oldUpdateProtocolTemplateReq,
                                            new = widget.toUpdateProtocolTemplateReq(),
                                        )?.let { r ->
                                            val req =
                                                r.copy(id = widget.oldUpdateProtocolTemplateReq.id) // id 在 diffObject 中被过滤掉
                                            Repository.remote.updateProtocolTemplate(req)
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
        LazyColumn(modifier =  Modifier.padding(paddingValues).imePadding().padding(horizontal = 16.dp)) {
            stickyHeader {
                ACLabelPrimary(
                    "所有绑定该模板的节点，都会应用该模板的设置",
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                )
            }

            item {
                Text("名称")
                ACTextField(
                    value = widget.name,
                    onValueChange = { vm.protocolTemplateName(it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                )
                Spacer(Modifier.height(16.dp))
            }

            item {
                Text("Inbounds")
                ACTextField(
                    value = widget.inbounds,
                    onValueChange = { vm.protocolTemplateInbounds(it) },
                    isError = widget.inboundsError.isNotEmpty(),
                    supportingText = { Text(widget.inboundsError) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    singleLine = false,
                    maxLines = 26
                )
            }
        }
    }
}

