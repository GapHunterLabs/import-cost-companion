package dev.gaphunter.importcostcompanion.resolve

import com.intellij.openapi.vfs.VirtualFile

/** Walks UP from the importing file's own directory looking for the nearest `node_modules` that actually contains the requested package -- the same resolution order Node.js itself uses. */
object NodeModulesResolver {

    fun findPackageDir(startFile: VirtualFile, packageName: String): VirtualFile? {
        var dir: VirtualFile? = if (startFile.isDirectory) startFile else startFile.parent
        while (dir != null) {
            val nodeModules = dir.findChild("node_modules")
            if (nodeModules != null) {
                resolveWithinNodeModules(nodeModules, packageName)?.let { return it }
            }
            dir = dir.parent
        }
        return null
    }

    private fun resolveWithinNodeModules(nodeModules: VirtualFile, packageName: String): VirtualFile? {
        if (packageName.startsWith("@") && packageName.contains("/")) {
            val (scope, name) = packageName.split("/", limit = 2)
            return nodeModules.findChild(scope)?.findChild(name)
        }
        return nodeModules.findChild(packageName)
    }
}
