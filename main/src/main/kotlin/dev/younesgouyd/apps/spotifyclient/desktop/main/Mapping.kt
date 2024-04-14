package dev.younesgouyd.apps.spotifyclient.desktop.main

import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.ArtistTopTracks
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.CurrentUser
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.Image
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.ImageOfFloatSize
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.album.AlbumTracks
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.album.ArtistAlbums
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.album.SavedAlbums
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.artist.FollowedArtists
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.playlist.PlaylistTracks
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.playlist.Playlists
import dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.playlist.UserPlaylists
import dev.younesgouyd.apps.spotifyclient.desktop.main.ui.models.*
import kotlin.time.Duration.Companion.milliseconds

fun Playlists.toModel(): List<PlaylistListItem?> {
    return this.items?.map { playlist ->
        if (playlist != null) {
            PlaylistListItem(
                id = playlist.id,
                name = playlist.name,
                images = Images.fromStandardImages(playlist.images)
            )
        } else null
    } ?: emptyList()
}

fun dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.playlist.Playlist.toModel(): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        images = Images.fromStandardImages(this.images),
        owner = this.owner?.let { Playlist.Owner(id = it.id, name = it.displayName) }
    )
}

fun CurrentUser.toModel(): User {
    return User(
        id = this.id,
        displayName = this.displayName,
        followerCount = this.followers?.total,
        profilePictureUrl = try { this.images?.get(1)?.url } catch (ignored: Exception) { null }
    )
}

fun dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.User.toModel(): User {
    return User(
        id = this.id,
        displayName = this.displayName,
        followerCount = this.followers?.total,
        profilePictureUrl = try { this.images?.get(1)?.url } catch (ignored: Exception) { null }
    )
}

fun PlaylistTracks.toModel(): List<Playlist.Track?> {
    return this.items?.map { (track) ->
        if (track != null) {
            Playlist.Track(
                id = track.id,
                name = track.name,
                images = Images.fromStandardImages(track.album?.images)
            )
        } else null
    } ?: emptyList()
}

fun SavedAlbums.toModel(): List<AlbumListItem?> {
    return this.items?.map { (album) ->
        if (album != null) {
            AlbumListItem(
                id = album.id,
                name = album.name,
                images = Images.fromStandardImages(album.images)
            )
        } else null
    } ?: emptyList()
}

fun dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.album.Album.toModel(): Album {
    return Album(
        id = this.id,
        name = this.name,
        artists = this.artists?.map {
            Album.Artist(
                id = it.id,
                name = it.name
            )
        } ?: emptyList(),
        images = Images.fromStandardImages(this.images)
    )
}

fun AlbumTracks.toModel(): List<Album.Track> {
    return this.items?.map {
        Album.Track(
            id = it.id,
            name = it.name
        )
    } ?: emptyList()
}

fun FollowedArtists.toModel(): List<Artist> {
    return this.artists?.items?.map {
        Artist(
            id = it.id,
            name = it.name,
            images = Images.fromStandardImages(it.images)
        )
    } ?: emptyList()
}

fun ArtistAlbums.toModel(): List<Artist.Album> {
    return this.items?.map { simplifiedAlbum ->
        Artist.Album(
            id = simplifiedAlbum.id,
            name = simplifiedAlbum.name,
            images = Images.fromStandardImages(simplifiedAlbum.images)
        )
    } ?: emptyList()
}

fun UserPlaylists.toModel(): List<User.Playlist?> {
    return this.items?.map {
        if (it != null) {
            User.Playlist(
                id = it.id,
                name = it.name,
                description = it.description,
                images = Images.fromStandardImages(it.images)
            )
        } else null
    } ?: emptyList()
}

fun dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.artist.Artist.toModel(): Artist {
    return Artist(
        id = this.id,
        name = this.name,
        images = Images.fromImagesOfFloatSize(this.images)
    )
}

fun ArtistTopTracks.toModel(): List<Artist.Track> {
    return this.tracks?.map {
        Artist.Track(
            id = it.id,
            name = it.name,
            images = Images.fromImagesOfFloatSize(it.album?.images)
        )
    } ?: emptyList()
}

fun dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.toModel(): PlaybackState {
    return PlaybackState(
        track = this.item?.let { track ->
            PlaybackState.Track(
                id = track.id,
                title = track.name,
                artists = track.artists?.let { artists ->
                    artists.filterNotNull().map { PlaybackState.Track.Artist(id = it.id, name = it.name) }
                } ?: emptyList(),
                album = track.album?.let { PlaybackState.Track.Album(id = it.id, name = it.name) },
                images = Images.fromStandardImages(track.album?.images),
                duration = track.durationMs?.milliseconds
            )
        },
        elapsedTime = this.progressMs?.milliseconds,
        playing = this.isPlaying,
        repeatState = this.repeatState?.let {
            when (it) {
                dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.RepeatState.Off -> PlaybackState.RepeatState.Off
                dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.RepeatState.Track -> PlaybackState.RepeatState.Track
                dev.younesgouyd.apps.spotifyclient.desktop.main.data.models.PlaybackState.RepeatState.Context -> PlaybackState.RepeatState.List
            }
        },
        shuffleState = shuffleState
    )
}

fun Images.Companion.fromStandardImages(list: List<Image>?): Images {
    return if (list == null) empty() else Images(
        large = try { list[0].url } catch (ignored: Exception) { null },
        medium = try { list[1].url } catch (ignored: Exception) { null },
        small = try { list[2].url } catch (ignored: Exception) { null }
    )
}

fun Images.Companion.fromImagesOfFloatSize(list: List<ImageOfFloatSize>?): Images {
    return if (list == null) empty() else Images(
        large = try { list[0].url } catch (ignored: Exception) { null },
        medium = try { list[1].url } catch (ignored: Exception) { null },
        small = try { list[2].url } catch (ignored: Exception) { null }
    )
}
