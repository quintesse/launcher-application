package io.fabric8.launcher.creator.core.resource

import io.fabric8.launcher.creator.core.Environment
import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.envOf

// Updates the environment variables for the BuildConfig selected
// by 'bcName' with the given key/values in the object 'env'. The values
// are either simple strings or they can be objects themselves in which
// case they are references to keys in a ConfigMap or a Secret.
fun setBuildEnv(res: Resources, env: Environment?, bcName: String? = null): Resources {
    TODO("not implemented")
}

// Updates the environment variables for the DeploymentConfig selected
// by 'dcName' with the given key/values in the object 'env'. The values
// are either simple strings or they can be objects themselves in which
// case they are references to keys in a ConfigMap or a Secret.
fun setDeploymentEnv(res: Resources, env: Environment?, dcName: String? = null): Resources {
    TODO("not implemented")
}

// Updates the contextDir in the source strategy of the BuildConfig selected
// by 'bcName' with the given path.
fun setBuildContextDir(res: Resources, contextDir: String?, bcName: String? = null): Resources {
    TODO("not implemented")
}

// Sets the cpu limits for the DeploymentConfig selected by 'dcName'
// with the given ComputeResources for cpu.
fun setCpuLimit(res: Resources, limit: String, dcName: String? = null): Resources {
    TODO("not implemented")
}

// Sets the cpu requests for the DeploymentConfig selected by 'dcName'
// with the given ComputeResources for cpu.
fun setCpuRequest(res: Resources, request: String, dcName: String? = null): Resources {
    TODO("not implemented")
}

// Sets the memory limits for the DeploymentConfig selected by 'dcName'
// with the given ComputeResources for memory.
fun setMemoryLimit(res: Resources, limit: String, dcName: String? = null): Resources {
    TODO("not implemented")
}

// Sets the memory requests for the DeploymentConfig selected by 'dcName'
// with the given ComputeResources for memory.
fun setMemoryRequest(res: Resources, request: String, dcName: String? = null): Resources {
    TODO("not implemented")
}

// Sets the given health check probe for the DeploymentConfig selected by 'dcName'.
fun setHealthProbe(res: Resources, probeName: String, probe: Properties, dcName: String? = null): Resources {
    TODO("not implemented")
}

// Sets the default health checks for the DeploymentConfig selected by 'dcName'.
// Both the readiness and the liveness check will use `/health`.
fun setPathHealthChecks(res: Resources,
                        readinessPath: String,
                        livenessPath: String,
                        dcName: String? = null): Resources {
    TODO("not implemented")
}

// Sets the default health checks for the DeploymentConfig selected by 'dcName'.
// Both the readiness and the liveness check will use `/health`.
fun setDefaultHealthChecks(res: Resources, dcName: String? = null): Resources {
    TODO("not implemented")
}

// Sets the "app" label on all resources to the given value
fun setAppLabel(res: Resources, label: String) : Resources {
    TODO("not implemented")
}

// Sets the "app" label on all resources to the given value
fun setAppLabel(res: Resources, label: Properties) : Resources {
    TODO("not implemented")
}

// Returns a list of resources that when applied will create
// an instance of the given image or template. Any environment
// variables being passed will be applied to any `DeploymentConfig`
// and `BuildConfig` resources that could be found in the image
fun newApp(appName: String,
           appLabel: String,
           imageName: String,
           sourceUri: String?,
           env: Environment? = envOf()): Resources {
    TODO("not implemented")
}

fun newApp(appName: String,
           appLabel: Properties,
           imageName: String,
           sourceUri: String?,
           env: Environment? = envOf()): Resources {
    TODO("not implemented")
}

fun newRoute(res: Resources,
             appName: String,
             appLabel: String,
             serviceName: String, port: Int = -1): Resources {
    TODO("not implemented")
}

fun newRoute(res: Resources,
             appName: String,
             appLabel: Properties,
             serviceName: String, port: Int = -1): Resources {
    TODO("not implemented")
}

fun newService(res: Resources,
               appName: String,
               appLabel: String,
               serviceName: String): Resources {
    TODO("not implemented")
}

fun newService(res: Resources,
               appName: String,
               appLabel: Properties,
               serviceName: String): Resources {
    TODO("not implemented")
}
