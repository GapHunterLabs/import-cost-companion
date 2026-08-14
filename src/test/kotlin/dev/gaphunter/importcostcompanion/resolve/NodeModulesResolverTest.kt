package dev.gaphunter.importcostcompanion.resolve

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class NodeModulesResolverTest : BasePlatformTestCase() {

    fun testFindsAPlainPackageInTheNearestNodeModules() {
        myFixture.addFileToProject("node_modules/lodash/index.js", "module.exports = {};")
        val sourceFile = myFixture.addFileToProject("src/app.js", "// app").virtualFile

        val found = NodeModulesResolver.findPackageDir(sourceFile, "lodash")

        assertNotNull(found)
        assertEquals("lodash", found!!.name)
    }

    fun testFindsAScopedPackage() {
        myFixture.addFileToProject("node_modules/@babel/core/index.js", "module.exports = {};")
        val sourceFile = myFixture.addFileToProject("src/app.js", "// app").virtualFile

        val found = NodeModulesResolver.findPackageDir(sourceFile, "@babel/core")

        assertNotNull(found)
        assertEquals("core", found!!.name)
    }

    fun testReturnsNullForAPackageThatIsNotInstalled() {
        myFixture.addFileToProject("node_modules/lodash/index.js", "module.exports = {};")
        val sourceFile = myFixture.addFileToProject("src/app.js", "// app").virtualFile

        assertNull(NodeModulesResolver.findPackageDir(sourceFile, "not-installed"))
    }

    fun testSearchesFromTheFilesOwnDirectoryUpward() {
        myFixture.addFileToProject("node_modules/lodash/index.js", "module.exports = {};")
        val deepSourceFile = myFixture.addFileToProject("src/deeply/nested/app.js", "// app").virtualFile

        val found = NodeModulesResolver.findPackageDir(deepSourceFile, "lodash")

        assertNotNull(found)
    }
}
