package io.fabric8.launcher.creator.core

fun join(vararg parts: String?) : String {
    return parts.filter { it != null }.joinToString("/")
}
