package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.CatalogItemContext
import io.fabric8.launcher.creator.core.catalog.enumItemNN
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases

interface PlatformNodejsProps : LanguageNodejsProps {
    companion object {
        fun build(_map: Properties = propsOf(), block: Data.() -> kotlin.Unit = {}) =
            BaseProperties.build(::Data, _map, block)
    }

    open class Data(map: Properties = propsOf()) : LanguageNodejsProps.Data(map), PlatformNodejsProps {
    }
}

class PlatformNodejs(ctx: CatalogItemContext) : BaseGenerator(ctx) {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val pnprops = PlatformNodejsProps.build(props)
        val lprops = LanguageNodejsProps.build(pnprops) {
            builderImage = BUILDER_NODEJS_APP
        }

        // Check if the service already exists, so we don't create it twice
        if (resources.service(pnprops.serviceName) == null) {
            generator(::PlatformBaseSupport).apply(resources, pnprops, extra)
            copy()
            transform(listOf("package.json"), cases(pnprops))
        }
        val res = generator(::LanguageNodejs).apply(resources, lprops, extra)
        setMemoryLimit(res, "1024Mi")

        val exProps = propsOf(
                "image" to BUILDER_NODEJS_APP,
                "enumInfo" to enumItemNN("runtime.name", "nodejs"),
                "service" to pnprops.serviceName,
                "route" to pnprops.routeName
        )
        extra.pathPut("shared.runtimeInfo", exProps)

        return res
    }
}
