/*
 * Copyright (C) 2018 The Android Open Source Project
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
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.widget.R;

import static android.widget.TextView.AUTO_SIZE_TEXT_TYPE_NONE;

/**
 * This class is designed to support resizing in MotionLayout more efficiently
 * It also support rounding the border
 */
public class MotionLabel extends View {
    static String TAG = "MotionLabel";
    PathMeasure mMeasure = new PathMeasure();
    float[] mDrawPoints = new float[10000];
    TextPaint mPaint = new TextPaint();
    int mCount = 0;
    Path mPath = new Path();
    private int mTextFillColor = 0xFFFF;
    private int mTextOutlineColor = 0xFFFF;
    private boolean mUseOutline = false;
    private float mRoundPercent = 0; // rounds the corners as a percent
    private float mRound = Float.NaN; // rounds the corners in dp if NaN RoundPercent is in effect
    ViewOutlineProvider mViewOutlineProvider;
    RectF mRect;

    private int mTextSize = 48;
    private int mStyleIndex;
    private int mTypefaceIndex;
    private float mTextOutlineThickness = 0;
    private String mText = "Hello World";
    boolean mNotBuilt = true;
    private Rect mTextBounds = new Rect();
    private CharSequence mTransformed;
    private int mPaddingLeft = 1;
    private int mPaddingRight = 1;
    private int mPaddingTop = 1;
    private int mPaddingBottom = 1;
    private String mFontFamily;
    //    private StaticLayout mStaticLayout;
    private Layout mLayout;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE = 3;
    private int mGravity = Gravity.TOP | Gravity.START;
    private int mAutoSizeTextType = AUTO_SIZE_TEXT_TYPE_NONE;
    private boolean mAutoSize = false; // decided during measure

    public MotionLabel(Context context) {
        super(context);
        init(context, null);
    }

    public MotionLabel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MotionLabel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        setUpTheme(context, attrs);

