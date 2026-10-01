package dev.gaphunter.importcostcompanion.model

/**
 * One import of the checked file. [sizeBytes] is null when the package genuinely couldn't be found on disk -- an
 * honest "not found" note, never a fake 0. [builtin] marks a Node.js built-in module (`fs`, `node:path`) that isn't
 * in `node_modules` either: it has no package to measure, so it isn't reported as "not found".
 */
data class ImportSizeEntry(
    val importPath: String,
    val packageName: String,
    val sizeBytes: Long?,
    val builtin: Boolean = false,
)
