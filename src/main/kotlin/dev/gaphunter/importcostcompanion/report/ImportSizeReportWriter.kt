package dev.gaphunter.importcostcompanion.report

import dev.gaphunter.importcostcompanion.model.ImportSizeEntry
import java.util.Locale

object ImportSizeReportWriter {

    const val LARGE_THRESHOLD_BYTES = 500_000L

    fun render(entries: List<ImportSizeEntry>): String = buildString {
        appendLine("# Import Cost Report")
        appendLine()
        if (entries.isEmpty()) {
            appendLine("_No `node_modules` imports found in this file._")
            return@buildString
        }
        val sorted = entries.sortedByDescending { it.sizeBytes ?: -1L }
        for (entry in sorted) {
            val sizeText = entry.sizeBytes?.let { formatSize(it) } ?: "not found locally"
            val flag = if ((entry.sizeBytes ?: 0L) > LARGE_THRESHOLD_BYTES) " -- ⚠️ large" else ""
            appendLine("- `${entry.importPath}`: $sizeText$flag")
        }
    }

    // Locale.ROOT explicitly -- a locale that uses ',' as the decimal
    // separator would otherwise render "1,5 MB", inconsistent with the
    // rest of this English-labeled report.
    fun formatSize(bytes: Long): String = when {
        bytes >= 1_000_000 -> String.format(Locale.ROOT, "%.1f MB", bytes / 1_000_000.0)
        bytes >= 1_000 -> String.format(Locale.ROOT, "%.1f KB", bytes / 1_000.0)
        else -> "$bytes B"
    }
}
