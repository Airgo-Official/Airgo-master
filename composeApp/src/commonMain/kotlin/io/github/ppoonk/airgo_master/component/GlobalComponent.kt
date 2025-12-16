package io.github.ppoonk.airgo_master.component

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import io.github.ppoonk.airgo_master.sharedViewModel.SharedVM

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GlobalComponent(
    snackbarHostState: SnackbarHostState,
    sharedVM: SharedVM,
) {

    LoadingDialog(sharedVM.loadingDialogVM)

    ErrorDialog(sharedVM.dialogVM)

    Snackbar(sharedVM.snackbarVM,snackbarHostState)

    BaseSearchDrawer(sharedVM.baseSearchVM)

}



