package io.fabric8.launcher.creator.core.template.transformers

import io.fabric8.launcher.creator.core.Properties
import io.fabric8.launcher.creator.core.template.Transformer

//
// Transformer that can filter special if-structures from a file
// determining whether to include or exclude those blocks of text
// depending on certain conditions.
// It does this by looking for lines that start with a line comment
// and a special token (by default "//$$" and then determines what
// to do with the following lines. Possible options are:
//
// {{if .keyName==value}}
//   If the property "keyName" has the value "value" in the given property
//   map then all following lines until the end of the block will be included
//   otherwise they will be dropped. All lines will have the first line
//   comments stripped. The block lasts until the next special token.
// {{else if .keyName==value}}
//   Just like in programming language the if will be tested if the previous
//   if-structure evaluated to false. The block will be included in the
//   output only when the if evaluates to true.
// {{else}}
//   Similarly an else-structure will be included in the output if all
//   previous if-structures evaluated to false.
// {{end}}
//   Signals the end of an if-block
// {{.keyName}}
//   Is replaced with the value of the property with the given name
//
// Example:
//
// function connect(host) {
// //{{if database==postgresql}}
//     return ConnectionManager.connect("jdbc:postgresql" + host);
// //{{else if database==mysql}}
// //    return ConnectionManager.connect("jdbc:mysql" + host);
// //{{end}}
// }
//
fun cases(props: Properties, lineComment: String = "//"): Transformer {
    TODO("not implemented")
}
