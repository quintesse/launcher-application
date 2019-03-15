package io.fabric8.launcher.creator.core.catalog

import io.fabric8.launcher.creator.core.*

data class EnabledWhen(
    val propId: String,
    val equals: List<Any>) {
}

interface PropertyDef {
    val id: String
    val name: String
    val description: String?
    val type: String?
    val required: Boolean?
    val default: Any?
    val shared: Boolean?
    val enabledWhen: EnabledWhen?

    data class Data(
            override val id: String,
            override val name: String,
            override val description: String?,
            override val type: String?,
            override val required: Boolean?,
            override val default: Any?,
            override val shared: Boolean?,
            override val enabledWhen: EnabledWhen?
    ) : PropertyDef
}

interface EnumPropertyDef : PropertyDef {
    val enumRef: String?
    val values: List<Any>?

    data class Data(
            override val id: String,
            override val name: String,
            override val description: String?,
            override val type: String?,
            override val required: Boolean?,
            override val default: Any?,
            override val shared: Boolean?,
            override val enabledWhen: EnabledWhen?,
            override val enumRef: String?,
            override val values: List<Any>?
    ) : EnumPropertyDef
}

interface PropertiesDef {
    val props: List<PropertyDef>
}

interface ObjectPropertyDef : PropertyDef, PropertiesDef {

    data class Data(
            override val id: String,
            override val name: String,
            override val description: String?,
            override val type: String?,
            override val required: Boolean?,
            override val default: Any?,
            override val shared: Boolean?,
            override val enabledWhen: EnabledWhen?,
            override val props: List<PropertyDef.Data>
    ) : ObjectPropertyDef
}

data class InfoMetadata(
        val category: String,
        val icon: String)

interface InfoDef : PropertiesDef {
    val type: String
    val name: String
    val description: String?
    val metadata: InfoMetadata

    data class Data(
            override val type: String,
            override val name: String,
            override val description: String?,
            override val metadata: InfoMetadata,
            override val props: List<PropertyDef.Data>
    ) : InfoDef
}

interface ModuleInfoDef : InfoDef {
    val module: String

    data class Data(
            override val module: String,
            override val type: String,
            override val name: String,
            override val description: String?,
            override val metadata: InfoMetadata,
            override val props: List<PropertyDef.Data>
    ) : ModuleInfoDef
}

class ValidationError(msg: String) : Exception(msg)

class DefinitionError(msg: String) : Exception(msg)

fun findProperty(pdef: PropertiesDef?, path: String): PropertyDef? {
    val elems = path.split ('.')
    val res = elems.fold(pdef) { acc, cur ->
        if (acc?.props != null) {
            acc.props.find { p -> p.id === cur } as PropertiesDef
        } else {
            null
        }
    }
    return res as PropertyDef?
}

fun findPropertyValues(pdef: PropertiesDef, path: String, enums: Enums): List<Any> {
    val p = findProperty (pdef, path)
    return if (p is EnumPropertyDef && p.values != null) {
        getValues(path, p, enums)
    } else {
        listOf()
    }
}

fun findPropertyWithValue(pdef: PropertiesDef, path: String, value: Any, enums: Enums): EnumPropertyDef? {
    val p = findProperty(pdef, path)
    if (p is EnumPropertyDef && p.values != null) {
        val values = getValues(path, p, enums)
        if (values.contains(value)) {
            return p
        }
    }
    return null
}

fun validateRequired(id: String, def: PropertyDef, enums: Enums, props: Properties) {
    val req = def.required ?: false
    if (req) {
        if (!props.pathExists(id)) {
            val default = def.default
            if (default != null) {
                props.pathPut(id, default)
            } else {
                throw ValidationError("Missing property: '$id'")
            }
        }
    }
}

fun validateTypeEnum(id: String, def: EnumPropertyDef, enums: Enums, props: Properties) {
    val values = getValues(id, def, enums, props)
    if (props.pathExists(id)) {
        val value = props.pathGet(id)
        if (!values.any { v -> v == value }) {
            throw ValidationError(
                    "Invalid enumeration value for property '$id': '$value', should be one of: $values")
        }
    }
}

