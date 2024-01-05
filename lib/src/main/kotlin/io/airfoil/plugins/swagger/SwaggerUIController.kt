package io.airfoil.plugins.swagger

import io.airfoil.common.plugin.KtorApplicationPlugin
import io.airfoil.plugins.swagger.config.SwaggerConfiguration
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

class SwaggerUIController(
    private val config: SwaggerConfiguration,
) : KtorApplicationPlugin {
    private val jsConfig: String = with(config) {
        """
        window.onload = function() {
            //<editor-fold desc="Changeable Configuration Block">

            window.ui = SwaggerUIBundle({
                urls: [{url: '$apiSpecUrl', name: 'api-spec'}],
                dom_id: '#swagger-ui',
                deepLinking: true,
                defaultModelExpandDepth: 4,
                defaultModelsExpandDepth: 4,
                displayOperationId: true,
                displayRequestDuration: true,
                docExpansion: '$docExpansion',
                filter: $filter,
                operationsSorter: 'alpha',
                persistAuthorization: $persistAuthorization,
                tagsSorter: 'alpha',
                tryItOutEnabled: $tryItOutEnabled,
                validatorUrl: '$validatorUrl',
                presets: [
                    SwaggerUIBundle.presets.apis,
                    SwaggerUIStandalonePreset
                ],
                plugins: [
                    SwaggerUIBundle.plugins.DownloadUrl
                ],
                layout: "StandaloneLayout",
                queryConfigEnabled: $queryConfigEnabled
            });

            //</editor-fold>
        }
        """.trimIndent()
    }

    private val swaggerRedirectPath =
        "${config.rootPath}${config.webJarPath}/index.html"
    private val swaggerInitializerJsPath =
        "${config.rootPath}${config.webJarPath}/swagger-initializer.js"

    override fun onApplicationStarted(application: Application) {
        application.routing {
            get(config.swaggerUIPath) {
                call.respondRedirect(swaggerRedirectPath)
            }
        }
    }

    override fun onCall(call: ApplicationCall) {
        if (!call.response.isCommitted) {
            val request = call.request

            if (request.path().startsWith(swaggerInitializerJsPath) && request.httpMethod == HttpMethod.Get) {
                // intercept request to webjar for configuration
                runBlocking {
                    call.respond(
                        ByteArrayContent(
                            jsConfig.toByteArray(),
                            ContentType.defaultForFileExtension("js"),
                            HttpStatusCode.OK,
                        ),
                    )
                }
            }
        }
    }
}
