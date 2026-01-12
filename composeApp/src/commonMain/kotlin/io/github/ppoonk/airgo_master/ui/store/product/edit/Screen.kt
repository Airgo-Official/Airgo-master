package io.github.ppoonk.airgo_master.ui.store.product.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.ui.component.AutoSizeFade
import io.github.ppoonk.ac.utils.diffObject
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.ProductCategory
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.sharedViewModel.SharedVM
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.node
import io.github.ppoonk.airgo_master.product_annual_price
import io.github.ppoonk.airgo_master.product_associated_protocol
import io.github.ppoonk.airgo_master.product_category
import io.github.ppoonk.airgo_master.product_create
import io.github.ppoonk.airgo_master.product_detail
import io.github.ppoonk.airgo_master.product_main_image
import io.github.ppoonk.airgo_master.product_monthly_price
import io.github.ppoonk.airgo_master.product_name
import io.github.ppoonk.airgo_master.product_quarterly_price
import io.github.ppoonk.airgo_master.product_semi_annual_price
import io.github.ppoonk.airgo_master.product_status
import io.github.ppoonk.airgo_master.product_update
import io.github.ppoonk.airgo_master.reset


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen() {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val sharedVM = LocalSharedVM.current
    val widget by sharedVM.storeVM.editProductWidget.collectAsState()
    val nodeList = sharedVM.nodeVM.protocolList.collectAsLazyPagingItems()
    val protocolList = sharedVM.nodeVM.protocolList.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(
                        stringResource(
                            when (widget.editType) {
                                EditType.CREATE -> Res.string.product_create
                                EditType.UPDATE -> Res.string.product_update
                            }
                        )
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
                                        )?.let { req ->
                                            Repository.remote.updateProduct(req.copy(id = widget.oldUpdateProductReq.id))
                                                .onFailure { r ->
                                                    sharedVM.dialogVM.openDialog(
                                                        title = { Text(r.code.toString()) },
                                                        text = { Text(r.message) }
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
        AutoSizeFade(
            compact = {
                compact(
                    modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
                    sharedVM
                )
            },
            medium = {
                compact(
                    modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
                    sharedVM = sharedVM,
                )
            },
            expanded = {
                expanded(
                    modifier = Modifier.padding(paddingValues).padding(horizontal = 16.dp),
                    sharedVM
                )
            },
        )

        // 富文本编辑
        ACRichTextEditorDrawer(
            modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars),
            text = widget.detail,
            expanded = widget.expandRichTextEditorDrawer,
            onDismissRequest = { sharedVM.storeVM.editProductWidgetExpandRichTextEditor(false) },
            onSaved = { sharedVM.storeVM.editProductWidgetDetail(it) },
        )

        // 关联协议
        ACModalBottomSheet(
            expanded = widget.expandSelectProtocol,
            onDismissRequest = { sharedVM.storeVM.editProductWidgetExpandSelectNode(false) },
            dragHandle = {
                ACDragHandle(
                    start = {
                        IconButton(onClick = {
                            sharedVM.storeVM.editProductWidgetExpandSelectNode(
                                false
                            )
                        }) {
                            ACIconSmall(ACIconDefault.AngleLeft, null)
                        }
                    },
                    end = {
                        TextButton(onClick = { sharedVM.storeVM.editProductWidgetClearCheckedProtocol() }) {
                            Text(stringResource(Res.string.reset))
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
                                stringResource(Res.string.node),
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
                                        onCheckedChange = {
                                            sharedVM.storeVM.editProductWidgetCheckedProtocol(
                                                p.id,
                                                it
                                            )
                                        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun compact(modifier: Modifier, sharedVM: SharedVM): Unit {
    val widget by sharedVM.storeVM.editProductWidget.collectAsState()

    LazyColumn(
        modifier = modifier,
    ) {
        item {
            Text(stringResource(Res.string.product_name))
            TextField(
                value = widget.name,
                onValueChange = { sharedVM.storeVM.editProductWidgetName(it) },
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
                Text(stringResource(Res.string.product_status))
                Switch(
                    checked = widget.status == Status.ENABLE,
                    onCheckedChange = { sharedVM.storeVM.editProductWidgetStatus(it) },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
            ) {
                Text(stringResource(Res.string.product_category))
                ExposedDropdownMenuBox(
                    expanded = widget.expandProductCategory,
                    onExpandedChange = {
                        sharedVM.storeVM.editProductWidgetExpandProductCategory(
                            it
                        )
                    },
                ) {
                    TextField(
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        value = widget.category.i18n(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ACIconSmall(ACIconDefault.Sort, null) }
                    )
                    ExposedDropdownMenu(
                        expanded = widget.expandProductCategory,
                        onDismissRequest = {
                            sharedVM.storeVM.editProductWidgetExpandProductCategory(
                                false
                            )
                        },
                    ) {
                        ProductCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.i18n()) },
                                onClick = { sharedVM.storeVM.editProductWidgetCategory(category) }
                            )
                        }
                    }
                }
            }
        }
        if (widget.category == ProductCategory.PRODUCT_CATEGORY_SUBSCRIBE) {
            item {
                Text(stringResource(Res.string.product_associated_protocol))
                TextField(
                    value = "${stringResource(Res.string.product_associated_protocol)}: ${widget.protocolIdList.size}",
                    onValueChange = {},
                    enabled = false,// 设置为 false 时 clickable 点击才有效
                    trailingIcon = { ACIconSmall(ACIconDefault.Sort, null) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                        .clickable {
                            sharedVM.storeVM.editProductWidgetExpandSelectNode(v = true)
                        }
                )
            }
        }

        item {
            Text(stringResource(Res.string.product_main_image))
            TextField(
                value = widget.mainImage,
                onValueChange = { sharedVM.storeVM.editProductWidgetMainImage(it) },
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            )
        }
        item {
            Text(stringResource(Res.string.product_monthly_price))
            TextField(
                value = widget.monthlyPrice,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = { sharedVM.storeVM.editProductWidgetMonthlyPrice(it) },
                isError = widget.monthlyPriceError.isNotEmpty(),
                supportingText = { Text(widget.monthlyPriceError) },
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            )
        }
        item {
            Text(stringResource(Res.string.product_quarterly_price))
            TextField(
                value = widget.quarterlyPrice,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = { sharedVM.storeVM.editProductWidgetQuarterlyPrice(it) },
                isError = widget.quarterlyPriceError.isNotEmpty(),
                supportingText = { Text(widget.quarterlyPriceError) },
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            )
        }
        item {
            Text(stringResource(Res.string.product_semi_annual_price))
            TextField(
                value = widget.semiAnnualPrice,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = { sharedVM.storeVM.editProductWidgetSemiAnnualPrice(it) },
                isError = widget.semiAnnualPriceError.isNotEmpty(),
                supportingText = { Text(widget.semiAnnualPriceError) },
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            )
        }
        item {
            Text(stringResource(Res.string.product_annual_price))
            TextField(
                value = widget.annualPrice,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                onValueChange = { sharedVM.storeVM.editProductWidgetAnnualPrice(it) },
                isError = widget.annualPriceError.isNotEmpty(),
                supportingText = { Text(widget.annualPriceError) },
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            )
        }
        item {
            Text(stringResource(Res.string.product_detail))
            ACDisplayRichText(
                text = widget.detail,
                enabled = false,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                    .clickable {
                        sharedVM.storeVM.editProductWidgetExpandRichTextEditor(true)
                    }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun expanded(modifier: Modifier, sharedVM: SharedVM): Unit {
    val widget by sharedVM.storeVM.editProductWidget.collectAsState()

    Row(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.padding(end = 8.dp).weight(1f),
        ) {
            item {
                Text(stringResource(Res.string.product_name))
                TextField(
                    value = widget.name,
                    onValueChange = { sharedVM.storeVM.editProductWidgetName(it) },
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
                    Text(stringResource(Res.string.product_status))
                    Switch(
                        checked = widget.status == Status.ENABLE,
                        onCheckedChange = { sharedVM.storeVM.editProductWidgetStatus(it) },
                        modifier = Modifier.fillMaxHeight()
                    )
                }
            }
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                ) {
                    Text(stringResource(Res.string.product_category))
                    ExposedDropdownMenuBox(
                        expanded = widget.expandProductCategory,
                        onExpandedChange = {
                            sharedVM.storeVM.editProductWidgetExpandProductCategory(
                                it
                            )
                        },
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            value = widget.category.i18n(),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ACIconSmall(ACIconDefault.Sort, null) }
                        )
                        ExposedDropdownMenu(
                            expanded = widget.expandProductCategory,
                            onDismissRequest = {
                                sharedVM.storeVM.editProductWidgetExpandProductCategory(
                                    false
                                )
                            },
                        ) {
                            ProductCategory.entries.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.i18n()) },
                                    onClick = { sharedVM.storeVM.editProductWidgetCategory(category) }
                                )
                            }
                        }
                    }
                }
            }
            if (widget.category == ProductCategory.PRODUCT_CATEGORY_SUBSCRIBE) {
                item {
                    Text(stringResource(Res.string.product_associated_protocol))
                    TextField(
                        value = "${stringResource(Res.string.product_associated_protocol)}: ${widget.protocolIdList.size}",
                        onValueChange = {},
                        enabled = false,// 设置为 false 时 clickable 点击才有效
                        trailingIcon = { ACIconSmall(ACIconDefault.Sort, null) },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                            .clickable {
                                sharedVM.storeVM.editProductWidgetExpandSelectNode(v = true)
                            }
                    )
                }
            }

            item {
                Text(stringResource(Res.string.product_main_image))
                TextField(
                    value = widget.mainImage,
                    onValueChange = { sharedVM.storeVM.editProductWidgetMainImage(it) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Text(stringResource(Res.string.product_monthly_price))
                TextField(
                    value = widget.monthlyPrice,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChange = { sharedVM.storeVM.editProductWidgetMonthlyPrice(it) },
                    isError = widget.monthlyPriceError.isNotEmpty(),
                    supportingText = { Text(widget.monthlyPriceError) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Text(stringResource(Res.string.product_quarterly_price))
                TextField(
                    value = widget.quarterlyPrice,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChange = { sharedVM.storeVM.editProductWidgetQuarterlyPrice(it) },
                    isError = widget.quarterlyPriceError.isNotEmpty(),
                    supportingText = { Text(widget.quarterlyPriceError) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Text(stringResource(Res.string.product_semi_annual_price))
                TextField(
                    value = widget.semiAnnualPrice,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChange = { sharedVM.storeVM.editProductWidgetSemiAnnualPrice(it) },
                    isError = widget.semiAnnualPriceError.isNotEmpty(),
                    supportingText = { Text(widget.semiAnnualPriceError) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
            item {
                Text(stringResource(Res.string.product_annual_price))
                TextField(
                    value = widget.annualPrice,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    onValueChange = { sharedVM.storeVM.editProductWidgetAnnualPrice(it) },
                    isError = widget.annualPriceError.isNotEmpty(),
                    supportingText = { Text(widget.annualPriceError) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp)
                )
            }
        }
        Column(
            modifier = Modifier.padding(start = 8.dp).weight(2f), // TODO 其他也修改padding
        ) {
            Text(stringResource(Res.string.product_detail))
            ACDisplayRichText(
                text = widget.detail,
                enabled = false,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp)
                    .clickable { sharedVM.storeVM.editProductWidgetExpandRichTextEditor(true) }
            )

        }
    }
}