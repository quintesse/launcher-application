package io.fabric8.launcher.creator.core

import kotlin.reflect.KFunction1

class CapabilityDescriptor(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val module: String by _map                  // The name of the applied capability
    val props: Properties? by _map              // The properties to pass to the capability
    val extra: Properties? by _map              // Any properties the capability might return

    companion object {
        fun build(_map: Properties = LinkedHashMap(), block: CapabilityDescriptor.Builder.() -> kotlin.Unit = {}): CapabilityDescriptor {
            val newobj = Builder(_map)
            block.invoke(newobj)
            return CapabilityDescriptor(newobj._map)
        }
        fun list(vararg block: CapabilityDescriptor.Builder.() -> kotlin.Unit): List<CapabilityDescriptor> {
            return block.map { build(block = it) }
        }
    }

    class Builder(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
        var module: String by _map                  // The name of the applied capability
        var props: Properties? by _map              // The properties to pass to the capability
        var extra: Properties? by _map              // Any properties the capability might return
    }
}

class PartDescriptor(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val subFolderName: String? by _map          // The name of the subFolderName
    val shared: Properties? by _map             // Any shared properties that will be passed to all capabilities
    val extra: Properties? by _map              // Any shared properties returned by capabilities
    val capabilities: List<CapabilityDescriptor> by _map   // All capabilities that are part of the subFolderName

    companion object {
        fun build(_map: Properties = LinkedHashMap(), block: PartDescriptor.Builder.() -> kotlin.Unit = {}): PartDescriptor {
            val newobj = Builder(_map)
            block.invoke(newobj)
            return PartDescriptor(newobj._map)
        }
        fun list(vararg block: PartDescriptor.Builder.() -> kotlin.Unit): List<PartDescriptor> {
            return block.map { build(block = it) }
        }
    }

    class Builder(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
        var subFolderName: String? by _map      // The name of the subFolderName
        var shared: Properties? by _map         // Any shared properties that will be passed to all capabilities
        var extra: Properties? by _map          // Any shared properties returned by capabilities
        var capabilities: List<CapabilityDescriptor> by _map   // All capabilities that are part of the subFolderName

        init {
            if (get("capabilities") != null) capabilities = ensureList(capabilities, ::CapabilityDescriptor)
        }
    }
}

class ApplicationDescriptor(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val application: String by _map             // The name of the application
    val extra: Properties? by _map              // Any application properties unused by the creator itself
    val parts: List<PartDescriptor> by _map     // Parts are groups of capabilities that make up the application

    companion object {
        fun build(_map: Properties = LinkedHashMap(), block: ApplicationDescriptor.Builder.() -> kotlin.Unit = {}): ApplicationDescriptor {
            val newobj = Builder(_map)
            block.invoke(newobj)
            return ApplicationDescriptor(newobj._map)
        }
        fun list(vararg block: ApplicationDescriptor.Builder.() -> kotlin.Unit): List<ApplicationDescriptor> {
            return block.map { build(block = it) }
        }
    }

    class Builder(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
        var application: String by _map             // The name of the application
        var extra: Properties? by _map              // Any application properties unused by the creator itself
        var parts: List<PartDescriptor> by _map     // Parts are groups of capabilities that make up the application

        init {
            if (get("parts") != null) parts = ensureList(parts, ::PartDescriptor)
        }
    }
}

class DeploymentDescriptor(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val applications: List<ApplicationDescriptor> by _map   // All applications that are part of the deployment

    companion object {
        fun build(_map: Properties = LinkedHashMap(), block: DeploymentDescriptor.Builder.() -> kotlin.Unit = {}): DeploymentDescriptor {
            val newobj = Builder(_map)
            block.invoke(newobj)
            return DeploymentDescriptor(newobj._map)
        }
        fun list(vararg block: DeploymentDescriptor.Builder.() -> kotlin.Unit): List<DeploymentDescriptor> {
            return block.map { build(block = it) }
        }
    }

    class Builder(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
        var applications: List<ApplicationDescriptor> by _map   // All applications that are part of the deployment

        init {
            if (get("applications") != null) applications = ensureList(applications, ::ApplicationDescriptor)
        }
    }
}
