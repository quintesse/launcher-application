package io.fabric8.launcher.creator.core.resource

import io.fabric8.launcher.creator.core.BaseProperties
import io.fabric8.launcher.creator.core.Enumeration
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.propsOf

const val BUILDER_JAVA = "registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift";
const val BUILDER_JAVAEE = "openshift/wildfly:latest";
const val BUILDER_NODEJS_APP = "nodeshift/centos7-s2i-nodejs";
const val BUILDER_NODEJS_WEB = "nodeshift/centos7-s2i-web-app";

const val IMAGE_MYSQL = "mysql";
const val IMAGE_POSTGRESQL = "postgresql";

class BuilderImageMetadata(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val language: String by _map
    val isBuilder: Boolean by _map
}

class BuilderImage(_map: Properties = LinkedHashMap()) : Enumeration(_map) {
    override val metadata: BuilderImageMetadata by _map
}

val builderImages: List<BuilderImage> = listOf(
        propsOf(
                "id" to BUILDER_JAVA,
                "name" to "Java Code Builder",
                "metadata" to propsOf(
                        "language" to "java",
                        "isBuilder" to true
                )
        ),
        propsOf(
                "id" to BUILDER_JAVAEE,
                "name" to "JavaEE Code Builder",
                "metadata" to propsOf(
                        "language" to "java",
                        "isBuilder" to true
                )
        ),
        propsOf(
                "id" to BUILDER_NODEJS_WEB,
                "name" to "Web App Node.js Code Builder",
                "metadata" to propsOf(
                        "language" to "nodejs",
                        "isBuilder" to true
                )
        ),
        propsOf(
                "id" to BUILDER_NODEJS_APP,
                "name" to "Generic Node.js Code Builder",
                "metadata" to propsOf(
                        "language" to "nodejs",
                        "isBuilder" to true
                )
        )
) as List<BuilderImage>

val databaseImages: List<BuilderImage> = listOf(
        propsOf(
                "id" to IMAGE_MYSQL,
                "name" to "MySQL Database"
        ),
        propsOf(
                "id" to IMAGE_POSTGRESQL,
                "name" to "PostgreSQL Database"
        )
) as List<BuilderImage>

val images: List<BuilderImage> = databaseImages + builderImages;

fun builderById(builderId: String): BuilderImage? {
    return builderImages.find { e -> e.id == builderId }
}

fun builderByLanguage(language: String): BuilderImage? {
    return builderImages.find { e -> e.metadata.language == language }
}
