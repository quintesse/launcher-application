package io.fabric8.launcher.creator.core

import kotlin.reflect.KFunction1

interface CapabilityDescriptor {
    val module: String                  // The name of the applied capability
    val props: Properties?              // The properties to pass to the capability
    val extra: Properties?              // Any properties the capability might return

    data class Data(
            override val module: String,
            override val props: Properties?,
            override val extra: Properties?
    ) : CapabilityDescriptor
}

interface PartDescriptor {
    val subFolderName: String?          // The name of the subFolderName
    val shared: Properties?             // Any shared properties that will be passed to all capabilities
    val extra: Properties?              // Any shared properties returned by capabilities
    val capabilities: List<CapabilityDescriptor>   // All capabilities that are part of the subFolderName

    data class Data(
            override val subFolderName: String?,
            override val shared: Properties?,
            override val extra: Properties?,
            override val capabilities: List<CapabilityDescriptor>
    ) : PartDescriptor
}

interface ApplicationDescriptor {
    val application: String             // The name of the application
    val extra: Properties?              // Any application properties unused by the creator itself
    val parts: List<PartDescriptor>     // Parts are groups of capabilities that make up the application

    data class Data(
            override val application: String,
            override val extra: Properties?,
            override val parts: List<PartDescriptor>
    ) : ApplicationDescriptor
}

interface DeploymentDescriptor {
    val applications: List<ApplicationDescriptor>   // All applications that are part of the deployment

    data class Data(
            override val applications: List<ApplicationDescriptor>
    ) : DeploymentDescriptor
}
