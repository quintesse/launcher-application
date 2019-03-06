package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*

open class PlatformWildflyProps(_map: Properties = LinkedHashMap()) : LanguageJavaProps(_map) {
    val maven: MavenCoords by _map
}

open class PlatformWildflyExtra(_map: Properties = LinkedHashMap()) : LanguageJavaExtra(_map) {
}

class PlatformWildfly : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val pwprops = PlatformWildflyProps(props)
        val jarName = "ROOT.war"
        val lprops = propsOf(
                pwprops,
                "jarName" to jarName,
                "builderImage" to BUILDER_JAVAEE
        )

        // Check if the service already exists, so we don't create it twice
        if (resources.service(pwprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, pwprops, extra)
            copy()
        }
        generator(::LanguageJava).apply(resources, lprops, extra)
        generator(::MavenSetup).apply(resources, lprops, extra)
        val readinessProbe = propsOf(
                "httpGet" to propsOf(
                        "path" to "/health",
                        "port" to 9990,
                        "scheme" to "HTTP"
                ),
                "initialDelaySeconds" to 5,
                "timeoutSeconds" to 3,
                "periodSeconds" to 10,
                "failureThreshold" to 10
        )
        val livenessProbe = propsOf(
                "httpGet" to propsOf(
                        "path" to "/health",
                        "port" to 9990,
                        "scheme" to "HTTP"
                ),
                "initialDelaySeconds" to 5,
                "timeoutSeconds" to 3
        )
        setHealthProbe(resources, "readinessProbe", readinessProbe, pwprops.application)
        setHealthProbe(resources, "livenessProbe", livenessProbe, pwprops.application)

        val exProps: PlatformWildflyExtra = propsOf(
                "image" to BUILDER_JAVAEE,
                "enumInfo" to enumItemNN("runtime.name", "wildfly"),
                "service" to pwprops.serviceName,
                "route" to pwprops.routeName
        ) as PlatformWildflyExtra
        extra.pathPut("shared.runtimeInfo", exProps)

        return resources
    }
}
