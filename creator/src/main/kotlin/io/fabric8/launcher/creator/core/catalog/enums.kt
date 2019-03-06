package io.fabric8.launcher.creator.core.catalog

import io.fabric8.launcher.creator.core.Enumeration

fun listEnums(): Map<String, List<Enumeration>> {
    TODO("not implemented")
}

fun enumItem(enumId: String, itemId: String): Enumeration? {
    val items: List<Enumeration> = listEnums()[enumId] ?: listOf();
    return items.find { e -> e.id == itemId }
}

fun enumItemNN(enumId: String, itemId: String): Enumeration {
    val item = enumItem(enumId, itemId)
    if (item == null) {
        throw NoSuchElementException("Item ${itemId} not found in enumeration ${enumId}")
    }
    return item
}
