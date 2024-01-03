package io.airfoil.plugins.swagger

import io.bkbn.kompendium.core.plugin.NotarizedRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

abstract class SwaggerRoute {
    abstract val path: String

    abstract fun spec(): NotarizedRoute.Config.() -> Unit
}

fun Route.swaggerRoute(
    swaggerRoute: SwaggerRoute,
    build: Route.() -> Unit,
) {
    this.route(swaggerRoute.path) {
        if (application.swaggerEnabled) {
            this.install(NotarizedRoute()) {
                this.apply(swaggerRoute.spec())
            }
        }

        this.build()
    }
}
