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

    // Regression (2026-10-01): the demo's own report listed `lodash` and
    // `lodash/debounce` as two lines with the same size, and `fs` as
    // "not found locally".
    fun testSubpathImportsOfOnePackageAreOneLine() {
        val report = ImportSizeReportWriter.render(
            listOf(
                ImportSizeEntry("lodash", "lodash", 12_000),
                ImportSizeEntry("lodash/debounce", "lodash", 12_000),
            ),
        )
        assertEquals(1, report.lines().count { it.startsWith("- ") })
        assertTrue(report, report.contains("- `lodash` (imported as `lodash`, `lodash/debounce`): 12.0 KB"))
        assertEquals(1, ImportSizeReportWriter.packageCount(listOf(ImportSizeEntry("lodash", "lodash", 1), ImportSizeEntry("lodash/fp", "lodash", 1))))
    }

    fun testNodeBuiltinsAreListedApartNotAsNotFound() {
        val report = ImportSizeReportWriter.render(
            listOf(
                ImportSizeEntry("moment", "moment", 35_000),
                ImportSizeEntry("fs", "fs", null, builtin = true),
                ImportSizeEntry("node:path", "node:path", null, builtin = true),
            ),
        )
        assertFalse(report, report.contains("not found locally"))
        assertTrue(report, report.contains("Node.js built-in modules (no package to measure): `fs`, `node:path`"))
        assertEquals(1, ImportSizeReportWriter.packageCount(listOf(ImportSizeEntry("fs", "fs", null, builtin = true), ImportSizeEntry("moment", "moment", 1))))
    }
}
