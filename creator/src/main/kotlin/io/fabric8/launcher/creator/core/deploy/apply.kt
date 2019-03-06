package io.fabric8.launcher.creator.core.deploy

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import io.fabric8.launcher.creator.cli.Apply
import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.*
import io.fabric8.launcher.creator.core.resource.Resource
import io.fabric8.launcher.creator.core.resource.Resources
import org.yaml.snakeyaml.Yaml
import java.awt.SystemColor.info
import java.nio.file.Files
import java.nio.file.Path

// Returns the name of the deployment file in the given directory
fun deploymentFileName(targetDir: Path): Path {
    return targetDir.resolve("deployment.json")
}

// Returns the name of the resources file in the given directory
fun resourcesFileName(targetDir: Path): Path {
    return targetDir.resolve(".openshiftio").resolve("application.yaml")
}

private fun emptyDeploymentDescriptor(): DeploymentDescriptor {
    return DeploymentDescriptor.build {
        applications = mutableListOf()
    }
}

// Returns a promise that will resolve to the JSON
// contents of the given file or to an empty object
// if the file wasn't found
fun readDeployment(deploymentFile: Path): DeploymentDescriptor {
    if (Files.exists(deploymentFile)) {
        try {
            deploymentFile.toFile().inputStream().use {
                val obj = Parser.default().parse(it) as JsonObject
                return DeploymentDescriptor.build(obj)
            }
        } catch (ex: Exception) {
            System.err.println("Failed to read deployment file ${deploymentFile}: ${ex}")
            throw ex
        }
    } else {
        return emptyDeploymentDescriptor()
    }
}

// Returns a promise that will resolve when the given
// deployment was written to the given file
fun writeDeployment(deploymentFile: Path, deployment: DeploymentDescriptor) {
    try {
        val obj = if (deployment._map is JsonObject) deployment._map else JsonObject(deployment._map)
        val str = obj.toJsonString(true)
        deploymentFile.toFile().writeText(str)
    } catch (ex: Exception) {
        System.err.println("Failed to write deployment file ${deploymentFile}: ${ex}")
        throw ex
    }
}

// Returns a promise that will resolve when the given
// resources were read from the given file
fun readResources(resourcesFile: Path): Resources {
    if (Files.exists(resourcesFile)) {
        try {
            resourcesFile.toFile().inputStream().use {
                val map = Yaml().load<Properties>(it)
                return Resources(Resource.build(map))
            }
        } catch (ex: Exception) {
            System.err.println("Failed to read resources file ${resourcesFile}: ${ex}")
            throw ex
        }
    } else {
        return Resources()
    }
}

// Returns a promise that will resolve when the given
// resources were written to the given file
fun writeResources(resourcesFile: Path, res: Resources) {
    try {
        val str = Yaml().dumpAsMap(res.json._map)
        resourcesFile.toFile().writeText(str)
    } catch (ex: Exception) {
        System.err.println("Failed to write resources file ${resourcesFile}: ${ex}")
        throw ex
    }
}

// Calls `apply()` on the given capability (which allows it to copy, generate
// and change files in the user's project) and adds information about the
// capability to the `deployment.json` in the project's root.
private fun applyCapability(generator, res: Resources, targetDir: Path, appName: String, subFolderName: String?, shared: Properties?, capability: CapabilityDescriptor): DeploymentDescriptor {
    val props : any = { ...capability.props, 'module': capability.module, 'application': appName }
    if (subFolderName != null) {
        props.subFolderName = subFolderName
    }

    // Validate the properties that we get passed are valid
    val capTargetDir = if (props.subFolderName == null) targetDir else targetDir.resolve(props.subFolderName)
    val capConst = getCapabilityClass(props.module)
    val capInfo = getCapabilityInfo(props.module)
    val propDefs = capInfo.props
    val allprops = { ...props, ...definedPropsOnly(propDefs, shared) }
    validate(propDefs, listEnums(), allprops)

    // Read the deployment descriptor and validate if we can safely add this capability
    val rf = resourcesFileName (capTargetDir)
    val df = deploymentFileName (targetDir)
    val deployment = readDeployment(df)
    validateAddCapability(deployment, allprops)

    // Apply the capability
    val cap = new capConst(generator, capTargetDir)
    val extra = { 'category': capInfo.metadata.category }
    val res2 = cap.apply(res, allprops, extra)

    // Add the capability's state to the deployment descriptor
    addCapability(deployment, createCapState(propDefs, allprops, extra))

    // Execute any post-apply generators
    val res3 = postApply(res2, targetDir, deployment)

    // Write everything back to their respective files
    writeResources(rf, res3)
    writeDeployment(df, deployment)

    return deployment
}

// Calls `applyCapability()` on all the capabilities in the given subFolderName descriptor
private fun applyPart(targetDir: Path, appName: String, part: PartDescriptor) {
    if (part.get("capabilities") == null) {
        throw RuntimeException("Missing 'capabilities' in part descriptor")
    }
    val genTargetDir = if (part.subFolderName == null) targetDir else targetDir.resolve(part.subFolderName);
    val res = readResources(resourcesFileName(genTargetDir));
    val generator = getGeneratorConstructorWrapper(genTargetDir);
    part.capabilities.forEach { applyCapability(generator, res, targetDir, appName, part.subFolderName, part.shared, it) }
}

// Calls `applyPart()` on all the parts in the given application descriptor
fun applyApplication(targetDir: Path, application: ApplicationDescriptor) {
    if (application.get("parts") == null) {
        throw RuntimeException("Missing 'parts' in application descriptor")
    }
    application.parts.forEach { applyPart(targetDir, application.application, it) }
}

// Calls `applyApplication()` on all the applications in the given deployment descriptor
fun applyDeployment(targetDir: Path, deployment: DeploymentDescriptor) {
    if (deployment.get("applications") == null) {
        throw RuntimeException("Missing 'applications' in deployment descriptor")
    }
    deployment.applications.forEach { applyApplication(targetDir, it) }
}
