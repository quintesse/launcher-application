package io.fabric8.launcher.creator.core

import org.yaml.snakeyaml.Yaml
import java.io.BufferedWriter
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

fun join(vararg parts: String?) : String {
    return parts.filter { it != null }.joinToString("/")
}

fun streamFromPath(path: Path): InputStream {
    return if (path.isAbsolute) {
        path.toFile().inputStream()
    } else {
        ::streamFromPath.javaClass.classLoader.getResourceAsStream(path.toString())
            ?: throw FileNotFoundException("Couldn't open stream to '$path'")
    }
}

fun existsFromPath(path: Path): Boolean {
    return if (path.isAbsolute) {
        Files.isRegularFile(path)
    } else {
        val url = ::streamFromPath.javaClass.classLoader.getResource(path.toString())
        url != null
    }
}

fun resolveClassPath(path: Path): Path {
    return if (path.isAbsolute || Files.exists(path)) {
        path
    } else {
        val url = ::streamFromPath.javaClass.classLoader.getResource(path.toString())
        if (url != null) {
            File(url.toURI()).toPath()
        } else {
            throw FileNotFoundException("Couldn't open stream to '$path'")
        }
    }
}

fun <T> ((T) -> T).compose(func: (T) -> T): (T) -> T = { t: T -> invoke(func.invoke(t)) }

fun <T> compose(vararg funcs: (T) -> T): (T) -> T {
    return funcs.reversedArray().reduce { acc, cur ->
        cur.compose(acc)
    }
}

fun Sequence<String>.writeLines(out: BufferedWriter) {
    forEach { line ->
        out.write(line)
        out.newLine()
    }
}
