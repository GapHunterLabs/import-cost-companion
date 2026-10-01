package dev.gaphunter.importcostcompanion.report

import dev.gaphunter.importcostcompanion.model.ImportSizeEntry
import java.util.Locale

object ImportSizeReportWriter {

    const val LARGE_THRESHOLD_BYTES = 500_000L

    /**
     * One line per PACKAGE, not per import: before 0.1.1 `lodash` and `lodash/debounce` were two lines with the same
     * size (the whole package, counted twice). Node.js built-ins go in their own line at the end instead of being
     * reported as "not found locally" (the demo's own report listed `fs` that way, found 2026-10-01).
     */
    fun render(entries: List<ImportSizeEntry>): String = buildString {
        appendLine("# Import Cost Report")
        appendLine()
        val packages = entries.filter { !it.builtin }.groupBy { it.packageName }
        val builtins = entries.filter { it.builtin }.map { it.importPath }.distinct()
        if (packages.isEmpty()) {
            appendLine("_No `node_modules` imports found in this file._")
        }
        val sorted = packages.entries.sortedByDescending { (_, imports) -> imports.first().sizeBytes ?: -1L }
        for ((packageName, imports) in sorted) {
            val size = imports.first().sizeBytes
            val sizeText = size?.let { formatSize(it) } ?: "not found locally"
            val flag = if ((size ?: 0L) > LARGE_THRESHOLD_BYTES) " -- ⚠️ large" else ""
            val paths = imports.map { it.importPath }.distinct()
            val via = if (paths == listOf(packageName)) "" else " (imported as ${paths.joinToString(", ") { "`$it`" }})"
            appendLine("- `$packageName`$via: $sizeText$flag")
        }
        if (builtins.isNotEmpty()) {
            if (packages.isNotEmpty()) appendLine()
            appendLine("Node.js built-in modules (no package to measure): ${builtins.joinToString(", ") { "`$it`" }}")
        }
    }

    /** Packages counted in a report (built-ins excluded): what the notification calls "checked". */
    fun packageCount(entries: List<ImportSizeEntry>): Int = entries.filter { !it.builtin }.map { it.packageName }.distinct().size

    // Locale.ROOT explicitly -- a locale that uses ',' as the decimal
    // separator would otherwise render "1,5 MB", inconsistent with the
    // rest of this English-labeled report.
    fun formatSize(bytes: Long): String = when {
        bytes >= 1_000_000 -> String.format(Locale.ROOT, "%.1f MB", bytes / 1_000_000.0)
        bytes >= 1_000 -> String.format(Locale.ROOT, "%.1f KB", bytes / 1_000.0)
        else -> "$bytes B"
    }
}
