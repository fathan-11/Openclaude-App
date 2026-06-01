package com.openclaude.android.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Macro-benchmark tests for measuring startup and scroll performance.
 *
 * Run with:
 *   ./gradlew :app:connectedBenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.class=com.openclaude.android.benchmark.StartupBenchmark
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupCold() = benchmarkRule.measureRepeated(
        packageName = "com.openclaude.android",
        metrics = listOf(StartupTimingMetric()),
        compilationMode = CompilationMode.DEFAULT,
        startupMode = StartupMode.COLD,
        iterations = 5,
    ) {
        pressHome()
        startActivityAndWait()
        // Wait for the main content to appear
        device.wait(Until.hasObject(By.text("Start a conversation")), 5_000)
    }

    @Test
    fun startupWarm() = benchmarkRule.measureRepeated(
        packageName = "com.openclaude.android",
        metrics = listOf(StartupTimingMetric()),
        compilationMode = CompilationMode.DEFAULT,
        startupMode = StartupMode.WARM,
        iterations = 5,
    ) {
        pressHome()
        startActivityAndWait()
        device.wait(Until.hasObject(By.text("Start a conversation")), 5_000)
    }
}
