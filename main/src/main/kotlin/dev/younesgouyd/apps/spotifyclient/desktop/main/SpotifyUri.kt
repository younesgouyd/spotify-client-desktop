package dev.younesgouyd.apps.spotifyclient.desktop.main

import kotlinx.serialization.Serializable

/**
 * The resource identifier of, for example, an artist, album or track.
 * This can be entered in the search box in a Spotify Desktop Client, to navigate to that resource.
 * To find a Spotify URI, right-click (on Windows) or Ctrl-Click (on a Mac) on the artist,
 * album or track name.
 *
 * Example: spotify:track:6rqhFgbbKwnb9MLmUQDhG6
 */
@Serializable
@JvmInline
value class SpotifyUri(val value: String) {
    override fun toString(): String {
        return value
    }
}

fun TrackId.toUri() = SpotifyUri("spotify:track:$value")

fun ArtistId.toUri() = SpotifyUri("spotify:artist:$value")

fun AlbumId.toUri() = SpotifyUri("spotify:album:$value")

fun PlaylistId.toUri() = SpotifyUri("spotify:playlist:$value")
