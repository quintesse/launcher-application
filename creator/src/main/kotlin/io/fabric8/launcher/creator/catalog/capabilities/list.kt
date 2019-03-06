package io.fabric8.launcher.creator.catalog.capabilities

import io.fabric8.launcher.creator.core.catalog.CapabilityConstructor
import io.fabric8.launcher.creator.core.catalog.readCapabilityInfoDef

enum class CapabilityInfo(val klazz: CapabilityConstructor) {
    health(::Health),
    rest(::Rest),
    welcome(::Welcome);

    val info by lazy { readCapabilityInfoDef(this.name) }
}
