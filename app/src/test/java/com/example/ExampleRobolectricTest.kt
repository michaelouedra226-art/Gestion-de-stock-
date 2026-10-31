package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("StockFlow", appName)
  }

  @get:org.junit.Rule
  val composeTestRule = androidx.compose.ui.test.junit4.createComposeRule()

  @Test
  fun `render OnboardingScreen and navigate wizard`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    StockFlowApplication.ensureFirebaseInitialized(context)
    val repo = com.example.data.repository.StockFlowRepository(context)
    composeTestRule.setContent {
      com.example.ui.theme.StockFlowTheme {
        com.example.ui.screens.OnboardingScreen(
          repository = repo,
          onCompleted = {}
        )
      }
    }
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("onboarding_company_name_input")).assertExists()
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("onboarding_company_name_input")).performTextInput("Mon Entreprise")
    composeTestRule.waitForIdle()
    
    // Step 1 -> Step 2
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("onboarding_next_button")).performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.mainClock.advanceTimeBy(600)
    composeTestRule.waitForIdle()

    // Step 2 should display palette picker and prev button
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("onboarding_prev_button")).performScrollTo().assertExists()
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("palette_chip_A78BFA")).performScrollTo().assertExists()
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("palette_chip_A78BFA")).performClick()

    // Step 2 -> Step 3
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("onboarding_next_button")).performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.mainClock.advanceTimeBy(600)
    composeTestRule.waitForIdle()

    // Step 3 should display optional category input
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("onboarding_category_input")).performScrollTo().assertExists()
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("onboarding_category_input")).performTextInput("Matériaux")
    composeTestRule.waitForIdle()

    // Step 3 -> Step 2 (test previous button)
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("onboarding_prev_button")).performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.mainClock.advanceTimeBy(600)
    composeTestRule.waitForIdle()
    composeTestRule.onNode(androidx.compose.ui.test.hasTestTag("palette_chip_A78BFA")).performScrollTo().assertExists()
  }
}
