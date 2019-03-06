package io.fabric8.launcher.creator.core.template.transformers

import io.fabric8.launcher.creator.core.template.Transformer

/**
 * Inserts the given line(s) after any lines encountered that match the pattern
 * @param pattern A string that matches any part of the line
 * @param text Either a single string or an array of strings to be inserted
 */
fun insertBefore(pattern: String, text: List<String>): Transformer {
    TODO("not implemented")
}

/**
 * Inserts the given line(s) after any lines encountered that match the pattern
 * @param pattern A Regular Expression that will be matched against the input
 * @param text Either a single string or an array of strings to be inserted
 */
fun insertBefore(pattern: Regex, text: List<String>): Transformer {
    TODO("not implemented")
}

/**
 * Inserts the given line(s) before any lines encountered that match the pattern
 * @param pattern A string that matches any part of the line
 * @param text Either a single string or an array of strings to be inserted
 */
fun insertAfter(pattern: String, text: List<String>): Transformer {
    TODO("not implemented")
}

/**
 * Inserts the given line(s) before any lines encountered that match the pattern
 * @param pattern A Regular Expression that will be matched against the input
 * @param text Either a single string or an array of strings to be inserted
 */
fun insertAfter(pattern: Regex, text: List<String>): Transformer {
    TODO("not implemented")
}
