package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.BaseGeneratorProps
import io.fabric8.launcher.creator.core.catalog.CatalogItemContext
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases

interface LanguageNodejsProps : BaseGeneratorProps {
    val builderImage: String?
    val env: Environment?
    val nodejs: NodejsCoords

    companion object {
        fun build(_map: Properties = propsOf(), block: Data.() -> kotlin.Unit = {}) =
            BaseProperties.build(::Data, _map, block)
    }

    open class Data(map: Properties = propsOf()) : BaseGeneratorProps.Data(map), LanguageNodejsProps {
        override var builderImage: String? by _map
        override var env: Environment? by _map
        override val nodejs: NodejsCoords by _map
    }
}

class LanguageNodejs(ctx: CatalogItemContext) : BaseGenerator(ctx) {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val lnprops = LanguageNodejsProps.build(props)
        // Check if the service already exists, so we don't create it twice
        if (resources.service(lnprops.serviceName) == null) {
            copy()
            transform("gap", cases(lnprops));
            val res = newApp(
                    lnprops.serviceName,
                    lnprops.application,
                    lnprops.builderImage ?: BUILDER_NODEJS_APP,
                    null,
                    lnprops.env);
            setBuildContextDir(res, lnprops.subFolderName);
            setMemoryLimit(res,"512Mi")
            setPathHealthChecks(res, "/", "/")
            resources.add(res);
            return newRoute(resources, lnprops.routeName, lnprops.application, lnprops.serviceName);
        } else {
            setBuildEnv(resources, lnprops.env, lnprops.serviceName);
            setDeploymentEnv(resources, lnprops.env, lnprops.serviceName);
            return resources;
        }
    }
}
