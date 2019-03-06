package io.fabric8.launcher.creator.core.catalog

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import io.fabric8.launcher.creator.core.Properties

fun getCapabilityInfo(capabilityName: String): Properties {
    try {
        val folderName = capabilityName.replace("-", "")
        val resPath = "io/fabric8/launcher/creator/catalog/capabilities/${folderName}/info.json"
        ::getCapabilityInfo.javaClass.classLoader.getResourceAsStream(resPath).use {
            return Parser.default().parse(it) as JsonObject
        }
    } catch(ex: Exception) {
        throw RuntimeException("No info found for capability '${capabilityName}'")
    }
}

fun getCapabilityClass(capabilityName: String): () -> Capability {
    return when(capabilityName) {
        else -> throw RuntimeException("Unknown capability '${capabilityName}'")
    }
}

