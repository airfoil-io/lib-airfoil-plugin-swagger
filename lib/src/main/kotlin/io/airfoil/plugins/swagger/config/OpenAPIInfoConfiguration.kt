package io.airfoil.plugins.swagger.config

import io.airfoil.common.extension.stringValueOrDefault
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.ApplicationConfigurationException

class OpenAPIInfoConfiguration {
    var title: String = DEFAULT_TITLE
    var version: String = DEFAULT_VERSION

    companion object {
        const val CONFIG_KEY = "info"

        const val DEFAULT_TITLE = "Unknown API"
        const val DEFAULT_VERSION = "0.0.0"

        fun load(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): OpenAPIInfoConfiguration = config.config(configKey).let { cfg ->
            OpenAPIInfoConfiguration().also {
                it.title = cfg.stringValueOrDefault("title", DEFAULT_TITLE)
                it.version = cfg.stringValueOrDefault("version", DEFAULT_VERSION)
            }
        }

        fun loadOrNull(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): OpenAPIInfoConfiguration? = try {
            load(config, configKey)
        } catch (ex: ApplicationConfigurationException) {
            null
        }

        fun loadOrDefault(
            config: ApplicationConfig,
            configKey: String = CONFIG_KEY,
        ): OpenAPIInfoConfiguration = loadOrNull(config, configKey) ?: OpenAPIInfoConfiguration()
    }
}
