package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.CatalogItemContext
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*

interface PlatformVertxProps : LanguageJavaProps, MavenSetupProps {
    companion object {
        fun build(_map: Properties = propsOf(), block: Data.() -> kotlin.Unit = {}) =
            BaseProperties.build(::Data, _map, block)
    }

    open class Data(map: Properties = propsOf()) : LanguageJavaProps.Data(map), PlatformVertxProps {
        override var maven: MavenCoords by _map
    }
}

class PlatformVertx(ctx: CatalogItemContext) : BaseGenerator(ctx) {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val pvprops = PlatformVertxProps.build(props)
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

        val exProps = propsOf(
                "image" to BUILDER_JAVA,
                "enumInfo" to enumItemNN("runtime.name", "vertx"),
                "service" to pvprops.serviceName,
                "route" to pvprops.routeName
        )
        extra.pathPut("shared.runtimeInfo", exProps)

        return resources
    }
}
