package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*

open class PlatformSpringbootProps(_map: Properties = LinkedHashMap()) : LanguageJavaProps(_map) {
    val maven: MavenCoords by _map
}

open class PlatformSpringbootExtra(_map: Properties = LinkedHashMap()) : LanguageJavaExtra(_map) {
}

class PlatformSpringboot : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val psprops = PlatformSpringbootProps(props)
        val jarName = psprops.maven.artifactId + '-' + psprops.maven.version + ".jar"
        val lprops = propsOf(
                psprops,
                "jarName" to jarName,
                "builderImage" to BUILDER_JAVA
        )

        // Check if the service already exists, so we don't create it twice
        if (resources.service(psprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, psprops, extra)
            copy()
        }
        generator(::LanguageJava).apply(resources, lprops, extra)
        generator(::MavenSetup).apply(resources, lprops, extra)

        val exProps: PlatformSpringbootExtra = propsOf(
                "image" to BUILDER_JAVA,
                "enumInfo" to enumItemNN("runtime.name", "springboot"),
                "service" to psprops.serviceName,
                "route" to psprops.routeName
        ) as PlatformSpringbootExtra
        extra.pathPut("shared.runtimeInfo", exProps)

        return resources
    }
}
