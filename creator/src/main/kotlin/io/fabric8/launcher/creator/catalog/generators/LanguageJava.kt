package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.Environment
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.BaseGeneratorProps
import io.fabric8.launcher.creator.core.catalog.CatalogItemProps
import io.fabric8.launcher.creator.core.catalog.toProperties
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases
import io.fabric8.launcher.creator.core.toJsonObject

interface LanguageJavaProps : BaseGeneratorProps {
    val builderImage: String
    val buildArgs: String?
    val jarName: String?
    val env: Environment?

    data class Data(
            override val application: String,
            override val subFolderName: String? = null,
            override val serviceName: String,
            override val routeName: String,
            override val builderImage: String,
            override val buildArgs: String? = null,
            override val jarName: String? = null,
            override val env: Environment? = null
    ) : LanguageJavaProps
}

open class LanguageJavaExtra(_map: Properties = LinkedHashMap()) : BasePlatformExtra(_map) {
}

class LanguageJava : BaseGenerator() {
    override fun apply(resources: Resources, props: CatalogItemProps, extra: Properties): Resources {
        val ljprops = props as LanguageJavaProps
        // Check if the service already exists, so we don't create it twice
        if (resources.service(ljprops.serviceName) == null) {
            copy()
            transform("gap", cases(ljprops.toProperties()))
            val res = newApp(
                    ljprops.serviceName,
                    ljprops.application,
                    ljprops.builderImage,
                    null,
                    ljprops.env);
            setBuildContextDir(res, ljprops.subFolderName)
            setMemoryLimit(res,"1G")
            setDefaultHealthChecks(res)
            resources.add(res)
            return newRoute(resources, ljprops.routeName, ljprops.application, ljprops.serviceName)
        } else {
            setBuildEnv(resources, ljprops.env, ljprops.serviceName)
            setDeploymentEnv(resources, ljprops.env, ljprops.serviceName)
            return resources
        }
    }
}
