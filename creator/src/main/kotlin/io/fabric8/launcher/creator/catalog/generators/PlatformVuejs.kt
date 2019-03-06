package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.CatalogItemContext
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases

interface PlatformVuejsProps : LanguageNodejsProps {
    companion object {
        fun build(_map: Properties = propsOf(), block: Data.() -> kotlin.Unit = {}) =
            BaseProperties.build(::Data, _map, block)
    }

    open class Data(map: Properties = propsOf()) : LanguageNodejsProps.Data(map), PlatformVuejsProps {
        override var nodejs: NodejsCoords by _map
    }
}

class PlatformVuejs(ctx: CatalogItemContext) : BaseGenerator(ctx) {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val pvprops = PlatformVuejsProps.build(props)
        val newenv = envOf(
                pvprops.env,
                "OUTPUT_DIR" to "dist/" + pvprops.application
        )
        val lprops = LanguageNodejsProps.build(pvprops) {
                env = newenv
                builderImage = BUILDER_NODEJS_WEB
        }

        // Check if the service already exists, so we don't create it twice
        if (resources.service(pvprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, pvprops, extra)
            copy()
            transform(listOf("package.json"), cases(pvprops))
        }
        val res = generator(::LanguageNodejs).apply(resources, lprops, extra)

        val exProps = propsOf(
                "image" to BUILDER_NODEJS_WEB,
                "enumInfo" to enumItemNN("runtime.name", "vuejs"),
                "service" to pvprops.serviceName,
                "route" to pvprops.routeName
        )
        extra.pathPut("shared.runtimeInfo", exProps)

        return res
    }
}
