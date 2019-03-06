package io.fabric8.launcher.creator.core.template

import java.nio.file.Path

typealias Transformer = (String) -> List<String>

fun transform(inFile: Path, outFile: Path, transformer: Transformer): Path {
    TODO("not implemented")
}

fun transformFiles(pattern: String, transformer: Transformer): Path {
    TODO("not implemented")
}

fun transformFiles(patterns: List<String>, transformer: Transformer): Path {
    TODO("not implemented")
}
