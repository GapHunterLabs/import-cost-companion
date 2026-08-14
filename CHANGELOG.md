<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Import Cost Companion Changelog

## [Unreleased]

## [0.1.0]

### Added

- **Check Import Sizes**: on-demand report of every `node_modules`
  package a JS/TS file imports, sorted by real on-disk size, large
  ones flagged (`import-cost-report.md`).
- Deliberately never runs automatically -- the direct fix for the
  cited competitor's real CPU-spike complaints, which trace back to
  continuous, inline, on-every-keystroke recomputation.
- Regex-based import detection, no JS/TS language plugin dependency.

[Unreleased]: https://github.com/GapHunterLabs/import-cost-companion/compare/0.1.0...HEAD
[0.1.0]: https://github.com/GapHunterLabs/import-cost-companion/commits/0.1.0
