package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases

class PlatformAngularProps(_map: Properties = LinkedHashMap()) : LanguageNodejsProps(_map) {
    val nodejs: NodejsCoords by _map
}

class PlatformAngularExtra(_map: Properties = LinkedHashMap()) : LanguageNodejsExtra(_map) {
}

class PlatformAngular : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val paprops = PlatformAngularProps(props)
        val env = envOf(
                paprops.env,
                "OUTPUT_DIR" to "dist/" + paprops.application
        )
        val lprops = propsOf(
                paprops,
                "env" to env,
                "builderImage" to BUILDER_NODEJS_WEB
        )

        // Check if the service already exists, so we don't create it twice
        if (resources.service(paprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, paprops, extra)
            copy()
            transform(listOf("angular.json", "package.json", "src/index.html", "src/**/*.ts", "e2e/**/*.ts"), cases(paprops))
        }
        val res = generator(::LanguageNodejs).apply(resources, lprops, extra)

        val exProps: PlatformAngularExtra = propsOf(
                "image" to BUILDER_NODEJS_WEB,
                "enumInfo" to enumItemNN("runtime.name", "angular"),
                "service" to paprops.serviceName,
                "route" to paprops.routeName
        ) as PlatformAngularExtra
        extra.pathPut("shared.runtimeInfo", exProps)

        return res
    }
}
