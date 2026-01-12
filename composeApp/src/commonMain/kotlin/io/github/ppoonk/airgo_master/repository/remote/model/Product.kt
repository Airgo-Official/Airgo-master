package io.github.ppoonk.airgo_master.repository.remote.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.operate
import io.github.ppoonk.airgo_master.product_annual_price
import io.github.ppoonk.airgo_master.product_category
import io.github.ppoonk.airgo_master.product_category_normal
import io.github.ppoonk.airgo_master.product_category_subscribe
import io.github.ppoonk.airgo_master.product_created_at
import io.github.ppoonk.airgo_master.product_detail
import io.github.ppoonk.airgo_master.product_id
import io.github.ppoonk.airgo_master.product_main_image
import io.github.ppoonk.airgo_master.product_monthly_price
import io.github.ppoonk.airgo_master.product_name
import io.github.ppoonk.airgo_master.product_quarterly_price
import io.github.ppoonk.airgo_master.product_semi_annual_price
import io.github.ppoonk.airgo_master.product_status
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant

@Serializable
data class Product(
    val id: UInt,
    @Contextual
    val createdAt: Instant,
    @Contextual
    val updatedAt: Instant?,
    val name: String,
    val category: String,
    val detail: String?,
    val status: Int,
    val mainImage: String?,
    val monthlyPrice: Double,
    val quarterlyPrice: Double?,
    val semiAnnualPrice: Double?,
    val annualPrice: Double?
)


@Serializable
data class CreateProductReq(
    val name: String,
    val category: String,
    val detail: String?,
    val status: Int,
    val mainImage: String?,
    val monthlyPrice: Double,
    val quarterlyPrice: Double?,
    val semiAnnualPrice: Double?,
    val annualPrice: Double?,
    val protocolIdList: List<UInt>?
)


@Serializable
data class UpdateProductReq(
    val id: UInt = 0u,
    val name: String? = null,
    val category: String? = null,
    val detail: String? = null,
    val status: Int? = null,
    val mainImage: String? = null,
    val monthlyPrice: Double? = null,
    val quarterlyPrice: Double? = null,
    val semiAnnualPrice: Double? = null,
    val annualPrice: Double? = null,
    val protocolIdList: List<UInt>? = null
)


@Serializable
data class DeleteProductReq(
    val id: UInt
)


@Serializable
data class GetProductListReq(
    val search: SearchProduct? = null,
    val filter: FilterProduct? = null,
    val order: SortOrder? = null,
    val pagination: Pagination = Pagination(),
)

@Serializable
data class SearchProduct(
    val id: UInt? = null,
    val name: String? = null,
)

@Serializable
data class FilterProduct(
    @Contextual
    val createdAtStart: Instant? = null,
    @Contextual
    val createdAtEnd: Instant? = null,
    val status: Int? = null,
    val category: String? = null
)


@Serializable
data class GetProductListRes(
    val total: Int,
    val list: List<Product>
)

enum class ProductCategory {
    PRODUCT_CATEGORY_SUBSCRIBE,
    PRODUCT_CATEGORY_NORMAL;

    @Composable
    fun i18n(): String {
        return when (this) {
            PRODUCT_CATEGORY_SUBSCRIBE -> stringResource(Res.string.product_category_subscribe)
            PRODUCT_CATEGORY_NORMAL -> stringResource(Res.string.product_category_normal)
        }
    }
}

enum class ProductTableColumn(
    val text: StringResource,
    val width: Dp
) {
    ID(Res.string.product_id, 100.dp),
    NAME(Res.string.product_name, 300.dp),
    STATUS(Res.string.product_status, 100.dp),
    CATEGORY(Res.string.product_category, 200.dp),
    MONTHLY_PRICE(Res.string.product_monthly_price, 200.dp),
    QUARTERLY_PRICE(Res.string.product_quarterly_price, 200.dp),
    SEMI_ANNUAL_PRICE(Res.string.product_semi_annual_price, 200.dp),
    ANNUAL_PRICE(Res.string.product_annual_price, 200.dp),
    MAIN_IMAGE(Res.string.product_main_image, 200.dp),
    CREATED_AT(Res.string.product_created_at, 150.dp),
    OPERATE(Res.string.operate, 150.dp);

}


