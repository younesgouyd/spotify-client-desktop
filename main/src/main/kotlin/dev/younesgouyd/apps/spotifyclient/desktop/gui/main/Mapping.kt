package dev.younesgouyd.apps.spotifyclient.desktop.gui.main

import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.Image
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.ImageOfFloatSize
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album.AlbumTracks
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album.ArtistAlbums
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album.SavedAlbums
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.artist.FollowedArtists
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist.PlaylistTracks
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist.Playlists
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.track.ArtistTopTracks
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.user.CurrentUser
import dev.younesgouyd.apps.spotifyclient.desktop.gui.main.ui.models.*

fun Playlists.toModel(): List<SimplifiedPlaylist> {
    return this.items?.map {
        SimplifiedPlaylist(
            id = it.id,
            name = it.name,
            images = Images.fromStandardImages(it.images)
        )
    } ?: emptyList()
}

fun dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.playlist.Playlist.toModel(): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        images = Images.fromStandardImages(this.images)
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

fun PlaylistTracks.toModel(): List<Playlist.Track> {
    return this.items?.filter { it.track != null }?.map { (_, _, _, track) ->
        Playlist.Track(
            id = track!!.id,
            name = track.name,
            artists = track.artists?.map {
                Playlist.Track.Artist(
                    id = it.id,
                    name = it.name
                )
            } ?: emptyList(),
            album = if (track.album != null) Playlist.Track.Album(
                id = track.album.id,
                name = track.album.name
            ) else null,
            images = Images.fromStandardImages(track.album?.images),
            playing = false
        )
    } ?: emptyList()
}

fun SavedAlbums.toModel(): List<Album> {
    return this.items?.filter { it.album != null }?.map { (_, album) ->
        Album(
            id = album!!.id,
            name = album.name,
            artist = album.artists?.map {
                Album.Artist(
                    id = it.id,
                    name = it.name
                )
            } ?: emptyList(),
            images = Images.fromStandardImages(album.images)
        )
    } ?: emptyList()
}

fun dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.album.Album.toModel(): Album {
    return Album(
        id = this.id,
        name = this.name,
        artist = this.artists?.map {
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

fun ArtistAlbums.toModel(): List<Album> {
    return this.items?.map { simplifiedAlbum ->
        Album(
            id = simplifiedAlbum.id,
            name = simplifiedAlbum.name,
            artist = simplifiedAlbum.artists?.map {
                Album.Artist(
                    id = it.id,
                    name = it.name
                )
            } ?: emptyList(),
            images = Images.fromStandardImages(simplifiedAlbum.images)
        )
    } ?: emptyList()
}

fun dev.younesgouyd.apps.spotifyclient.desktop.gui.main.data.models.artist.Artist.toModel(): Artist {
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
