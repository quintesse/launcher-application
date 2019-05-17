package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.CatalogItemContext
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases

interface PlatformAngularProps : LanguageNodejsProps {
    companion object {
        fun build(_map: Properties = propsOf(), block: Data.() -> kotlin.Unit = {}) =
            BaseProperties.build(::Data, _map, block)
    }

    open class Data(map: Properties = propsOf()) : LanguageNodejsProps.Data(map), PlatformAngularProps {
        override val nodejs: NodejsCoords by _map
    }
}

class PlatformAngular(ctx: CatalogItemContext) : BaseGenerator(ctx) {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val paprops = PlatformAngularProps.build(props)
        val newenv = envOf(
                paprops.env,
                "OUTPUT_DIR" to "dist/" + paprops.application
        )
        val lprops = LanguageNodejsProps.build(paprops) {
            env = newenv
            builderImage = BUILDER_NODEJS_WEB
        }

        // Check if the service already exists, so we don't create it twice
        if (resources.service(paprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, paprops, extra)
            copy()
            transform(listOf("angular.json", "package.json", "src/index.html", "src/**/*.ts", "e2e/**/*.ts"), cases(paprops))
        }
        val res = generator(::LanguageNodejs).apply(resources, lprops, extra)

        val exProps = propsOf(
                "image" to BUILDER_NODEJS_WEB,
                "enumInfo" to enumItemNN("runtime.name", "angular"),
                "service" to paprops.serviceName,
                "route" to paprops.routeName
        )
        extra.pathPut("shared.runtimeInfo", exProps)

        return res
    }
}
