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
import pl.treksoft.kvision.Application
import pl.treksoft.kvision.core.Position
import pl.treksoft.kvision.core.UNIT.perc
import pl.treksoft.kvision.core.UNIT.px
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.main
import pl.treksoft.kvision.module
import pl.treksoft.kvision.panel.root
import pl.treksoft.kvision.startApplication

class App : Application() {
    override fun start(state: Map<String, Any>) {
        SiteManager.initialize()
        root("kvapp", addRow = false) {
            HeaderComponent(this)

            main(SiteManager.siteStore) { state ->
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
