package com.example.examplescomposemotionlayout


import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource


@Composable
fun MotionImage(
    panX: Float = 0.0f,
    panY: Float = 0.0f,
    zoom: Float = 1f,
    rotate: Float = 0f,
    contrast: Float = 1f,
    brightness: Float = 1f,
    saturation: Float = 1f,
    warmth: Float = 1f,
    @DrawableRes id: Int,
    modifier: Modifier = Modifier
        .fillMaxSize()
) {
    MotionImage(
        panX =  panX,
        panY = panY,
        zoom =  zoom,
        rotate = rotate,
        contrast =  contrast,
        brightness = brightness,
        saturation = saturation,
        warmth = warmth,
        painter = painterResource(id),
        modifier = modifier
    )
}


@Composable
fun MotionImage(
    panX: Float = 0.0f,
    panY: Float = 0.0f,
    zoom: Float = 1f,
    rotate: Float = 0f,
    contrast: Float = 1f,
    brightness: Float = 1f,
    saturation: Float = 1f,
    warmth: Float = 1f,
    painter: Painter,
    modifier: Modifier = Modifier.fillMaxSize()
) {

    Canvas(modifier = modifier) {
        clipRect {
            var iw: Float = size.width
            var ih: Float = size.height
            var sw: Float = painter.intrinsicSize.width
            var sh: Float = painter.intrinsicSize.height

            var scale: Float = (if (iw * sh < ih * sw) sw / iw else sh / ih)
            var sx = zoom * sw / iw / scale
            var sy = zoom * sh / ih / scale
            var tx: Float = (sw - sx * iw) * panX
            var ty: Float = (sh - sy * ih) * panY
            println("pan = $tx,$ty")
            val cf = ColorMatrix()
            updateMatrix(cf, brightness = brightness, saturation = saturation, contrast = contrast, warmth = warmth);
            with(painter) {
                withTransform({
                    rotate(rotate)
                    translate(tx, ty)
                    scale(sx, sy)


                }) {
                    draw(size, colorFilter = ColorFilter.colorMatrix(cf))
                }
            }
        }
    }

}


private fun saturation(mMatrix: FloatArray, saturationStrength: Float) {
    val Rf = 0.2999f
    val Gf = 0.587f
    val Bf = 0.114f
    val ms = 1.0f - saturationStrength
    val Rt = Rf * ms
    val Gt = Gf * ms
    val Bt = Bf * ms
    mMatrix[0] = Rt + saturationStrength
    mMatrix[1] = Gt
    mMatrix[2] = Bt
    mMatrix[3] = 0f
    mMatrix[4] = 0f
    mMatrix[5] = Rt
    mMatrix[6] = Gt + saturationStrength
    mMatrix[7] = Bt
    mMatrix[8] = 0f
    mMatrix[9] = 0f
    mMatrix[10] = Rt
    mMatrix[11] = Gt
    mMatrix[12] = Bt + saturationStrength
    mMatrix[13] = 0f
    mMatrix[14] = 0f
    mMatrix[15] = 0f
    mMatrix[16] = 0f
    mMatrix[17] = 0f
    mMatrix[18] = 1f
    mMatrix[19] = 0f
}

