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

@file:OptIn(ExperimentalMotionApi::class)

package com.example.composeanimateddraganddrop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.node.Ref
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.FlowStyle
import androidx.constraintlayout.compose.HorizontalAlign
import androidx.constraintlayout.compose.Wrap
import androidx.constraintlayout.compose.layoutId

private const val ITEM_COUNT = 40
private const val BASE_ITEM_SIZE = 80
private const val INITIAL_COLUMN_COUNT = 4

@Preview
@Composable
internal fun FlowDragAndDropExample() {
    val itemCount = ITEM_COUNT
    var columnCount by remember { mutableStateOf(INITIAL_COLUMN_COUNT) }
    val itemModel = remember {
        List(ITEM_COUNT) { ItemState() }
    }
    val gridOrderByIndex: SnapshotStateList<Int> =
        remember { mutableStateListOf(elements = List(itemCount) { it }.toTypedArray()) }

    val constraintSet = ConstraintSet {
        val itemRefs = List(itemCount) { createRefFor("item$it") }

        val flow = createFlow(
            elements = gridOrderByIndex.map { itemRefs[it] }.toTypedArray(),
            flowVertically = false,
            maxElement = columnCount,
            horizontalStyle = FlowStyle.Spread,
            horizontalFlowBias = 0.5f,
            horizontalAlign = HorizontalAlign.Center,
            wrapMode = Wrap.Chain
        )
        constrain(flow) {
            width = Dimension.fillToConstraints
            top.linkTo(parent.top)
            centerHorizontallyTo(parent)
        }

        itemRefs.forEachIndexed { index, itemRef ->
            val widthDp =
                if (itemModel[index].isHorizontallyExpanded) BASE_ITEM_SIZE * 2 else BASE_ITEM_SIZE
            constrain(itemRef) {
                height = BASE_ITEM_SIZE.dp.asDimension
                width = widthDp.dp.asDimension
            }
        }
    }

    val scrollState = rememberScrollState()
    val listBounds = remember { Ref<Rect>().apply { value = Rect.Zero } }
    val windowBounds = remember { Ref<Rect>().apply { value = Rect.Zero } }
    val scope = rememberCoroutineScope()
    val boundsById: MutableMap<Int, Rect> = remember { mutableMapOf() }
    val onMove: (Int, Int) -> Unit = { from, to ->
        // TODO: Implement a way that moves items directionally (moving up pushes items down) instead of in a Flow
        gridOrderByIndex.add(to, gridOrderByIndex.removeAt(from))
    }
    val dragHandler = remember {
        LayoutDragHandler(
            boundsById = boundsById,
            orderedIds = gridOrderByIndex,
            listBounds = listBounds,
            windowBounds = windowBounds,
            scrollState = scrollState,
            scope = scope,
            onMove = onMove
        )
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        val movableItems = remember {
            List(itemCount) { it }.map { id ->
                movableContentOf {
                    Item(
                        text = "item$id",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState, true)
        ) {
            Column(
                modifier = Modifier.align(Alignment.End),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        columnCount = INITIAL_COLUMN_COUNT
                        gridOrderByIndex.sort()
                        itemModel.forEach {
                            it.isHorizontallyExpanded = false
                            it.isVerticallyExpanded = false
                        }
                    }) {
                        Text(text = "Reset")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { gridOrderByIndex.shuffle() }) {
                        Text(text = "Shuffle")
                    }
                    Button(onClick = {
                        var newColumnCount = columnCount + 1
                        if (newColumnCount > 4) {
                            newColumnCount = 1
                        }
                        columnCount = newColumnCount
                    }) {
                        Text(text = "Columns ($columnCount)")
                    }
                }

                AnimatedConstraintLayout(
                    constraintSet = constraintSet,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .onGloballyPositioned {
                            listBounds.value =
                                it
                                    .findRootCoordinates()
                                    .localBoundingBoxOf(it, false)
                            windowBounds.value =
                                it
                                    .findRootCoordinates()
                                    .localBoundingBoxOf(it, true)
                        }
                        .dragAndDrop(dragHandler)
                ) {
                    for (i in 0 until itemCount) {
                        val name = "item$i"
                        Box(
                            modifier = Modifier
                                .layoutId(name)
                                .onStartEndBoundsChanged(name) { _, endBounds ->
                                    boundsById[i] = endBounds
                                }
                                .clickable {
                                    itemModel[i].isHorizontallyExpanded =
                                        !itemModel[i].isHorizontallyExpanded
                                }
                        ) {
                            if (i != dragHandler.draggedId) {
                                movableItems[i]()
                            } else {
                                // Leave a border so that it's clear where the Item will end up when the
                                // drag interaction finishes
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            shape = CardDefaults.shape
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
        // Placeholder should be sibling of ConstraintLayout, if it's a child, even if it's on
        // a fixed position, the animated relayout will cause instability with the drag interaction
        DraggablePlaceholder(
            modifier = Modifier,
            dragHandler = dragHandler
        ) {
            // TODO: Create placeholder dynamically, since dragStart might overlap with dragEnd
            if (dragHandler.draggedId != -1) {
                movableItems[dragHandler.draggedId]()
            }
        }
    }
}

class ItemState {
    var isHorizontallyExpanded by mutableStateOf(false)
    var isVerticallyExpanded by mutableStateOf(false)
}

@Composable
fun Item(
    text: String,
    modifier: Modifier = Modifier
) {
    val color = remember { Color.hsv(IntRange(0, 360).random().toFloat(), 0.5f, 0.8f) }
    Box(
        modifier = modifier.background(color, CardDefaults.shape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}