package io.airfoil.plugins.swagger

import io.airfoil.plugins.swagger.config.SwaggerConfiguration
import io.airfoil.common.plugin.KtorApplicationPlugin

class SwaggerUIController(
    private val config: SwaggerConfiguration,
) : KtorApplicationPlugin {
}
