package dev.gaphunter.importcostcompanion.parse

import junit.framework.TestCase

class ImportExtractorTest : TestCase() {

    fun testExtractsADefaultImport() {
        assertEquals(listOf("react"), ImportExtractor.extractBareImportPaths("import React from 'react';"))
    }

    fun testExtractsANamedImport() {
        assertEquals(listOf("lodash"), ImportExtractor.extractBareImportPaths("import { debounce } from 'lodash';"))
    }

    fun testExtractsAStarImport() {
        assertEquals(listOf("lodash"), ImportExtractor.extractBareImportPaths("import * as _ from 'lodash';"))
    }

    fun testExtractsASideEffectOnlyImport() {
        assertEquals(listOf("some-polyfill"), ImportExtractor.extractBareImportPaths("import 'some-polyfill';"))
    }

    fun testExtractsARequireCall() {
        assertEquals(listOf("express"), ImportExtractor.extractBareImportPaths("const express = require('express');"))
    }

    fun testExcludesRelativeImports() {
        assertEquals(emptyList<String>(), ImportExtractor.extractBareImportPaths("import { helper } from './utils';"))
    }

    fun testExcludesAbsoluteImports() {
        assertEquals(emptyList<String>(), ImportExtractor.extractBareImportPaths("import x from '/abs/path';"))
    }

    fun testDeduplicatesRepeatedImportsOfTheSamePackage() {
        val text = "import a from 'lodash';\nimport b from 'lodash/debounce';\n"
        assertEquals(listOf("lodash", "lodash/debounce"), ImportExtractor.extractBareImportPaths(text))
    }

    fun testPackageNameForAPlainImport() {
        assertEquals("lodash", ImportExtractor.packageNameFor("lodash"))
    }

    fun testPackageNameForADeepImport() {
        assertEquals("lodash", ImportExtractor.packageNameFor("lodash/debounce"))
    }

    fun testPackageNameForAScopedPackage() {
        assertEquals("@babel/core", ImportExtractor.packageNameFor("@babel/core"))
    }

    fun testPackageNameForADeepScopedImport() {
        assertEquals("@babel/core", ImportExtractor.packageNameFor("@babel/core/lib/something"))
    }

    // Regression (2026-10-01): dynamic imports were not seen at all.
    fun testDynamicImportIsExtracted() {
        val text = "const chart = await import('chart.js');\nconst x = import(\"lodash/debounce\");\n"
        assertEquals(listOf("chart.js", "lodash/debounce"), ImportExtractor.extractBareImportPaths(text))
    }

    fun testNodeBuiltinsAreRecognizedWithAndWithoutTheNodePrefix() {
        assertTrue(ImportExtractor.isNodeBuiltin("fs"))
        assertTrue(ImportExtractor.isNodeBuiltin("fs/promises"))
        assertTrue(ImportExtractor.isNodeBuiltin("node:crypto"))
        assertFalse(ImportExtractor.isNodeBuiltin("lodash"))
        assertFalse(ImportExtractor.isNodeBuiltin("@acme/fs"))
    }
}
