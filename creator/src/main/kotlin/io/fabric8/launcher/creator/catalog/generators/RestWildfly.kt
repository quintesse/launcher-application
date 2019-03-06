package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.resource.*

class RestWildflyProps(_map: Properties = LinkedHashMap()) : PlatformWildflyProps(_map) {
}

class RestWildflyExtra(_map: Properties = LinkedHashMap()) : PlatformWildflyExtra(_map) {
}

class RestWildfly : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val rwprops = RestWildflyProps(props)
        // Check if the generator was already applied, so we don't do it twice
        if (!filesCopied()) {
            // First copy the files from the base springboot platform module
            // and then copy our own over that
            val pprops = propsOfNN(
                    "application" to rwprops.application,
                    "subFolderName" to rwprops.subFolderName,
                    "serviceName" to rwprops.serviceName,
                    "routeName" to rwprops.routeName,
                    "maven" to rwprops.maven
            ) as PlatformWildflyProps
            generator(::PlatformWildfly).apply(resources, pprops, extra)
            copy()
            mergePoms()
        }
        extra["sourceMapping"] = propsOf("greetingEndpoint" to join(rwprops.subFolderName, "src/main/java/io/openshift/booster/service/GreetingController.java"))
        return resources
    }
}
