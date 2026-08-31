package sidly.wynnadhoc.wapi

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import sidly.wynnadhoc.utils.ApiUtils
import sidly.wynnadhoc.wapi.item.WynnItem
import java.util.concurrent.CompletableFuture

object ItemDatabase {
    fun searchApi(item: String): CompletableFuture<Set<WynnItem>> {
        val query = ApiUtils.basicQuery(ApiUtils.Endpoint.QUICK_ITEM_SEARCH, item)
        val future: CompletableFuture<Set<WynnItem>> = CompletableFuture()
        query.whenComplete { response, throwable ->
            val gson = GsonBuilder().registerTypeAdapter(
                WynnItem::class.java,
                ItemDeserializer(sidly.wynnadhoc.wapi.ApiUtils.GSON)
            ).create()
            val type = object : TypeToken<Set<WynnItem>>() {}.type
            val result = gson.fromJson<Set<WynnItem>>(response.body(), type)
            future.complete(result)
        }
        return future
    }
}