package io.fabric8.launcher.creator.core.template

import io.fabric8.launcher.creator.core.writeLines
import java.io.BufferedWriter
import java.io.File
import java.io.Writer
import java.nio.file.*

typealias Transformer = (Sequence<String>) -> Sequence<String>

fun transform(inFile: Path, outFile: Path, transform: Transformer): Path {
    val actualOutFile = if (Files.isSameFile(outFile, inFile)) File.createTempFile("transform", "tmp").toPath() else outFile
    Files.newBufferedWriter(actualOutFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { out ->
        inFile.toFile().useLines { lines ->
            transform(lines).writeLines(out)
        }
    }
    if (!Files.isSameFile(outFile, actualOutFile)) {
        Files.setPosixFilePermissions(actualOutFile, Files.getPosixFilePermissions(outFile))
        Files.move(actualOutFile, outFile, StandardCopyOption.REPLACE_EXISTING)
    }
    return outFile
}

fun transformFiles(dir: Path, pattern: String, transformer: Transformer): Int {
    var result = 0
    val matcher = FileSystems.getDefault().getPathMatcher("glob:$pattern")
    Files.walk(dir).forEach {
        val rel = dir.relativize(it)
        if (matcher.matches(rel)) {
            transform(it, it, transformer)
            result++
        }
    }
    return result
}

fun transformFiles(dir: Path, patterns: List<String>, transformer: Transformer): Int {
    return patterns.fold(0) { acc: Int, cur: String ->
        acc + transformFiles(dir, cur, transformer)
    }
}
