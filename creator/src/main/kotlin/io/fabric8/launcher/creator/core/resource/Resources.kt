package io.fabric8.launcher.creator.core.resource

import io.fabric8.launcher.creator.core.Properties

interface NamedProperties {
    var name: String?

    data class Data(
            override var name: String? = null
    ) : NamedProperties
}

interface Metadata : NamedProperties {
    var annotations: Properties?
    var labels: Properties?

    data class Data(
            override var name: String? = null,
            override var annotations: Properties? = null,
            override var labels: Properties? = null
    ) : Metadata
}

interface Parameter : NamedProperties {
    var description: String?
    var displayName: String?
    var value: Any?
    var required: Boolean?

    data class Data(
            override var name: String? = null,
            override var description: String? = null,
            override var displayName: String? = null,
            override var value: Any? = null,
            override var required: Boolean? = null
    ) : Parameter
}

interface Resource {
    var apiVersion: String?
    var kind: String?
    var metadata: Metadata?

    data class Data(
            override var apiVersion: String? = null,
            override var kind: String? = null,
            override var metadata: Metadata? = null
    ) : Resource
}

interface ListResource : Resource {
    var items: List<Resource>?

    data class Data(
            override var apiVersion: String? = null,
            override var kind: String? = null,
            override var metadata: Metadata? = null,
            override var items: List<Resource>? = null
    ) : ListResource
}

interface TemplateResource : Resource {
    var objects: List<Resource>?
    var parameters: List<Parameter>?

    data class Data(
            override var apiVersion: String? = null,
            override var kind: String? = null,
            override var metadata: Metadata? = null,
            override var objects: List<Resource>? = null,
            override var parameters: List<Parameter>? = null
    ) : TemplateResource
}

class Resources(private var res: Resource = Resource.Data()) {

    companion object {
        // Returns an array of all the resources found in the given object.
        // Will return the items of a List or the objects contained in a
        // Template. It will return an array of one if there's just a single
        // item that's neither a List nor a Template. Will return an empty
        // array if no resources were found at all. And finally if the argument
        // already is an array it will return that as-is.
        fun asList(res: Any): List<Resource> {
            if (res is Resources) {
                return Resources.asList(res.json)
            } else if (res is List<*>) {
                return res as List<Resource>
            } else if (res is Resource) {
                if (res.kind?.toLowerCase() == "list") {
                    return (res as ListResource).items ?: listOf()
                } else if (res.kind?.toLowerCase() == "template") {
                    return (res as TemplateResource).objects ?: listOf()
                } else if (res.kind != null) {
                    return listOf(res)
                } else {
                    return listOf()
                }
            } else {
                throw RuntimeException("Unsupported resource type '${res.javaClass.name}', should be resource or list")
            }
        }

        // Takes a list of resources and turns them into a "List"
        fun makeList(items: List<Resource>): Resource {
            return mapOf(
                "apiVersion" to "v1",
                "kind" to "List",
                "items" to items
            ) as Resource
        }

        // Takes an array of resources and turns them into a "Template"
        fun makeTemplate(objects: List<Resource>, params: List<Parameter>?): Resource {
            val ps = params ?: listOf<Parameter>()
            return mapOf(
                "apiVersion" to "v1",
                "kind" to "Template",
                "parameters" to ps,
                "objects" to objects
            ) as Resource
        }

        // Selects resources by their 'kind' property
        fun selectByKind(res: Any, kind: String): List<Resource> {
            return Resources.asList(res).filter { r -> r.kind?.toLowerCase() == kind.toLowerCase() }
        }

        // Selects resources by their 'metadata/name' property
        fun selectByName(res: Any, name: String): List<Resource> {
            return Resources.asList(res).filter { r -> r.metadata?.name == name }
        }

        // Selects the first resource that matches the given 'metadata/name' property
        fun findByName(res: Any, name: String): Resource? {
            return Resources.asList(res).find { r -> r.metadata?.name == name }
        }
    }
    
    // Returns an array of the separate resource items
    val items: List<Resource>
        get() = Resources.asList(res)

    // Returns true if no resources were found in the given argument
    val isEmpty: Boolean
        get() = items.isEmpty()

    // Returns the wrapped object
    val json: Resource
        get() {
            return res
        }

    // Returns the parameters (if any)
    val parameters: List<Parameter>
        get() {
            val r = res
            if (r is TemplateResource) {
                val ps = r.parameters
                if (ps != null) {
                    return ps
                }
            }
            return listOf<Parameter>()
        }

    // Finds a parameter by name
    fun parameter(name: String): Parameter? {
        return parameters.find { p -> p.name == name }
    }

    // Turns the current resources into a List
    fun toList(): Resources {
        if (res.kind?.toLowerCase() != "list") {
            res = Resources.makeList(items)
        }
        return this
    }

    // Turns the current resources into a Template
    fun toTemplate(params: List<Parameter>? = listOf()): Resources {
        if (res.kind?.toLowerCase() != "template") {
            res = Resources.makeTemplate(items, params)
        }
        return this
    }

