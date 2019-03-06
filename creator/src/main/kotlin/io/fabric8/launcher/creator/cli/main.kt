package io.fabric8.launcher.creator.cli

import com.github.ajalt.clikt.core.NoRunCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.subcommands

class Creator : NoRunCliktCommand() {
    init {
        context { allowInterspersedArgs = false }
    }
}

fun main(args: Array<String>) = Creator()
        .subcommands(Apply(), Generate())
        .main(args)
