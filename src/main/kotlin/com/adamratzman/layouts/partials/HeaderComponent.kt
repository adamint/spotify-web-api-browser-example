package com.adamratzman.layouts.partials

import com.adamratzman.layouts.SiteStatefulComponent
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.html.h2
import pl.treksoft.kvision.html.header

class HeaderComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { _ ->
    header {
        h2("spotify-web-api-kotlin browser test application - Spotify Web Playback SDK wrapper")
    }
})