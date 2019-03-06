package io.fabric8.launcher.creator.core.catalog

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import io.fabric8.launcher.creator.catalog.generators.*
import io.fabric8.launcher.creator.core.Properties

fun getGeneratorInfo(generatorName: String): Properties {
    try {
        val folderName = generatorName.replace("-", "")
        val resPath = "io/fabric8/launcher/creator/catalog/generators/${folderName}/info.json"
        ::getGeneratorInfo.javaClass.classLoader.getResourceAsStream(resPath).use {
            return Parser.default().parse(it) as JsonObject
        }
    } catch(ex: Exception) {
        throw RuntimeException("No info found for generator '${generatorName}'")
    }
}

fun getGeneratorClass(generatorName: String): () -> Generator {
    return when(generatorName) {
        "language-java" -> ::LanguageJava
        "language-nodejs" -> ::LanguageNodejs
        "maven-setup" -> ::MavenSetup
        "platform-angular" -> ::PlatformAngular
        "platform-base-support" -> ::PlatformBaseSupport
        "platform-nodejs" -> ::PlatformNodejs
        "platform-react" -> ::PlatformReact
        "platform-springboot" -> ::PlatformSpringboot
        "platform-thorntail" -> ::PlatformThorntail
        "platform-vertx" -> ::PlatformVertx
        "platform-vuejs" -> ::PlatformVuejs
        "platform-wildfly" -> ::PlatformWildfly
        else -> throw RuntimeException("Unknown generator '${generatorName}'")
    }
}

