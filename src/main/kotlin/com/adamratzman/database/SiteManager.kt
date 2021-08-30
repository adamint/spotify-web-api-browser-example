package com.adamratzman.database

import com.adamratzman.database.SiteAction.*
import com.adamratzman.security.spotifyAuthRedirectUri
import com.adamratzman.spotify.utils.parseSpotifyCallbackHashToToken
import com.adamratzman.utils.toDevOrProdUrl
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.get
import org.w3c.dom.set
import io.kvision.core.Col
import io.kvision.core.Color
import io.kvision.core.Container
import io.kvision.core.style
import io.kvision.html.h2
import io.kvision.redux.createReduxStore
import io.kvision.routing.routing
import io.kvision.navigo.Navigo

object SiteManager {
    var redirectBackUrl: String?
        get() = localStorage[redirectBackToLocalStorageKey]
        set(value) = value?.let { localStorage[redirectBackToLocalStorageKey] = value } ?: localStorage.removeItem(
            redirectBackToLocalStorageKey
        )

    val domain = "${window.location.protocol}//${
        if (window.location.port in listOf("80", "443")) window.location.hostname
        else window.location.host
    }"

    val siteStore = createReduxStore(::siteStateReducer, SiteState())

    val navigo = Navigo(null, isDevServer, if (isDevServer) "#!" else null)

    fun stringParameter(params: dynamic, parameterName: String): String {
        return (params[parameterName]).toString()
    }

    fun initialize() {
        navigo.initialize().resolve()
    }

    fun replaceWithUrl(url: String) {
        window.location.replace(url.toDevOrProdUrl())
    }

    fun redirectToUrl(url: String) {
        window.location.href = url
    }

    fun redirect(view: View) {
        routing.navigate(view.url)
    }

    fun redirectBack(defaultUrl: String) {
        val redirectBackTo = redirectBackUrl
        redirectBackUrl = null
        window.location.href = redirectBackTo ?: defaultUrl.toDevOrProdUrl()
    }

    fun redirectToSpotifyAuthentication(parent: Container) {
        try {
            redirectBackUrl = window.location.href
            window.location.href = spotifyAuthRedirectUri
        } catch (exception: Exception) {
            parent.h2 {
                style { color = Color.name(Col.RED) }
                +"Your browser does not support localStorage. Please exit incognito mode."
            }
        }
    }

    fun setSpotifyApi() {
        siteStore.dispatch(SetSpotifyApi(parseSpotifyCallbackHashToToken()))
    }
}

fun homePage() = SiteManager.siteStore.dispatch(LoadHomePage)
fun notFoundPage() = SiteManager.siteStore.dispatch(LoadNotFoundPage)

