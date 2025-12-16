package io.github.ppoonk.airgo_master.ui.store.product.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import io.github.ppoonk.ac.ui.component.ACDisplayRichText
import io.github.ppoonk.ac.ui.component.ACDragHandle
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACLabelPrimary
import io.github.ppoonk.ac.ui.component.ACModalBottomSheet
import io.github.ppoonk.ac.ui.component.ACRichTextEditorDrawer
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.diffObject
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.ProductCategory
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen() {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val sharedVM = LocalSharedVM.current
    val widget by sharedVM.storeVM.productWidget.collectAsState()
    val nodeList = sharedVM.nodeVM.protocolList.collectAsLazyPagingItems()
    val protocolList = sharedVM.nodeVM.protocolList.collectAsLazyPagingItems()
    val vm = sharedVM.storeVM


    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(
                        when (widget.editType) {
                            EditType.CREATE -> "创建商品"
                            EditType.UPDATE -> "编辑商品"
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
                                        Repository.remote.createProduct(widget.toCreateProductReq())
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
                                        diffObject(
                                            widget.oldUpdateProductReq,
                                            widget.toUpdateProductReq()
                                        )?.let {
                                            Repository.remote.updateProduct(it.copy(id = widget.oldUpdateProductReq.id))
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
                        enabled = widget.createIsValid // TODO
                    ) {
                        ACIconSmall(ACIconDefault.Check, null)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(paddingValues).imePadding()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                Text("商品类别")
                ExposedDropdownMenuBox(
                    expanded = widget.expandProductCategory,
                    onExpandedChange = { vm.expandProductCategory(it) },
                ) {
                    ACTextField(
                        modifier = Modifier.fillParentMaxWidth()
                            .padding(top = 8.dp, bottom = 16.dp)
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = widget.category.i18n(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ACIconSmall(ACIconDefault.Sort, null) }
                    )
                    ExposedDropdownMenu(
                        expanded = widget.expandProductCategory,
                        onDismissRequest = { vm.expandProductCategory(false) },
                    ) {
                        ProductCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.i18n()) },
                                onClick = { vm.productCategory(category) }
                            )
                        }
                    }
                }
            }
            item {
                Text("商品名称")
                ACTextField(
                    value = widget.name,
                    onValueChange = { vm.productName(it) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text("商品状态")
                    Switch(
                        checked = widget.status == Status.ENABLE,
                        onCheckedChange = { vm.productStatus(it) },
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
            if (widget.category == ProductCategory.PRODUCT_CATEGORY_SUBSCRIBE) {
                item {
                    Text("绑定协议数量")
                    ACTextField(
                        value = "绑定协议数量：${widget.protocolIdList.size}",
                        onValueChange = {},
                        enabled = false,// 设置为 false 时 clickable 点击才有效
                        trailingIcon = { ACIconSmall(ACIconDefault.Sort, null) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                            .clickable {
                                vm.expandSelectNode(v = true)
                            }
                    )
                }
            }

            item {
                Text("主图")
                ACTextField(
                    value = widget.mainImage,
                    onValueChange = { vm.productMainImage(it) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Text("基础价格")
                ACTextField(
                    value = widget.monthlyPrice,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChange = { vm.productBasePrice(it) },
                    isError = widget.monthlyPriceError.isNotEmpty(),
                    supportingText = { Text(widget.monthlyPriceError) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Text("季度价格")
                ACTextField(
                    value = widget.quarterlyPrice,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChange = { vm.productQuarterlyPrice(it) },
                    isError = widget.quarterlyPriceError.isNotEmpty(),
                    supportingText = { Text(widget.quarterlyPriceError) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Text("半年价格")
                ACTextField(
                    value = widget.semiAnnualPrice,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChange = { vm.productSemiAnnualPrice(it) },
                    isError = widget.semiAnnualPriceError.isNotEmpty(),
                    supportingText = { Text(widget.semiAnnualPriceError) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Text("年价格")
                ACTextField(
                    value = widget.annualPrice,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChange = { vm.productAnnualPrice(it) },
                    isError = widget.annualPriceError.isNotEmpty(),
                    supportingText = { Text(widget.annualPriceError) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Text("商品详情")
                ACDisplayRichText(
                    text = widget.detail,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                        .clickable { vm.productRichTextEditor(true) }
                )
            }
        }

        // 富文本编辑
        ACRichTextEditorDrawer(
            modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars),
            text = widget.detail,
            expanded = widget.expandRichTextEditorDrawer,
            onDismissRequest = { vm.productRichTextEditor(false) },
            onSaved = { vm.productDetail(it) }
        )

        // 关联协议
        ACModalBottomSheet(
            expanded = widget.expandSelectNode,
            onDismissRequest = { vm.expandSelectNode(false) },
            dragHandle = {
                ACDragHandle(
                    start = {
                        IconButton(onClick = { vm.expandSelectNode(false) }) {
                            ACIconSmall(ACIconDefault.AngleLeft, null)
                        }
                    },
                    end = {
                        TextButton(onClick = { vm.productClearCheckedProtocolId() }) {
                            Text("重置")
                        }
                    },
                )
            },
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                nodeList.itemSnapshotList.items.forEach { n ->
                    // key 使用字符串可避免错误：Type of the key xxxxxxx is not supported. On Android you can only use types which can be stored inside the Bundle.
                    stickyHeader(key = n.id.toString()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp)
                        ) {
                            ACLabelPrimary(
                                "节点",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(n.name, style = MaterialTheme.typography.titleMedium)
                        }

                    }

                    item {
                        protocolList.itemSnapshotList.items.filter { it.nodeId == n.id }
                            .forEach { p ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                                ) {

                                    Checkbox(
                                        checked = widget.protocolIdList.contains(p.id),
                                        onCheckedChange = { vm.productCheckedProtocolId(p.id, it) }
                                    )
                                    Text(p.name)
                                }
                            }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}