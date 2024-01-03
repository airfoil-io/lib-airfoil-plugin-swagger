package io.airfoil.plugins.swagger.config

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigurationException

class OpenAPIConfiguration {
    lateinit var info: OpenAPIInfoConfiguration

    companion object {
        const val CONFIG_KEY = "openapi"

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): OpenAPIConfiguration = config.config(configKey).let { cfg ->
            OpenAPIConfiguration().also {
                it.info = OpenAPIInfoConfiguration.loadOrDefault(cfg)
            }
        }

        fun loadOrNull(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): OpenAPIConfiguration? = try {
            load(config, configKey)
        } catch (ex: ApplicationConfigurationException) {
            null
        }

        fun loadOrDefault(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): OpenAPIConfiguration = loadOrNull(config, configKey) ?: OpenAPIConfiguration().also {
            it.info = OpenAPIInfoConfiguration.loadOrDefault(config)
        }
    }
}
