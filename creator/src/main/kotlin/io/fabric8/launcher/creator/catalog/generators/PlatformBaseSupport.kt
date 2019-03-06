package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.Enumeration
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.BaseGeneratorExtra
import io.fabric8.launcher.creator.core.catalog.BaseGeneratorProps
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases
import java.nio.file.Paths

open class PlatformBaseSupportProps(_map: Properties = LinkedHashMap()) : BaseGeneratorProps(_map) {
}

open class BasePlatformExtra(_map: Properties = LinkedHashMap()) : BaseGeneratorExtra(_map) {
    var route: String by _map
    var enumInfo: Enumeration by _map
}

class PlatformBaseSupport : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val pbsprops = PlatformBaseSupportProps(props)
        // This is here in case we get applied in a subFolderName of our own
        // (meaning there's no runtime so there's no gap or README)
        val files = Paths.get("files")
        val parent = Paths.get("..")
        if (pbsprops.subFolderName != null && !filesCopied(files, parent)) {
            copy(files, parent);
            transform("../gap", cases(pbsprops));
        }
        return resources;
    }
}