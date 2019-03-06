package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.Environment
import io.fabric8.launcher.creator.core.MavenCoords
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.BaseGeneratorProps
import io.fabric8.launcher.creator.core.propsOf
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases

open class LanguageJavaProps(_map: Properties = LinkedHashMap()) : BaseGeneratorProps(_map) {
    val builderImage: String by _map
    val jarName: String? by _map
    val env: Environment? by _map
}

open class LanguageJavaExtra(_map: Properties = LinkedHashMap()) : BasePlatformExtra(_map) {
}

class LanguageJava : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val ljprops = LanguageJavaProps(props)
        // Check if the service already exists, so we don't create it twice
        if (resources.service(ljprops.serviceName) == null) {
            copy()
            transform("gap", cases(ljprops));
            val res = newApp(
                    ljprops.serviceName,
                    ljprops.application,
                    ljprops.builderImage,
                    null,
                    ljprops.env);
            setBuildContextDir(res, ljprops.subFolderName);
            setMemoryLimit(res,"1G")
            setDefaultHealthChecks(res)
            resources.add(res);
            return newRoute(resources, ljprops.routeName, ljprops.application, ljprops.serviceName);
        } else {
            setBuildEnv(resources, ljprops.env, ljprops.serviceName);
            setDeploymentEnv(resources, ljprops.env, ljprops.serviceName);
            return resources;
        }
    }
}
