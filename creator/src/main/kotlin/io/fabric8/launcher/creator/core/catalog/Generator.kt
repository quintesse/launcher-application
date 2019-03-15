package io.fabric8.launcher.creator.core.catalog

import io.fabric8.launcher.creator.core.BasePropertiesX
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.resource.Resources

interface Generator : CatalogItem {
}

interface BaseGeneratorProps : CatalogItemProps {
    val application: String
    val subFolderName: String?
    val serviceName: String
    val routeName: String

    data class Data(
            override val application: String,
            override val subFolderName: String? = null,
            override val serviceName: String,
            override val routeName: String
    ) : BaseGeneratorProps
}

open class BaseGeneratorExtra(_map: Properties = LinkedHashMap()) : BasePropertiesX(_map) {
    var image: String by _map
    var service: String by _map

    companion object {
        fun build(_map: Properties = LinkedHashMap(), block: BaseGeneratorExtra.Builder.() -> kotlin.Unit = {}): BaseGeneratorExtra {
            val newobj = Builder(_map)
            block.invoke(newobj)
            return BaseGeneratorExtra(newobj._map)
        }
        fun list(vararg block: BaseGeneratorExtra.Builder.() -> kotlin.Unit): List<BaseGeneratorExtra> {
            return block.map { build(block = it) }
        }
    }

    class Builder(_map: Properties = LinkedHashMap()) : BasePropertiesX(_map) {
        var image: String by _map
        var service: String by _map
    }
}

abstract class BaseGenerator : BaseCatalogItem(), Generator {
    abstract override fun apply(resources: Resources, props: CatalogItemProps, extra: Properties): Resources
}
