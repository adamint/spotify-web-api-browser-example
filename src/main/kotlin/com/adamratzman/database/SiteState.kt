package com.adamratzman.database

import com.adamratzman.database.SiteAction.LoadHomePage
import com.adamratzman.database.SiteAction.LoadNotFoundPage
import com.adamratzman.database.SiteAction.SetSpotifyApi
import com.adamratzman.database.SiteManager.redirectToSpotifyAuthentication
import com.adamratzman.database.SiteManager.setSpotifyApi
import com.adamratzman.database.View.Home
import com.adamratzman.database.View.NotFound
import com.adamratzman.security.spotifyClientId
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.spotifyImplicitGrantApi
import com.adamratzman.spotify.utils.getCurrentTimeMs
import com.adamratzman.utils.toDevOrProdUrl
import io.kvision.html.Div
import io.kvision.navigo.Navigo
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set
import redux.RAction

const val spotifyTokenLocalStorageKey = "spotifyToken"
const val spotifyTokenExpiryLocalStorageKey = "spotifyTokenExpirationMillis"
const val lastSpotifyAccessToken = "lastSpotifyAccessToken"
const val redirectBackToLocalStorageKey = "redirectBackTo"
val isDevServer = window.location.host == "localhost:3000"


data class SiteState(
    val view: View = Home,
    val redirectAfterCallbackUri: String? = null
) {
    val spotifyImplicitGrantApi
        get(): SpotifyImplicitGrantApi? = localStorage[spotifyTokenLocalStorageKey]?.let { tokenString ->
            localStorage[spotifyTokenExpiryLocalStorageKey]?.toLongOrNull()?.let {
                if (getCurrentTimeMs() >= it) {
                    localStorage.removeItem(spotifyTokenLocalStorageKey)
                    localStorage.removeItem(spotifyTokenExpiryLocalStorageKey)
                    return null
                }
            }
            val token = Json.decodeFromString(Token.serializer(), tokenString)
            spotifyImplicitGrantApi(spotifyClientId, token) {
                refreshTokenProducer = { redirectToSpotifyAuthentication(Div()); throw IllegalStateException() }
                enableDebugMode = true
            }
        }

    var loadingDiv: Div? = null

    val lastSpotifyAccessToken get() = localStorage.getItem("lastSpotifyAccessToken")
}

sealed class View(val name: String, val url: String, val needsInitialLoadingSpinner: Boolean = false) {
    object Home : View("Home", "/")
    object NotFound : View("404 Not Found", "/404")

    fun devOrProdUrl() = url.toDevOrProdUrl()
    fun isSameView(other: View) = this::class == other::class
    val baseUrl: String = url.toDevOrProdUrl()
}

sealed class SiteAction : RAction {
    object LoadHomePage : SiteAction()
    object LoadNotFoundPage : SiteAction()
    data class SetSpotifyApi(val token: Token) : SiteAction()
}

fun siteStateReducer(state: SiteState, action: SiteAction): SiteState = when (action) {
    LoadHomePage -> state.copy(view = Home)
    LoadNotFoundPage -> state.copy(view = NotFound)
    is SetSpotifyApi -> {
        localStorage[spotifyTokenLocalStorageKey] = Json.encodeToString(action.token)
        localStorage[spotifyTokenExpiryLocalStorageKey] = action.token.expiresAt.toString()
        state
    }
}

fun Navigo.initialize(): Navigo {
    println(window.location.pathname)
    return on(Home.url, { _ ->
        when {
            window.location.hash.startsWith("#access_token") -> {
                setSpotifyApi()
                SiteManager.redirectBack(defaultUrl = Home.url)
            }
            SiteManager.redirectBackUrl != null -> SiteManager.redirectBack(defaultUrl = Home.url)
            window.location.pathname.length <= 1 -> homePage()
            else -> notFoundPage()
        }
    })
        .apply { notFound({ _ -> notFoundPage() }) }
}