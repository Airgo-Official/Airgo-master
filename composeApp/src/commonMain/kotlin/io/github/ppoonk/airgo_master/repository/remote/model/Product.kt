package io.github.ppoonk.airgo_master.repository.remote.model

import androidx.compose.runtime.Composable
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.product_category_normal
import io.github.ppoonk.airgo_master.product_category_subscribe
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class Product(
    val id: UInt,
    val createdAt: Instant,
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
    val createdAtStart: Instant? = null,
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



