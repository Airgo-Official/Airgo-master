package io.github.ppoonk.airgo_master.repository.local

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.github.ppoonk.ac.utils.Logger
import io.github.ppoonk.ac.utils.decode
import io.github.ppoonk.ac.utils.encode
import io.github.ppoonk.ac.utils.withLimit
import io.github.ppoonk.airgo_master.component.SearchHistory
import io.github.ppoonk.airgo_master.repository.remote.model.User
import kotlinx.serialization.json.Json

object LocalData {

    // multiplatform-settings 实例
    private val settings: Settings = Settings()

    // keys
    private const val PERSON = "PERSON"
    private const val PERSON_TOKEN = "PERSON_TOKEN"
    private const val USER_SEARCH_HISTORY = "USER_SEARCH_HISTORY"
    private const val NODE_SEARCH_HISTORY = "NODE_SEARCH_HISTORY"
    private const val PRODUCT_SEARCH_HISTORY = "PRODUCT_SEARCH_HISTORY"
    private const val BASE_URL = "BASE_URL"
    private const val ADMIN_PATH = "ADMIN_PATH"

    fun clear() {
        settings.clear()
    }

    fun getSignedUser(): User? {
        val str: String? = settings[PERSON]
        var u: User? = null
        str?.let {
            try {
                u = Json.decodeFromString(it)
            } catch (e: Exception) {
                Logger.error(tag = Logger.DATASTORE) { e.toString() }
            }
        }
        return u
    }

    fun setSignedUser(newValue: User?) {
        if (newValue == null) {
            settings[PERSON] = null
        } else {
            try {
                settings[PERSON] = Json.encodeToString(newValue)
            } catch (e: Exception) {
                Logger.error(tag = Logger.DATASTORE) { e.toString() }
            }
        }

    }

    fun getToken(): String? {
        return settings[PERSON_TOKEN]
    }

    fun setToken(newValue: String?) {
        settings[PERSON_TOKEN] = newValue
    }

    fun getBaseUrl(): String? {
        return settings[BASE_URL]
    }
    fun setBaseUrl(newValue: String?) {
        settings[BASE_URL] = newValue
    }

    fun getAdminPath(): String? {
        return settings[ADMIN_PATH]
    }
    fun setAdminPath(newValue: String?) {
        settings[ADMIN_PATH] = newValue
    }

    private fun getSearchHistory(key: String): List<SearchHistory> {
        val s: String? = settings[key]
        return s?.decode<List<SearchHistory>>() ?: emptyList()
    }

    private fun setSearchHistory(key: String, newValue: SearchHistory?) {
        if (newValue == null) {
            settings[key] = null
        } else {
            settings[key] = getSearchHistory(key).withLimit(newValue, 20) {
                it.search == newValue.search
            }.encode()
        }
    }

    fun getUserSearchHistory(): List<SearchHistory> {
        return getSearchHistory(USER_SEARCH_HISTORY)
    }

    fun setUserSearchHistory(newValue: SearchHistory?) {
        setSearchHistory(USER_SEARCH_HISTORY, newValue)
    }

    fun getNodeSearchHistory(): List<SearchHistory> {
        return getSearchHistory(NODE_SEARCH_HISTORY)
    }

    fun setNodeSearchHistory(newValue: SearchHistory?) {
        setSearchHistory(NODE_SEARCH_HISTORY, newValue)
    }


    fun getProductSearchHistory(): List<SearchHistory> {
        return getSearchHistory(PRODUCT_SEARCH_HISTORY)
    }

    fun setProductSearchHistory(newValue: SearchHistory?) {
        setSearchHistory(PRODUCT_SEARCH_HISTORY, newValue)
    }
}
