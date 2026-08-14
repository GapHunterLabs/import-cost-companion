package dev.gaphunter.importcostcompanion.actions

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.testFramework.TestActionEvent
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CheckImportSizesActionTest : BasePlatformTestCase() {

    fun testActionIsEnabledForARealFile() {
        val file = myFixture.addFileToProject("src/app.js", "import x from 'lodash';").virtualFile
        val dataContext = SimpleDataContext.builder()
            .add(CommonDataKeys.PROJECT, project)
            .add(CommonDataKeys.VIRTUAL_FILE, file)
            .build()
        val action = CheckImportSizesAction()
        val event = TestActionEvent.createTestEvent(action, dataContext)
        action.update(event)

        assertTrue(event.presentation.isEnabledAndVisible)
    }

    fun testActionIsDisabledWithNoFileInTheDataContext() {
        val dataContext = SimpleDataContext.builder().add(CommonDataKeys.PROJECT, project).build()
        val action = CheckImportSizesAction()
        val event = TestActionEvent.createTestEvent(action, dataContext)
        action.update(event)

        assertFalse(event.presentation.isEnabledAndVisible)
    }
}
