package io.github.ppoonk.airgo_master.ui.configuration.push.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACCard
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.navigation.toEditPush
import io.github.ppoonk.airgo_master.repository.remote.model.PushType

@Composable
fun EmailScreen() {
    val navController = LocalNavController.current
    val sharedVM = LocalSharedVM.current
    val pushList by sharedVM.configurationVM.pushList.collectAsState()

    LaunchedEffect(Unit) {
        sharedVM.configurationVM.getPushList()
    }


    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).imePadding(),
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    sharedVM.configurationVM.initEditPush(EditType.CREATE)
                    navController.toEditPush()
                }) {
                    ACIconSmall(ACIconDefault.Plus, null)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        item {
            Text("Email")
            Spacer(Modifier.height(8.dp))
            pushList.filter { it.pushType == PushType.EMAIL.name }.forEach { p ->
                ACCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text(p.name)
                        Text(p.pushType)
                    }

                }
            }
            Spacer(Modifier.height(16.dp))
        }
        item {
            Text("Telegram bot")
            Spacer(Modifier.height(8.dp))
            pushList.filter { it.pushType == PushType.TG_BOT.name }.forEach { p ->
                ACCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(16.dp).clickable {
                            sharedVM.configurationVM.initEditPush(EditType.UPDATE, p)
                            navController.toEditPush()
                        }
                    ) {
                        Text(p.name)
                        Text(p.pushType)
                    }

                }
            }
            Spacer(Modifier.height(16.dp))
        }


    }

}
