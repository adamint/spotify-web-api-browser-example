package com.adamratzman.layouts.partials

import com.adamratzman.layouts.SiteStatefulComponent
import io.kvision.core.Container
import io.kvision.html.h2
import io.kvision.html.header

class HeaderComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { _ ->
    header {
        h2("spotify-web-api-kotlin browser test application - Spotify Web Playback SDK wrapper")
    }
})