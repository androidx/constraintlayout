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

@file:OptIn(ExperimentalCoilApi::class, ExperimentalComposeUiApi::class)

package com.example.constraintlayout

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Motion
import androidx.constraintlayout.compose.layoutId
import androidx.constraintlayout.compose.rememberMotionListItems
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import kotlin.math.roundToInt

private val ItemHeight = 100.dp

@Preview
@Composable
private fun ExpandableListPreview() {
    val durationMs = 800
    val itemsData = createItemDataList(count = 10)

    /**
     * ConstraintLayout when collapsed, Column when expanded
     */
    var collapsedOrExpanded by remember { mutableStateOf(true) }

    /**
     * Define the items that'll be present both in ConstraintLayout and Column, since the parent
     * node will change from one to the other we need to use [rememberMotionListItems].
     */
    val items = rememberMotionListItems(count = itemsData.size) { index ->
        CartItem(
            modifier = Modifier
                // In this case, these Dimensions do not cause conflict with ConstraintLayout
                .fillMaxWidth()
                .height(ItemHeight)
                .layoutId(index.toString()) // For ConstraintLayout
                // ignoreAxisChanges prevents animation from triggering during scroll
                .motion(animationSpec = tween(durationMs), ignoreAxisChanges = true) {
                    // We may apply keyframes to modify the items during animation
                    keyAttributes {
                        frame(50) {
                            scaleX = 0.7f
                            scaleY = 0.7f
                        }
                    }
                }
                .zIndex((itemsData.size - index).toFloat()),
            item = itemsData[index]
        )
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            Modifier
                .fillMaxWidth()
        ) {
            // Motion Composable enables the `motion` Modifier.
            Motion(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .padding(bottom = 8.dp)
                    .animateContentSize(tween(durationMs)) // Match the animated content
            ) {
                // Here we'll change the Layout Composable for our items based on the
                // `collapsedOrExpanded` state, we may use the `emit` extension function to
                // simplify it.

                if (collapsedOrExpanded) {
                    CollapsedCartList(
                        modifier = Modifier
                            .fillMaxWidth(),
                        dataSize = items.size
                    ) {
                        items.emit()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items.emit()
                    }
                }
            }
        }
        Text(text = "Layout: " + if (collapsedOrExpanded) "ConstraintLayout" else "Column")
        Button(onClick = { collapsedOrExpanded = !collapsedOrExpanded }) {
            Text(text = "Toggle")
        }
    }
}

/**
 * Collapsed view using ConstraintLayout, will layout the first three items one below the other, all
 * other items will be stacked under each other.
 *
 * The content most have their [Modifier.layoutId] assigned from 0 to [dataSize] - 1.
 *
 * @see CollapsedCartListPreview
 */
@Composable
private fun CollapsedCartList(
    modifier: Modifier = Modifier,
    dataSize: Int,
    content: @Composable () -> Unit
) {
    val itemIds: List<Int> = remember { List(dataSize) { index -> index } }
    val cSet = remember {
        val applyDimensions: ConstrainScope.() -> Unit = {
            width = Dimension.fillToConstraints
            height = Dimension.value(ItemHeight)
        }

        ConstraintSet {
            val itemRefsById = itemIds.associateWith { createRefFor(it.toString()) }
            itemRefsById.forEach { (id, ref) ->
                when (id) {
                    0, 1, 2 -> { // Stack the first three items below each other
                        constrain(ref) {
                            val lastRef = itemRefsById[id - 1] ?: parent
                            applyDimensions()
                            start.linkTo(lastRef.start, 8.dp)
                            end.linkTo(lastRef.end, 8.dp)
                            top.linkTo(lastRef.top, 16.dp)
                            translationZ = (6 - (2 * id)).coerceAtLeast(0).dp
                        }
                    }
                    else -> { // Stack/hide together all other items
                        if (id > 0) {
                            val lastRef = itemRefsById[2]!!
                            constrain(ref) {
                                applyDimensions()
                                centerTo(lastRef)
                            }
                        }
                    }
                }
            }
        }
    }
    ConstraintLayout(
        modifier = modifier,
        constraintSet = cSet
    ) {
        content()
    }
}

/**
 * Non-ConstraintLayout variant of [CollapsedCartList].
 */
