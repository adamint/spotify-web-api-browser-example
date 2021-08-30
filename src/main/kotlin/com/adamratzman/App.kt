package com.adamratzman

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.Home
import com.adamratzman.database.View.NotFound
import com.adamratzman.layouts.HomePageComponent
import com.adamratzman.layouts.NotFoundComponent
import com.adamratzman.layouts.partials.FooterComponent
import com.adamratzman.layouts.partials.HeaderComponent
import com.adamratzman.utils.UikitName.UkSpinnerAttribute
import com.adamratzman.utils.addAttributes
import com.soywiz.korio.lang.Thread_sleep
import io.kvision.Application
import io.kvision.core.Position
import io.kvision.core.UNIT.perc
import io.kvision.core.UNIT.px
import io.kvision.core.style
import io.kvision.html.div
import io.kvision.html.main
import io.kvision.module
import io.kvision.panel.root
import io.kvision.startApplication
import io.kvision.state.bind
import kotlinx.browser.window

class App : Application() {
    override fun start(state: Map<String, Any>) {
        SiteManager.initialize()
        println("checking spotify sdk...")
        while (window.asDynamic().SPOTIFY_PLAYBACK_SDK_READY != true) {
            Thread_sleep(250)
            println("waiting")
        }
        println("done")
        root("kvapp", addRow = false) {
            HeaderComponent(this)

            main().bind(SiteManager.siteStore) { state ->
                id = "main"
                println(state.view.name)

                state.loadingDiv = div {
                    div {
                        if (!state.view.needsInitialLoadingSpinner) hide()
                        style {
                            right = 0 to px
                            position = Position.ABSOLUTE
                            marginRight = 10 to perc
                        }
                        addAttributes(UkSpinnerAttribute to "ratio: 2;")
                    }
                }

                when (state.view) {
                    Home -> HomePageComponent(this)
                    NotFound -> NotFoundComponent(this)
                }
            }

            FooterComponent(this)
        }
    }
}

fun main() {
    startApplication(::App, module.hot)
}
