package io.airfoil.plugins.swagger.config

import io.airfoil.common.extension.boolValueOrDefault
import io.airfoil.common.extension.stringValueOrDefault
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigurationException

class SwaggerConfiguration {
    lateinit var openApi: OpenAPIConfiguration

    var swaggerUIPath: String = DEFAULT_SWAGGER_UI_PATH
    var apiSpecUrl: String = DEFAULT_API_SPEC_URL
    var webJarPath: String = DEFAULT_WEBJAR_PATH
    var rootPath: String = DEFAULT_ROOT_PATH
    var docExpansion: String = DEFAULT_DOC_EXPANSION
    var filter: Boolean = DEFAULT_FILTER
    var tryItOutEnabled: Boolean = DEFAULT_TRY_IT_OUT_ENABLED
    var queryConfigEnabled: Boolean = DEFAULT_QUERY_CONFIG_ENABLED
    var persistAuthorization: Boolean = DEFAULT_PERSIST_AUTHORIZATION
    var validatorUrl: String = DEFAULT_VALIDATOR_URL

    companion object {
        const val CONFIG_KEY = "swagger"

        const val DEFAULT_SWAGGER_UI_PATH = "/swagger-ui"
        const val DEFAULT_API_SPEC_URL = ""
        const val DEFAULT_WEBJAR_PATH = "/webjars/swagger-ui"
        const val DEFAULT_ROOT_PATH = "/swagger"
        const val DEFAULT_DOC_EXPANSION = "list"
        const val DEFAULT_FILTER = false
        const val DEFAULT_TRY_IT_OUT_ENABLED = true
        const val DEFAULT_QUERY_CONFIG_ENABLED = true
        const val DEFAULT_PERSIST_AUTHORIZATION = false
        const val DEFAULT_VALIDATOR_URL = "https://validator.swagger.io/validator"

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): SwaggerConfiguration = config.config(configKey).let { cfg ->
            SwaggerConfiguration().also {
                it.openApi = OpenAPIConfiguration.loadOrDefault(cfg)

                it.swaggerUIPath = cfg.stringValueOrDefault("swaggerUIPath", DEFAULT_SWAGGER_UI_PATH)
                it.apiSpecUrl = cfg.stringValueOrDefault("apiSpecUrl", DEFAULT_API_SPEC_URL)
                it.webJarPath = cfg.stringValueOrDefault("webJarPath", DEFAULT_WEBJAR_PATH)
                it.rootPath = cfg.stringValueOrDefault("rootPath", DEFAULT_ROOT_PATH)
                it.docExpansion = cfg.stringValueOrDefault("docExpansion", DEFAULT_DOC_EXPANSION)
                it.filter = cfg.boolValueOrDefault("filter", DEFAULT_FILTER)
                it.tryItOutEnabled = cfg.boolValueOrDefault("tryItOutEnabled", DEFAULT_TRY_IT_OUT_ENABLED)
                it.queryConfigEnabled = cfg.boolValueOrDefault("queryConfigEnabled", DEFAULT_QUERY_CONFIG_ENABLED)
                it.persistAuthorization = cfg.boolValueOrDefault("persistAuthorization", DEFAULT_PERSIST_AUTHORIZATION)
                it.validatorUrl = cfg.stringValueOrDefault("validatorUrl", DEFAULT_VALIDATOR_URL)
            }
        }

        fun loadOrNull(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): SwaggerConfiguration? = try {
            load(config, configKey)
        } catch (ex: ApplicationConfigurationException) {
            null
        }

        fun loadOrDefault(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): SwaggerConfiguration = loadOrNull(config, configKey) ?: SwaggerConfiguration()
    }
}
