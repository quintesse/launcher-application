package io.fabric8.launcher.creator.core.resource

import io.fabric8.launcher.creator.core.*

const val BUILDER_JAVA = "registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift";
const val BUILDER_JAVAEE = "openshift/wildfly:latest";
const val BUILDER_NODEJS_APP = "nodeshift/centos7-s2i-nodejs";
const val BUILDER_NODEJS_WEB = "nodeshift/centos7-s2i-web-app";

const val IMAGE_MYSQL = "mysql";
const val IMAGE_POSTGRESQL = "postgresql";

typealias BuilderImage = Enumeration

val builderImages = listOf(
        Enumeration.Data(
                id = BUILDER_JAVA,
                name = "Java Code Builder",
                metadata = propsOf(
                        "language" to "java",
                        "isBuilder" to true
                )
        ),
        Enumeration.Data(
                id = BUILDER_JAVAEE,
                name = "JavaEE Code Builder",
                metadata = propsOf(
                        "language" to "java",
                        "isBuilder" to true
                )
        ),
        Enumeration.Data(
                id = BUILDER_NODEJS_WEB,
                name = "Web App Node.js Code Builder",
                metadata = propsOf(
                        "language" to "nodejs",
                        "isBuilder" to true
                )
        ),
        Enumeration.Data(
                id = BUILDER_NODEJS_APP,
                name = "Generic Node.js Code Builder",
                metadata = propsOf(
                        "language" to "nodejs",
                        "isBuilder" to true
                )
        )
)

val databaseImages: List<BuilderImage> = listOf(
        Enumeration.Data(
                id = IMAGE_MYSQL,
                name = "MySQL Database"
        ),
        Enumeration.Data(
                id = IMAGE_POSTGRESQL,
                name = "PostgreSQL Database"
        )
)

val images: List<BuilderImage> = databaseImages + builderImages;

fun builderById(builderId: String): BuilderImage? {
    return builderImages.find { e -> e.id == builderId }
}

fun builderByLanguage(language: String): BuilderImage? {
    return builderImages.find { e -> e.metadata?.get("language") == language }
}