        if (attrs != null) {
            TypedArray a = getContext()
                    .obtainStyledAttributes(attrs, R.styleable.MotionLabel);
            final int N = a.getIndexCount();

            int k = 0;
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.MotionLabel_android_text) {
                    setText(a.getText(attr));
                } else if (attr == R.styleable.MotionLabel_android_fontFamily) {
                    mFontFamily = a.getString(attr);
                } else if (attr == R.styleable.MotionLabel_android_textSize) {
                    mTextSize = a.getDimensionPixelSize(attr, mTextSize);
                } else if (attr == R.styleable.MotionLabel_android_textStyle) {
                    mStyleIndex = a.getInt(attr, mStyleIndex);
                } else if (attr == R.styleable.MotionLabel_android_typeface) {
                    mTypefaceIndex = a.getInt(attr, mTypefaceIndex);
                } else if (attr == R.styleable.MotionLabel_android_textColor) {
                    mTextFillColor = a.getColor(attr, mTextFillColor);
                } else if (attr == R.styleable.MotionLabel_borderRound) {
                    mRound = a.getDimension(attr, mRound);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setRound(mRound);
                    }
                } else if (attr == R.styleable.MotionLabel_borderRoundPercent) {
                    mRoundPercent = a.getFloat(attr, mRoundPercent);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setRoundPercent(mRoundPercent);
                    }
                } else if (attr == R.styleable.MotionLabel_android_gravity) {
                    setGravity(a.getInt(attr, -1));
                } else if (attr == R.styleable.MotionLabel_android_autoSizeTextType ) {
                    mAutoSizeTextType = a.getInt(attr, AUTO_SIZE_TEXT_TYPE_NONE);
                }
            }
            a.recycle();
        }

        setupPath();
    }

    /**
     * Sets the horizontal alignment of the text and the
     * vertical gravity that will be used when there is extra space
     * in the TextView beyond what is required for the text itself.
     *
     * @attr ref android.R.styleable#TextView_gravity
     * @see android.view.Gravity
     */
    public void setGravity(int gravity) {
        if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.START;
        }
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.TOP;
        }
        boolean newLayout = false;
        if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) !=
                (mGravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK)) {
            newLayout = true;
        }
        if (gravity != mGravity) {
            invalidate();

        }
        mGravity = gravity;

    }

    int getHorizontalOffset() {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        int hoffset = 0;
        final int gravity = mGravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        mPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);

        if (gravity != Gravity.LEFT) {
            int boxWidth = getMeasuredWidth() - getPaddingLeft() -
                    getPaddingRight();

            int textWidth = mTextBounds.width();
            if (textWidth < boxWidth) {
                if (gravity == Gravity.RIGHT)
                    hoffset = boxWidth - textWidth;
                else // (gravity == Gravity.CENTER_VERTICAL)
                    hoffset = (boxWidth - textWidth) >> 1;
            }
        }
        return hoffset;
    }

    int getVerticalOffset() {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        int voffset = 0;
        final int gravity = mGravity & Gravity.VERTICAL_GRAVITY_MASK;

        if (gravity != Gravity.TOP) {
            int boxht = getMeasuredHeight() - getPaddingTop() -
                    getPaddingBottom();

            int textht = (int) (fm.bottom - fm.ascent);
            if (textht < boxht) {
                if (gravity == Gravity.BOTTOM)
                    voffset = boxht - textht;
                else // (gravity == Gravity.CENTER_VERTICAL)
                    voffset = (boxht - textht) >> 1;
            }
        }
        return voffset - (int) fm.ascent;
    }

    private void setUpTheme(Context context, @Nullable AttributeSet attrs) {
        TypedValue typedValue = new TypedValue();
        final Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        mPaint.setColor(mTextFillColor = typedValue.data);

    }


    private void setText(CharSequence text) {
        mText = text.toString();
    }

    void setupPath() {
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();
        setTypefaceFromAttrs(mFontFamily, mTypefaceIndex, mStyleIndex);
        mPaint.setColor(mTextFillColor);
        mPaint.setStrokeWidth(mTextOutlineThickness);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        setRawTextSize(mTextSize);

        mPaint.setAntiAlias(true);
        //   mLayout = new StaticLayout(mText, mPaint, getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 0, true);
    }

    void buildShape() {
        if (!mUseOutline) {
            return;
        }
        mPath.reset();
        String str = mText.toString();
        int strlen = str.length();
        mPaint.setTextSize(getHeight());
        mPaint.getTextBounds(str, 0, strlen, mTextBounds);
        mPaint.getTextPath(str, 0, strlen, 0, 0, mPath);
        Matrix matrix = new Matrix();
        mTextBounds.right--;
        mTextBounds.left++;
        mTextBounds.bottom++;
        mTextBounds.top--;
        RectF src = new RectF(mTextBounds);
        RectF rect = new RectF();
        rect.bottom = getHeight();
        rect.right = getWidth();
        matrix.setRectToRect(src, rect, Matrix.ScaleToFit.CENTER);
        mPath.transform(matrix);
        mNotBuilt = false;
    }

    Rect mTempRect;
    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        if (mAutoSize) {

            if (mTempRect == null) {

                mTempRect = new Rect();
            }

            Rect mTempRect = new Rect();
            mPaint.getTextBounds(mText,0,mText.length(),mTempRect);
            int tw = mTempRect.width();
            int th =  (int) (1.3f*mTempRect.height());
            int vw = r-l - mPaddingRight-mPaddingLeft;
            int vh = b-t - mPaddingBottom-mPaddingTop;
            Log.v(TAG, Debug.getLoc()+" vh = "+vh+" ="+b+" ="+t+" mPaddingBottom ="+mPaddingBottom+" mPaddingTop ="+mPaddingTop);
            if (tw*vh > th*vw) { // width limited tw/vw > th/vh
                Log.v(TAG, Debug.getLoc()+"(tw*vh > th*vw) tw,th vw,vh = "+tw+" ,"+th+"  "+vw+" , "+vh);
                mPaint.setTextSize ( (mPaint.getTextSize() * vw)/(tw));
            } else { // height limited
                Log.v(TAG, Debug.getLoc()+"(=======else============) tw,th vw,vh = "+tw+" ,"+th+"  "+vw+" , "+vh);
                Log.v(TAG, Debug.getLoc()+"(=======else============) tmPaint.getTextSize() = "+mPaint.getTextSize());
                mPaint.setTextSize ( (mPaint.getTextSize() * vh)/( th));
            }

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!mUseOutline) {
            int x = mPaddingLeft + getHorizontalOffset();
            int y = mPaddingTop + getVerticalOffset();
            Log.v(TAG, Debug.getLoc() + " " + x + " , " + y);
            //mPaint.setColor(Color.BLACK);
            canvas.drawText(mText, x, y, mPaint);
            return;
        }
        if (mNotBuilt) {
            buildShape();
        }
        if (mUseOutline) {
            mPaint.setColor(mTextFillColor);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(mTextOutlineThickness);
            canvas.drawPath(mPath, mPaint);

            mPaint.setColor(mTextOutlineColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mTextOutlineThickness);
            canvas.drawPath(mPath, mPaint);
        } else {
            mPaint.setColor(mTextFillColor);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setStrokeWidth(mTextOutlineThickness);
            canvas.drawPath(mPath, mPaint);
        }
    }

    public void setTextOutlineThickness(float width) {
        mTextOutlineThickness = width;
        invalidate();
    }

    public void setTextFillColor(int color) {
        mTextFillColor = color;
        invalidate();
    }

    public void setTextOutlineColor(int color) {
        mTextOutlineColor = color;
        invalidate();
    }

    private void setTypefaceFromAttrs(String familyName, int typefaceIndex, int styleIndex) {
        Typeface tf = null;
        if (familyName != null) {
            tf = Typeface.create(familyName, styleIndex);
            if (tf != null) {
                setTypeface(tf);
                return;
            }
        }
        switch (typefaceIndex) {
            case SANS:
                tf = Typeface.SANS_SERIF;
                break;
            case SERIF:
                tf = Typeface.SERIF;
                break;
            case MONOSPACE:
                tf = Typeface.MONOSPACE;
                break;
        }

        if (styleIndex > 0) {
            if (tf == null) {
                tf = Typeface.defaultFromStyle(styleIndex);
            } else {
                tf = Typeface.create(tf, styleIndex);
            }
            setTypeface(tf);
            // now compute what (if any) algorithmic styling is needed
            int typefaceStyle = tf != null ? tf.getStyle() : 0;
            int need = styleIndex & ~typefaceStyle;
            mPaint.setFakeBoldText((need & Typeface.BOLD) != 0);
            mPaint.setTextSkewX((need & Typeface.ITALIC) != 0 ? -0.25f : 0);
        } else {
            mPaint.setFakeBoldText(false);
            mPaint.setTextSkewX(0);
            setTypeface(tf);
        }
    }


    private void setTypeface(Typeface tf) {
        if (mPaint.getTypeface() != tf) {
            mPaint.setTypeface(tf);
            if (mLayout != null) {
                mLayout = null;
                requestLayout();
                invalidate();
            }
        }
    }

    /**
     * @return the current typeface and style in which the text is being
     * displayed.
     * @attr ref android.R.styleable#TextView_fontFamily
     * @attr ref android.R.styleable#TextView_typeface
     * @attr ref android.R.styleable#TextView_textStyle
     * @see #setTypeface(Typeface)
     */
    public Typeface getTypeface() {
        return mPaint.getTypeface();
    }


    //   @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int width = widthSize;
        int height = heightSize;

            mAutoSize = false;

        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();
        if (widthMode != View.MeasureSpec.EXACTLY || heightMode != View.MeasureSpec.EXACTLY) {
            mPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
            // WIDTH
            if (widthMode != View.MeasureSpec.EXACTLY) {
                width = (int) (mTextBounds.width() + 0.99999f);
            }
            width += mPaddingLeft + mPaddingRight;

            if (heightMode != View.MeasureSpec.EXACTLY) {
                int desired = (int) (mPaint.getFontMetricsInt(null) + 0.99999f);
                if (heightMode == View.MeasureSpec.AT_MOST) {
                    Log.v(TAG, Debug.getLoc() + "# View.MeasureSpec.AT_MOST");

                    height = Math.min(height, desired);
                } else {
                    height = desired;
                }
                height += mPaddingTop + mPaddingBottom;
            }
        }  else {
            if (mAutoSizeTextType != AUTO_SIZE_TEXT_TYPE_NONE) {
                mAutoSize = true;
            }

        }

        setMeasuredDimension(width, height);
    }

    //============================= rounding ==============================================

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
    //===========================================================================================

    /**
     * set text size
     *
     * @param size
     */
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * Set the default text size to a given unit and value.  See {@link
     * TypedValue} for the possible dimension units.
     *
     * @param unit The desired dimension unit.
     * @param size The desired size in the given units.
     * @attr ref android.R.styleable#TextView_textSize
     */
    public void setTextSize(int unit, float size) {
        Context c = getContext();
        Resources r;
        if (c == null)
            r = Resources.getSystem();
        else
            r = c.getResources();
        setRawTextSize(TypedValue.applyDimension(
                unit, size, r.getDisplayMetrics()));
    }

    private void setRawTextSize(float size) {
        Log.v(TAG, Debug.getLoc() + " size = " + size);
        if (size != mPaint.getTextSize()) {
            mPaint.setTextSize(size);
            if (mLayout != null) {
                requestLayout();
                invalidate();
            }
        }
    }
}
