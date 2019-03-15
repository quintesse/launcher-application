package io.fabric8.launcher.creator.core

fun Map<String, Any?>.pathExists(path: String): Boolean {
    val parts = path.split('.')
    val parents = parts.subList(0, parts.size - 1)
    val parent = parents.fold(this as Map<String, Any>?) { acc, s -> acc?.get(s) as Map<String, Any>? }
    return parent != null && parent.containsKey(parts.last())
}

fun Map<String, Any?>.pathGet(path: String): Any? {
    val parts = path.split('.')
    val parents = parts.subList(0, parts.size - 1)
    val parent = parents.fold(this as Map<String, Any>?) { acc, s -> acc?.get(s) as Map<String, Any>? }
    return parent?.get(parts.last())
}

fun Map<String, Any?>.pathGet(path: String, default: Any): Any {
    return this.pathGet(path) ?: default
}

fun MutableMap<String, Any?>.pathPut(path: String, value: Any): MutableMap<String, Any?> {
    val parts = path.split('.')
    val parents = parts.subList(0, parts.size - 1)
    val parent = parents.fold(this) { acc, s ->
        var p = acc[s] as MutableMap<String, Any?>?
        if (p == null) {
            p = mutableMapOf()
            acc[s] = p
        }
        p
    }
    parent[parts.last()] = value
    return this
}

// Properties

typealias Properties = MutableMap<String, Any?>

abstract class BasePropertiesX(val _map: Properties = LinkedHashMap()) : Properties by _map {

    protected inline fun <reified T> ensureObject(obj: Any, klazz: (Properties) -> T): T {
        if (obj is T) {
            return obj
        } else if (obj is Map<*, *>) {
            return klazz(obj as Properties)
        } else {
            throw RuntimeException("Unexpected property type")
        }
    }

    protected inline fun <reified T> ensureList(list: List<Any>, klazz: (Properties) -> T): List<T> {
        if (list.isEmpty() || list.all { it is T }) {
            return list as List<T>
        } else {
            return list.map { ensureObject(it, klazz) }.toMutableList()
        }
    }

    override fun toString() = _map.toString()
}

fun <T: MutableMap<*, *>> T.nonulls(recursive: Boolean = false): T {
    val nullValueKeys = this.keys.filter { get(it) == null }
    nullValueKeys.forEach { remove(it) }
    if (recursive) {
        entries.forEach {
            val v = it.value
            if (v is MutableMap<*, *>) {
                v.nonulls(true)
            } else if (v is MutableList<*>) {
                v.nonulls(true)
            }
        }
    }
    return this
}

fun <T : MutableList<*>> T.nonulls(recursive: Boolean = false): T {
    removeIf { it == null }
    if (recursive) {
        forEach {
            if (it is MutableMap<*, *>) {
                it.nonulls(true)
            } else if (it is MutableList<*>) {
                it.nonulls(true)
            }
        }
    }
    return this
}

fun propsOf(): Properties = mutableMapOf<String, Any?>()

fun propsOf(vararg pairs: Pair<String, Any?>): Properties = mutableMapOf(*pairs)

fun propsOfNN(vararg pairs: Pair<String, Any?>) : Properties {
    val m = mutableMapOf<String, Any?>()
    m.plusAssign(pairs.filter { it.second != null } as Iterable<out Pair<String, Any>>)
    return m
}

fun propsOf(map: Map<String, Any?>?, vararg pairs: Pair<String, Any?>): Properties {
    return mutableMapOf<String, Any?>().run {
        if (map != null) {
            putAll(map)
        }
        putAll(pairs)
        this
    }
}

// Environment

typealias Environment = MutableMap<String, String>

fun envOf() = mutableMapOf<String, String>()

fun envOf(vararg pairs: Pair<String, String>) = mutableMapOf<String, String>(*pairs)

fun envOf(map: Map<String, String>?, vararg pairs: Pair<String, String>): Environment {
    return mutableMapOf<String, String>().run {
        if (map != null) {
            putAll(map)
        }
        putAll(pairs)
        this
    }
}

// Enums

interface Enumeration {
    val id: String
    val name: String
    val description: String?
    val icon: String?
    val metadata: Properties?

    data class Data(
            override val id: String,
            override val name: String,
            override val description: String? = null,
            override val icon: String? = null,
            override val metadata: Properties? = null
    ) : Enumeration
}

typealias Enums = Map<String, List<Enumeration>>

// Misc

interface Runtime {
    val name: String
    val version: String?

    data class Data(
            override val name: String,
            override val version: String?
    ) : Runtime

}

fun toRuntime(arg: String?): Runtime? {
    if (arg != null) {
        val parts = arg.split('/', limit = 2);
        val rt = Runtime.Data(
            name = parts[0],
            version = if (parts.size > 1) parts[1] else null
        )
        return rt
    } else {
        return null
    }
}

interface MavenCoords {
    val groupId: String
    val artifactId: String
    val version: String

    data class Data(
            override val groupId: String,
            override val artifactId: String,
            override val version: String
    ) : MavenCoords
}

interface NodejsCoords {
    val name: String
    val version: String

    data class Data(
            override val name: String,
            override val version: String
    ) : NodejsCoords
}