private fun warmth(matrix: FloatArray, warmth: Float) {
    var warmth = warmth
    val baseTemperature = 5000f
    if (warmth <= 0) warmth = .01f
    var tmpColor_r: Float
    var tmpColor_g: Float
    var tmpColor_b: Float
    var kelvin = baseTemperature / warmth
    run {
        // simulate a black body radiation
        val centiKelvin = kelvin / 100
        val colorR: Float
        val colorG: Float
        val colorB: Float
        if (centiKelvin > 66) {
            val tmp = centiKelvin - 60f
            // Original statements (all decimal values)
            // colorR = (329.698727446f * (float) Math.pow(tmp, -0.1332047592f))
            // colorG = (288.1221695283f * (float) Math.pow(tmp, 0.0755148492f))
            colorR = 329.69873f * Math.pow(tmp.toDouble(), -0.13320476).toFloat()
            colorG = 288.12216f * Math.pow(tmp.toDouble(), 0.07551485).toFloat()
        } else {
            // Original statements (all decimal values)
            // colorG = (99.4708025861f * (float) Math.log(centiKelvin) - 161.1195681661f);
            colorG = 99.4708f * Math.log(centiKelvin.toDouble()).toFloat() - 161.11957f
            colorR = 255f
        }
        colorB = if (centiKelvin < 66) {
            if (centiKelvin > 19) {
                // Original statements (all decimal values)
                // 138.5177312231f * (float) Math.log(centiKelvin - 10) - 305.0447927307f);
                (138.51773f
                        * Math.log((centiKelvin - 10).toDouble())
                    .toFloat() - 305.0448f)
            } else {
                0f
            }
        } else {
            255f
        }
        tmpColor_r = Math.min(255f, Math.max(colorR, 0f))
        tmpColor_g = Math.min(255f, Math.max(colorG, 0f))
        tmpColor_b = Math.min(255f, Math.max(colorB, 0f))
    }
    var color_r = tmpColor_r
    var color_g = tmpColor_g
    var color_b = tmpColor_b
    kelvin = baseTemperature

    // simulate a black body radiation
    val centiKelvin = kelvin / 100
    val colorR: Float
    val colorG: Float
    val colorB: Float
    if (centiKelvin > 66) {
        val tmp = centiKelvin - 60f
        // Original statements (all decimal values)
        //  colorR = (329.698727446f * (float) Math.pow(tmp, -0.1332047592f));
        //  colorG = (288.1221695283f * (float) Math.pow(tmp, 0.0755148492f));
        colorR = 329.69873f * Math.pow(tmp.toDouble(), -0.13320476).toFloat()
        colorG = 288.12216f * Math.pow(tmp.toDouble(), 0.07551485).toFloat()
    } else {
        // Original statements (all decimal values)
        //float of (99.4708025861f * (float) Math.log(centiKelvin) - 161.1195681661f);
        colorG = 99.4708f * Math.log(centiKelvin.toDouble()).toFloat() - 161.11957f
        colorR = 255f
    }
    colorB = if (centiKelvin < 66) {
        if (centiKelvin > 19) {
            // Original statements (all decimal values)
            //float of (138.5177312231 * Math.log(centiKelvin - 10) - 305.0447927307);
            138.51773f * Math.log((centiKelvin - 10).toDouble())
                .toFloat() - 305.0448f
        } else {
            0f
        }
    } else {
        255f
    }
    tmpColor_r = Math.min(255f, Math.max(colorR, 0f))
    tmpColor_g = Math.min(255f, Math.max(colorG, 0f))
    tmpColor_b = Math.min(255f, Math.max(colorB, 0f))

    color_r /= tmpColor_r
    color_g /= tmpColor_g
    color_b /= tmpColor_b
    matrix[0] = color_r
    matrix[1] = 0f
    matrix[2] = 0f
    matrix[3] = 0f
    matrix[4] = 0f
    matrix[5] = 0f
    matrix[6] = color_g
    matrix[7] = 0f
    matrix[8] = 0f
    matrix[9] = 0f
    matrix[10] = 0f
    matrix[11] = 0f
    matrix[12] = color_b
    matrix[13] = 0f
    matrix[14] = 0f
    matrix[15] = 0f
    matrix[16] = 0f
    matrix[17] = 0f
    matrix[18] = 1f
    matrix[19] = 0f
}

private fun brightness(matrix: FloatArray, brightness: Float) {
    matrix[0] = brightness
    matrix[1] = 0f
    matrix[2] = 0f
    matrix[3] = 0f
    matrix[4] = 0f
    matrix[5] = 0f
    matrix[6] = brightness
    matrix[7] = 0f
    matrix[8] = 0f
    matrix[9] = 0f
    matrix[10] = 0f
    matrix[11] = 0f
    matrix[12] = brightness
    matrix[13] = 0f
    matrix[14] = 0f
    matrix[15] = 0f
    matrix[16] = 0f
    matrix[17] = 0f
    matrix[18] = 1f
    matrix[19] = 0f
}

fun updateMatrix(
    out: ColorMatrix,
    brightness: Float = 1f,
    saturation: Float = 1f,
    contrast: Float = 1f,
    warmth: Float = 1f
) {
    var used = false
    var tmp = ColorMatrix()
    out.reset()
    if (saturation != 1.0f) {
        saturation(tmp.values, saturation)
        tmp.values.copyInto(out.values)
        used = true;
    }
    if (contrast != 1.0f) {
        if (!used) {
            out.setToScale(contrast, contrast, contrast, 1f)
        } else {
            tmp.setToScale(contrast, contrast, contrast, 1f)
            out.timesAssign(tmp)
        }
        used = true;
    }
    if (warmth != 1.0f) {
        if (!used) {
            warmth(out.values, warmth)
        } else {
            warmth(tmp.values, warmth)
            out.timesAssign(tmp)
        }
        used = true;
    }
    if (brightness != 1.0f) {

        if (!used) {
            brightness(out.values, brightness)

        } else {
            brightness(tmp.values, brightness)
            out.timesAssign(tmp)
        }
        used = true;
    }

}
