package com.adamratzman.layouts

import com.adamratzman.database.lastSpotifyAccessToken
import com.adamratzman.security.guardValidSpotifyApi
import com.adamratzman.spotify.webplayer.Player
import com.adamratzman.spotify.webplayer.PlayerInit
import com.adamratzman.spotify.webplayer.WebPlaybackInstance
import com.adamratzman.utils.UikitName.MarginAuto
import com.adamratzman.utils.UikitName.MarginMediumTop
import com.adamratzman.utils.UikitName.WidthTwoThirds
import com.adamratzman.utils.addCssClasses
import com.adamratzman.utils.addLineBreak
import com.adamratzman.utils.labelWithBoldedValue
import com.adamratzman.utils.nameSetOf
import io.kvision.core.Container
import io.kvision.form.text.text
import io.kvision.html.Div
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.html.h4
import io.kvision.html.h5
import io.kvision.html.p
import kotlinx.browser.localStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomePageComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        lateinit var playerDeviceId: String

        div {
            addCssClasses(nameSetOf(MarginMediumTop, WidthTwoThirds, MarginAuto))
            h4("Spotify api has been instantiated.")
            GlobalScope.launch {
                p("User: ${api.getUserId()}")

                val playerStatus = h5("Registering online player.. (Disconnected)")

                val player = Player(
                    object : PlayerInit {
                        override var name: String = "spotify-web-api-kotlin browser example player"
                        override var volume: Number? = 0.5f
                        override fun getOAuthToken(cb: (token: String) -> Unit) {
                            // cannot reference outer closures within an override fun! otherwise we'd just call api
                            cb(localStorage.getItem(lastSpotifyAccessToken)!!)
                        }

                    }
                )

                player.addListener("ready") { webPlaybackInstance: WebPlaybackInstance ->
                    playerDeviceId = webPlaybackInstance.device_id
                    playerStatus.content = "Registered online player (Ready! Device id: $playerDeviceId)"
                }

                player.connect().then {
                    playerStatus.content = "Registered online player (Not ready)"
                }

                val currentPlayerStateDiv = div()

                GlobalScope.launch {
                    while (true) {
                        delay(500)
                        val newCurrentPlayerStateDiv = Div()
                        with(newCurrentPlayerStateDiv) {
                            val currentlyPlayingState = api.player.getCurrentlyPlaying()

                            when (currentlyPlayingState?.isPlaying) {
                                null -> labelWithBoldedValue("Player state: ", "not playing anything")
                                true -> {
                                    labelWithBoldedValue("Player state: ", "playing")
                                }
                                false -> {
                                    labelWithBoldedValue("Player state: ", "paused")
                                }
                            }

                            if (currentlyPlayingState?.isPlaying != null) {
                                player.getCurrentState().then { playbackState ->
                                    val currentTrack = playbackState?.track_window?.current_track
                                    if (currentTrack != null) {
                                        labelWithBoldedValue(
                                            "Current track playing through this browser: ",
                                            "${currentTrack.name}, by ${currentTrack.artists.joinToString(", ") { it.name }}"
                                        )
                                    } else {
                                        labelWithBoldedValue(
                                            "Current track playing through this browser: ",
                                            "unavailable/null"
                                        )
                                    }
                                }
                            }

                            val currentlyPlayingTrackInAccount = api.player.getCurrentlyPlaying()
                            val track = currentlyPlayingTrackInAccount?.track
                            if (track == null) {
                                labelWithBoldedValue(
                                    "Current track playing through this account (not necessarily this device): ",
                                    "none"
                                )
                            } else {
                                labelWithBoldedValue(
                                    "Current track playing through this account (not necessarily this device): ",
                                    "${track.name}, by ${track.artists.joinToString(", ") { it.name }}"
                                )
                            }


                            if (currentlyPlayingState != null) {
                                if (currentlyPlayingState.progressMs != null) {
                                    labelWithBoldedValue(
                                        "Progress: ",
                                        "${currentlyPlayingState.progressMs!! / 1000} seconds"
                                    )
                                }

                            }

                            delay(1000)
                        }

                        currentPlayerStateDiv.removeAll()
                        currentPlayerStateDiv.addAll(newCurrentPlayerStateDiv.getChildren())
                    }
                }

                val trackNameInput = text(label = "Enter a track to play")
                button(text = "Search and play this track (using spotify-web-api-kotlin)") {
                    onClick {
                        trackNameInput.value?.let { trackQuery ->
                            GlobalScope.launch {
                                val track = api.search.searchTrack(trackQuery)[0]
                                api.player.startPlayback(
                                    playableUrisToPlay = listOf(track.uri),
                                    deviceId = playerDeviceId
                                )
                            }
                        }
                    }
                }

                addLineBreak()
                addLineBreak()

                button(text = "Toggle play - unpause/pause (if playing through this browser)") {
                    onClick {
                        player.togglePlay()
                    }
                }
            }
        }
    }
})