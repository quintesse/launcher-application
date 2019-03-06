package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.resource.*

class RestThorntailProps(_map: Properties = LinkedHashMap()) : PlatformThorntailProps(_map) {
}

class RestThorntailExtra(_map: Properties = LinkedHashMap()) : PlatformThorntailExtra(_map) {
}

class RestThorntail : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val rtprops = RestThorntailProps(props)
        // Check if the generator was already applied, so we don't do it twice
        if (!filesCopied()) {
            // First copy the files from the base springboot platform module
            // and then copy our own over that
            val pprops = propsOfNN(
                    "application" to rtprops.application,
                    "subFolderName" to rtprops.subFolderName,
                    "serviceName" to rtprops.serviceName,
                    "routeName" to rtprops.routeName,
                    "maven" to rtprops.maven
            ) as PlatformThorntailProps
            generator(::PlatformThorntail).apply(resources, pprops, extra)
            copy()
            mergePoms()
        }
        extra["sourceMapping"] = propsOf("greetingEndpoint" to join(rtprops.subFolderName, "src/main/java/io/openshift/booster/http/GreetingEndpoint.java"))
        return resources
    }
}
