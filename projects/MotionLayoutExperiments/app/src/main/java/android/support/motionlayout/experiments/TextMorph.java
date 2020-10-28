package android.support.motionlayout.experiments;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.Debug;

import java.util.Arrays;

public class TextMorph extends View {
    static String TAG = "TextMorph";
    PathMeasure mMeasure = new PathMeasure();
    float[] mDrawPoints = new float[10000];
    TextPaint mPaint = new TextPaint();
    int mCount = 0;
    Path mPath = new Path();
    private int mTextFillColor = 0xFFFF;
    private int mTextOutlineColor = 0xFFFF;
    private boolean mUseOutline = false;

    private int mTextSize;
    private int mStyleIndex;
    private int mTypefaceIndex;
    private float mTextOutlineThickness = 0;
    private CharSequence  mText = "Hello World";
    boolean mNotBuilt = true;
    private Rect mTextBounds = new Rect();
    private CharSequence mTransformed;
    private int mPaddingLeft =1;
    private int mPaddingRight = 1;
    private int mPaddingTop = 1;
    private int mPaddingBottom = 1;
    private String mFontFamily;
//    private StaticLayout mStaticLayout;
    private Layout mLayout;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int MONOSPACE = 3;

    public TextMorph(Context context) {
        super(context);
        init(context, null);
    }

    public TextMorph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TextMorph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }



    private void init(Context context, AttributeSet attrs) {


        if (attrs != null) {
            TypedArray a = getContext()
                    .obtainStyledAttributes(attrs, androidx.constraintlayout.widget.R.styleable.MotionLabel);
            final int N = a.getIndexCount();

            int k = 0;
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == androidx.constraintlayout.widget.R.styleable.MotionLabel_android_text) {
                    setText(a.getText(attr));
                } else  if (attr == androidx.constraintlayout.widget.R.styleable.MotionLabel_textFillColor) {
                    mTextFillColor = a.getColor(attr, mTextFillColor);
                }  else  if (attr == androidx.constraintlayout.widget.R.styleable.MotionLabel_android_fontFamily) {
                    mFontFamily = a.getString(attr);
                }   else  if (attr == androidx.constraintlayout.widget.R.styleable.MotionLabel_android_textSize) {
                    mTextSize = a.getDimensionPixelSize(attr, mTextSize);
                }  else  if (attr == androidx.constraintlayout.widget.R.styleable.MotionLabel_android_textStyle) {
                    mStyleIndex = a.getInt(attr, mStyleIndex);
                }  else  if (attr == androidx.constraintlayout.widget.R.styleable.MotionLabel_android_typeface) {
                    mTypefaceIndex = a.getInt(attr, mTypefaceIndex);
                }  else  if (attr == androidx.constraintlayout.widget.R.styleable.MotionLabel_textOutlineColor) {
                    mTextOutlineColor = a.getColor(attr,mTextOutlineColor);
                    mUseOutline = true;
                }  else  if (attr == androidx.constraintlayout.widget.R.styleable.MotionLabel_textOutlineThickness) {
                    mTextOutlineThickness = a.getDimension(attr, mTextOutlineThickness);
                }
            }
            a.recycle();
        }

        setupPath();
    }



    private void setText(CharSequence text) {
        mText = text;
    }

    void setupPath() {
        setTypefaceFromAttrs(mFontFamily,mTypefaceIndex,mStyleIndex);
        mPaint.setColor(mTextFillColor);
        mPaint.setStrokeWidth(mTextOutlineThickness);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        Log.v(TAG,Debug.getLoc()+" ====  #"+Integer.toHexString(mTextOutlineColor)+" , #"+Integer.toHexString(mTextFillColor)+" , "+mTextOutlineThickness);
        mLayout =new StaticLayout(mText,mPaint,getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 0,   true);
    }

    void buildShape() {
        mPath.reset();
        String str = mText.toString();
        int strlen = str.length();
        mPaint.setTextSize(getHeight());
        mPaint.getTextBounds(str, 0, strlen, mTextBounds);
        Log.v(TAG, Debug.getLoc()+" "+mTextBounds);
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

    public void setTextFillColor(int color){
        mTextFillColor = color;
        invalidate();
    }

    public void setTextOutlineColor(int color){
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
     *
     * @see #setTypeface(Typeface)
     *
     * @attr ref android.R.styleable#TextView_fontFamily
     * @attr ref android.R.styleable#TextView_typeface
     * @attr ref android.R.styleable#TextView_textStyle
     */
    public Typeface getTypeface() {
        return mPaint.getTypeface();
    }


    void buildHack () {
        float w2 = getWidth() / 2.0f;
        float h2 = getHeight() / 2.0f;
        Path path = new Path();
        mMeasure.setPath(mPath, false);

        float[] pos = new float[2];
        mCount = 0;
        mDrawPoints = new float[0];

        float step = 0.1f;
        int xsum = 0;
        int ysum = 0;
        mPath.reset();
        do {
            float len = mMeasure.getLength();
            mDrawPoints = Arrays.copyOf(mDrawPoints, mDrawPoints.length + 4 + 2 * (int) (len / step));

            for (float i = 0; i < len; i += step) {
                mMeasure.getPosTan(i, pos, null);
                mDrawPoints[mCount] = w2 / 3 + pos[0] * 5;
                mDrawPoints[mCount + 1] = h2 + pos[1] * 5;
                if (i == 0) {
                    mPath.moveTo(mDrawPoints[mCount], mDrawPoints[mCount + 1]);
                } else {
                    mPath.lineTo(mDrawPoints[mCount], mDrawPoints[mCount + 1]);

                }
                xsum += pos[0];
                ysum += pos[1];
                Log.v(TAG, Debug.getLoc() + " [" + mCount + "]= " + pos[0] + ", " + pos[1]);
            }
            mPath.close();
        }
        while (mMeasure.nextContour());
        Log.v(TAG, Debug.getLoc() + " count =" + mCount + "  " + (xsum / (float) mCount) + ", " + (ysum / (float) mCount));
    }


    //   @Override
    protected void onMeasure2(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width=1;
        int height=heightSize;

        int des = -1;
        boolean fromexisting = false;
        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            width = widthSize;
        } else {


            if (des < 0) {
                des = (int)  (Layout.getDesiredWidth(mTransformed, mPaint)+0.99999f);
            }
            width = des;
        }

        width += mPaddingLeft  + mPaddingRight;

        if (heightMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            height = heightSize;

        } else {
            int pad =mPaddingTop + mPaddingBottom;
            int desired = 2;//Layout.getLineTop(1);

//            desired += pad;

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(desired, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }

}
