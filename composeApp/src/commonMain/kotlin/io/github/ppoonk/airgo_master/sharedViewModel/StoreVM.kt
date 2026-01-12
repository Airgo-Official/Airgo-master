package io.github.ppoonk.airgo_master.sharedViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import io.github.ppoonk.ac.utils.Result
import io.github.ppoonk.ac.utils.ValidationResult
import io.github.ppoonk.ac.utils.ValidationUtils
import io.github.ppoonk.airgo_master.component.BaseSearchWidget
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.SearchType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.Coupon
import io.github.ppoonk.airgo_master.repository.remote.model.CouponType
import io.github.ppoonk.airgo_master.repository.remote.model.CreateCouponReq
import io.github.ppoonk.airgo_master.repository.remote.model.CreateProductReq
import io.github.ppoonk.airgo_master.repository.remote.model.FilterProduct
import io.github.ppoonk.airgo_master.repository.remote.model.GetCouponListReq
import io.github.ppoonk.airgo_master.repository.remote.model.GetProductListReq
import io.github.ppoonk.airgo_master.repository.remote.model.Product
import io.github.ppoonk.airgo_master.repository.remote.model.ProductCategory
import io.github.ppoonk.airgo_master.repository.remote.model.SearchProduct
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateCouponReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateProductReq
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

class StoreVM() : ViewModel() {
    private val _currentProduct = MutableStateFlow<Product?>(null)
    val currentProduct: StateFlow<Product?> = _currentProduct
    fun refreshCurrentProduct(new: Product): Unit {
        _currentProduct.value = new
    }


    private val _currentCoupon = MutableStateFlow<Coupon?>(null)
    val currentCoupon: StateFlow<Coupon?> = _currentCoupon
    fun refreshCurrentCoupon(new: Coupon): Unit {
        _currentCoupon.value = new
    }


