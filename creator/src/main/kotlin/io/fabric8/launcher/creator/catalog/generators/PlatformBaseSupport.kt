package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.Enumeration
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.catalog.*
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.cases
import io.fabric8.launcher.creator.core.toJsonObject
import java.nio.file.Paths

interface PlatformBaseSupportProps : BaseGeneratorProps {
    data class Data(
            override val application: String,
            override val subFolderName: String?,
            override val serviceName: String,
            override val routeName: String
    ) : PlatformBaseSupportProps
}

open class BasePlatformExtra(_map: Properties = LinkedHashMap()) : BaseGeneratorExtra(_map) {
    var route: String by _map
    var enumInfo: Enumeration by _map
}

class PlatformBaseSupport : BaseGenerator() {
    override fun apply(resources: Resources, props: CatalogItemProps, extra: Properties): Resources {
        val pbsprops = props as PlatformBaseSupportProps
        // This is here in case we get applied in a subFolderName of our own
        // (meaning there's no runtime so there's no gap or README)
        val files = Paths.get("files")
        val parent = Paths.get("..")
        if (pbsprops.subFolderName != null && !filesCopied(files, parent)) {
            copy(files, parent);
            transform("../gap", cases(pbsprops.toProperties()))
        }
        return resources
    }
}
