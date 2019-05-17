package io.fabric8.launcher.creator.core.resource

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.data.arrayFromPath
import io.fabric8.launcher.creator.core.data.yamlIo
import java.nio.file.Paths

const val BUILDER_JAVA = "registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift"
const val BUILDER_JAVAEE = "openshift/wildfly:latest"
const val BUILDER_NODEJS_APP = "nodeshift/centos7-s2i-nodejs"
const val BUILDER_NODEJS_WEB = "nodeshift/centos7-s2i-web-app"

const val IMAGE_MYSQL = "mysql"
const val IMAGE_POSTGRESQL = "postgresql"

interface BuilderImageMetadata : BaseProperties, Properties {
    val language: String
    val isBuilder: Boolean
    val suggestedEnv: Properties?

    companion object {
        fun build(_map: Properties = propsOf(), block: Data.() -> Unit = {}) =
            BaseProperties.build(::Data, _map, block)
    }

    open class Data(map: Properties = propsOf()) : BaseProperties.Data(map), BuilderImageMetadata {
        override var language: String by _map
        override var isBuilder: Boolean by _map
        override var suggestedEnv: Properties? by _map
    }
}

interface BuilderImage : Enumeration {
    override val metadata: BuilderImageMetadata?

    companion object {
        fun build(_map: Properties = propsOf(), block: Data.() -> Unit = {}) =
            BaseProperties.build(::Data, _map, block)
    }

    open class Data(map: Properties = propsOf()) : BaseProperties.Data(map), BuilderImage {
        override var id: String by _map
        override var name: String by _map
        override var description: String? by _map
        override var icon: String? by _map
        override var metadata: BuilderImageMetadata? by _map
        fun metadata_(block: BuilderImageMetadata.Data.() -> Unit) {
            metadata = BuilderImageMetadata.build(block=block)
        }

        init {
            ensureObject(::metadata, BuilderImageMetadata::Data)
        }
    }
}

val images: List<BuilderImage> by lazy {
    val list = yamlIo.arrayFromPath(Paths.get("io/fabric8/launcher/creator/resource/images.yaml"))
    list.map {
        ensureObject("builderImage", it, BuilderImage::Data)
    }
}

val builderImages = images.filter { it.metadata?.isBuilder ?: false }

fun builderById(builderId: String): BuilderImage? {
    return builderImages.find { e -> e.id == builderId }
}

fun builderByLanguage(language: String): BuilderImage? {
    return builderImages.find { e -> e.metadata?.language == language }
}
