package io.fabric8.launcher.creator.catalog.generators

import io.fabric8.launcher.creator.core.*
import io.fabric8.launcher.creator.core.catalog.BaseGenerator
import io.fabric8.launcher.creator.core.catalog.CatalogItemContext
import io.fabric8.launcher.creator.core.resource.*
import io.fabric8.launcher.creator.core.template.transformers.insertAfter
import java.nio.charset.Charset
import java.nio.file.Files

class RestNodejs(ctx: CatalogItemContext) : BaseGenerator(ctx) {
    override fun apply(resources: Resources, props: Properties, extra: Properties): Resources {
        val pprops = PlatformNodejsProps.build(props)
        // Check if the generator was already applied, so we don't do it twice
        if (!filesCopied()) {
            // First copy the files from the base nodejs platform module
            // and then copy our own over that
            generator(::PlatformNodejs).apply(resources, pprops, extra);
            val mergeFile = sourceDir.resolve("merge/app.merge.js");
            transform("app.js",
                    insertAfter("//TODO: Add routes", Files.readAllLines(mergeFile, Charset.forName("utf8"))));
            copy();
        }
        extra["sourceMapping"] = propsOf("greetingEndpoint" to join(pprops.subFolderName, "/greeting.js"))
        return resources
    }
}
