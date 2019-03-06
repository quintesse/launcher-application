package io.fabric8.launcher.creator.core.catalog

import io.fabric8.launcher.creator.core.BaseProperties
import io.fabric8.launcher.creator.core.Properties

open class EnabledWhen(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val propId: String by _map
    val equals: List<Any> by _map

open class PropertyDef(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val id: String by _map
    val name: String by _map
    val description: String? by _map
    val type: String? by _map
    val required: Boolean? by _map
    val default: Any? by _map
    val shared: Boolean? by _map
    val enabledWhen: EnabledWhen by _map
}

class EnumPropertyDef(_map: Properties = LinkedHashMap()) : PropertyDef(_map) {
    val enumRef: String? by _map
    val values_: List<Any>?
        get() = get("values") as List<Any>?
}

class PropertiesDef(_map: Properties = LinkedHashMap()) : BaseProperties(_map) {
    val props: List<PropertyDef> by _map
}

class ObjectPropertyDef(_map: Properties = LinkedHashMap()) : PropertyDef(_map) {
    val props: List<PropertyDef> by _map
}

class InfoDef : PropertiesDef {
    type: String;
    name: String;
    description ?: String;
    metadata: {
        category: String;
        icon ?: String;
    };
}

interface ModuleInfoDef extends InfoDef {
    module: String;
}

class ValidationError(msg: String) : Exception(msg) {
}

class DefinitionError(msg: String) : Exception(msg) {
}

fun findProperty(pdef: PropertiesDef, path: String): PropertyDef {
    val elems = path.split ('.')
    val res = elems.reduce ((acc, cur) => {
        if (!!acc && !!acc.props) {
            return acc.props.find(p => p . id === cur) as ObjectPropertyDef;
        } else {
            return null;
        }
    }, pdef);
    return res as ObjectPropertyDef;
}

fun findPropertyValues(pdef: PropertiesDef, path: String, enums: Enums): List<Any> {
    val p = findProperty (pdef, path) as EnumPropertyDef;
    if (!!p && !!p.values) {
        return getValues(path, p, enums);
    } else {
        return [];
    }
}

fun findPropertyWithValue(pdef: PropertiesDef, path: String, value: Any, enums: Enums): EnumPropertyDef {
    val p = findProperty (pdef, path) as EnumPropertyDef;
    if (!!p && !!p.values) {
        val values = getValues (path, p, enums);
        if (values.includes(value)) {
            return p;
        }
    }
    return null;
}

fun validateRequired(id: String, def: PropertyDef, enums: Enums, props: Properties) {
    if (def.required === true) {
        if (!_.has(props, id)) {
            if (def.default) {
                _.set(props, id, def.default);
            } else {
                throw new ValidationError (`Missing property: '${id}'`);
            }
        }
    }
}

fun validateTypeEnum(id: String, def: EnumPropertyDef, enums: Enums, props: Properties) {
    val values = getValues(id, def, enums, props);
    if (_.has(props, id)) {
        val value = _.get(props, id);
        if (!values.some(v => v === value)) {
            throw new ValidationError (
                    `Invalid enumeration value for property '${id}': '${val}', should be one of: ${values}`);
        }
    }
}

fun getValues(id: String, def: EnumPropertyDef, enums: Enums, props: Properties?) : List<Any> {
    if (!!def.values) {
        if (!Array.isArray(def.values) || def.values.length === 0) {
            throw new DefinitionError (`Missing or invalid 'values' for property: '${id}'`);
        }
        return def.values;
    } else {
        val ref = replaceProps (def.enumRef || id, props);
        val values = enums [ref];
        if (!values) {
            if (!ref.includes('${') || !!props) {
                if (!!def.enumRef) {
                    throw new DefinitionError (`Invalid value '${ref}' as 'enumRef' for property: '${id}'`);
                } else {
                    throw new DefinitionError (`Missing 'values' or 'enumRef' for property: '${id}'`);
                }
            }
        }
        return values.map(v => v . id);
    }
}

private fun replaceProps(ref: String, props: Properties?) {
    val re = new RegExp('\\${([a-zA-Z0-9-.]+)}', 'g');
    return ref.replace(re, (match, id) => _.get(props, id));
}

private fun validateType(id: String, def: PropertyDef, enums: Enums, props: Properties) {
    if (def.type === 'enum')
    {
        validateTypeEnum(id, def as EnumPropertyDef, enums, props);
    } else if (def.type === 'string' || !def.type)
    {
        // Nothing to validate here
    } else
    {
        throw new DefinitionError (`Unknown type '${def.type}' for property: '${id}'`);
    }
}

private fun validateProperty(id: String, def: PropertyDef, enums: Enums, props: Properties) {
    validateRequired(id, def, enums, props);
    validateType(id, def, enums, props);
}

fun validatePossibleObject(id: String, def: PropertyDef, enums: Enums, props: Properties) {
    if (isEnabled(id, def, props)) {
        if (def.type === 'object') {
            val objdef = def as ObjectPropertyDef;
            objdef.props.forEach(def2 => validatePossibleObject (id + '.' + def2.id, def2, enums, props));
        } else {
            validateProperty(id, def, enums, props);
        }
    }
}

private fun isEnabled(id: String, def: PropertyDef, props: Properties) {
    if (def.hasOwnProperty('enabledWhen')) {
        val fld = def . enabledWhen . propId;
        if (!fld) {
            throw new DefinitionError (`Missing 'enabledWhen.propId' for property: '${id}'`);
        }
        val eq = def . enabledWhen . equals;
        if (!eq || !Array.isArray(eq) || eq.length === 0) {
            throw new DefinitionError (`Missing 'enabledWhen.propId' for property: '${id}'`);
        }
        val value = _ . get (props, fld);
        return eq.includes(value);
    } else {
        return true;
    }
}

fun validate(defs: List<PropertyDef>, enums: Enums, props: Properties) {
    if (defs) {
        defs.forEach(def => validatePossibleObject (def.id, def, enums, props));
    }
}

private fun printRequired(id: String, def: PropertyDef) {
    if (def.required === false) {
        process.stdout.write(`(optional) `);
    }
}

private fun printEnumType(id: String, def: EnumPropertyDef, enums: Enums) {
    val values = getValues (id, def, enums);
    process.stdout.write(def.description || def.name);
    process.stdout.write(`. Should be one of: ${values}`);
}

private fun printType(id: String, def: PropertyDef, enums: Enums) {
    if (def.type === 'enum') {
        printEnumType(id, def, enums);
    } else {
        process.stdout.write(def.description || def.name);
    }
}

private fun printProperty(id: String, def: PropertyDef, enums: Enums, indent: number, namePad: number) {
    process.stdout.write(`${' '.repeat(indent)}${id.padEnd(namePad)} - `);
    printRequired(id, def);
    printType(id, def, enums);
    process.stdout.write(`\n`);
}

private fun printPossibleObject(id: String, def: PropertyDef, enums: Enums, indent: Int, namePad: Int) {
    printProperty(id, def, enums, indent, namePad);
    if (def.type === 'object') {
        val objdef = def as ObjectPropertyDef;
        val maxLen = Math . max (13, Math.min(20, _.max(objdef.props.map(def2 => def2.id.length))));
        objdef.props.forEach(def2 => printPossibleObject (def2.id, def2, enums, indent+3, maxLen));
    }
}

fun printUsage(defs: List<PropertyDef>, enums: Enums) {
    if (defs) {
        val maxLen = Math . max (13, Math.min(20, _.max(defs.map(def => def.id.length))));
        defs.forEach(def => printPossibleObject (def.id, def, enums, 8, maxLen));
    }
}
