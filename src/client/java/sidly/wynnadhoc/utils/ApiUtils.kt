package sidly.wynnadhoc.utils

import sidly.wynnadhoc.WynnAdhocClient
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.CompletableFuture

object ApiUtils {
    fun basicQuery(endpoint: Endpoint, value: String): CompletableFuture<HttpResponse<String>> {
        val client = HttpClient.newHttpClient()
        var string = endpoint.string + value
        if (endpoint.fullResult) string += "?fullResult"
        val request = HttpRequest.newBuilder()
            .uri(URI.create(string))
            .GET()
            .build()
        val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        response.whenComplete { response, throwable ->
            if (response.statusCode() != 200) {
                WynnAdhocClient.LOGGER.warn("Api query failed with status ${response.statusCode()} $endpoint $value response: ${response.body()}")
            }
            if (throwable != null) {
                WynnAdhocClient.LOGGER.warn("Api query $endpoint $value failed with throwable $throwable")
            }
        }
        return response
    }


    enum class Endpoint {
        PLAYER_DATA("https://api.wynncraft.com/v3/player/", true),
        QUICK_ITEM_SEARCH("https://api.wynncraft.com/v3/item/search/");

        val string: String
        val fullResult: Boolean

        constructor(string: String, fullResult: Boolean = false) {
            this.string = string
            this.fullResult = fullResult
        }
    }
}