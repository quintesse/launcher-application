package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.Environment
import io.fabric8.launcher.creator.core.NodejsCoords
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.BaseGeneratorProps
import io.fabric8.launcher.creator.core.catalog.CatalogItemProps
import io.fabric8.launcher.creator.core.catalog.toProperties
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases

interface LanguageNodejsProps : BaseGeneratorProps {
    val builderImage: String
    val env: Environment?

    data class Data(
            override val application: String,
            override val subFolderName: String? = null,
            override val serviceName: String,
            override val routeName: String,
            override val builderImage: String,
            override val env: Environment? = null
    ) : LanguageNodejsProps
}

open class LanguageNodejsExtra(_map: Properties = LinkedHashMap()) : BasePlatformExtra(_map) {
}

class LanguageNodejs : BaseGenerator() {
    override fun apply(resources: Resources, props: CatalogItemProps, extra: Properties): Resources {
        val lnprops = props as LanguageNodejsProps
        // Check if the service already exists, so we don't create it twice
        if (resources.service(lnprops.serviceName) == null) {
            copy()
            transform("gap", cases(lnprops.toProperties()));
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
