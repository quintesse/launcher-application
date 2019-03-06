package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.CatalogItemContext
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*

interface PlatformThorntailProps : LanguageJavaProps, MavenSetupProps {
    companion object {
        fun build(_map: Properties = propsOf(), block: Data.() -> kotlin.Unit = {}) =
            BaseProperties.build(::Data, _map, block)
    }

    open class Data(map: Properties = propsOf()) : LanguageJavaProps.Data(map), PlatformThorntailProps {
        override var maven: MavenCoords by _map
    }
}

class PlatformThorntail(ctx: CatalogItemContext) : BaseGenerator(ctx) {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val ptprops = PlatformThorntailProps.build(props)
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

        val exProps = propsOf(
                "image" to BUILDER_JAVA,
                "enumInfo" to enumItemNN("runtime.name", "thorntail"),
                "service" to ptprops.serviceName,
                "route" to ptprops.routeName
        )
        extra.pathPut("shared.runtimeInfo", exProps)

        return resources
    }
}
