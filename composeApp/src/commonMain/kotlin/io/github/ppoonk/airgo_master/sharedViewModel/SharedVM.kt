package io.github.ppoonk.airgo_master.sharedViewModel

import io.github.ppoonk.airgo_master.component.BaseSearchVM
import io.github.ppoonk.airgo_master.component.DialogVM
import io.github.ppoonk.airgo_master.component.LoadingDialogVM
import io.github.ppoonk.airgo_master.component.SnackbarVM


class SharedVM(
    val baseSearchVM: BaseSearchVM = BaseSearchVM(),
    val dialogVM: DialogVM = DialogVM(),
    val loadingDialogVM: LoadingDialogVM = LoadingDialogVM(),
    val snackbarVM: SnackbarVM = SnackbarVM(),

    val nodeVM: NodeVM = NodeVM(),
    val userVM: UserVM = UserVM(),
    val storeVM: StoreVM = StoreVM(),
    val configurationVM: ConfigurationVM = ConfigurationVM()
)
