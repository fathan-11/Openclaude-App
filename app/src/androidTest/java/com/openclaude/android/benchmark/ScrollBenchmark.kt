package com.openclaude.android.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Scroll performance benchmark for the chat message list.
 *
 * Run with:
 *   ./gradlew :app:connectedBenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.class=com.openclaude.android.benchmark.ScrollBenchmark
 */
@RunWith(AndroidJUnit4::class)
class ScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollChatList() = benchmarkRule.measureRepeated(
        packageName = "com.openclaude.android",
        metrics = listOf(FrameTimingMetric()),
        compilationMode = CompilationMode.DEFAULT,
        iterations = 5,
    ) {
        pressHome()
        startActivityAndWait()
        device.wait(Until.hasObject(By.text("Start a conversation")), 5_000)

        // If messages are present, scroll through them
        val chatList = device.findObject(By.res("chat_list"))
        chatList?.setGestureMargin(device.displayWidth / 5)

        // Scroll down and up to measure frame timing
        repeat(3) {
            chatList?.scroll(androidx.test.uiautomator.Direction.DOWN, 1.0f)
            device.waitForIdle()
        }
        repeat(3) {
            chatList?.scroll(androidx.test.uiautomator.Direction.UP, 1.0f)
            device.waitForIdle()
        }
    }
}
