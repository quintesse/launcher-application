package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.CatalogItemContext
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*

interface PlatformQuarkusProps : LanguageJavaProps, MavenSetupProps {
    companion object {
        fun build(_map: Properties = propsOf(), block: Data.() -> kotlin.Unit = {}) =
            BaseProperties.build(::Data, _map, block)
    }

    open class Data(map: Properties = propsOf()) : LanguageJavaProps.Data(map), PlatformQuarkusProps {
        override var maven: MavenCoords by _map
    }
}

class PlatformQuarkus(ctx: CatalogItemContext) : BaseGenerator(ctx) {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val pqprops = PlatformQuarkusProps.build(props)
        val jarName = pqprops.maven.artifactId + "-runner.jar"
        val newenv = envOf(
            pqprops.env,
            "JAVA_APP_JAR" to jarName,
            "ARTIFACT_COPY_ARGS" to "-p -r lib/ $jarName"
        )
        val lprops = propsOf(
            pqprops,
            "env" to newenv,
            "jarName" to jarName,
            "builderImage" to BUILDER_JAVA,
            "buildArgs" to "-DuberJar=true"
        )

        // Check if the service already exists, so we don't create it twice
        if (resources.service(pqprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, pqprops, extra)
            copy()
        }
        generator(::LanguageJava).apply(resources, lprops, extra)
        generator(::MavenSetup).apply(resources, lprops, extra)

        val exProps = propsOf(
                "image" to BUILDER_JAVA,
                "enumInfo" to enumItemNN("runtime.name", "quarkus"),
                "service" to pqprops.serviceName,
                "route" to pqprops.routeName
        )
        extra.pathPut("shared.runtimeInfo", exProps)

        return resources
    }
}
