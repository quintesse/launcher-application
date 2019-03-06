package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.MavenCoords
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.BaseGeneratorProps
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.resource.Resources

class MavenSetupProps(_map: Properties = LinkedHashMap()) : BaseGeneratorProps(_map) {
    val maven: MavenCoords by _map
}

class MavenSetup : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val msprops = MavenSetupProps(props)
        updatePom(msprops.application, msprops.maven.groupId, msprops.maven.artifactId, msprops.maven.version)
        return resources
    }
}
