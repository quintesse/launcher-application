package io.fabric8.launcher.creator.core.template.transformers

import io.fabric8.launcher.creator.core.template.Transformer

/**
 * Replaces any lines encountered that match the pattern with the given line(s)
 * @param pattern A string that matches any part of the line
 * @param text Either a single string or an array of strings to be inserted
 */
fun replace(pattern: String, text: List<String>): Transformer {
    TODO("not implemented")
}

/**
 * Replaces any lines encountered that match the pattern with the given line(s)
 * @param pattern A Regular Expression that will be matched against the input
 * @param text Either a single string or an array of strings to be inserted
 */
fun replace(pattern: Regex, text: List<String>): Transformer {
    TODO("not implemented")
}
