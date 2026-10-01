package dev.gaphunter.importcostcompanion.parse

/**
 * Finds import specifiers by scanning the file's own TEXT with a
 * regex, never by parsing a real JavaScript/TypeScript AST --
 * deliberate: it works without depending on a JS/TS language plugin
 * being installed, at the honest cost of missing genuinely exotic
 * import syntax (dynamic `import(...)` behind a runtime-computed
 * expression, for instance). Matches the common, real-world forms:
 * `import x from 'pkg'`, `import { a, b } from 'pkg'`,
 * `import * as x from 'pkg'`, `import 'pkg'` (side-effect only), and
 * `require('pkg')`.
 */
object ImportExtractor {

    // `import('x')` (dynamic import) too: before 0.1.1 only static imports and require() were seen
    private val IMPORT_PATTERN = Regex("""(?:\bfrom\s+|\brequire\s*\(\s*|\bimport\s*\(\s*|\bimport\s+)['"]([^'"]+)['"]""")

    /** Node.js built-in modules (also reachable with the `node:` prefix, and as subpaths like `fs/promises`). */
    private val NODE_BUILTINS = setOf(
        "assert", "async_hooks", "buffer", "child_process", "cluster", "console", "constants", "crypto", "dgram",
        "diagnostics_channel", "dns", "domain", "events", "fs", "http", "http2", "https", "inspector", "module", "net",
        "os", "path", "perf_hooks", "process", "punycode", "querystring", "readline", "repl", "stream",
        "string_decoder", "sys", "test", "timers", "tls", "trace_events", "tty", "url", "util", "v8", "vm", "wasi",
        "worker_threads", "zlib",
    )

    /**
     * True for a Node.js built-in module. A bare name like `buffer` or `events` is also a common npm polyfill for
     * browser bundles, so the caller only treats it as built-in when no such package exists in `node_modules`; a
     * `node:` import is always the built-in.
     */
    fun isNodeBuiltin(importPath: String): Boolean =
        importPath.startsWith("node:") || packageNameFor(importPath) in NODE_BUILTINS

    /** Raw import path strings, in file order, deduplicated. Relative (`./x`) and absolute (`/x`) imports are already excluded here -- only bare specifiers (resolvable from `node_modules`) are returned. */
    fun extractBareImportPaths(text: String): List<String> =
        IMPORT_PATTERN.findAll(text)
            .map { it.groupValues[1] }
            .filter { !it.startsWith(".") && !it.startsWith("/") }
            .distinct()
            .toList()

    /** The installable package name for an import path -- `"lodash/debounce"` -> `"lodash"`, `"@scope/pkg/sub"` -> `"@scope/pkg"`, `"lodash"` -> `"lodash"`. */
    fun packageNameFor(importPath: String): String {
        val segments = importPath.split("/")
        return if (importPath.startsWith("@") && segments.size >= 2) "${segments[0]}/${segments[1]}" else segments[0]
    }
}
