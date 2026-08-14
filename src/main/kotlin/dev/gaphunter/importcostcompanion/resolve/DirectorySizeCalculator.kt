package dev.gaphunter.importcostcompanion.resolve

import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor

/**
 * Real on-disk file sizes, summed recursively -- never a bundler
 * invocation, never a network lookup against a size-estimation
 * service. This is the direct fix for the cited competitor's root
 * cause: reading file sizes off disk is cheap; running a bundler
 * synchronously and repeatedly is what actually caused the reported
 * CPU spikes.
 */
object DirectorySizeCalculator {

    fun totalSize(dir: VirtualFile): Long {
        var total = 0L
        VfsUtilCore.visitChildrenRecursively(dir, object : VirtualFileVisitor<Unit>() {
            override fun visitFile(file: VirtualFile): Boolean {
                if (!file.isDirectory) total += file.length
                return true
            }
        })
        return total
    }
}
