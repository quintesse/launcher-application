package io.fabric8.launcher.creator.core.maven

import io.fabric8.maven.Maven
import io.fabric8.maven.merge.SmartModelMerger
import org.apache.maven.model.Parent
import java.nio.file.Path
import java.nio.file.Paths

object maven {
    fun mergePoms(target: Path, vararg sources: Path) {
        val targetPath = target.toAbsolutePath()
        val targetModel = Maven.readModel(targetPath)
        val sourceDominant = java.lang.Boolean.getBoolean("sourceDominant")
        val merger = SmartModelMerger()

        for (source in sources) {
            val sourcePath = source.toAbsolutePath()
            val sourceModel = Maven.readModel(sourcePath)
            merger.merge(targetModel, sourceModel, sourceDominant, null)
        }
        Maven.writeModel(targetModel)
    }

    fun updateGAV(target: Path, groupId: String, artifactId: String, version: String) {
        val targetPath = target.toAbsolutePath()
        val targetModel = Maven.readModel(targetPath)
        targetModel.groupId = groupId
        targetModel.artifactId = artifactId
        targetModel.version = version
        Maven.writeModel(targetModel)
    }

    fun updateParentGAV(target: Path, groupId: String, artifactId: String) {
        val targetPath = target.toAbsolutePath()
        val targetModel = Maven.readModel(targetPath)
        var parent: Parent? = targetModel.parent
        if (parent == null) {
            parent = Parent()
            targetModel.parent = parent
        }
        parent.groupId = groupId
        parent.artifactId = artifactId
        Maven.writeModel(targetModel)
    }

    fun updateMetadata(target: Path, name: String, description: String) {
        val targetPath = target.toAbsolutePath()
        val targetModel = Maven.readModel(targetPath)
        targetModel.name = name
        targetModel.description = description
        Maven.writeModel(targetModel)
    }
}