    private val _getProductListReq = MutableStateFlow(GetProductListReq())
    fun refreshGetProductListReq(v: BaseSearchWidget): Unit {
        with(v) {
            var req = GetProductListReq()

            // search 参数
            search.isNotEmpty().let {
                val r1 = when (searchType) {
                    SearchType.ID -> SearchProduct(id = search.toUIntOrNull())
                    SearchType.NAME -> SearchProduct(name = search)
                }
                req = req.copy(search = r1)
            }

            // filter 参数
            val r2 = FilterProduct(
                status = if (status == Status.ALL) null else status.ordinal,
                createdAtStart = datePickerStart,
                createdAtEnd = datePickerEnd
            )
            req = req.copy(filter = r2)
            _getProductListReq.value = req
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val productList = _getProductListReq.flatMapLatest {
        Repository.newPagingSource { p ->
            when (val res = Repository.remote.getProductList(
                it.copy(
                    pagination = it.pagination.copy(
                        page = p.first,
                        pageSize = p.second
                    )
                )
            )) {
                is Result.Error -> Pair(emptyList(), 0)
                is Result.Success -> Pair(res.data!!.list, res.data!!.total)
            }
        }.flow.cachedIn(viewModelScope)
    }

    private val _getCouponListReq = MutableStateFlow(GetCouponListReq())

    @OptIn(ExperimentalCoroutinesApi::class)
    val couponList = _getCouponListReq.flatMapLatest {
        Repository.newPagingSource { p ->
            when (val res = Repository.remote.getCouponList(
                it.copy(
                    pagination = it.pagination.copy(
                        page = p.first,
                        pageSize = p.second
                    )
                )
            )) {
                is Result.Error -> Pair(emptyList(), 0)
                is Result.Success -> Pair(res.data!!.list, res.data!!.total)
            }
        }.flow.cachedIn(viewModelScope)
    }


    private val _editCouponWidget = MutableStateFlow(EditCouponWidget())
    val editCouponWidget: StateFlow<EditCouponWidget> = _editCouponWidget

    fun initEditCoupon(editType: EditType, current: Coupon? = null): Unit {
        when (editType) {
            EditType.CREATE -> _editCouponWidget.value = EditCouponWidget()

            EditType.UPDATE -> {
                current?.let {
                    _currentCoupon.value = it
                    _editCouponWidget.value = EditCouponWidget(
                        id = it.id,
                        name = it.name,
                        status = Status.entries[it.status],
                        couponCode = it.couponCode,
                        couponType = CouponType.valueOf(it.couponType),
                        discount = it.discount.toString(),
                        minOrderAmount = it.minOrderAmount.toString(),
//                        productIdList = it.productIdList, // TODO 加载
                        oldUpdateCouponReq = UpdateCouponReq(
                            id = it.id,
                            name = it.name,
                            status = it.status,
                            couponCode = it.couponCode,
                            couponType = it.couponType,
                            discount = it.discount,
                            minOrderAmount = it.minOrderAmount,
//                            productIdList = it.productIdList, // TODO
                        ),
                        editType = editType,
                    )
                }
            }
        }


    }

    fun editCouponWidgetExpandCouponType(v: Boolean): Unit {
        _editCouponWidget.value = _editCouponWidget.value.copy(
            expandCouponType = v,
        )
    }

    fun editCouponWidgetCouponType(v: CouponType): Unit {
        _editCouponWidget.value = _editCouponWidget.value.copy(
            couponType = v,
            discount = "",
            discountError = "",
            expandCouponType = false,
        )
    }

    fun editCouponWidgetName(v: String): Unit {
        _editCouponWidget.value = _editCouponWidget.value.copy(
            name = v,
        )
    }

    fun editCouponWidgetStatus(v: Boolean): Unit {
        _editCouponWidget.value = _editCouponWidget.value.copy(
            status = if (v) Status.ENABLE else Status.DISABLE,
        )
    }

    fun editCouponWidgetCouponCode(v: String): Unit {
        _editCouponWidget.value = _editCouponWidget.value.copy(
            couponCode = v,
        )
    }

    fun editCouponWidgetDiscount(v: String): Unit {
        val res = when (_editCouponWidget.value.couponType) {
            CouponType.DISCOUNT_RATE -> { // 按比例折扣
                ValidationUtils.validateDecimalPlaces2(
                    input = v,
                    range = 0.0..1.0
                )
            }

            else -> {
                ValidationUtils.validateDecimalPlaces2(
                    input = v,
                )
            }
        }
        val err = when (res) {
            is ValidationResult.Failure -> res.error
            is ValidationResult.Success -> ""
        }


        _editCouponWidget.value = _editCouponWidget.value.copy(
            discount = v,
            discountError = err
        )
    }

    fun editCouponWidgetMinOrderAmount(v: String): Unit {
        val err = when (val res = ValidationUtils.validateDecimalPlaces2(v)) {
            is ValidationResult.Failure -> res.error

            is ValidationResult.Success -> ""
        }
        _editCouponWidget.value = _editCouponWidget.value.copy(
            minOrderAmount = v,
            minOrderAmountError = err
        )
    }

    fun editCouponWidgetExpandSelectProduct(v: Boolean): Unit {
        _editCouponWidget.value = _editCouponWidget.value.copy(
            expandSelectProduct = v,
        )
    }

    fun editCouponWidgetCheckedProduct(v: UInt, checked: Boolean) {
        val l = _editCouponWidget.value.productIdList.toMutableList().apply {
            if (checked) add(v) else remove(v)
        }
        _editCouponWidget.value = _editCouponWidget.value.copy(productIdList = l)
    }

    fun editCouponWidgetClearCheckedProduct() {
        _editCouponWidget.value = _editCouponWidget.value.copy(productIdList = emptyList())
    }


    private val _editProductWidget = MutableStateFlow(EditProductWidget())
    val editProductWidget: StateFlow<EditProductWidget> = _editProductWidget

    fun initEditProduct(editType: EditType, current: Product? = null): Unit {
        when (editType) {
            EditType.CREATE -> {
                _editProductWidget.value = EditProductWidget()
            }

            EditType.UPDATE -> {
                current?.let { v ->
                    _editProductWidget.value = EditProductWidget(
                        id = v.id,
                        name = v.name,
                        category = ProductCategory.valueOf(v.category),
                        detail = v.detail ?: "",
                        status = Status.entries[v.status],
                        mainImage = v.mainImage ?: "",
                        monthlyPrice = v.monthlyPrice.toString(),
                        quarterlyPrice = v.quarterlyPrice.toString(),
                        semiAnnualPrice = v.semiAnnualPrice.toString(),
                        annualPrice = v.annualPrice.toString(),
                        oldUpdateProductReq = UpdateProductReq(
                            id = v.id,
                            name = v.name,
                            category = v.category,
                            detail = v.detail,
                            status = v.status,
                            mainImage = v.mainImage,
                            monthlyPrice = v.monthlyPrice,
                            quarterlyPrice = v.quarterlyPrice,
                            semiAnnualPrice = v.semiAnnualPrice,
                            annualPrice = v.annualPrice,
                        ),
                        editType = editType,
                    )
                }
            }
        }

    }

    fun editProductWidgetExpandProductCategory(v: Boolean): Unit {
        _editProductWidget.value = _editProductWidget.value.copy(
            expandProductCategory = v
        )
    }

    fun editProductWidgetCategory(v: ProductCategory): Unit {
        _editProductWidget.value = _editProductWidget.value.copy(
            category = v,
            expandProductCategory = false
        )
    }

    fun editProductWidgetName(v: String): Unit {
        _editProductWidget.value = _editProductWidget.value.copy(
            name = v
        )
    }

    fun editProductWidgetStatus(v: Boolean): Unit {
        _editProductWidget.value = _editProductWidget.value.copy(
            status = if (v) Status.ENABLE else Status.DISABLE
        )
    }

    fun editProductWidgetExpandSelectNode(v: Boolean): Unit {
        _editProductWidget.value = _editProductWidget.value.copy(
            expandSelectProtocol = v
        )
    }

    fun editProductWidgetClearSelectProtocol(): Unit {
        _editProductWidget.value = _editProductWidget.value.copy(
            protocolIdList = emptyList()
        )
    }

    fun editProductWidgetMainImage(v: String): Unit {
        _editProductWidget.value = _editProductWidget.value.copy(
            mainImage = v
        )
    }

    fun editProductWidgetMonthlyPrice(v: String): Unit {
        val err = when (val res = ValidationUtils.validateDecimalPlaces2(v)) {
            is ValidationResult.Failure -> res.error

            is ValidationResult.Success -> ""
        }

        _editProductWidget.value = _editProductWidget.value.copy(
            monthlyPrice = v,
            monthlyPriceError = err
        )
    }

    fun editProductWidgetQuarterlyPrice(v: String): Unit {
        val err = when (val res = ValidationUtils.validateDecimalPlaces2(v)) {
            is ValidationResult.Failure -> res.error

            is ValidationResult.Success -> ""
        }

        _editProductWidget.value = _editProductWidget.value.copy(
            quarterlyPrice = v,
            quarterlyPriceError = err
        )
    }

    fun editProductWidgetSemiAnnualPrice(v: String): Unit {
        val err = when (val res = ValidationUtils.validateDecimalPlaces2(v)) {
            is ValidationResult.Failure -> res.error

            is ValidationResult.Success -> ""
        }

        _editProductWidget.value = _editProductWidget.value.copy(
            semiAnnualPrice = v,
            semiAnnualPriceError = err
        )
    }

    fun editProductWidgetAnnualPrice(v: String): Unit {
        val err = when (val res = ValidationUtils.validateDecimalPlaces2(v)) {
            is ValidationResult.Failure -> res.error

            is ValidationResult.Success -> ""
        }

        _editProductWidget.value = _editProductWidget.value.copy(
            annualPrice = v,
            annualPriceError = err
        )
    }

    fun editProductWidgetDetail(v: String): Unit {
        _editProductWidget.value = _editProductWidget.value.copy(
            detail = v
        )
    }

    fun editProductWidgetCheckedProtocol(v: UInt, checked: Boolean) {
        val l = _editProductWidget.value.protocolIdList.toMutableList().apply {
            if (checked) add(v) else remove(v)
        }
        _editProductWidget.value = _editProductWidget.value.copy(protocolIdList = l)
    }

    fun editProductWidgetClearCheckedProtocol() {
        _editProductWidget.value = _editProductWidget.value.copy(protocolIdList = emptyList())
    }

    fun editProductWidgetExpandRichTextEditor(v: Boolean) {
        _editProductWidget.value = _editProductWidget.value.copy(expandRichTextEditorDrawer = v)
    }



}

data class EditCouponWidget(
    val id: UInt = 0u,
    val name: String = "",
    val status: Status = Status.ENABLE,
    val couponCode: String = "",
    val couponType: CouponType = CouponType.THRESHOLD,
    val discount: String = "",
    val minOrderAmount: String = "",
    val productIdList: List<UInt> = emptyList(),

    val expandCouponType: Boolean = false,
    val expandSelectProduct: Boolean = false,

    val discountError: String = "",
    val minOrderAmountError: String = "",

    val oldUpdateCouponReq: UpdateCouponReq = UpdateCouponReq(),

    val editType: EditType = EditType.CREATE,


    ) {
    val createIsValid: Boolean
        get() = name.isNotEmpty() &&
                couponCode.isNotEmpty() &&
                discount.isNotEmpty() &&
                discountError.isEmpty() &&
                discountError.isEmpty() &&
                minOrderAmountError.isEmpty()

    val updateIsValid: Boolean
        get() = discountError.isEmpty() &&
                minOrderAmountError.isEmpty()

    fun toCreateCouponReq(): CreateCouponReq {
        return CreateCouponReq(
            name = this.name,
            status = this.status.ordinal,
            couponCode = this.couponCode,
            couponType = this.couponType.name,
            discount = this.discount.toDouble(),
            minOrderAmount = this.minOrderAmount.toDouble(),
            productIdList = this.productIdList
        )
    }

    fun toUpdateCouponReq(): UpdateCouponReq {
        return UpdateCouponReq(
            id = this.id,
            name = this.name,
            status = this.status.ordinal,
            couponCode = this.couponCode,
            couponType = this.couponType.name,
            discount = this.discount.toDoubleOrNull(),
            minOrderAmount = this.minOrderAmount.toDoubleOrNull(),
            productIdList = this.productIdList
        )
    }
}


data class EditProductWidget(
    val id: UInt = 0u,
    val name: String = "",
    val category: ProductCategory = ProductCategory.PRODUCT_CATEGORY_SUBSCRIBE,
    val detail: String = "",
    val status: Status = Status.ENABLE,
    val mainImage: String = "",
    val monthlyPrice: String = "0.0",
    val quarterlyPrice: String = "0.0",
    val semiAnnualPrice: String = "0.0",
    val annualPrice: String = "0.0",
    val protocolIdList: List<UInt> = emptyList(),

    val monthlyPriceError: String = "",
    val quarterlyPriceError: String = "",
    val semiAnnualPriceError: String = "",
    val annualPriceError: String = "",

    val expandProductCategory: Boolean = false,
    val expandSelectProtocol: Boolean = false,
    val expandRichTextEditorDrawer: Boolean = false,

    val oldUpdateProductReq: UpdateProductReq = UpdateProductReq(),

    val editType: EditType = EditType.CREATE,
) {
    val createIsValid: Boolean
        get() = monthlyPriceError.isEmpty()
                && quarterlyPriceError.isEmpty()
                && semiAnnualPriceError.isEmpty()
                && annualPriceError.isEmpty()

    val updateIsValid: Boolean
        get() = monthlyPriceError.isEmpty()
                && quarterlyPriceError.isEmpty()
                && semiAnnualPriceError.isEmpty()
                && annualPriceError.isEmpty()

    fun toCreateProductReq(): CreateProductReq {
        return CreateProductReq(
            name = this.name,
            category = this.category.name,
            detail = this.detail,
            status = this.status.ordinal,
            mainImage = this.mainImage,
            monthlyPrice = if (this.monthlyPrice.isEmpty()) 0.0 else this.monthlyPrice.toDouble(),
            quarterlyPrice = if (this.quarterlyPrice.isEmpty()) 0.0 else this.quarterlyPrice.toDouble(),
            semiAnnualPrice = if (this.semiAnnualPrice.isEmpty()) 0.0 else this.semiAnnualPrice.toDouble(),
            annualPrice = if (this.annualPrice.isEmpty()) 0.0 else this.annualPrice.toDouble(),
            protocolIdList = this.protocolIdList
        )
    }

    fun toUpdateProductReq(): UpdateProductReq {
        return UpdateProductReq(
            id = this.id,
            name = this.name,
            category = this.category.name,
            detail = this.detail,
            status = this.status.ordinal,
            mainImage = this.mainImage,
            monthlyPrice = if (this.monthlyPrice.isEmpty()) 0.0 else this.monthlyPrice.toDouble(),
            quarterlyPrice = if (this.quarterlyPrice.isEmpty()) 0.0 else this.quarterlyPrice.toDouble(),
            semiAnnualPrice = if (this.semiAnnualPrice.isEmpty()) 0.0 else this.semiAnnualPrice.toDouble(),
            annualPrice = if (this.annualPrice.isEmpty()) 0.0 else this.annualPrice.toDouble(),
            protocolIdList = this.protocolIdList
        )
    }

}

