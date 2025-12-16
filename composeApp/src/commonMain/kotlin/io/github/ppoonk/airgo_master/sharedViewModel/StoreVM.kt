package io.github.ppoonk.airgo_master.sharedViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import io.github.ppoonk.ac.utils.Result
import io.github.ppoonk.ac.utils.ValidationResult
import io.github.ppoonk.ac.utils.ValidationUtils
import io.github.ppoonk.airgo_master.component.BaseSearchWidget
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.SearchHistory
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


    private val _historyList = MutableStateFlow<List<SearchHistory>>(emptyList())
    val historyList: StateFlow<List<SearchHistory>> = _historyList
    fun getSearchHistory(): Unit {
        _historyList.value = Repository.local.getProductSearchHistory()
    }

    fun setSearchHistory(v: SearchHistory?): Unit {
        Repository.local.setProductSearchHistory(v)
        getSearchHistory()
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

    fun refreshGetProductListReq2(v: BaseSearchWidget): Unit {
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


    private val _couponWidget = MutableStateFlow(EditCouponWidget())
    val editCouponWidget: StateFlow<EditCouponWidget> = _couponWidget

    fun initEditCoupon(editType: EditType, current: Coupon? = null): Unit {
        when (editType) {
            EditType.CREATE -> _couponWidget.value = EditCouponWidget()

            EditType.UPDATE -> {
                current?.let {
                    _currentCoupon.value = it
                    _couponWidget.value = EditCouponWidget(
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

    fun expandCouponType(v: Boolean): Unit {
        _couponWidget.value = _couponWidget.value.copy(
            expandCouponType = v,
        )
    }

    fun couponType(v: CouponType): Unit {
        _couponWidget.value = _couponWidget.value.copy(
            couponType = v,
            discount = "",
            discountError = "",
            expandCouponType = false,
        )
    }

    fun couponName(v: String): Unit {
        _couponWidget.value = _couponWidget.value.copy(
            name = v,
        )
    }

    fun couponStatus(v: Boolean): Unit {
        _couponWidget.value = _couponWidget.value.copy(
            status = if (v) Status.ENABLE else Status.DISABLE,
        )
    }

    fun couponCode(v: String): Unit {
        _couponWidget.value = _couponWidget.value.copy(
            couponCode = v,
        )
    }

    fun couponDiscount(v: String): Unit {
        val res = when (_couponWidget.value.couponType) {
            CouponType.DISCOUNT_RATE -> {
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


        _couponWidget.value = _couponWidget.value.copy(
            discount = v,
            discountError = err
        )
    }

    fun couponMinOrderAmount(v: String): Unit {
        val err = when (val res = ValidationUtils.validateDecimalPlaces2(v)) {
            is ValidationResult.Failure -> res.error

            is ValidationResult.Success -> ""
        }
        _couponWidget.value = _couponWidget.value.copy(
            minOrderAmount = v,
            minOrderAmountError = err
        )
    }

    fun couponExpandSelectProduct(v: Boolean): Unit {
        _couponWidget.value = _couponWidget.value.copy(
            expandSelectProduct = v,
        )
    }

    fun couponCheckedProductId(v: UInt, checked: Boolean) {
        val l = _couponWidget.value.productIdList.toMutableList().apply {
            if (checked) add(v) else remove(v)
        }
        _couponWidget.value = _couponWidget.value.copy(productIdList = l)
    }

    fun couponClearCheckedProductId() {
        _couponWidget.value = _couponWidget.value.copy(productIdList = emptyList())
    }


    private val _productWidget = MutableStateFlow(EditProductWidget())
    val productWidget: StateFlow<EditProductWidget> = _productWidget

    fun initEditProduct(editType: EditType, current: Product? = null): Unit {
        when (editType) {
            EditType.CREATE -> {
                _productWidget.value = EditProductWidget()
            }

            EditType.UPDATE -> {
                current?.let { v ->
                    _productWidget.value = EditProductWidget(
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


//        protocolIdList?.let { l ->
//            _productWidget.value = _productWidget.value.copy(
//                protocolIdList = l,
//                oldUpdateProductReq = _productWidget.value.oldUpdateProductReq.copy(
//                    protocolIdList = l
//                )
//            )
//        }

    }

    fun expandProductCategory(v: Boolean): Unit {
        _productWidget.value = _productWidget.value.copy(
            expandProductCategory = v
        )
    }

    fun productCategory(v: ProductCategory): Unit {
        _productWidget.value = _productWidget.value.copy(
            category = v,
            expandProductCategory = false
        )
    }

    fun productName(v: String): Unit {
        _productWidget.value = _productWidget.value.copy(
            name = v
        )
    }

    fun productStatus(v: Boolean): Unit {
        _productWidget.value = _productWidget.value.copy(
            status = if (v) Status.ENABLE else Status.DISABLE
        )
    }

    fun expandSelectNode(v: Boolean): Unit {
        _productWidget.value = _productWidget.value.copy(
            expandSelectNode = v
        )
    }

    fun productClearSelectNode(): Unit {
        _productWidget.value = _productWidget.value.copy(
            protocolIdList = emptyList()
        )
    }

    fun productMainImage(v: String): Unit {
        _productWidget.value = _productWidget.value.copy(
            mainImage = v
        )
    }

    fun productBasePrice(v: String): Unit {
        val err = when (val res = ValidationUtils.validateDecimalPlaces2(v)) {
            is ValidationResult.Failure -> res.error

            is ValidationResult.Success -> ""
        }

        _productWidget.value = _productWidget.value.copy(
            monthlyPrice = v,
            monthlyPriceError = err
        )
    }

    fun productQuarterlyPrice(v: String): Unit {
        val err = when (val res = ValidationUtils.validateDecimalPlaces2(v)) {
            is ValidationResult.Failure -> res.error

            is ValidationResult.Success -> ""
        }

        _productWidget.value = _productWidget.value.copy(
            quarterlyPrice = v,
            quarterlyPriceError = err
        )
    }

    fun productSemiAnnualPrice(v: String): Unit {
        val err = when (val res = ValidationUtils.validateDecimalPlaces2(v)) {
            is ValidationResult.Failure -> res.error

            is ValidationResult.Success -> ""
        }

        _productWidget.value = _productWidget.value.copy(
            semiAnnualPrice = v,
            semiAnnualPriceError = err
        )
    }

    fun productAnnualPrice(v: String): Unit {
        val err = when (val res = ValidationUtils.validateDecimalPlaces2(v)) {
            is ValidationResult.Failure -> res.error

            is ValidationResult.Success -> ""
        }

        _productWidget.value = _productWidget.value.copy(
            annualPrice = v,
            annualPriceError = err
        )
    }

    fun productDetail(v: String): Unit {
        _productWidget.value = _productWidget.value.copy(
            detail = v
        )
    }

    fun productCheckedProtocolId(v: UInt, checked: Boolean) {
        val l = _productWidget.value.protocolIdList.toMutableList().apply {
            if (checked) add(v) else remove(v)
        }
        _productWidget.value = _productWidget.value.copy(protocolIdList = l)
    }

    fun productClearCheckedProtocolId() {
        _productWidget.value = _productWidget.value.copy(protocolIdList = emptyList())
    }

    fun productRichTextEditor(v: Boolean) {
        _productWidget.value = _productWidget.value.copy(expandRichTextEditorDrawer = v)
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
    val expandSelectNode: Boolean = false,
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

