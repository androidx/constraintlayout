/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.example.composeanimateddraganddrop

import android.util.Log
import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.node.Ref
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


private const val CONTAINS_FROM_CENTER_PERCENT = 0.75f

fun Modifier.dragAndDrop(dragHandler: LayoutDragHandler): Modifier =
    with(dragHandler) {
        pointerInput(Unit) {
            detectDragAndDrop()
        }
    }


class LayoutDragHandler(
    private val boundsById: Map<Int, Rect>,
    private val orderedIds: SnapshotStateList<Int>,
    private val listBounds: Ref<Rect>,
    private val windowBounds: Ref<Rect>,
    private val scrollState: ScrollState?,
    private val scope: CoroutineScope,
    private val onMove: (from: Int, to: Int) -> Unit
) {
    private var ignoreBounds = Rect.Zero
    private var draggedIndex: Int = -1
    private var lastScrollPosition = 0

    /**
     * Flow to handle the dragging offset, since it may need to check for "collision" across
     * multiple items it's best to make it non-blocking for the dragging event.
     */
    private val draggedOffsetFlow = MutableStateFlow(Offset.Unspecified)

    private fun viewportBounds(): Rect =
        windowBounds.value ?: Rect.Zero

    private val contentOffset: Float
        get() = listBounds.value?.top ?: 0f

    private val scrollPosition: Int
        get() = scrollState?.value ?: 0

    private val scrollChannel = Channel<Float>(Channel.CONFLATED)

    var draggedId: Int by mutableStateOf(-1)
        private set

    val draggedSize: Size
        get() = boundsById[draggedId]?.size ?: Size.Zero

    /**
     * Offset of the dragged item with respect to the root bounds.
     *
     * Meant to be used for overlays on the layout, such as a draggable placeholder.
     */
    val draggingOffset: Animatable<Offset, AnimationVector2D> =
        Animatable(Offset.Zero, Offset.VectorConverter)

    init {
        scope.launch {
            // Check if the offset corresponds to an existing item
            draggedOffsetFlow.collect { offset ->
                if (offset.isUnspecified) {
                    // Reset when unspecified
                    ignoreBounds = Rect.Zero
                    draggedIndex = -1
                    draggedId = -1
                    return@collect
                }
                if (ignoreBounds.contains(offset)) {
                    return@collect
                }

                val targetIndex = boundsById.firstNotNullOfOrNull { (id, bounds) ->
                    if (draggedId != id && bounds.containsCloseToCenter(
                            offset,
                            CONTAINS_FROM_CENTER_PERCENT
                        )
                    ) {
                        ignoreBounds = bounds
                        id
                    } else {
                        null
                    }
                }?.let { id ->
                    orderedIds.indexOf(id)
                }

                if (targetIndex != null) {
                    val initialIndex = draggedIndex
                    draggedIndex = targetIndex
                    onMove(initialIndex, targetIndex)
                }
                val scrollInto = Rect(draggingOffset.value, ignoreBounds.size)
                scope.launch {
                    scrollIntoIfNeeded(scrollInto)
                }
            }
        }
        scope.launch {
            // Consume scroll
            while (coroutineContext.isActive) {
                for (scrollInto in scrollChannel) {
                    val diff = scrollChannel.tryReceive().getOrNull() ?: scrollInto
                    scrollState?.scrollBy(diff)
                }
            }
        }
    }

    /**
     * Extension function to enable the DragAndDrop functionality on the receiving Composable.
     */
    suspend fun PointerInputScope.detectDragAndDrop() {
        detectDragGesturesAfterLongPress(
            onDragStart = ::onStartDrag,
            onDragEnd = ::onEndDrag,
            onDragCancel = ::onEndDrag,
            onDrag = { change, dragAmount ->
                change.consume()
                onDrag(dragAmount)
            },
        )
    }

    private fun onStartDrag(offset: Offset) {
        val currContentOffset = contentOffset
        lastScrollPosition = scrollPosition
        boundsById.forEach { (id, bounds) ->
            if (bounds.contains(offset)) {
                val topLeft = bounds.topLeft
                scope.launch {
                    draggingOffset.snapTo(topLeft + Offset(0f, currContentOffset))
                }
                draggedIndex = orderedIds.indexOf(id)
                ignoreBounds = bounds
                draggedId = id
                draggedOffsetFlow.value = offset
                return
            }
        }
    }

    private fun onDrag(dragAmount: Offset) {
        if (draggedIndex == -1 || draggedId == -1) {
            Log.i("RowDndDemo", "onDrag: Unspecified dragged element or offset")
            return
        }
        scope.launch {
            draggingOffset.snapTo(draggingOffset.value + dragAmount)
        }
        val scrollChange = Offset(0f, (scrollPosition - lastScrollPosition).toFloat())
        lastScrollPosition = scrollPosition
        draggedOffsetFlow.update { old ->
            old + dragAmount + scrollChange
        }
    }

    private fun onEndDrag() {
        if (draggedId != -1) {
            val currContentOffset = contentOffset
            boundsById[draggedId]?.topLeft?.let { targetOffset ->
                scope.launch {
                    draggingOffset.animateTo(targetOffset + Offset(0f, currContentOffset))

                    // Reset after animation is done
                    draggedOffsetFlow.value = Offset.Unspecified
                    draggingOffset.snapTo(Offset.Zero)
                }
            }
        }
    }

    /**
     * Whether the given [offset] is within the given distance percent from the center.
     */
    private fun Rect.containsCloseToCenter(
        offset: Offset,
        @FloatRange(0.0, 1.0)
        percentFromCenter: Float = 0.5f
    ): Boolean {
        if (offset.x < this.left || offset.x > this.right) {
            return false
        }
        if (offset.y < this.top || offset.y > this.bottom) {
            return false
        }

        val horDistance = width * 0.5f * percentFromCenter
        val center = this.center
        if (offset.x < (center.x - horDistance) || offset.x > (center.x + horDistance)) {
            return false
        }

        val verDistance = height * 0.5f * percentFromCenter
        if (offset.y < (center.y - verDistance) || offset.y > (center.y + verDistance)) {
            return false
        }
        return true
    }

    /**
     * Scroll into the given [bounds] in case they are located outside the viewport.
     *
     * Note that nothing may happen if the given [bounds] are outside the bounds of the original
     * content held by the viewport.
     */
    private suspend fun scrollIntoIfNeeded(bounds: Rect) {
        val visibleBounds = viewportBounds()
        if (bounds.top - visibleBounds.top < 0f) {
            scrollChannel.send(bounds.top - visibleBounds.top)
        } else if (bounds.bottom - visibleBounds.bottom > 0f) {
            scrollChannel.send(bounds.bottom - visibleBounds.bottom)
        }
    }
}