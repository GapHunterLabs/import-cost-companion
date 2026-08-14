package dev.gaphunter.importcostcompanion.report

import dev.gaphunter.importcostcompanion.model.ImportSizeEntry
import junit.framework.TestCase

class ImportSizeReportWriterTest : TestCase() {

    fun testSortsLargestFirst() {
        val entries = listOf(
            ImportSizeEntry("small", "small", 100L),
            ImportSizeEntry("big", "big", 900_000L),
        )
        val report = ImportSizeReportWriter.render(entries)

        assertTrue(report.indexOf("`big`") < report.indexOf("`small`"))
    }

    fun testFlagsEntriesOverTheThreshold() {
        val entries = listOf(ImportSizeEntry("huge", "huge", ImportSizeReportWriter.LARGE_THRESHOLD_BYTES + 1))
        val report = ImportSizeReportWriter.render(entries)

        assertTrue(report.contains("large"))
    }

    fun testNotFoundPackageGetsAnHonestNoteNotAFakeSize() {
        val entries = listOf(ImportSizeEntry("ghost-pkg", "ghost-pkg", null))
        val report = ImportSizeReportWriter.render(entries)

        assertTrue(report.contains("not found locally"))
    }

    fun testFormatsSizesInHumanReadableUnits() {
        assertEquals("500 B", ImportSizeReportWriter.formatSize(500))
        assertEquals("1.5 KB", ImportSizeReportWriter.formatSize(1500))
        assertEquals("2.0 MB", ImportSizeReportWriter.formatSize(2_000_000))
    }

    fun testEmptyListProducesAnHonestMessageNotABlankReport() {
        val report = ImportSizeReportWriter.render(emptyList())
        assertTrue(report.contains("No `node_modules` imports found"))
    }
}
