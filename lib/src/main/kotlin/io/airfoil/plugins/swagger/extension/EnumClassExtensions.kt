package io.airfoil.plugins.swagger.extension

import kotlin.reflect.KClass

inline fun <reified T: Any> KClass<T>.enumSchema(): Set<String> =
    this.java.enumConstants.map { it.toString() }.toSet()
