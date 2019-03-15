package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.MavenCoords
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.BaseGeneratorProps
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.catalog.CatalogItemProps
import io.fabric8.launcher.creator.core.resource.Resources

interface MavenSetupProps : BaseGeneratorProps {
    val maven: MavenCoords

    data class Data(
            override val application: String,
            override val subFolderName: String? = null,
            override val serviceName: String,
            override val routeName: String,
            override val maven: MavenCoords
    ) : MavenSetupProps
}

class MavenSetup : BaseGenerator() {
    override fun apply(resources: Resources, props: CatalogItemProps, extra: Properties): Resources {
        val msprops = props as MavenSetupProps
        updatePom(msprops.application, msprops.maven.groupId, msprops.maven.artifactId, msprops.maven.version)
        return resources
    }
}
