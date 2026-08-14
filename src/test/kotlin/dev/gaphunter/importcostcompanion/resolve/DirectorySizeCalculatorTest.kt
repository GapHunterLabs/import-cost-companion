package dev.gaphunter.importcostcompanion.resolve

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class DirectorySizeCalculatorTest : BasePlatformTestCase() {

    fun testSumsFileSizesRecursively() {
        myFixture.addFileToProject("node_modules/pkg/index.js", "1234567890") // 10 bytes
        myFixture.addFileToProject("node_modules/pkg/lib/helper.js", "12345") // 5 bytes
        val pkgDir = myFixture.findFileInTempDir("node_modules/pkg")!!

        assertEquals(15L, DirectorySizeCalculator.totalSize(pkgDir))
    }

    fun testEmptyDirectoryHasZeroSize() {
        myFixture.tempDirFixture.findOrCreateDir("node_modules/empty-pkg")
        val pkgDir = myFixture.findFileInTempDir("node_modules/empty-pkg")!!

        assertEquals(0L, DirectorySizeCalculator.totalSize(pkgDir))
    }
}
