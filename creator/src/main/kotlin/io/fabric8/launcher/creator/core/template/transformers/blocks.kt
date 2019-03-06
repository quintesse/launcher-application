package io.fabric8.launcher.creator.core.template.transformers

import io.fabric8.launcher.creator.core.template.Transformer

typealias BlockTransformer = (List<String>) -> List<String>

/**
 * Looks for blocks of text starting with a line that matches the start
 * pattern and ends with a line that matches the end pattern. It then
 * passes that block to a filter and replaces the entire block with
 * the result returned by the filter function.
 * @param startPattern
 * @param endPattern
 * @param filter
 */
fun blocks(startPattern: String,
           endPattern: String,
           filter: BlockTransformer): Transformer {
    TODO("not implemented")
}

/**
 * Looks for blocks of text starting with a line that matches the start
 * pattern and ends with a line that matches the end pattern. It then
 * passes that block to a filter and replaces the entire block with
 * the result returned by the filter function.
 * @param startPattern
 * @param endPattern
 * @param filter
 */
fun blocks(startPattern: Regex,
           endPattern: Regex,
           filter: BlockTransformer): Transformer {
    TODO("not implemented")
}

/**
 * No-op blocks filter, just returns its input unchanged
 */
fun bid(): BlockTransformer {
    return { block: List<String> -> block }
}

/**
 * Blocks filter that will insert the given lines at the start of any code block.
 * The filter will take into account that all lines in the block must be separated
 * by commas. The last line in a block will never have a comma added.
 * @param lines The lines to insert
 */
fun insertAtStart(lines: List<String>): BlockTransformer {
    TODO("not implemented")
}

/**
 * Blocks filter that will insert the given lines at the end of any code block.
 * The filter will take into account that all lines in the block must be separated
 * by commas. The last line in a block will never have a comma added.
 * @param lines The lines to insert
 */
fun insertAtEnd(lines: List<String>): BlockTransformer{
    TODO("not implemented")
}