fun getValues(id: String, def: EnumPropertyDef, enums: Enums, props: Properties = propsOf()) : List<Any> {
    val vs = def.values
    if (vs != null) {
        if (vs.isEmpty()) {
            throw DefinitionError("Missing 'values' for property: '$id'")
        }
        return vs
    } else {
        val ref = replaceProps (def.enumRef ?: id, props)
        val values = enums[ref]
        if (values == null) {
            if (!ref.contains("\${") || !props.isEmpty()) {
                if (def.enumRef != null) {
                    throw DefinitionError("Invalid value '$ref' as 'enumRef' for property: '$id'")
                } else {
                    throw DefinitionError("Missing 'values' or 'enumRef' for property: '$id'")
                }
            }
            return listOf()
        }
        return values.map { v -> v.id }
    }
}

private fun replaceProps(ref: String, props: Properties): String {
    val re = """\$\{([a-zA-Z0-9-.]+)}""".toRegex()
    return re.replace(ref) { props.pathGet(it.value, "") as String }
}

private fun validateType(id: String, def: PropertyDef, enums: Enums, props: Properties) {
    if (def.type == "enum") {
        validateTypeEnum(id, def as EnumPropertyDef, enums, props)
    } else if (def.type == "string" || def.type == null) {
        // Nothing to validate here
    } else {
        throw DefinitionError("Unknown type '${def.type}' for property: '$id")
    }
}

private fun validateProperty(id: String, def: PropertyDef, enums: Enums, props: Properties) {
    validateRequired(id, def, enums, props)
    validateType(id, def, enums, props)
}

fun validatePossibleObject(id: String, def: PropertyDef, enums: Enums, props: Properties) {
    if (isEnabled(id, def, props)) {
        if (def.type == "object") {
            val objdef = def as ObjectPropertyDef
            objdef.props.forEach { def2 -> validatePossibleObject (id + '.' + def2.id, def2, enums, props) }
        } else {
            validateProperty(id, def, enums, props)
        }
    }
}

private fun isEnabled(id: String, def: PropertyDef, props: Properties): Boolean {
    val ewhen = def.enabledWhen
    return if (ewhen != null) {
        val fld = ewhen.propId
        val eq = ewhen.equals
        val value = props.pathGet(fld)
        eq.contains(value)
    } else {
        true
    }
}

fun validate(defs: List<PropertyDef>, enums: Enums, props: Properties) {
    defs.forEach { def -> validatePossibleObject(def.id, def, enums, props) }
}

private fun printRequired(id: String, def: PropertyDef) {
    val req = def.required ?: false
    if (req) {
        System.out.print("(optional) ")
    }
}

private fun printEnumType(id: String, def: EnumPropertyDef, enums: Enums) {
    val values = getValues(id, def, enums)
    System.out.print(def.description ?: def.name)
    System.out.print(". Should be one of: $values")
}

private fun printType(id: String, def: PropertyDef, enums: Enums) {
    if (def.type == "enum") {
        printEnumType(id, def as EnumPropertyDef, enums)
    } else {
        System.out.print(def.description ?: def.name)
    }
}

private fun printProperty(id: String, def: PropertyDef, enums: Enums, indent: Int, namePad: Int) {
    System.out.print("${" ".repeat(indent)}${id.padEnd(namePad)} - ")
    printRequired(id, def)
    printType(id, def, enums)
    System.out.println()
}

private fun printPossibleObject(id: String, def: PropertyDef, enums: Enums, indent: Int, namePad: Int) {
    printProperty(id, def, enums, indent, namePad)
    if (def.type == "object") {
        val objdef = def as ObjectPropertyDef
        val maxLen = Math.max(13, Math.min(20, objdef.props.map { def2 -> def2.id.length }.max() ?: 0))
        objdef.props.forEach { def2 -> printPossibleObject (def2.id, def2, enums, indent+3, maxLen) }
    }
}

fun printUsage(defs: List<PropertyDef>, enums: Enums) {
    val maxLen = Math.max(13, Math.min(20, defs.map { def -> def.id.length }.max() ?: 0))
    defs.forEach { def -> printPossibleObject (def.id, def, enums, 8, maxLen) }
}
