package com.openclaude.android.benchmark

import androidx.benchmark.macro.BaselineProfileScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates a baseline profile for the app.
 *
 * Run with:
 *   ./gradlew :app:generateBaselineProfile -P android.testInstrumentationRunnerArguments.class=com.openclaude.android.benchmark.BaselineProfileGenerator
 *
 * The generated profile will be placed in app/src/main/baseline-prof.txt
 * and will be used by ART to pre-compile critical paths for faster startup.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() = baselineProfileRule.collect(
        packageName = "com.openclaude.android",
        maxIterations = 5,
    ) {
        pressHome()
        startActivityAndWait()
        device.wait(Until.hasObject(By.text("Start a conversation")), 5_000)

        // Navigate through key user flows to capture critical paths
        // The profile will include these hot paths for AOT compilation
        device.waitForIdle()
    }
}
