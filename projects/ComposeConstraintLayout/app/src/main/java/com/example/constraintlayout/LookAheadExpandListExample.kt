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
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.Transition
import androidx.constraintlayout.compose.rememberMotionMovableListItems
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import kotlin.math.roundToInt

@Preview(group = "collapsed")
@Composable
private fun ExpandableListPreview() {
    val itemProvider = rememberItemProvider()
    val itemsData = List(6) { itemProvider() }
    var collapsedOrExpanded by remember { mutableStateOf(true) }
    val items = rememberMotionMovableListItems(count = 6) { index ->
        val itemModifier = if (collapsedOrExpanded) {
            // ConstraintLayout needs LayoutId
            // TODO: Consider setting the width and height here instead of the ConstraintSet
            Modifier
        }
        else {
            Modifier
                .fillMaxWidth()
                .height(100.dp)
        }
        CartItem(
            modifier = Modifier
                .layoutId(index.toString())
                .motionId(index)
                .zIndex((6 - index).toFloat())
                .then(itemModifier),
            item = itemsData[index]
        )
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        StandardLookAheadLayout {
            MotionLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .padding(bottom = 8.dp)
                    .animateBounds(Modifier.wrapContentHeight(), tween(2000)),
                transition = Transition {
                }) {
                if (collapsedOrExpanded) {
//                CollapsedCartList(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(300.dp),
//                    dataSize = items.size
//                ) {
//                    items.emit()
//                }
                    CollapsedCartListCustom {
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
        Row(
            Modifier
                .fillMaxWidth()
                .weight(1.0f, true)
        ) {
            Text(text = "Hello, World!")
        }
        Button(onClick = { collapsedOrExpanded = !collapsedOrExpanded }) {
            Text(text = "Toggle")
        }
    }
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


private data class ItemData(
    val id: Int,
    val thumbnailUri: Uri,
    val name: String,
    val cost: Float
)

@Composable
private fun rememberItemProvider(): ItemProvider {
    val context = LocalContext.current
    return remember {
        var providedCount = 0
        object : () -> ItemData {
            override fun invoke(): ItemData {
                return ItemData(
                    id = providedCount++,
                    thumbnailUri = context.drawableUri(R.drawable.pepper),
                    name = itemNames.random(),
                    cost = IntRange(5, 50).random().toFloat() + (IntRange(0, 99).random() / 100f)
                )
            }
        }
    }
}

private typealias ItemProvider = () -> ItemData

@Preview
@Composable
fun CartItemPreview() {
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

@Composable
private fun ExpandedCartList(modifier: Modifier = Modifier, itemsData: List<ItemData>) {
    Column(
        modifier = modifier,
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
private fun ExpandedCartListPreview() {
    val itemDataProvider = rememberItemProvider()
    val itemsData = (0..5).toList().map { itemDataProvider() }
    ExpandedCartList(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        itemsData = itemsData
    )
}

// Version compatible with LookaheadLayout, receives the content instead of the data
@Composable
private inline fun CollapsedCartList(
    modifier: Modifier = Modifier,
    dataSize: Int,
    crossinline content: @Composable () -> Unit
) {
    val itemIds = List(dataSize) { index -> index }
    val cSet = remember {
        ConstraintSet {
            val itemRefsById = itemIds.associateWith { createRefFor(it.toString()) }
            itemRefsById.forEach { (id, ref) ->
                when (id) {
                    0, 1, 2 -> { // Stack the first three items below each other
                        constrain(ref) {
                            val lastRef = itemRefsById[id - 1] ?: parent
                            width = Dimension.fillToConstraints
                            height = Dimension.value(100.dp)
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
                                width = Dimension.fillToConstraints
                                height = Dimension.value(100.dp)
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

@Composable
private fun CollapsedCartList(modifier: Modifier = Modifier, itemsData: List<ItemData>) {
    val itemIds = List(itemsData.size) { index -> index }
    val cSet = remember {
        ConstraintSet {
            val itemRefsById = itemIds.associateWith { createRefFor(it.toString()) }
            itemRefsById.forEach { (id, ref) ->
                when (id) {
                    0, 1, 2 -> { // Stack the first three items below each other
                        constrain(ref) {
                            val lastRef = itemRefsById[id - 1] ?: parent
                            width = Dimension.fillToConstraints
                            height = Dimension.value(100.dp)
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
                                width = Dimension.fillToConstraints
                                height = Dimension.value(100.dp)
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
        itemsData.forEachIndexed { id, itemData ->
            CartItem(
                modifier = Modifier.layoutId(id.toString()),
                item = itemData
            )
        }
    }
}

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
                    constraints.maxWidth,
                    with(density) { itemsToWrap * 16.dp.roundToPx() + 100.dp.roundToPx() }) {
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

@Preview(group = "collapsed")
@Composable
private fun CollapsedCartListPreview() {
    val itemDataProvider = rememberItemProvider()
    val itemIds = remember {
        (0..5).toList()
    }
    val itemData = itemIds.map { itemDataProvider() }
    CollapsedCartList(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        itemsData = itemData
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

private val itemNames = listOf<String>(
    "Fruit",
    "Vegetables",
    "Bread",
    "Pet Food",
    "Cereal",
    "Milk",
    "Eggs",
    "Yogurt"
)