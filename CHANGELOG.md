<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Import Cost Companion Changelog

## [Unreleased]

## [0.1.1]

### Fixed

- A package imported through subpaths (`lodash` and `lodash/debounce`)
  was listed once per import, each with the whole package's size. The
  report now has one line per package, naming its imports.
- Node.js built-in modules (`fs`, `node:path`) were reported as "not
  found locally". They are listed apart as built-ins, unless a package
  of that name is installed (browser polyfills such as `buffer`).
- Dynamic `import('x')` was not detected.

## [0.1.0]

### Added

- **Check Import Sizes**: on-demand report of every `node_modules`
  package a JS/TS file imports, sorted by real on-disk size, large
  ones flagged (`import-cost-report.md`).
- Deliberately never runs automatically -- the direct fix for the
  cited competitor's real CPU-spike complaints, which trace back to
  continuous, inline, on-every-keystroke recomputation.
- Regex-based import detection, no JS/TS language plugin dependency.

[Unreleased]: https://github.com/GapHunterLabs/import-cost-companion/compare/0.1.1...HEAD
[0.1.1]: https://github.com/GapHunterLabs/import-cost-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/import-cost-companion/commits/0.1.0
