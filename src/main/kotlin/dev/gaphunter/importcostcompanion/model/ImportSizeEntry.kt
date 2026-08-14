package dev.gaphunter.importcostcompanion.model

/** [sizeBytes] is null when the package genuinely couldn't be found on disk -- an honest "not found" note, never a fake 0. */
data class ImportSizeEntry(
    val importPath: String,
    val packageName: String,
    val sizeBytes: Long?,
)
