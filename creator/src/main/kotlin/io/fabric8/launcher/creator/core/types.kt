package io.fabric8.launcher.creator.core

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

abstract class BaseProperties(val _map: Properties = LinkedHashMap()) : Properties by _map {

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

open class Enumeration(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val id: String by _map
    val name: String by _map
    val description: String? by _map
    val icon: String? by _map
    open val metadata: Properties by _map

    companion object {
        fun build(_map: Properties = LinkedHashMap(), block: Enumeration.Builder.() -> kotlin.Unit = {}): Enumeration {
            val newobj = Builder(_map)
            block.invoke(newobj)
            return Enumeration(newobj._map)
        }

        fun list(vararg block: Enumeration.Builder.() -> kotlin.Unit): List<Enumeration> {
            return block.map { build(block = it) }
        }
    }

    open class Builder(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
        var id: String by _map
        var name: String by _map
        var description: String? by _map
        var icon: String? by _map
        var metadata: Properties by _map
    }
}

// Misc

class Runtime(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val name: String by _map
    val version: String? by _map

    companion object {
        fun build(_map: Properties = LinkedHashMap(), block: Runtime.Builder.() -> kotlin.Unit = {}): Runtime {
            val newobj = Builder(_map)
            block.invoke(newobj)
            return Runtime(newobj._map)
        }
        fun list(vararg block: Runtime.Builder.() -> kotlin.Unit): List<Runtime> {
            return block.map { build(block = it) }
        }
    }

    class Builder(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
        var name: String by _map
        var version: String? by _map
    }
}

fun toRuntime(arg: String?): Runtime? {
    if (arg != null) {
        val parts = arg.split('/', limit = 2);
        val rt = Runtime.build {
            name = parts[0]
            if(parts.size > 1) version = parts[1]
        }
        return rt
    } else {
        return null
    }
}

class MavenCoords(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val groupId: String by _map
    val artifactId: String by _map
    val version: String by _map

    companion object {
        fun build(_map: Properties = LinkedHashMap(), block: MavenCoords.Builder.() -> kotlin.Unit = {}): MavenCoords {
            val newobj = Builder(_map)
            block.invoke(newobj)
            return MavenCoords(newobj._map)
        }

        fun list(vararg block: MavenCoords.Builder.() -> kotlin.Unit): List<MavenCoords> {
            return block.map { build(block = it) }
        }
    }

    class Builder(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
        var groupId: String by _map
        var artifactId: String by _map
        var version: String by _map
    }
}

class NodejsCoords(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val name: String by _map
    val version: String by _map

    companion object {
        fun build(_map: Properties = LinkedHashMap(), block: NodejsCoords.Builder.() -> kotlin.Unit = {}): NodejsCoords {
            val newobj = Builder(_map)
            block.invoke(newobj)
            return NodejsCoords(newobj._map)
        }

        fun list(vararg block: NodejsCoords.Builder.() -> kotlin.Unit): List<NodejsCoords> {
            return block.map { build(block = it) }
        }
    }

    class Builder(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
        var name: String by _map
        var version: String? by _map
    }
}
