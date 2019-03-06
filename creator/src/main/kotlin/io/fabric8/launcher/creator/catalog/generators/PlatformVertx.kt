package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*

open class PlatformVertxProps(_map: Properties = LinkedHashMap()) : LanguageJavaProps(_map) {
    val maven: MavenCoords by _map
}

open class PlatformVertxExtra(_map: Properties = LinkedHashMap()) : LanguageJavaExtra(_map) {
}

class PlatformVertx : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val pvprops = PlatformVertxProps(props)
        val jarName = pvprops.maven.artifactId + '-' + pvprops.maven.version + ".jar"
        val lprops = propsOf(
                pvprops,
                "jarName" to jarName,
                "builderImage" to BUILDER_JAVA
        )

        // Check if the service already exists, so we don't create it twice
        if (resources.service(pvprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, pvprops, extra)
            copy()
        }
        generator(::LanguageJava).apply(resources, lprops, extra)
        generator(::MavenSetup).apply(resources, lprops, extra)

        val exProps: PlatformVertxExtra = propsOf(
                "image" to BUILDER_JAVA,
                "enumInfo" to enumItemNN("runtime.name", "vertx"),
                "service" to pvprops.serviceName,
                "route" to pvprops.routeName
        ) as PlatformVertxExtra
        extra.pathPut("shared.runtimeInfo", exProps)

        return resources
    }
}
