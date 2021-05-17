/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.constraintlayout.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class DesignInfoProviderTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun withConstraintSet() {
        rule.setContent {
            ConstraintLayout(
                modifier = Modifier.size(50.dp),
                constraintSet = ConstraintSet {
                    val box1 = createRefFor("box1")
                    val box2 = createRefFor("box2")
                    val guideline = createGuidelineFromStart(fraction = 0.5f)
                    val barrier = createEndBarrier(box1, box2)
                    val box3 = createRefFor("box3")

                    constrain(box1) {
                        top.linkTo(parent.top)
                        end.linkTo(guideline)
                    }
                    constrain(box2) {
                        top.linkTo(box1.bottom)
                        start.linkTo(guideline)
                    }

                    constrain(box3) {
                        start.linkTo(barrier)
                        top.linkTo(box2.bottom)
                    }
                }
            ) {
                for (i in 1..3) {
                    Box(
                        modifier = Modifier
                            .layoutId("box$i")
                            .size(10.dp)
                    )
                }
            }
        }
        rule.waitForIdle()

        val designInfoJson = getDesignInfoJson()
        assertEquals("CONSTRAINTS", designInfoJson["type"])
        assertEquals(1, designInfoJson["version"])
        val contentJson = designInfoJson["content"] as JSONObject
        assertEquals(6, contentJson.length())
        for (i in 1..3) {
            val boxJson = contentJson["box$i"] as JSONObject
            assertEquals(2, (boxJson["constraints"] as JSONArray).length())
        }
        val viewInfoList =
            contentJson.keys().asSequence().map { contentJson[it] as JSONObject }.toList()
        assertEquals(1, viewInfoList.filter { it.getBoolean("isRoot") }.size)
        val helpers = viewInfoList.filter { it.getBoolean("isHelper") }
        assertEquals(1, helpers.size)
        val helperReferences = helpers[0]["helperReferences"] as JSONArray
        assertEquals(2, helperReferences.length())
        assertEquals("box1", helperReferences[0])
        assertEquals("box2", helperReferences[1])
    }

    @Test
    fun withDsl() {
        rule.setContent {
            ConstraintLayout(
                modifier = Modifier.size(50.dp)
            ) {
                val (box1, box2, box3) = createRefs()
                val guideline = createGuidelineFromStart(fraction = 0.5f)
                val barrier = createEndBarrier(box1, box2)
                Box(modifier = Modifier.size(10.dp).constrainAs(box1){
                    top.linkTo(parent.top)
                    end.linkTo(guideline)
                })
                Box(modifier = Modifier.size(10.dp).constrainAs(box2){
                    top.linkTo(box1.bottom)
                    start.linkTo(guideline)
                })
                Box(modifier = Modifier.size(10.dp).constrainAs(box3){
                    top.linkTo(box2.bottom)
                    start.linkTo(barrier)
                })
            }
        }
        rule.waitForIdle()

        val designInfoJson = getDesignInfoJson()
        assertEquals("CONSTRAINTS", designInfoJson["type"])
        assertEquals(1, designInfoJson["version"])
        val contentJson = designInfoJson["content"] as JSONObject
        assertEquals(6, contentJson.length())
        val viewInfoList =
            contentJson.keys().asSequence().map { contentJson[it] as JSONObject }.toList()
        assertEquals(1, viewInfoList.filter { it.getBoolean("isRoot") }.size)
        val helpers = viewInfoList.filter { it.getBoolean("isHelper") }
        assertEquals(1, helpers.size)
        val helperReferences = helpers[0]["helperReferences"] as JSONArray
        assertEquals(2, helperReferences.length())
    }

    private fun getDesignInfoJson(): JSONObject {
        val nodeInteraction = rule.onNode(SemanticsMatcher.keyIsDefined(DesignInfoDataKey))
        nodeInteraction.assertExists()
        val designInfo =
            nodeInteraction.fetchSemanticsNode().config[DesignInfoDataKey].getDesignInfo(0, 0, "")
        return JSONObject(designInfo)
    }
}