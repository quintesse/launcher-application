package io.fabric8.launcher.creator.core.catalog

import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.resource.Resources

interface Capability : CatalogItem {
    fun postApply(resources: Resources, props: Properties?, deployment: Any?) {
    }
}

abstract class BaseCapability : BaseCatalogItem(), Capability {
}
