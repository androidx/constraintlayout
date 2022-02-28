/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.constraintlayout.compose.motion.statemanager

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.test.TestMonotonicFrameClock
import androidx.constraintlayout.compose.JSONMotionScene
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import org.intellij.lang.annotations.Language
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

internal class StateToStateMotionManagerTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCorrectStartAndEnd() = runBlockingTest {
        @Language("JSON5")
        val content = """
            {
              ConstraintSets: {
                a: {
                  box: {
                    width: 50, height: 50,
                  }
                },
                b: {
                  Extends: 'a',
                },
                c: {
                  Extends: 'a',
                },
              },
              Transitions: {
                default: {
                  from: 'b',
                  to: 'c'
                },
                ab: {
                  from: 'a',
                  to: 'b'
                }
              }
            }
        """.trimIndent()
        val motionScene = JSONMotionScene(content)
        val progress = Animatable(0f)

        val stateManager = StateToStateMotionManager(
            scene = motionScene,
            animatableProgress = progress,
            animationSpec = tween(100)
        )

        var start = stateManager.startConstraintSet
        var end = stateManager.endConstraintSet
        var transition = stateManager.transition
        withContext(TestMonotonicFrameClock(this, 1_000_000L)) {
            launch {
                stateManager.setTo("c")
                // b -> c, default Transition
                assertSame(start, stateManager.startConstraintSet)
                assertSame(end, stateManager.endConstraintSet)
                assertSame(transition, stateManager.transition)
                assertEquals(1.0f, progress.value)
            }.join()

            launch {
                stateManager.setTo("a")
                // c -> a, default Transition
                assertSame(end, stateManager.startConstraintSet)
                assertNotSame(start, stateManager.endConstraintSet)
                assertSame(transition, stateManager.transition)
                assertEquals(1.0f, progress.value)
                start = stateManager.startConstraintSet
                end = stateManager.endConstraintSet
            }.join()

            launch {
                stateManager.setTo("b")
                // a -> b, Ab Transition
                assertSame(end, stateManager.startConstraintSet)
                assertNotSame(start, stateManager.endConstraintSet)
                assertNotSame(transition, stateManager.transition)
                assertEquals(1.0f, progress.value)
                start = stateManager.startConstraintSet
                end = stateManager.endConstraintSet
                transition = stateManager.transition
            }.join()

            launch {
                stateManager.setTo("a")
                // a -> b, Ab Transition, reverse
                assertSame(start, stateManager.startConstraintSet)
                assertSame(end, stateManager.endConstraintSet)
                assertSame(transition, stateManager.transition)
                assertEquals(0.0f, progress.value)
            }.join()
        }
    }
}