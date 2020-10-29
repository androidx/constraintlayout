/*
 * Copyright (C) 2020 The Android Open Source Project
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
package androidx.constraintlayout.utils.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.widget.R;

/**
 * An ImageView that can display, combine  and filter images. <b>Added in 2.0</b>
 * <p>
 * Subclass of ImageView to handle various common filtering operations
 * </p>
 *
 * <h2>ImageFilterViewattributes</h2>
 * <table summary="KeyTrigger attributes">
 * <tr>
 * <td>altSrc</td>
 * <td>Provide and alternative image to the src image to allow cross fading </td>
 * </tr>
 * <tr>
 * <td>saturation</td>
 * <td>Sets the saturation of the image.<br>  0 = grayscale, 1 = original, 2 = hyper saturated </td>
 * </tr
 * <tr>
 * <td>brightness</td>
 * <td>Sets the brightness of the image.<br>  0 = black, 1 = original, 2 = twice as bright
 * </td>
 * </tr>
 * <tr>
 * <td>warmth</td>
 * <td>This adjust the apparent color temperature of the image.<br> 1=neutral, 2=warm, .5=cold </td>
 * </tr>
 * <tr>
 * <td>contrast</td>
 * <td>This sets the contrast. 1 = unchanged, 0 = gray, 2 = high contrast</td>
 * </tr>
 * <tr>
 * <td>crossfade</td>
 * <td>Set the current mix between the two images. <br>  0=src 1= altSrc image </td>
 * </tr>
 * <tr>
 * <td>round</td>
 * <td>(id) call the TransitionListener with this trigger id</td>
 * </tr>
 * <tr>
 * <td>roundPercent &nbs; </td>
 * <td>Set the corner radius of curvature  as a fraction of the smaller side.
 *    For squares 1 will result in a circle</td>
 * </tr>
 * <tr>
 * <td>overlay</td>
 * <td>Defines whether the alt image will be faded in on top of the original image or if it will be
 * crossfaded with it. Default is true. Set to false for semitransparent objects</td>
 * </tr>
 * </table>
 */
public class MotionLabel extends androidx.appcompat.widget.AppCompatImageView {
    static String TAG = "TextMorph";
    private CharSequence mText;
    private int mTextColor;
    private int mTextSize;
    private int mStyleIndex;
    private int mTypefaceIndex;
    private int mTextOutlineColor;
    private int mTextOutlineThickness = 0;

    private boolean mOverlay = true;
    private float mCrossfade = 0;
    private float mRoundPercent = 0; // rounds the corners as a percent
    private float mRound = Float.NaN; // rounds the corners in dp if NaN RoundPercent is in effect
    private Path mPath;
    ViewOutlineProvider mViewOutlineProvider;
    RectF mRect;

    Paint mPaint = new Paint();

    boolean mNotBuilt = true;
    private Rect mTextBounds = new Rect();


    // ======================== support for pan/zoom/rotate =================
    // defined as 0 = center of screen
    // if with < scree with,  1 is the right edge lines up with screen
    // if width > screen width, 1 is thee left edge lines up
    // -1 works similarly
    // zoom 1 = the image fits such that the view is filed

    float mPanX = Float.NaN;
    float mPanY = Float.NaN;
    float mZoom = Float.NaN;
    float mRotate = Float.NaN;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private int mShadowColor;



    public MotionLabel(Context context) {
        super(context);
        init(context, null);
    }

