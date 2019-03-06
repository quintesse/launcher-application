package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases

class PlatformVuejsProps(_map: Properties = LinkedHashMap()) : LanguageNodejsProps(_map) {
    val nodejs: NodejsCoords by _map
}

class PlatformVuejsExtra(_map: Properties = LinkedHashMap()) : LanguageNodejsExtra(_map) {
}

class PlatformVuejs : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val pvprops = PlatformVuejsProps(props)
        val env = envOf(
                pvprops.env,
                "OUTPUT_DIR" to "dist/" + pvprops.application
        )
        val lprops = propsOf(
                pvprops,
                "env" to env,
                "builderImage" to BUILDER_NODEJS_WEB
        )

        // Check if the service already exists, so we don't create it twice
        if (resources.service(pvprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, pvprops, extra)
            copy()
            transform(listOf("package.json"), cases(pvprops))
        }
        val res = generator(::LanguageNodejs).apply(resources, lprops, extra)

        val exProps: PlatformVuejsExtra = propsOf(
                "image" to BUILDER_NODEJS_WEB,
                "enumInfo" to enumItemNN("runtime.name", "vuejs"),
                "service" to pvprops.serviceName,
                "route" to pvprops.routeName
        ) as PlatformVuejsExtra
        extra.pathPut("shared.runtimeInfo", exProps)

        return res
    }
}
