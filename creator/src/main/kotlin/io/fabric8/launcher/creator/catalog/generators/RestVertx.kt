package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.resource.*

class RestVertxProps(_map: Properties = LinkedHashMap()) : PlatformVertxProps(_map) {
}

class RestVertxExtra(_map: Properties = LinkedHashMap()): PlatformVertxExtra(_map) {
}

class RestVertx : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val rvprops = RestVertxProps(props)
        // Check if the generator was already applied, so we don't do it twice
        if (!filesCopied()) {
            // First copy the files from the base springboot platform module
            // and then copy our own over that
            val pprops = propsOfNN(
                    "application" to rvprops.application,
                    "subFolderName" to rvprops.subFolderName,
                    "serviceName" to rvprops.serviceName,
                    "routeName" to rvprops.routeName,
                    "maven" to rvprops.maven
            ) as PlatformVertxProps
            generator(::PlatformVertx).apply(resources, pprops, extra)
            copy()
            mergePoms()
        }
        extra["sourceMapping"] = propsOf("greetingEndpoint" to join(rvprops.subFolderName, "src/main/java/io/openshift/booster/service/GreetingController.java"))
        return resources
    }
}