    public MotionLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MotionLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = getContext()
                    .obtainStyledAttributes(attrs, R.styleable.MotionLabel);
            final int N = a.getIndexCount();

int k = 0;
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.MotionLabel_android_text) {
                    setText(a.getText(attr));
                } else if (attr == R.styleable.MotionLabel_android_shadowColor) {
                    mShadowColor = a.getColor(attr, mShadowColor);
                } else  if (attr == R.styleable.MotionLabel_android_shadowDx) {
                    mShadowDx =a.getFloat(attr,mShadowDx);
                }  else  if (attr == R.styleable.MotionLabel_android_shadowDy) {
                    mShadowDy =a.getFloat(attr,mShadowDy);
                }  else  if (attr == R.styleable.MotionLabel_android_shadowRadius) {
                    mShadowRadius =a.getFloat(attr,mShadowRadius);
                }  else  if (attr == R.styleable.MotionLabel_textFillColor) {
                    mTextColor = a.getColor(attr,mTextColor);
                }  else  if (attr == R.styleable.MotionLabel_android_textSize) {
                    mTextSize = a.getDimensionPixelSize(attr, mTextSize);
                }  else  if (attr == R.styleable.MotionLabel_android_textStyle) {
                    mStyleIndex = a.getInt(attr, mStyleIndex);
                }  else  if (attr == R.styleable.MotionLabel_android_typeface) {
                    mTypefaceIndex = a.getInt(attr, mTypefaceIndex);
                }  else  if (attr == R.styleable.MotionLabel_textOutlineColor) {
                    mTextOutlineColor = a.getColor(attr,mTextOutlineColor);
                }  else  if (attr == R.styleable.MotionLabel_textOutlineThickness) {
                    mTextOutlineThickness = a.getDimensionPixelSize(attr, mTextOutlineThickness);
                } else if (attr == R.styleable.MotionLabel_borderRound) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setRound(a.getDimension(attr, 0));
                    }
                } else if (attr == R.styleable.MotionLabel_borderRoundPercent) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setRoundPercent(a.getFloat(attr, 0));
                    }
                }
            }
            a.recycle();
        }
        updatePaint();
    }

   void  updatePaint () {
       mPaint.setColor(mTextColor);
       mPaint.setStrokeWidth(mTextOutlineThickness);
       mPaint.setStyle(Paint.Style.STROKE);
       mPaint.setTypeface(Typeface.SERIF);
       mPaint.setTextSize(mTextSize);
       mPaint.setAntiAlias(true);
    }

    public void setText(CharSequence text) {
        mText = text;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    void buildShape() {
        mPath.reset();

        String str = "Hello World";
        int strlen = str.length();
        mPaint.getTextBounds(str, 0, strlen, mTextBounds);
        Log.v(TAG, Debug.getLoc()+" "+mTextBounds);
        mPaint.getTextPath(str, 0, strlen, 0, 0, mPath);
        Matrix matrix = new Matrix();
        RectF src = new RectF(mTextBounds);
        RectF rect = new RectF();
        rect.bottom = getHeight();
        rect.right = getWidth();
        matrix.setRectToRect(src,rect, Matrix.ScaleToFit.CENTER);
        mPath.transform(matrix);
        mNotBuilt = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mNotBuilt) {
            buildShape();
        }
        mPaint.setStrokeWidth(5);
        mPaint.setColor(mTextColor);
        canvas.drawPath(mPath, mPaint);
        canvas.translate(0, 100);
        mPaint.setStrokeWidth(0);
        canvas.drawPath(mPath, mPaint);


    }



    /**
     * Set the corner radius of curvature  as a fraction of the smaller side.
     * For squares 1 will result in a circle
     *
     * @param round the radius of curvature as a fraction of the smaller width
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void setRoundPercent(float round) {
        boolean change = (mRoundPercent != round);
        mRoundPercent = round;
        if (mRoundPercent != 0.0f) {
            if (mPath == null) {
                mPath = new Path();
            }
            if (mRect == null) {
                mRect = new RectF();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mViewOutlineProvider == null) {
                    mViewOutlineProvider = new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            int w = getWidth();
                            int h = getHeight();
                            float r = Math.min(w, h) * mRoundPercent / 2;
                            outline.setRoundRect(0, 0, w, h, r);
                        }
                    };
                    setOutlineProvider(mViewOutlineProvider);
                }
                setClipToOutline(true);

            }
            int w = getWidth();
            int h = getHeight();
            float r = Math.min(w, h) * mRoundPercent / 2;
            mRect.set(0, 0, w, h);
            mPath.reset();
            mPath.addRoundRect(mRect, r, r, Path.Direction.CW);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setClipToOutline(false);
            }
        }
        if (change) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                invalidateOutline();
            }
        }

    }

    /**
     * Set the corner radius of curvature
     *
     * @param round the radius of curvature  NaN = default meaning roundPercent in effect
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public void setRound(float round) {
        if (Float.isNaN(round)) {
            mRound = round;
            float tmp = mRoundPercent;
            mRoundPercent = -1;
            setRoundPercent(tmp); // force eval of roundPercent
            return;
        }
        boolean change = (mRound != round);
        mRound = round;

        if (mRound != 0.0f) {
            if (mPath == null) {
                mPath = new Path();
            }
            if (mRect == null) {
                mRect = new RectF();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mViewOutlineProvider == null) {
                    mViewOutlineProvider = new ViewOutlineProvider() {
                        @Override
                        public void getOutline(View view, Outline outline) {
                            int w = getWidth();
                            int h = getHeight();
                            outline.setRoundRect(0, 0, w, h, mRound);
                        }
                    };
                    setOutlineProvider(mViewOutlineProvider);
                }
                setClipToOutline(true);
            }
            int w = getWidth();
            int h = getHeight();
            mRect.set(0, 0, w, h);
            mPath.reset();
            mPath.addRoundRect(mRect, mRound, mRound, Path.Direction.CW);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setClipToOutline(false);
            }
        }
        if (change) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                invalidateOutline();
            }
        }

    }

    /**
     * Get the fractional corner radius of curvature.
     *
     * @return Fractional radius of curvature with respect to smallest size
     */
    public float getRoundPercent() {
        return mRoundPercent;
    }

    /**
     * Get the corner radius of curvature NaN = RoundPercent in effect.
     *
     * @return Radius of curvature
     */
    public float getRound() {
        return mRound;
    }

    @Override
    public void draw(Canvas canvas) {
        boolean clip = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (mRoundPercent != 0.0f && mPath != null) {
                clip = true;
                canvas.save();
                canvas.clipPath(mPath);
            }
        }
        super.draw(canvas);
        if (clip) {
            canvas.restore();
        }
    }
}
