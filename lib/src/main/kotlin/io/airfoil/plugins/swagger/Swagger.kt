package io.airfoil.plugins.swagger

import io.airfoil.common.extension.withLogMetadata
import io.airfoil.common.plugin.createKtorApplicationPlugin
import io.airfoil.plugins.swagger.config.SwaggerConfiguration
import io.bkbn.kompendium.core.plugin.NotarizedApplication
import io.bkbn.kompendium.json.schema.KotlinXSchemaConfigurator
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.server.Server
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*
import io.ktor.util.*
import mu.KotlinLogging
import java.net.URI

private const val TAG = "SwaggerPlugin"
private val log = KotlinLogging.logger(TAG)

val swaggerEnabledAttrKey = AttributeKey<Boolean>("Boolean")

val Application.swaggerEnabled: Boolean
    get() = attributes[swaggerEnabledAttrKey]

fun Application.swaggerEnabled(swaggerEnabled: Boolean) {
    attributes.put(swaggerEnabledAttrKey, swaggerEnabled)
}

val swaggerConfigAttrKey = AttributeKey<SwaggerConfiguration>("SwaggerConfiguration")

val Application.swaggerConfig: SwaggerConfiguration
    get() = attributes[swaggerConfigAttrKey]

fun Application.swaggerConfig(swaggerConfig: SwaggerConfiguration) {
    attributes.put(swaggerConfigAttrKey, swaggerConfig)
}

fun Application.loadSwaggerConfig() {
    swaggerConfig(SwaggerConfiguration.loadOrDefault(environment.config))
}

val swaggerUIControllerAttrKey = AttributeKey<SwaggerUIController>("SwaggerUIController")

val Application.swaggerUIController: SwaggerUIController
    get() = attributes[swaggerUIControllerAttrKey]

fun Application.swaggerUIController(swaggerUIController: SwaggerUIController) {
    attributes.put(swaggerUIControllerAttrKey, swaggerUIController)
}

fun Application.configureSwagger(serverUri: URI) {
    loadSwaggerConfig()

    val apiSpecPath = "${serverUri.path}/swagger/openapi.json"

    swaggerEnabled(true)
    install(NotarizedApplication()) {
        spec = {
            OpenApiSpec(
                openapi = "3.0.3",
                jsonSchemaDialect = "",
                info = Info(
                    title = swaggerConfig.openApi.info.title,
                    version = swaggerConfig.openApi.info.version,
                ),
                servers = listOf(
                    Server(
                        url = serverUri,
                    ),
                ).toMutableList(),
            )
        }
        specRoute = { spec, routing ->
            routing.route(apiSpecPath) {
                get {
                    call.respond(spec)
                }
            }
        }
        schemaConfigurator = KotlinXSchemaConfigurator()
    }

    // install the Swagger UI
    install(SwaggerUIControllerPlugin) {
        openApi = swaggerConfig.openApi

        swaggerUIPath = "${serverUri.path}${swaggerConfig.swaggerUIPath}"
        apiSpecUrl =  "$serverUri/swagger/openapi.json"
        webJarPath = swaggerConfig.webJarPath
        rootPath = "${serverUri.path}${swaggerConfig.rootPath}"
        docExpansion = swaggerConfig.docExpansion
        filter = swaggerConfig.filter
        tryItOutEnabled = swaggerConfig.tryItOutEnabled
        queryConfigEnabled = swaggerConfig.queryConfigEnabled
        persistAuthorization = swaggerConfig.persistAuthorization
        validatorUrl = swaggerConfig.validatorUrl
    }

    // install Webjars
    // (MUST be initialized AFTER SwaggerUI so that SwaggerUI can intercept the js config file and update it)
    install(Webjars) {
        path = "swagger/webjars"
    }
}

val SwaggerUIControllerPlugin = createKtorApplicationPlugin(
    name = "Swagger UI Controller Plugin",
    createConfiguration = ::SwaggerConfiguration,
) {
    log.info(
        "Configuring Swagger UI".withLogMetadata(
            "swaggerUIPath" to pluginConfig.swaggerUIPath,
            "apiSpecUrl" to pluginConfig.apiSpecUrl,
            "webJarPath" to pluginConfig.webJarPath,
            "rootPath" to pluginConfig.rootPath,
            "docExpansion" to pluginConfig.docExpansion,
            "filter" to pluginConfig.filter,
            "tryItOutEnabled" to pluginConfig.tryItOutEnabled,
            "queryConfigEnabled" to pluginConfig.queryConfigEnabled,
            "persistAuthorization" to pluginConfig.persistAuthorization,
            "validatorUrl" to pluginConfig.validatorUrl,
        )
    )

    application.swaggerUIController(
        SwaggerUIController(
            config = pluginConfig,
        )
    )

    application.swaggerUIController
}
