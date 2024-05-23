package dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.search

import dev.younesgouyd.apps.spotifyclient.desktop.main.SearchType
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.repoes.auth.AuthRepo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class SearchRepo(
    private val client: HttpClient,
    private val authRepo: AuthRepo
) {
    /**
     * GET /search
     * @param query Your search query.
     * You can narrow down your search using field filters. The available filters are album, artist, track, year, upc, tag:hipster, tag:new, isrc, and genre. Each field filter only applies to certain result types.
     * The artist and year filters can be used while searching albums, artists and tracks. You can filter on a single year or a range (e.g. 1955-1960).
     * The album filter can be used while searching albums and tracks.
     * The genre filter can be used while searching artists and tracks.
     * The isrc and track filters can be used while searching tracks.
     * The upc, tag:new and tag:hipster filters can only be used while searching albums. The tag:new filter will return albums released in the past two weeks and tag:hipster can be used to return only albums with the lowest 10% popularity.
     * Example: q=remaster%2520track%3ADoxy%2520artist%3AMiles%2520Davis
     */
    suspend fun search(query: String): SearchResult {
        return client.get("search") {
            header("Authorization", "Bearer ${authRepo.getToken()}")
            parameter("q", query)
            parameter("type", SearchType.entries.joinToString(separator = ",", transform = { it.name.lowercase() }))
            parameter("limit", 50)
            parameter("offset", 0)
        }.body<SearchResult>()
    }
}