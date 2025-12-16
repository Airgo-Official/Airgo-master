package io.github.ppoonk.airgo_master.ui.store.product.list


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import io.github.ppoonk.ac.ui.component.ACCard
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.EmptyPlaceholder
import io.github.ppoonk.airgo_master.component.rememberLazyListState
import io.github.ppoonk.airgo_master.navigation.toEditProduct
import io.github.ppoonk.airgo_master.repository.Repository
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen() {
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val productList = sharedVM.storeVM.productList.collectAsLazyPagingItems()
    val navController = LocalNavController.current

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = { },
                actions = {
                    // 搜索
                    IconButton(onClick = {
                        // 打开搜索抽屉，初始化搜索参数
                        sharedVM.baseSearchVM.openSearchDrawer(
                            getSearchHistory = Repository.local::getProductSearchHistory,
                            setSearchHistory = Repository.local::setProductSearchHistory,
                            onConfirm = sharedVM.storeVM::refreshGetProductListReq
                        )
                        // 打开搜索抽屉后，获取搜索历史
                        sharedVM.baseSearchVM.getSearchHistory()
                    }) {
                        ACIconSmall(ACIconDefault.Search, null)
                    }
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
        LazyColumn(
            state = productList.rememberLazyListState(),
            modifier = Modifier.fillMaxSize().padding(it).padding(horizontal = 16.dp).imePadding()
        ) {
            items(productList.itemCount) { index ->
                productList[index]?.let { p ->
                    ACCard(
                        modifier = Modifier.fillMaxWidth().clickable {
                            sharedVM.storeVM.initEditProduct(EditType.UPDATE, p)
                            navController.toEditProduct()
                        },
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = p.id.toString(),
                                )
                                Text(
                                    text = p.category,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = p.name,
                                )
                                Text(
                                    text = p.monthlyPrice.toString(),
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            when (productList.loadState.refresh) {
                is LoadState.Loading -> item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Error -> {}
                else -> if (productList.itemSnapshotList.isEmpty()) item { EmptyPlaceholder() }
            }
        }
    }
}
