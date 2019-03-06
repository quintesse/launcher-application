package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.catalog.GeneratorConstructor
import io.fabric8.launcher.creator.core.catalog.readGeneratorInfoDef

enum class GeneratorInfo(val klazz: GeneratorConstructor) {
    `language-java`(::LanguageJava),
    `language-nodejs`(::LanguageNodejs),
    `maven-setup`(::MavenSetup),
    `platform-angular`(::PlatformAngular),
    `platform-base-support`(::PlatformBaseSupport),
    `platform-nodejs`(::PlatformNodejs),
    `platform-quarkus`(::PlatformQuarkus),
    `platform-react`(::PlatformReact),
    `platform-springboot`(::PlatformSpringboot),
    `platform-thorntail`(::PlatformThorntail),
    `platform-vertx`(::PlatformVertx),
    `platform-vuejs`(::PlatformVuejs),
    `platform-wildfly`(::PlatformWildfly),
    `rest-nodejs`(::RestNodejs),
    `rest-quarkus`(::RestQuarkus),
    `rest-springboot`(::RestSpringboot),
    `rest-thorntail`(::RestThorntail),
    `rest-vertx`(::RestVertx),
    `rest-wildfly`(::RestWildfly),
    `welcome-app`(::WelcomeApp);

    val info by lazy { readGeneratorInfoDef(this.name) }
}
