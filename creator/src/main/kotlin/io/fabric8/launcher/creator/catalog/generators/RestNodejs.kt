package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.insertAfter
import java.nio.charset.Charset
import java.nio.file.Files

class RestNodejsProps(_map: Properties = LinkedHashMap()) : PlatformNodejsProps(_map) {
}

class RestNodejsExtra(_map: Properties = LinkedHashMap()) : PlatformNodejsExtra(_map) {
}

class RestNodejs : BaseGenerator() {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val rnprops = RestNodejsProps(props)
        // Check if the generator was already applied, so we don't do it twice
        if (!filesCopied()) {
            // First copy the files from the base nodejs platform module
            // and then copy our own over that
            val pprops = propsOfNN(
                    "application" to rnprops.application,
                    "subFolderName" to rnprops.subFolderName,
                    "serviceName" to rnprops.serviceName,
                    "routeName" to rnprops.routeName,
                    "nodejs" to rnprops.nodejs
            )

            generator(::PlatformNodejs).apply(resources, pprops, extra);
            val mergeFile = sourceDir.resolve("merge/app.merge.js");
            transform("app.js",
                    insertAfter("//TODO: Add routes", Files.readAllLines(mergeFile, Charset.forName("utf8"))));
            copy();
        }
        extra["sourceMapping"] = propsOf("greetingEndpoint" to join(rnprops.subFolderName, "/greeting.js"))
        return resources
    }
}
