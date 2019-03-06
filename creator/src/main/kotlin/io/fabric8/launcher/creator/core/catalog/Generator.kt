package io.fabric8.launcher.creator.core.catalog

import io.fabric8.launcher.creator.core.BaseProperties
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.resource.Resources

interface Generator : CatalogItem {
}

open class BaseGeneratorProps(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val application: String by _map
    val subFolderName: String? by _map
    val serviceName: String by _map
    val routeName: String by _map

    companion object {
        fun build(_map: Properties = LinkedHashMap(), block: BaseGeneratorProps.Builder.() -> kotlin.Unit = {}): BaseGeneratorProps {
            val newobj = Builder(_map)
            block.invoke(newobj)
            return BaseGeneratorProps(newobj._map)
        }
        fun list(vararg block: BaseGeneratorProps.Builder.() -> kotlin.Unit): List<BaseGeneratorProps> {
            return block.map { build(block = it) }
        }
    }

    class Builder(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
        var application: String by _map
        var subFolderName: String? by _map
        var serviceName: String by _map
        var routeName: String by _map
    }
}

open class BaseGeneratorExtra(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
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

    class Builder(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
        var image: String by _map
        var service: String by _map
    }
}

abstract class BaseGenerator : BaseCatalogItem(), Generator {
    abstract override fun apply(resources: Resources, props: Properties, extra: Properties): Resources
}
