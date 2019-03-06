package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases

open class PlatformNodejsProps(_map: Properties = LinkedHashMap()) : LanguageNodejsProps(_map) {
    val runtime: Runtime by _map
    val nodejs: NodejsCoords by _map
}

open class PlatformNodejsExtra(_map: Properties = LinkedHashMap()) : LanguageNodejsExtra(_map) {
}

class PlatformNodejs : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val pnprops = PlatformNodejsProps(props)
        val lprops = propsOf(
                pnprops,
                "builderImage" to BUILDER_NODEJS_APP
        )

        // Check if the service already exists, so we don't create it twice
        if (resources.service(pnprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, pnprops, extra)
            copy()
            transform(listOf("package.json"), cases(pnprops))
        }
        val res = generator(::LanguageNodejs).apply(resources, lprops, extra)
        setMemoryLimit(res, "1024Mi")

        val exProps: PlatformNodejsExtra = propsOf(
                "image" to BUILDER_NODEJS_APP,
                "enumInfo" to enumItemNN("runtime.name", "nodejs"),
                "service" to pnprops.serviceName,
                "route" to pnprops.routeName
        ) as PlatformNodejsExtra
        extra.pathPut("shared.runtimeInfo", exProps)

        return res
    }
}
