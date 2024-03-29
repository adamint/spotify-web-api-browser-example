package com.adamratzman.security

import com.adamratzman.database.SiteManager
import com.adamratzman.database.SiteState
import com.adamratzman.database.lastSpotifyAccessToken
import com.adamratzman.database.spotifyTokenExpiryLocalStorageKey
import com.adamratzman.database.spotifyTokenLocalStorageKey
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.getSpotifyAuthorizationUrl
import com.adamratzman.spotify.utils.getCurrentTimeMs
import kotlinx.browser.localStorage
import org.w3c.dom.get
import io.kvision.core.Container

const val spotifyClientId = "4341dad364794fbaa97a37fd4739b088"
val spotifyRedirectUri = encodeURIComponent(SiteManager.domain)

val spotifyAuthRedirectUri = getSpotifyAuthorizationUrl(
    *SpotifyScope.values(),
    clientId = spotifyClientId,
    redirectUri = spotifyRedirectUri,
    isImplicitGrantFlow = true
)

external fun encodeURIComponent(encodedURI: String): String
external fun decodeURIComponent(encodedURI: String): String

fun Container.guardValidSpotifyApi(state: SiteState, block: (SpotifyImplicitGrantApi) -> Unit) {
    localStorage[spotifyTokenExpiryLocalStorageKey]?.toLongOrNull()?.let {
        if (getCurrentTimeMs() >= it) {
            localStorage.removeItem(spotifyTokenLocalStorageKey)
            localStorage.removeItem(spotifyTokenExpiryLocalStorageKey)
            SiteManager.redirectToSpotifyAuthentication(this@guardValidSpotifyApi)
            return
        }
    }

    if (state.spotifyImplicitGrantApi?.token?.shouldRefresh() == false) {
        localStorage.setItem(lastSpotifyAccessToken, state.spotifyImplicitGrantApi!!.token.accessToken)
        block(state.spotifyImplicitGrantApi!!)
    } else SiteManager.redirectToSpotifyAuthentication(this)
}