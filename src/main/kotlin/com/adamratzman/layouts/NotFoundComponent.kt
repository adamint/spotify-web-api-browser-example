package com.adamratzman.layouts

import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.h3

class NotFoundComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = {
    div {
        h3("Page not found.")
    }
})