@Composable
private fun CollapsedCartListCustom(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    Layout(
        measurePolicy = remember {
            MeasurePolicy { measurables, constraints ->
                var count = 2
                val placeables = mutableListOf<Placeable>()
                var maxWidth = constraints.maxWidth
                var itemsToWrap = 0
                measurables.forEach { measurable ->
                    if (count-- >= 0) {
                        itemsToWrap++
                        maxWidth -= with(density) { 16.dp.toPx() }.roundToInt()
                        placeables.add(
                            measurable.measure(
                                Constraints.fixed(
                                    maxWidth,
                                    with(density) { 100.dp.toPx() }.roundToInt()
                                )
                            )
                        )
                    } else {
                        placeables.add(
                            measurable.measure(
                                Constraints.fixed(
                                    maxWidth,
                                    with(density) { 100.dp.toPx() }.roundToInt()
                                )
                            )
                        )
                    }
                }
                layout(
                    width = constraints.maxWidth,
                    height = with(density) { itemsToWrap * 16.dp.roundToPx() + 100.dp.roundToPx() }
                ) {
                    count = 2
                    var x = 0
                    var y = 0
                    placeables.forEach { placeable ->
                        if (count-- >= 0) {
                            x += with(density) { 8.dp.roundToPx() }
                            y += with(density) { 16.dp.roundToPx() }
                            placeable.place(x, y, (count + 1).times(2).toFloat())
                        } else {
                            placeable.place(x, y, 0f)
                        }
                    }
                }
            }
        },
        modifier = modifier,
        content = content
    )
}

@Composable
private fun CartItem(modifier: Modifier = Modifier, item: ItemData) {
    ConstraintLayout(
        modifier = modifier
            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
            .padding(8.dp),
        constraintSet = remember {
            ConstraintSet {
                val image = createRefFor("image")
                val name = createRefFor("name")
                val id = createRefFor("id")
                val cost = createRefFor("cost")

                constrain(image) {
                    width = Dimension.ratio("1:1")
                    height = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
                constrain(cost) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                constrain(name) {
                    width = Dimension.fillToConstraints
                    start.linkTo(image.end, 8.dp)
                    end.linkTo(cost.start, 8.dp)
                    baseline.linkTo(cost.baseline)
                }
                constrain(id) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
            }
        }
    ) {
        Image(
            modifier = Modifier
                .layoutId("image")
                .clip(RoundedCornerShape(10.dp)),
            painter = rememberImagePainter(
                request = ImageRequest.Builder(LocalContext.current)
                    .data(item.thumbnailUri)
                    .placeholder(R.drawable.pepper)
                    .build()
            ),
            contentDescription = null
        )
        Text(modifier = Modifier.layoutId("name"), text = item.name)
        Text(modifier = Modifier.layoutId("cost"), text = "$${item.cost}")
        Text(modifier = Modifier.layoutId("id"), text = item.id.toString())
    }
}

@Stable
private data class ItemData(
    val id: Int,
    val thumbnailUri: Uri,
    val name: String,
    val cost: Float
)

/**
 * Returns a list of [ItemData] objects with randomly populated values.
 *
 * @param count amount of [ItemData] generated
 */
@Composable
private fun createItemDataList(count: Int): List<ItemData> {
    val context = LocalContext.current
    return remember(count) {
        List(count) { index ->
            ItemData(
                id = index,
                thumbnailUri = context.drawableUri(R.drawable.pepper),
                name = itemNames.random(),
                cost = IntRange(5, 50).random().toFloat() + (IntRange(0, 99).random() / 100f)
            )
        }
    }
}

@Preview
@Composable
private fun CollapsedCartListPreview() {
    val itemsData = createItemDataList(count = 5)
    Column(Modifier.fillMaxWidth()) {
        Column(Modifier.background(Color.LightGray)) {
            Text(text = "ConstraintLayout")
            CollapsedCartList(
                modifier = Modifier.fillMaxWidth(),
                dataSize = itemsData.size
            ) {
                itemsData.forEachIndexed { index, itemData ->
                    CartItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .layoutId(index.toString()),
                        item = itemData
                    )
                }
            }
        }
        Column(Modifier.background(Color.Gray)) {
            Text(text = "Custom Layout")
            CollapsedCartListCustom(modifier = Modifier.fillMaxWidth()) {
                itemsData.forEachIndexed { _, itemData ->
                    CartItem(item = itemData)
                }
            }
        }
    }
}

@Preview
@Composable
private fun ExpandedCartListPreview() {
    val itemsData = createItemDataList(count = 5)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsData.forEach {
            CartItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                item = it
            )
        }
    }
}

@Preview
@Composable
private fun CartItemPreview() {
    CartItem(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        item = ItemData(
            id = 0,
            thumbnailUri = LocalContext.current.drawableUri(R.drawable.pepper),
            name = "Pepper",
            cost = 5.56f
        )
    )
}

internal fun Context.drawableUri(@DrawableRes resourceId: Int): Uri =
    with(resources) {
        Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(getResourcePackageName(resourceId))
            .appendPath(getResourceTypeName(resourceId))
            .appendPath(getResourceEntryName(resourceId))
            .build()
    }

private val itemNames = listOf(
    "Fruit",
    "Vegetables",
    "Bread",
    "Pet Food",
    "Cereal",
    "Milk",
    "Eggs",
    "Yogurt"
)