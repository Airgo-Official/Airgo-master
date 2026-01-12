package io.github.ppoonk.airgo_master.ui.store.product.list


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import io.github.ppoonk.ac.ui.component.ACButtonError
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.TimeUtils
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalPlatform
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.EmptyPlaceholder
import io.github.ppoonk.airgo_master.delete
import io.github.ppoonk.airgo_master.delete_confirmation
import io.github.ppoonk.airgo_master.navigation.toEditProduct
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.CouponTableColumn
import io.github.ppoonk.airgo_master.repository.remote.model.DeleteProductReq
import io.github.ppoonk.airgo_master.repository.remote.model.ProductCategory
import io.github.ppoonk.airgo_master.repository.remote.model.ProductTableColumn
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.warning
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen() {
    val scope = rememberCoroutineScope()
    val platform = LocalPlatform.current
    val sharedVM = LocalSharedVM.current
    val list = sharedVM.storeVM.productList.collectAsLazyPagingItems()
    val navController = LocalNavController.current
    val rowState = rememberLazyListState()
    val columnState = rememberLazyListState()

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = { },
                actions = {
                    // 新建
                    IconButton(onClick = {
                        scope.launch {
                            sharedVM.storeVM.initEditProduct(EditType.CREATE)
                            navController.toEditProduct()
                        }
                    }) { ACIconSmall(ACIconDefault.Plus, null) }
                }
            )
        }
    ) {
        Column(Modifier.padding(it).padding(horizontal = 16.dp)) {
            // 外层横向滚动（包裹表头+内容）
            Row(modifier = Modifier.weight(1f)) {
                LazyRow(
                    state = rowState,
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Column {
                            // 表头
                            Row(
                                modifier = Modifier.padding(bottom = 16.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        CardDefaults.shape
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ProductTableColumn.entries.forEach { t ->
                                    Text(
                                        text = stringResource(t.text),
                                        modifier = Modifier.width(t.width),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }

                            // 内容列表
                            LazyColumn(state = columnState) {
                                if (list.itemSnapshotList.isEmpty()) {
                                    item {
                                        EmptyPlaceholder()
                                    }
                                } else {
                                    items(list.itemSnapshotList.items) { item ->
                                        Card(
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                SelectionContainer {
                                                    Text(
                                                        text = item.id.toString(),
                                                        modifier = Modifier.width(ProductTableColumn.ID.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = item.name,
                                                        modifier = Modifier.width(ProductTableColumn.NAME.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = Status.i18nByOrdinal(item.status),
                                                        modifier = Modifier.width(ProductTableColumn.STATUS.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = ProductCategory.valueOf(item.category).i18n(),
                                                        modifier = Modifier.width(ProductTableColumn.CATEGORY.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = item.monthlyPrice.toString(),
                                                        modifier = Modifier.width(ProductTableColumn.MONTHLY_PRICE.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = item.quarterlyPrice.toString(),
                                                        modifier = Modifier.width(ProductTableColumn.QUARTERLY_PRICE.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = item.semiAnnualPrice.toString(),
                                                        modifier = Modifier.width(ProductTableColumn.SEMI_ANNUAL_PRICE.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = item.annualPrice.toString(),
                                                        modifier = Modifier.width(ProductTableColumn.ANNUAL_PRICE.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = item.mainImage ?: "",
                                                        modifier = Modifier.width(ProductTableColumn.MAIN_IMAGE.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = TimeUtils.toLocalDateString(
                                                            item.createdAt
                                                        ),
                                                        modifier = Modifier.width(ProductTableColumn.CREATED_AT.width)
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.width(CouponTableColumn.OPERATE.width),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    IconButton(onClick = {
                                                        sharedVM.storeVM.initEditProduct(
                                                            EditType.UPDATE,
                                                            item
                                                        )
                                                        navController.toEditProduct()
                                                    }) {
                                                        ACIconSmall(ACIconDefault.Edit, null)
                                                    }
                                                    IconButton(onClick = {
                                                        sharedVM.dialogVM.openDialog(
                                                            title = { Text(stringResource(Res.string.warning)) },
                                                            text = { Text(stringResource(Res.string.delete_confirmation)) }
                                                        ) {
                                                            ACButtonError(onClick = {
                                                                scope.launch {
                                                                    Repository.remote.deleteProduct(
                                                                        DeleteProductReq(id = item.id)
                                                                    )
                                                                        .onFailure { r ->
                                                                            sharedVM.dialogVM.openDialog(
                                                                                title = { Text(r.code.toString()) },
                                                                                text = { Text(r.message) }
                                                                            )
                                                                        }
                                                                        .onSuccess {
                                                                            sharedVM.dialogVM.closeDialog()
                                                                            list.refresh()
                                                                        }
                                                                }

                                                            }) {
                                                                Text(stringResource(Res.string.delete))
                                                            }
                                                        }
                                                    }) {
                                                        ACIconSmall(ACIconDefault.Trash, null)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                platform.VerticalScrollbar(
                    state = columnState,
                    modifier = Modifier.padding(start = 16.dp).fillMaxHeight()
                )

            }
            platform.HorizontalScrollbar(
                state = rowState,
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
            )
        }
    }
}
