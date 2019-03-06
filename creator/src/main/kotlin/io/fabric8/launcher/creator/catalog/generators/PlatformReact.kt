package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases

class PlatformReactProps(_map: Properties = LinkedHashMap()) : LanguageNodejsProps(_map) {
    val nodejs: NodejsCoords by _map
}

class PlatformReactExtra(_map: Properties = LinkedHashMap()) : LanguageNodejsExtra(_map) {
}

class PlatformReact : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val prprops = PlatformReactProps(props)
        val lprops = propsOf(
                prprops,
                "builderImage" to BUILDER_NODEJS_WEB
        )

        // Check if the service already exists, so we don't create it twice
        if (resources.service(prprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, prprops, extra)
            copy()
            transform(listOf("package.json"), cases(prprops))
        }
        val res = generator(::LanguageNodejs).apply(resources, lprops, extra)

        val exProps: PlatformReactExtra = propsOf(
                "image" to BUILDER_NODEJS_WEB,
                "enumInfo" to enumItemNN("runtime.name", "react"),
                "service" to prprops.serviceName,
                "route" to prprops.routeName
        ) as PlatformReactExtra
        extra.pathPut("shared.runtimeInfo", exProps)

        return res
    }
}
