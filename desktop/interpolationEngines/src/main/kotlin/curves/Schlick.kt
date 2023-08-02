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
package curves

/**
 * Schlick's bias and gain functions
 * curve for use in an easing function including quantize functions
 */
class Schlick internal constructor(configString: String) : Easing() {
    var mS: Float
    var mT: Float
    var mEps = 0f

    init {
        // done this way for efficiency
        mStr = configString
        val start = configString.indexOf('(')
        val off1 = configString.indexOf(',', start)
        mS = configString.substring(start + 1, off1).trim { it <= ' ' }.toFloat()
        val off2 = configString.indexOf(',', off1 + 1)
        mT = configString.substring(off1 + 1, off2).trim { it <= ' ' }.toFloat()
    }

    private fun func(x: Float): Float {
        return if (x < mT) {
            mT * x / (x + mS * (mT - x))
        } else (1 - mT) * (x - 1) / (1 - x - mS * (mT - x))
    }

    private fun dfunc(x: Float): Float {
        return if (x < mT) {
            mS * mT * mT / ((mS * (mT - x) + x) * (mS * (mT - x) + x))
        } else mS * (mT - 1) * (mT - 1) / ((-mS * (mT - x) - x + 1) * (-mS * (mT - x) - x + 1))
    }

    // @TODO: add description
    override fun getDiff(x: Float): Float {
        return dfunc(x)
    }

    // @TODO: add description
    override fun get(x: Float): Float {
        return func(x)
    }

}