    // Adds new resources from 'newres' to the wrapped object.
    // If the current wrapped object is a List the new resources will be added
    // to its items. If it's a Template they will be added to its objects. If
    // it's a single resource a List will be created containing that resource
    // plus all the new ones. If the current wrapped object is empty a new List
    // will be created if 'newres' has multiple resources or it will be set to
    // contain the single 'newres' item if there's only one.
    fun add(newres: Any): Resources {
        var params: List<Parameter>? = null
        val items = Resources.asList(newres)
        if (res.kind?.toLowerCase() == "list") {
            val lres = res as ListResource
            val newitems = mutableListOf<Resource>()
            val resitems = lres.items
            if (resitems != null) {
                newitems.addAll(resitems)
            }
            newitems.addAll(items)
            lres.items = items
        } else if (res.kind?.toLowerCase() == "template") {
            val tres = res as TemplateResource
            params = tres.parameters
            val newobjects = mutableListOf<Resource>()
            val resobjects = tres.objects
            if (resobjects != null) {
                newobjects.addAll(resobjects)
            }
            newobjects.addAll(items)
            tres.objects = newobjects
        } else if (res.kind != null) {
            val newitems = mutableListOf<Resource>(res)
            newitems.addAll(items)
            res = Resources.makeList(newitems)
        } else {
            if (items.size > 1) {
                res = Resources.makeList(items)
            } else if (items.size === 1) {
                res = items[0]
            }
        }

        // If there are any parameters merge them
        var resparams: List<Parameter>? = null
        if (newres is Resource && newres.kind == "template") {
            resparams =(newres as TemplateResource).parameters
        }
        if (params != null || resparams != null) {
            val newparams = mutableListOf<Parameter>()
            if (params != null) {
                newparams.addAll(params)
            }
            if (resparams != null) {
                newparams.addAll(resparams)
            }
            this.toTemplate()
            (res as TemplateResource).parameters = resparams
        }

        return this
    }

    // Adds the given parameter to the current list of parameters.
    // The resources will be turned into a Template first if necessary
    fun addParam(param: Parameter): Resources {
        val newparams = mutableListOf<Parameter>()
        newparams.addAll(toTemplate().parameters)
        newparams.add(param)
        (res as TemplateResource).parameters = newparams
        return this
    }

    val builds: List<Resource>
        get() {
            return Resources.selectByKind(res, "build")
        }

    fun build(name: String): Resource? {
        return Resources.findByName(builds, name)
    }

    val buildConfigs: List<Resource>
        get() {
            return Resources.selectByKind(res, "buildconfig")
        }

    fun buildConfig(name: String): Resource? {
        return Resources.findByName(buildConfigs, name)
    }

    val configMaps: List<Resource>
        get() {
            return Resources.selectByKind(res, "configmap")
        }

    fun configMap(name: String): Resource? {
        return Resources.findByName(configMaps, name)
    }

    val deployments: List<Resource>
        get() {
            return Resources.selectByKind(res, "deployment")
        }

    fun deployment(name: String): Resource? {
        return Resources.findByName(deployments, name)
    }

    val deploymentConfigs: List<Resource>
        get() {
            return Resources.selectByKind(res, "deploymentconfig")
        }

    fun deploymentConfig(name: String): Resource? {
        return Resources.findByName(deploymentConfigs, name)
    }

    val imageStreamImages: List<Resource>
        get() {
            return Resources.selectByKind(res, "imagestreamimage")
        }

    fun imageStreamImage(name: String): Resource? {
        return Resources.findByName(imageStreamImages, name)
    }

    val imageStreams: List<Resource>
        get() {
            return Resources.selectByKind(res, "imagestream")
        }

    fun imageStream(name: String): Resource? {
        return Resources.findByName(imageStreams, name)
    }

    val imageStreamTags: List<Resource>
        get() {
            return Resources.selectByKind(res, "imagestreamtag")
        }

    fun imageStreamTag(name: String): Resource? {
        return Resources.findByName(imageStreamTags, name)
    }

    val persistentVolumes: List<Resource>
        get() {
            return Resources.selectByKind(res, "persistentvolume")
        }

    fun persistentVolume(name: String): Resource? {
        return Resources.findByName(persistentVolumes, name)
    }

    val persistentVolumeClaims: List<Resource>
        get() {
            return Resources.selectByKind(res, "persistentvolumeclaim")
        }

    fun persistentVolumeClaim(name: String): Resource? {
        return Resources.findByName(persistentVolumeClaims, name)
    }

    val roles: List<Resource>
        get() {
            return Resources.selectByKind(res, "role")
        }

    fun role(name: String): Resource? {
        return Resources.findByName(roles, name)
    }

    val roleBindings: List<Resource>
        get() {
            return Resources.selectByKind(res, "rolebinding")
        }

    fun roleBinding(name: String): Resource? {
        return Resources.findByName(roleBindings, name)
    }

    val routes: List<Resource>
        get() {
            return Resources.selectByKind(res, "route")
        }

    fun route(name: String): Resource? {
        return Resources.findByName(routes, name)
    }

    val secrets: List<Resource>
        get() {
            return Resources.selectByKind(res, "secret")
        }

    fun secret(name: String): Resource? {
        return Resources.findByName(secrets, name)
    }

    val services: List<Resource>
        get() {
            return Resources.selectByKind(res, "service")
        }

    fun service(name: String): Resource? {
        return Resources.findByName(services, name)
    }
}

