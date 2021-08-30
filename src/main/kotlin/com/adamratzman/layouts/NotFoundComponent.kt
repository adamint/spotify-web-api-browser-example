package com.adamratzman.layouts

import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h3

class NotFoundComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = {
    div {
        h3("Page not found.")
    }
})