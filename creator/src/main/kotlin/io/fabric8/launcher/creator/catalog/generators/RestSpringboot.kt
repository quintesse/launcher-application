package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.resource.*

class RestSpringbootProps(_map: Properties = LinkedHashMap()) : PlatformSpringbootProps(_map) {
}

class RestSpringbootExtra(_map: Properties = LinkedHashMap()) : PlatformSpringbootExtra(_map) {
}

class RestSpringboot : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val rsprops = RestSpringbootProps(props)
        // Check if the generator was already applied, so we don't do it twice
        if (!filesCopied()) {
            // First copy the files from the base springboot platform module
            // and then copy our own over that
            val pprops = propsOfNN(
                    "application" to rsprops.application,
                    "subFolderName" to rsprops.subFolderName,
                    "serviceName" to rsprops.serviceName,
                    "routeName" to rsprops.routeName,
                    "maven" to rsprops.maven
            ) as PlatformSpringbootProps
            generator(::PlatformSpringboot).apply(resources, pprops, extra)
            copy()
            mergePoms()
        }
        extra["sourceMapping"] = propsOf("greetingEndpoint" to join(rsprops.subFolderName, "src/main/java/io/openshift/booster/service/GreetingController.java"))
        return resources
    }
}
