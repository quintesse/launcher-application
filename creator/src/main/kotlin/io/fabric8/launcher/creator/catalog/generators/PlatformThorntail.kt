package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*

open class PlatformThorntailProps(_map: Properties = LinkedHashMap()) : LanguageJavaProps(_map) {
    val maven: MavenCoords by _map
}

open class PlatformThorntailExtra(_map: Properties = LinkedHashMap()) : LanguageJavaExtra(_map) {
}

class PlatformThorntail : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val ptprops = PlatformThorntailProps(props)
        val jarName = ptprops.maven.artifactId + '-' + ptprops.maven.version + "-thorntail.jar";
        val lprops = propsOf(
                ptprops,
                "jarName" to jarName,
                "builderImage" to BUILDER_JAVA
        )

        // Check if the service already exists, so we don't create it twice
        if (resources.service(ptprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, ptprops, extra)
            copy()
        }
        generator(::LanguageJava).apply(resources, lprops, extra)
        generator(::MavenSetup).apply(resources, lprops, extra)

        val exProps: PlatformThorntailExtra = propsOf(
                "image" to BUILDER_JAVA,
                "enumInfo" to enumItemNN("runtime.name", "thorntail"),
                "service" to ptprops.serviceName,
                "route" to ptprops.routeName
        ) as PlatformThorntailExtra
        extra.pathPut("shared.runtimeInfo", exProps)

        return resources
    }
}
