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

package android.support.constraint.app;

import android.animation.StateListAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.LocaleList;
import android.os.Parcelable;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Display;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewOverlay;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.ViewStructure;
import android.view.ViewTreeObserver;
import android.view.WindowId;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.Animation;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.textclassifier.TextClassifier;
import android.widget.Scroller;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class DebugButton extends AppCompatButton {

    private static final String TAG = "DebugButton";

    StackTrak mStackTrak = null;
    int count = 0;

    private void log(String... strs) {
        if (true) return;
        Debug.logStack(TAG,"",2);
        if (true) return;
        Throwable throwable = new Throwable();
        StackTraceElement[] st = throwable.getStackTrace();

        if (mStackTrak == null) {
            mStackTrak = StackTrak.buildTree(st, 1);
        } else {
            Log.v(TAG, " " + mStackTrak.addToTree(st, 1));
        }
        if (count++ > 10) {
            StackTrak s = mStackTrak.findFirstBranch();
            if (s != null) {
                Log.v(TAG, s.toString());
                count = 0;
                mStackTrak = null;
            }
        }
        if (true) return;
        Thread thread = Thread.currentThread();

        StackTraceElement s = st[1];
        String str = " > " + thread.getName() + " " + s.getMethodName() + "(";
        for (int i = 0; i < strs.length; i++) {
            str += (i > 0 ? "," : " ") + strs[i];
        }
        str += ")";
        for (int i = 1; i < st.length; i++) {
            StackTraceElement stackTraceElement = st[i];

            if (i < 4 || st[i].getClassName().startsWith("android.support."))
                str += "\n step " + i + " .(" + st[i].getFileName() + ":" + st[i].getLineNumber() + ") " + st[i].getMethodName() + "()";
        }

        Log.v(TAG, str);
    }

    public DebugButton(Context context) {
        super(context);
        log();

    }

    public DebugButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        log();

    }

    public DebugButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        log();
    }

    @Override
    public void setBackgroundResource(int resId) {
        log();
        super.setBackgroundResource(resId);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        log();
        super.setBackgroundDrawable(background);
    }


    @Override
    protected void drawableStateChanged() {
        log();
        super.drawableStateChanged();
    }

    @Override
    public void setTextAppearance(Context context, int resId) {
        log();
        super.setTextAppearance(context, resId);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        log();
        super.onInitializeAccessibilityEvent(event);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        log();
        super.onInitializeAccessibilityNodeInfo(info);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        log();
        View v = (View) getParent();
        //Debug.logStack(TAG,"foo ", 18);
      //  Log.v(TAG,Debug.getLoc()+ " parent ("+v.getWidth() + ", " + v.getHeight() + ") view: " + left + ", " + top);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void setTextSize(int unit, float size) {
        log();
        super.setTextSize(unit, size);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        log();
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    public void setSupportAllCaps(boolean allCaps) {
        log();
        super.setSupportAllCaps(allCaps);
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        log();
        return super.getAccessibilityClassName();
    }

    @Override
    public PointerIcon onResolvePointerIcon(MotionEvent event, int pointerIndex) {
        log();
        return super.onResolvePointerIcon(event, pointerIndex);
    }

    @Override
    public void setEnabled(boolean enabled) {
        log();
        super.setEnabled(enabled);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        log();
        super.setTypeface(tf, style);
    }

    @Override
    protected boolean getDefaultEditable() {
        log();
        return super.getDefaultEditable();
    }

    @Override
    protected MovementMethod getDefaultMovementMethod() {
        log();
        return super.getDefaultMovementMethod();
    }

    @Override
    public CharSequence getText() {
        log();
        return super.getText();
    }

    @Override
    public int length() {
        log();
        return super.length();
    }

    @Override
    public Editable getEditableText() {
        log();
        return super.getEditableText();
    }

    @Override
    public int getLineHeight() {
        log();
        return super.getLineHeight();
    }

    @Override
    public void setKeyListener(KeyListener input) {
        log();
        super.setKeyListener(input);
    }


    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        log();
        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(int left, int top, int right, int bottom) {
        log();
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        log();
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    @Override
    public void setCompoundDrawablesRelative(Drawable start, Drawable top, Drawable end, Drawable bottom) {
        log();
        super.setCompoundDrawablesRelative(start, top, end, bottom);
    }

    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int start, int top, int end, int bottom) {
        log();
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
    }

    @Override
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(Drawable start, Drawable top, Drawable end, Drawable bottom) {
        log();
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
    }

    @Override
    public Drawable[] getCompoundDrawables() {
        log();
        return super.getCompoundDrawables();
    }

    @Override
    public Drawable[] getCompoundDrawablesRelative() {
        log();
        return super.getCompoundDrawablesRelative();
    }

    @Override
    public void setCompoundDrawablePadding(int pad) {
        log();
        super.setCompoundDrawablePadding(pad);
    }

    @Override
    public int getCompoundDrawablePadding() {
        log();
        return super.getCompoundDrawablePadding();
    }

    @Override
    public void setCompoundDrawableTintList(ColorStateList tint) {
        log();
        super.setCompoundDrawableTintList(tint);
    }

    @Override
    public ColorStateList getCompoundDrawableTintList() {
        log();
        return super.getCompoundDrawableTintList();
    }

    @Override
    public void setCompoundDrawableTintMode(PorterDuff.Mode tintMode) {
        log();
        super.setCompoundDrawableTintMode(tintMode);
    }

    @Override
    public PorterDuff.Mode getCompoundDrawableTintMode() {
        log();
        return super.getCompoundDrawableTintMode();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        log();
        super.setPadding(left, top, right, bottom);
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        log();
        super.setPaddingRelative(start, top, end, bottom);
    }

    @Override
    public void setTextAppearance(int resId) {
        log();
        super.setTextAppearance(resId);
    }

    @Override
    public Locale getTextLocale() {
        log();
        return super.getTextLocale();
    }

    @Override
    public LocaleList getTextLocales() {
        log();
        return super.getTextLocales();
    }

    @Override
    public void setTextLocale(Locale locale) {
        log();
        super.setTextLocale(locale);
    }

    @Override
    public void setTextLocales(LocaleList locales) {
        log();
        super.setTextLocales(locales);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        log();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public float getTextSize() {
        log();
        return super.getTextSize();
    }

    @Override
    public void setTextSize(float size) {
        log();
        super.setTextSize(size);
    }

    @Override
    public float getTextScaleX() {
        log();
        return super.getTextScaleX();
    }

    @Override
    public void setTextScaleX(float size) {
        log();
        super.setTextScaleX(size);
    }

    @Override
    public void setTypeface(Typeface tf) {
        log();
        super.setTypeface(tf);
    }

    @Override
    public Typeface getTypeface() {
        log();
        return super.getTypeface();
    }

    @Override
    public void setElegantTextHeight(boolean elegant) {
        log();
        super.setElegantTextHeight(elegant);
    }

    @Override
    public float getLetterSpacing() {
        log();
        return super.getLetterSpacing();
    }

    @Override
    public void setLetterSpacing(float letterSpacing) {
        log();
        super.setLetterSpacing(letterSpacing);
    }

    @Override
    public String getFontFeatureSettings() {
        log();
        return super.getFontFeatureSettings();
    }

    @Override
    public String getFontVariationSettings() {
        log();
        return super.getFontVariationSettings();
    }

    @Override
    public void setBreakStrategy(int breakStrategy) {
        log();
        super.setBreakStrategy(breakStrategy);
    }

    @Override
    public int getBreakStrategy() {
        log();
        return super.getBreakStrategy();
    }

    @Override
    public void setHyphenationFrequency(int hyphenationFrequency) {
        log();
        super.setHyphenationFrequency(hyphenationFrequency);
    }

    @Override
    public int getHyphenationFrequency() {
        log();
        return super.getHyphenationFrequency();
    }

    @Override
    public void setJustificationMode(int justificationMode) {
        log();
        super.setJustificationMode(justificationMode);
    }

    @Override
    public int getJustificationMode() {
        log();
        return super.getJustificationMode();
    }

    @Override
    public void setFontFeatureSettings(String fontFeatureSettings) {
        log();
        super.setFontFeatureSettings(fontFeatureSettings);
    }

    @Override
    public boolean setFontVariationSettings(String fontVariationSettings) {
        log();
        return super.setFontVariationSettings(fontVariationSettings);
    }

    @Override
    public void setTextColor(int color) {
        log();
        super.setTextColor(color);
    }

    @Override
    public void setTextColor(ColorStateList colors) {
        log();
        super.setTextColor(colors);
    }

    @Override
    public void setHighlightColor(int color) {
        log();
        super.setHighlightColor(color);
    }

    @Override
    public int getHighlightColor() {
        log();
        return super.getHighlightColor();
    }

    @Override
    public void setShadowLayer(float radius, float dx, float dy, int color) {
        log();
        super.setShadowLayer(radius, dx, dy, color);
    }

    @Override
    public float getShadowRadius() {
        log();
        return super.getShadowRadius();
    }

    @Override
    public float getShadowDx() {
        log();
        return super.getShadowDx();
    }

    @Override
    public float getShadowDy() {
        log();
        return super.getShadowDy();
    }

    @Override
    public int getShadowColor() {
        log();
        return super.getShadowColor();
    }

    @Override
    public TextPaint getPaint() {
        log();
        return super.getPaint();
    }

    @Override
    public URLSpan[] getUrls() {
        log();
        return super.getUrls();
    }

    @Override
    public void setGravity(int gravity) {
        log();
        super.setGravity(gravity);
    }

    @Override
    public int getGravity() {
        log();
        return super.getGravity();
    }

    @Override
    public int getPaintFlags() {
        log();
        return super.getPaintFlags();
    }

    @Override
    public void setPaintFlags(int flags) {
        log();
        super.setPaintFlags(flags);
    }

    @Override
    public void setHorizontallyScrolling(boolean whether) {
        log();
        super.setHorizontallyScrolling(whether);
    }

    @Override
    public void setMinLines(int minLines) {
        log();
        super.setMinLines(minLines);
    }

    @Override
    public int getMinLines() {
        log();
        return super.getMinLines();
    }

    @Override
    public void setMinHeight(int minPixels) {
        log();
        super.setMinHeight(minPixels);
    }

    @Override
    public int getMinHeight() {
        log();
        return super.getMinHeight();
    }

    @Override
    public void setMaxLines(int maxLines) {
        log();
        super.setMaxLines(maxLines);
    }

    @Override
    public int getMaxLines() {
        log();
        return super.getMaxLines();
    }

    @Override
    public void setMaxHeight(int maxPixels) {
        log();
        super.setMaxHeight(maxPixels);
    }

    @Override
    public int getMaxHeight() {
        log();
        return super.getMaxHeight();
    }

    @Override
    public void setLines(int lines) {
        log();
        super.setLines(lines);
    }

    @Override
    public void setHeight(int pixels) {
        log();
        super.setHeight(pixels);
    }

    @Override
    public void setMinEms(int minEms) {
        log();
        super.setMinEms(minEms);
    }

    @Override
    public int getMinEms() {
        log();
        return super.getMinEms();
    }

    @Override
    public void setMinWidth(int minPixels) {
        log();
        super.setMinWidth(minPixels);
    }

    @Override
    public int getMinWidth() {
        log();
        return super.getMinWidth();
    }

    @Override
    public void setMaxEms(int maxEms) {
        log();
        super.setMaxEms(maxEms);
    }

    @Override
    public int getMaxEms() {
        log();
        return super.getMaxEms();
    }

    @Override
    public void setMaxWidth(int maxPixels) {
        log();
        super.setMaxWidth(maxPixels);
    }

    @Override
    public int getMaxWidth() {
        log();
        return super.getMaxWidth();
    }

    @Override
    public void setEms(int ems) {
        log();
        super.setEms(ems);
    }

    @Override
    public void setWidth(int pixels) {
        log();
        super.setWidth(pixels);
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        log();
        super.setLineSpacing(add, mult);
    }

    @Override
    public float getLineSpacingMultiplier() {
        log();
        return super.getLineSpacingMultiplier();
    }

    @Override
    public float getLineSpacingExtra() {
        log();
        return super.getLineSpacingExtra();
    }

    @Override
    public void append(CharSequence text, int start, int end) {
        log();
        super.append(text, start, end);
    }

    @Override
    public void drawableHotspotChanged(float x, float y) {
        log();
        super.drawableHotspotChanged(x, y);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        log();
        return super.onSaveInstanceState();
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        log();
        super.onRestoreInstanceState(state);
    }

    @Override
    public void setFreezesText(boolean freezesText) {
        log();
        super.setFreezesText(freezesText);
    }

    @Override
    public boolean getFreezesText() {
        log();
        return super.getFreezesText();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        log();
        super.setText(text, type);
    }

    @Override
    public CharSequence getHint() {
        log();
        return super.getHint();
    }

    @Override
    public void setInputType(int type) {
        log();
        super.setInputType(type);
    }

    @Override
    public void setRawInputType(int type) {
        log();
        super.setRawInputType(type);
    }

    @Override
    public int getInputType() {
        log();
        return super.getInputType();
    }

    @Override
    public void setImeOptions(int imeOptions) {
        log();
        super.setImeOptions(imeOptions);
    }

    @Override
    public int getImeOptions() {
        log();
        return super.getImeOptions();
    }

    @Override
    public void setImeActionLabel(CharSequence label, int actionId) {
        log();
        super.setImeActionLabel(label, actionId);
    }

    @Override
    public CharSequence getImeActionLabel() {
        log();
        return super.getImeActionLabel();
    }

    @Override
    public int getImeActionId() {
        log();
        return super.getImeActionId();
    }

    @Override
    public void setOnEditorActionListener(OnEditorActionListener l) {
        log();
        super.setOnEditorActionListener(l);
    }

    @Override
    public void onEditorAction(int actionCode) {
        log();
        super.onEditorAction(actionCode);
    }

    @Override
    public void setPrivateImeOptions(String type) {
        log();
        super.setPrivateImeOptions(type);
    }

    @Override
    public String getPrivateImeOptions() {
        log();
        return super.getPrivateImeOptions();
    }

    @Override
    public void setInputExtras(int xmlResId) throws XmlPullParserException, IOException {
        log();
        super.setInputExtras(xmlResId);
    }

    @Override
    public Bundle getInputExtras(boolean create) {
        log();
        return super.getInputExtras(create);
    }

    @Override
    public void setImeHintLocales(LocaleList hintLocales) {
        log();
        super.setImeHintLocales(hintLocales);
    }

    @Override
    public LocaleList getImeHintLocales() {
        log();
        return super.getImeHintLocales();
    }

    @Override
    public CharSequence getError() {
        log();
        return super.getError();
    }

    @Override
    public void setError(CharSequence error) {
        log();
        super.setError(error);
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        log();
        super.setError(error, icon);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        log();
        return super.setFrame(l, t, r, b);
    }

    @Override
    public void setFilters(InputFilter[] filters) {
        log();
        super.setFilters(filters);
    }

    @Override
    public InputFilter[] getFilters() {
        log();
        return super.getFilters();
    }

    @Override
    public boolean onPreDraw() {
        log();
        return super.onPreDraw();
    }

    @Override
    protected void onAttachedToWindow() {
        log();
        super.onAttachedToWindow();
    }

    @Override
    public void onScreenStateChanged(int screenState) {
        log();
        super.onScreenStateChanged(screenState);
    }

    @Override
    protected boolean isPaddingOffsetRequired() {
        log();
        return super.isPaddingOffsetRequired();
    }

    @Override
    protected int getLeftPaddingOffset() {
        log();
        return super.getLeftPaddingOffset();
    }

    @Override
    protected int getTopPaddingOffset() {
        log();
        return super.getTopPaddingOffset();
    }

    @Override
    protected int getBottomPaddingOffset() {
        log();
        return super.getBottomPaddingOffset();
    }

    @Override
    protected int getRightPaddingOffset() {
        log();
        return super.getRightPaddingOffset();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        log();
        return super.verifyDrawable(who);
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        log();
        super.jumpDrawablesToCurrentState();
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        log();
        super.invalidateDrawable(drawable);
    }

    @Override
    public boolean hasOverlappingRendering() {
        log();
        return super.hasOverlappingRendering();
    }

    @Override
    public boolean isTextSelectable() {
        log();
        return super.isTextSelectable();
    }

    @Override
    public void setTextIsSelectable(boolean selectable) {
        log();
        super.setTextIsSelectable(selectable);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        log();
        return super.onCreateDrawableState(extraSpace);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        log();
        super.onDraw(canvas);
        long time = System.nanoTime();
        time -= 10_000_000_000L*(time / 10_000_000_000L);
        canvas.drawText(Float.toString((float)(10_000L*(time / 10_000L))*1E-9f),getWidth()/2,getHeight()-20,getPaint());
        canvas.drawLine(0,mr_y, mr_x,0,getPaint());
        canvas.drawLine(0,0, mr_x,mr_y,getPaint());
        Log.v(TAG , Debug.getLoc()+"---------------------draw-------------------------");
    }

    @Override
    public void getFocusedRect(Rect r) {
        log();
        super.getFocusedRect(r);
    }

    @Override
    public int getLineCount() {
        log();
        return super.getLineCount();
    }

    @Override
    public int getLineBounds(int line, Rect bounds) {
        log();
        return super.getLineBounds(line, bounds);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        log();
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        log();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        log();
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        log();
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        log();
        return super.onCheckIsTextEditor();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        log();
        return super.onCreateInputConnection(outAttrs);
    }

    @Override
    public boolean extractText(ExtractedTextRequest request, ExtractedText outText) {
        log();
        return super.extractText(request, outText);
    }

    @Override
    public void setExtractedText(ExtractedText text) {
        log();
        super.setExtractedText(text);
    }

    @Override
    public void onCommitCompletion(CompletionInfo text) {
        log();
        super.onCommitCompletion(text);
    }

    @Override
    public void onCommitCorrection(CorrectionInfo info) {
        log();
        super.onCommitCorrection(info);
    }

    @Override
    public void beginBatchEdit() {
        log();
        super.beginBatchEdit();
    }

    @Override
    public void endBatchEdit() {
        log();
        super.endBatchEdit();
    }

    @Override
    public void onBeginBatchEdit() {
        log();
        super.onBeginBatchEdit();
    }

    @Override
    public void onEndBatchEdit() {
        log();
        super.onEndBatchEdit();
    }

    @Override
    public boolean onPrivateIMECommand(String action, Bundle data) {
        log();
        return super.onPrivateIMECommand(action, data);
    }

    @Override
    public void setIncludeFontPadding(boolean includepad) {
        log();
        super.setIncludeFontPadding(includepad);
    }

    @Override
    public boolean getIncludeFontPadding() {
        log();
        return super.getIncludeFontPadding();
    }
    int m_x, m_y;
    int mr_x, mr_y;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        log();
        Log.v(TAG, Debug.getLoc()+" "+MeasureSpec.toString(widthMeasureSpec) + " , "+ MeasureSpec.toString(heightMeasureSpec) );
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        m_x = MeasureSpec.getSize(widthMeasureSpec);
        m_y = MeasureSpec.getSize(heightMeasureSpec);

        mr_x = getMeasuredWidth();
        mr_y = getMeasuredHeight();
        float p = ((MotionLayout)getParent()).getProgress();
        Debug.logStack(TAG, "getMesure "+mr_x+" , "+mr_y+"  ms"+m_x+","+m_y+" p:"+p,6);
    }

    @Override
    public boolean bringPointIntoView(int offset) {
        log();
        return super.bringPointIntoView(offset);
    }

    @Override
    public boolean moveCursorToVisibleOffset() {
        log();
        return super.moveCursorToVisibleOffset();
    }

    @Override
    public void computeScroll() {
        log();
        super.computeScroll();
    }

    @Override
    public void debug(int depth) {
        log();
        super.debug(depth);
    }

    @Override
    public int getSelectionStart() {
        log();
        return super.getSelectionStart();
    }

    @Override
    public int getSelectionEnd() {
        log();
        return super.getSelectionEnd();
    }

    @Override
    public boolean hasSelection() {
        log();
        return super.hasSelection();
    }

    @Override
    public void setSingleLine() {
        log();
        super.setSingleLine();
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        log();
        super.setAllCaps(allCaps);
    }

    @Override
    public void setSingleLine(boolean singleLine) {
        log();
        super.setSingleLine(singleLine);
    }

    @Override
    public void setEllipsize(TextUtils.TruncateAt where) {
        log();
        super.setEllipsize(where);
    }

    @Override
    public void setMarqueeRepeatLimit(int marqueeLimit) {
        log();
        super.setMarqueeRepeatLimit(marqueeLimit);
    }

    @Override
    public int getMarqueeRepeatLimit() {
        log();
        return super.getMarqueeRepeatLimit();
    }

    @Override
    public TextUtils.TruncateAt getEllipsize() {
        log();
        return super.getEllipsize();
    }

    @Override
    public void setSelectAllOnFocus(boolean selectAllOnFocus) {
        log();
        super.setSelectAllOnFocus(selectAllOnFocus);
    }

    @Override
    public void setCursorVisible(boolean visible) {
        log();
        super.setCursorVisible(visible);
    }

    @Override
    public boolean isCursorVisible() {
        log();
        return super.isCursorVisible();
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        log();
        super.onSelectionChanged(selStart, selEnd);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        log();
        super.addTextChangedListener(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        log();
        super.removeTextChangedListener(watcher);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        log();
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        log();
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        log();
        super.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public void clearComposingText() {
        log();
        super.clearComposingText();
    }

    @Override
    public void setSelected(boolean selected) {
        log();
        super.setSelected(selected);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        log();
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        log();
        return super.onGenericMotionEvent(event);
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        log();
        super.onCreateContextMenu(menu);
    }

    @Override
    public boolean showContextMenu() {
        log();
        return super.showContextMenu();
    }

    @Override
    public boolean showContextMenu(float x, float y) {
        log();
        return super.showContextMenu(x, y);
    }

    @Override
    public boolean didTouchFocusSelect() {
        log();
        return super.didTouchFocusSelect();
    }

    @Override
    public void cancelLongPress() {
        log();
        super.cancelLongPress();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        log();
        return super.onTrackballEvent(event);
    }

    @Override
    public void setScroller(Scroller s) {
        log();
        super.setScroller(s);
    }

    @Override
    protected float getLeftFadingEdgeStrength() {
        log();
        return super.getLeftFadingEdgeStrength();
    }

    @Override
    protected float getRightFadingEdgeStrength() {
        log();
        return super.getRightFadingEdgeStrength();
    }

    @Override
    protected int computeHorizontalScrollRange() {
        log();
        return super.computeHorizontalScrollRange();
    }

    @Override
    protected int computeVerticalScrollRange() {
        log();
        return super.computeVerticalScrollRange();
    }

    @Override
    protected int computeVerticalScrollExtent() {
        log();
        return super.computeVerticalScrollExtent();
    }

    @Override
    public void findViewsWithText(ArrayList<View> outViews, CharSequence searched, int flags) {
        log();
        super.findViewsWithText(outViews, searched, flags);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        log();
        return super.onKeyShortcut(keyCode, event);
    }

    @Override
    public void onProvideStructure(ViewStructure structure) {
        log();
        super.onProvideStructure(structure);
    }

    @Override
    public void onProvideAutofillStructure(ViewStructure structure, int flags) {
        log();
        super.onProvideAutofillStructure(structure, flags);
    }

    @Override
    public void autofill(AutofillValue value) {
        log();
        super.autofill(value);
    }

    @Override
    public int getAutofillType() {
        log();
        return super.getAutofillType();
    }

    @Override
    public AutofillValue getAutofillValue() {
        log();
        return super.getAutofillValue();
    }

    @Override
    public void addExtraDataToAccessibilityNodeInfo(AccessibilityNodeInfo info, String extraDataKey, Bundle arguments) {
        log();
        super.addExtraDataToAccessibilityNodeInfo(info, extraDataKey, arguments);
    }

    @Override
    public boolean isInputMethodTarget() {
        log();
        return super.isInputMethodTarget();
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        log();
        return super.onTextContextMenuItem(id);
    }

    @Override
    public boolean performLongClick() {
        log();
        return super.performLongClick();
    }

    @Override
    protected void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        log();
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
    }

    @Override
    public boolean isSuggestionsEnabled() {
        log();
        return super.isSuggestionsEnabled();
    }

    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        log();
        super.setCustomSelectionActionModeCallback(actionModeCallback);
    }

    @Override
    public ActionMode.Callback getCustomSelectionActionModeCallback() {
        log();
        return super.getCustomSelectionActionModeCallback();
    }

    @Override
    public void setCustomInsertionActionModeCallback(ActionMode.Callback actionModeCallback) {
        log();
        super.setCustomInsertionActionModeCallback(actionModeCallback);
    }

    @Override
    public ActionMode.Callback getCustomInsertionActionModeCallback() {
        log();
        return super.getCustomInsertionActionModeCallback();
    }

    @Override
    public void setTextClassifier(TextClassifier textClassifier) {
        log();
        super.setTextClassifier(textClassifier);
    }

    @Override
    public TextClassifier getTextClassifier() {
        log();
        return super.getTextClassifier();
    }

    @Override
    public int getOffsetForPosition(float x, float y) {
        log();
        return super.getOffsetForPosition(x, y);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        log();
        return super.onDragEvent(event);
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        log();
        super.onRtlPropertiesChanged(layoutDirection);
    }

    @Override
    public String toString() {
        log();
        return super.toString();
    }

    @Override
    public int getVerticalFadingEdgeLength() {
        log();
        return super.getVerticalFadingEdgeLength();
    }

    @Override
    public void setFadingEdgeLength(int length) {
        log();
        super.setFadingEdgeLength(length);
    }

    @Override
    public int getHorizontalFadingEdgeLength() {
        log();
        return super.getHorizontalFadingEdgeLength();
    }

    @Override
    public int getVerticalScrollbarWidth() {
        log();
        return super.getVerticalScrollbarWidth();
    }

    @Override
    protected int getHorizontalScrollbarHeight() {
        log();
        return super.getHorizontalScrollbarHeight();
    }

    @Override
    public void setVerticalScrollbarPosition(int position) {
        log();
        super.setVerticalScrollbarPosition(position);
    }

    @Override
    public int getVerticalScrollbarPosition() {
        log();
        return super.getVerticalScrollbarPosition();
    }

    @Override
    public void setScrollIndicators(int indicators) {
        log();
        super.setScrollIndicators(indicators);
    }

    @Override
    public void setScrollIndicators(int indicators, int mask) {
        log();
        super.setScrollIndicators(indicators, mask);
    }

    @Override
    public int getScrollIndicators() {
        log();
        return super.getScrollIndicators();
    }

    @Override
    public void setOnScrollChangeListener(OnScrollChangeListener l) {
        log();
        super.setOnScrollChangeListener(l);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        log();
        super.setOnFocusChangeListener(l);
    }

    @Override
    public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
        log();
        super.addOnLayoutChangeListener(listener);
    }

    @Override
    public void removeOnLayoutChangeListener(OnLayoutChangeListener listener) {
        log();
        super.removeOnLayoutChangeListener(listener);
    }

    @Override
    public void addOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        log();
        super.addOnAttachStateChangeListener(listener);
    }

    @Override
    public void removeOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        log();
        super.removeOnAttachStateChangeListener(listener);
    }

    @Override
    public OnFocusChangeListener getOnFocusChangeListener() {
        log();
        return super.getOnFocusChangeListener();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        log();
        super.setOnClickListener(l);
    }

    @Override
    public boolean hasOnClickListeners() {
        log();
        return super.hasOnClickListeners();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        log();
        super.setOnLongClickListener(l);
    }

    @Override
    public void setOnContextClickListener(OnContextClickListener l) {
        log();
        super.setOnContextClickListener(l);
    }

    @Override
    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
        log();
        super.setOnCreateContextMenuListener(l);
    }

    @Override
    public boolean performClick() {
        log();
        return super.performClick();
    }

    @Override
    public boolean callOnClick() {
        log();
        return super.callOnClick();
    }

    @Override
    public boolean performLongClick(float x, float y) {
        log();
        return super.performLongClick(x, y);
    }

    @Override
    public boolean performContextClick(float x, float y) {
        log();
        return super.performContextClick(x, y);
    }

    @Override
    public boolean performContextClick() {
        log();
        return super.performContextClick();
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        log();
        return super.startActionMode(callback);
    }

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        log();
        return super.startActionMode(callback, type);
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        log();
        super.setOnKeyListener(l);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        log();
        super.setOnTouchListener(l);
    }

    @Override
    public void setOnGenericMotionListener(OnGenericMotionListener l) {
        log();
        super.setOnGenericMotionListener(l);
    }

    @Override
    public void setOnHoverListener(OnHoverListener l) {
        log();
        super.setOnHoverListener(l);
    }

    @Override
    public void setOnDragListener(OnDragListener l) {
        log();
        super.setOnDragListener(l);
    }

    @Override
    public boolean requestRectangleOnScreen(Rect rectangle) {
        log();
        return super.requestRectangleOnScreen(rectangle);
    }

    @Override
    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        log();
        return super.requestRectangleOnScreen(rectangle, immediate);
    }

    @Override
    public void clearFocus() {
        log();
        super.clearFocus();
    }

    @Override
    public boolean hasFocus() {
        log();
        return super.hasFocus();
    }

    @Override
    public boolean hasFocusable() {
        log();
        return super.hasFocusable();
    }

    @Override
    public boolean hasExplicitFocusable() {
        log();
        return super.hasExplicitFocusable();
    }

    @Override
    public void sendAccessibilityEvent(int eventType) {
        log();
        super.sendAccessibilityEvent(eventType);
    }

    @Override
    public void announceForAccessibility(CharSequence text) {
        log();
        super.announceForAccessibility(text);
    }

    @Override
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        log();
        super.sendAccessibilityEventUnchecked(event);
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        log();
        return super.dispatchPopulateAccessibilityEvent(event);
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        log();
        super.onPopulateAccessibilityEvent(event);
    }

    @Override
    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        log();
        return super.createAccessibilityNodeInfo();
    }

    @Override
    public void onProvideVirtualStructure(ViewStructure structure) {
        log();
        super.onProvideVirtualStructure(structure);
    }

    @Override
    public void onProvideAutofillVirtualStructure(ViewStructure structure, int flags) {
        log();
        super.onProvideAutofillVirtualStructure(structure, flags);
    }

    @Override
    public void autofill(SparseArray<AutofillValue> values) {
        log();
        super.autofill(values);
    }

    @Override
    public String[] getAutofillHints() {
        log();
        return super.getAutofillHints();
    }

    @Override
    public int getImportantForAutofill() {
        log();
        return super.getImportantForAutofill();
    }

    @Override
    public void setImportantForAutofill(int mode) {
        log();
        super.setImportantForAutofill(mode);
    }

    @Override
    public void dispatchProvideStructure(ViewStructure structure) {
        log();
        super.dispatchProvideStructure(structure);
    }

    @Override
    public void dispatchProvideAutofillStructure(ViewStructure structure, int flags) {
        log();
        super.dispatchProvideAutofillStructure(structure, flags);
    }

    @Override
    public void setAccessibilityDelegate(AccessibilityDelegate delegate) {
        log();
        super.setAccessibilityDelegate(delegate);
    }

    @Override
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        log();
        return super.getAccessibilityNodeProvider();
    }

    @Override
    public void setContentDescription(CharSequence contentDescription) {
        log();
        super.setContentDescription(contentDescription);
    }

    @Override
    public void setAccessibilityTraversalBefore(int beforeId) {
        log();
        super.setAccessibilityTraversalBefore(beforeId);
    }

    @Override
    public int getAccessibilityTraversalBefore() {
        log();
        return super.getAccessibilityTraversalBefore();
    }

    @Override
    public void setAccessibilityTraversalAfter(int afterId) {
        log();
        super.setAccessibilityTraversalAfter(afterId);
    }

    @Override
    public int getAccessibilityTraversalAfter() {
        log();
        return super.getAccessibilityTraversalAfter();
    }

    @Override
    public int getLabelFor() {
        log();
        return super.getLabelFor();
    }

    @Override
    public void setLabelFor(int id) {
        log();
        super.setLabelFor(id);
    }

    @Override
    public boolean isFocused() {
        log();
        return super.isFocused();
    }

    @Override
    public View findFocus() {
        log();
        return super.findFocus();
    }

    @Override
    public boolean isScrollContainer() {
        log();
        return super.isScrollContainer();
    }

    @Override
    public void setScrollContainer(boolean isScrollContainer) {
        log();
        super.setScrollContainer(isScrollContainer);
    }

    @Override
    public int getDrawingCacheQuality() {
        log();
        return super.getDrawingCacheQuality();
    }

    @Override
    public void setDrawingCacheQuality(int quality) {
        log();
        super.setDrawingCacheQuality(quality);
    }

    @Override
    public boolean getKeepScreenOn() {
        log();
        return super.getKeepScreenOn();
    }

    @Override
    public void setKeepScreenOn(boolean keepScreenOn) {
        log();
        super.setKeepScreenOn(keepScreenOn);
    }

    @Override
    public int getNextFocusLeftId() {
        log();
        return super.getNextFocusLeftId();
    }

    @Override
    public void setNextFocusLeftId(int nextFocusLeftId) {
        log();
        super.setNextFocusLeftId(nextFocusLeftId);
    }

    @Override
    public int getNextFocusRightId() {
        log();
        return super.getNextFocusRightId();
    }

    @Override
    public void setNextFocusRightId(int nextFocusRightId) {
        log();
        super.setNextFocusRightId(nextFocusRightId);
    }

    @Override
    public int getNextFocusUpId() {
        log();
        return super.getNextFocusUpId();
    }

    @Override
    public void setNextFocusUpId(int nextFocusUpId) {
        log();
        super.setNextFocusUpId(nextFocusUpId);
    }

    @Override
    public int getNextFocusDownId() {
        log();
        return super.getNextFocusDownId();
    }

    @Override
    public void setNextFocusDownId(int nextFocusDownId) {
        log();
        super.setNextFocusDownId(nextFocusDownId);
    }

    @Override
    public int getNextFocusForwardId() {
        log();
        return super.getNextFocusForwardId();
    }

    @Override
    public void setNextFocusForwardId(int nextFocusForwardId) {
        log();
        super.setNextFocusForwardId(nextFocusForwardId);
    }

    @Override
    public int getNextClusterForwardId() {
        log();
        return super.getNextClusterForwardId();
    }

    @Override
    public void setNextClusterForwardId(int nextClusterForwardId) {
        log();
        super.setNextClusterForwardId(nextClusterForwardId);
    }

    @Override
    public boolean isShown() {
        log();
        return super.isShown();
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {
        log();
        return super.fitSystemWindows(insets);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        log();
        return super.onApplyWindowInsets(insets);
    }

    @Override
    public void setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener listener) {
        log();
        super.setOnApplyWindowInsetsListener(listener);
    }

    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        log();
        return super.dispatchApplyWindowInsets(insets);
    }

    @Override
    public WindowInsets getRootWindowInsets() {
        log();
        return super.getRootWindowInsets();
    }

    @Override
    public WindowInsets computeSystemWindowInsets(WindowInsets in, Rect outLocalInsets) {
        log();
        return super.computeSystemWindowInsets(in, outLocalInsets);
    }

    @Override
    public void setFitsSystemWindows(boolean fitSystemWindows) {
        log();
        super.setFitsSystemWindows(fitSystemWindows);
    }

    @Override
    public boolean getFitsSystemWindows() {
        log();
        return super.getFitsSystemWindows();
    }

    @Override
    public void requestFitSystemWindows() {
        log();
        super.requestFitSystemWindows();
    }

    @Override
    public void requestApplyInsets() {
        log();
        super.requestApplyInsets();
    }

    @Override
    public int getVisibility() {
        log();
        return super.getVisibility();
    }

    @Override
    public void setVisibility(int visibility) {
        log();
        super.setVisibility(visibility);
    }

    @Override
    public boolean isEnabled() {
        log();
        return super.isEnabled();
    }

    @Override
    public void setFocusable(boolean focusable) {
        log();
        super.setFocusable(focusable);
    }

    @Override
    public void setFocusable(int focusable) {
        log();
        super.setFocusable(focusable);
    }

    @Override
    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        log();
        super.setFocusableInTouchMode(focusableInTouchMode);
    }

    @Override
    public void setAutofillHints(String... autofillHints) {
        log();
        super.setAutofillHints(autofillHints);
    }

    @Override
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        log();
        super.setSoundEffectsEnabled(soundEffectsEnabled);
    }

    @Override
    public boolean isSoundEffectsEnabled() {
        log();
        return super.isSoundEffectsEnabled();
    }

    @Override
    public void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) {
        log();
        super.setHapticFeedbackEnabled(hapticFeedbackEnabled);
    }

    @Override
    public boolean isHapticFeedbackEnabled() {
        log();
        return super.isHapticFeedbackEnabled();
    }

    @Override
    public void setLayoutDirection(int layoutDirection) {
        log();
        super.setLayoutDirection(layoutDirection);
    }

    @Override
    public int getLayoutDirection() {
        log();
        return super.getLayoutDirection();
    }

    @Override
    public boolean hasTransientState() {
        log();
        return super.hasTransientState();
    }

    @Override
    public void setHasTransientState(boolean hasTransientState) {
        log();
        super.setHasTransientState(hasTransientState);
    }

    @Override
    public boolean isAttachedToWindow() {
        log();
        return super.isAttachedToWindow();
    }

    @Override
    public boolean isLaidOut() {
        log();
        return super.isLaidOut();
    }

    @Override
    public void setWillNotDraw(boolean willNotDraw) {
        log();
        super.setWillNotDraw(willNotDraw);
    }

    @Override
    public boolean willNotDraw() {
        log();
        return super.willNotDraw();
    }

    @Override
    public void setWillNotCacheDrawing(boolean willNotCacheDrawing) {
        log();
        super.setWillNotCacheDrawing(willNotCacheDrawing);
    }

    @Override
    public boolean willNotCacheDrawing() {
        log();
        return super.willNotCacheDrawing();
    }

    @Override
    public boolean isClickable() {
        log();
        return super.isClickable();
    }

    @Override
    public void setClickable(boolean clickable) {
        log();
        super.setClickable(clickable);
    }

    @Override
    public boolean isLongClickable() {
        log();
        return super.isLongClickable();
    }

    @Override
    public void setLongClickable(boolean longClickable) {
        log();
        super.setLongClickable(longClickable);
    }

    @Override
    public boolean isContextClickable() {
        log();
        return super.isContextClickable();
    }

    @Override
    public void setContextClickable(boolean contextClickable) {
        log();
        super.setContextClickable(contextClickable);
    }

    @Override
    public void setPressed(boolean pressed) {
        log();
        super.setPressed(pressed);
    }

    @Override
    protected void dispatchSetPressed(boolean pressed) {
        log();
        super.dispatchSetPressed(pressed);
    }

    @Override
    public boolean isPressed() {
        log();
        return super.isPressed();
    }

    @Override
    public boolean isSaveEnabled() {
        log();
        return super.isSaveEnabled();
    }

    @Override
    public void setSaveEnabled(boolean enabled) {
        log();
        super.setSaveEnabled(enabled);
    }

    @Override
    public boolean getFilterTouchesWhenObscured() {
        log();
        return super.getFilterTouchesWhenObscured();
    }

    @Override
    public void setFilterTouchesWhenObscured(boolean enabled) {
        log();
        super.setFilterTouchesWhenObscured(enabled);
    }

    @Override
    public boolean isSaveFromParentEnabled() {
        log();
        return super.isSaveFromParentEnabled();
    }

    @Override
    public void setSaveFromParentEnabled(boolean enabled) {
        log();
        super.setSaveFromParentEnabled(enabled);
    }

    @Override
    public int getFocusable() {
        log();
        return super.getFocusable();
    }

    @Override
    public View focusSearch(int direction) {
        log();
        return super.focusSearch(direction);
    }

    @Override
    public void setKeyboardNavigationCluster(boolean isCluster) {
        log();
        super.setKeyboardNavigationCluster(isCluster);
    }

    @Override
    public void setFocusedByDefault(boolean isFocusedByDefault) {
        log();
        super.setFocusedByDefault(isFocusedByDefault);
    }

    @Override
    public View keyboardNavigationClusterSearch(View currentCluster, int direction) {
        log();
        return super.keyboardNavigationClusterSearch(currentCluster, direction);
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        log();
        return super.dispatchUnhandledMove(focused, direction);
    }

    @Override
    public void setDefaultFocusHighlightEnabled(boolean defaultFocusHighlightEnabled) {
        log();
        super.setDefaultFocusHighlightEnabled(defaultFocusHighlightEnabled);
    }

    @Override
    public ArrayList<View> getFocusables(int direction) {
        log();
        return super.getFocusables(direction);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction) {
        log();
        super.addFocusables(views, direction);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        log();
        super.addFocusables(views, direction, focusableMode);
    }

    @Override
    public void addKeyboardNavigationClusters(Collection<View> views, int direction) {
        log();
        super.addKeyboardNavigationClusters(views, direction);
    }

    @Override
    public ArrayList<View> getTouchables() {
        log();
        return super.getTouchables();
    }

    @Override
    public void addTouchables(ArrayList<View> views) {
        log();
        super.addTouchables(views);
    }

    @Override
    public boolean isAccessibilityFocused() {
        log();
        return super.isAccessibilityFocused();
    }

    @Override
    public boolean restoreDefaultFocus() {
        log();
        return super.restoreDefaultFocus();
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        log();
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public int getImportantForAccessibility() {
        log();
        return super.getImportantForAccessibility();
    }

    @Override
    public void setAccessibilityLiveRegion(int mode) {
        log();
        super.setAccessibilityLiveRegion(mode);
    }

    @Override
    public int getAccessibilityLiveRegion() {
        log();
        return super.getAccessibilityLiveRegion();
    }

    @Override
    public void setImportantForAccessibility(int mode) {
        log();
        super.setImportantForAccessibility(mode);
    }

    @Override
    public boolean isImportantForAccessibility() {
        log();
        return super.isImportantForAccessibility();
    }

    @Override
    public ViewParent getParentForAccessibility() {
        log();
        return super.getParentForAccessibility();
    }

    @Override
    public void addChildrenForAccessibility(ArrayList<View> outChildren) {
        log();
        super.addChildrenForAccessibility(outChildren);
    }

    @Override
    public boolean dispatchNestedPrePerformAccessibilityAction(int action, Bundle arguments) {
        log();
        return super.dispatchNestedPrePerformAccessibilityAction(action, arguments);
    }

    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        log();
        return super.performAccessibilityAction(action, arguments);
    }

    @Override
    public void dispatchStartTemporaryDetach() {
        log();
        super.dispatchStartTemporaryDetach();
    }

    @Override
    public void onStartTemporaryDetach() {
        log();
        super.onStartTemporaryDetach();
    }

    @Override
    public void dispatchFinishTemporaryDetach() {
        log();
        super.dispatchFinishTemporaryDetach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        log();
        super.onFinishTemporaryDetach();
    }

    @Override
    public KeyEvent.DispatcherState getKeyDispatcherState() {
        log();
        return super.getKeyDispatcherState();
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        log();
        return super.dispatchKeyEventPreIme(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        log();
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        log();
        return super.dispatchKeyShortcutEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        log();
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        log();
        return super.onFilterTouchEventForSecurity(event);
    }

    @Override
    public boolean dispatchTrackballEvent(MotionEvent event) {
        log();
        return super.dispatchTrackballEvent(event);
    }

    @Override
    public boolean dispatchCapturedPointerEvent(MotionEvent event) {
        log();
        return super.dispatchCapturedPointerEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        log();
        return super.dispatchGenericMotionEvent(event);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        log();
        return super.dispatchHoverEvent(event);
    }

    @Override
    protected boolean dispatchGenericPointerEvent(MotionEvent event) {
        log();
        return super.dispatchGenericPointerEvent(event);
    }

    @Override
    protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
        log();
        return super.dispatchGenericFocusedEvent(event);
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        log();
        super.dispatchWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean hasWindowFocus() {
        log();
        return super.hasWindowFocus();
    }

    @Override
    protected void dispatchVisibilityChanged(View changedView, int visibility) {
        log();
        super.dispatchVisibilityChanged(changedView, visibility);
    }

    @Override
    public void dispatchDisplayHint(int hint) {
        log();
        super.dispatchDisplayHint(hint);
    }

    @Override
    protected void onDisplayHint(int hint) {
        log();
        super.onDisplayHint(hint);
    }

    @Override
    public void dispatchWindowVisibilityChanged(int visibility) {
        log();
        super.dispatchWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        log();
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        log();
        super.onVisibilityAggregated(isVisible);
    }

    @Override
    public int getWindowVisibility() {
        log();
        return super.getWindowVisibility();
    }

    @Override
    public void getWindowVisibleDisplayFrame(Rect outRect) {
        log();
        super.getWindowVisibleDisplayFrame(outRect);
    }

    @Override
    public void dispatchConfigurationChanged(Configuration newConfig) {
        log();
        super.dispatchConfigurationChanged(newConfig);
    }

    @Override
    public boolean isInTouchMode() {
        log();
        return super.isInTouchMode();
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        log();
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean checkInputConnectionProxy(View view) {
        log();
        return super.checkInputConnectionProxy(view);
    }

    @Override
    public void createContextMenu(ContextMenu menu) {
        log();
        super.createContextMenu(menu);
    }

    @Override
    protected ContextMenu.ContextMenuInfo getContextMenuInfo() {
        log();
        return super.getContextMenuInfo();
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        log();
        return super.onHoverEvent(event);
    }

    @Override
    public boolean isHovered() {
        log();
        return super.isHovered();
    }

    @Override
    public void setHovered(boolean hovered) {
        log();
        super.setHovered(hovered);
    }

    @Override
    public void onHoverChanged(boolean hovered) {
        log();
        super.onHoverChanged(hovered);
    }

    @Override
    public void setTouchDelegate(TouchDelegate delegate) {
        log();
        super.setTouchDelegate(delegate);
    }

    @Override
    public TouchDelegate getTouchDelegate() {
        log();
        return super.getTouchDelegate();
    }

    @Override
    public void bringToFront() {
        log();
        super.bringToFront();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        log();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        log();
        super.dispatchDraw(canvas);
    }

    @Override
    public void setScrollX(int value) {
        log();
        super.setScrollX(value);
    }

    @Override
    public void setScrollY(int value) {
        log();
        super.setScrollY(value);
    }

    @Override
    public void getDrawingRect(Rect outRect) {
        log();
        super.getDrawingRect(outRect);
    }

    @Override
    public Matrix getMatrix() {
        log();
        return super.getMatrix();
    }

    @Override
    public float getCameraDistance() {
        log();
        return super.getCameraDistance();
    }

    @Override
    public void setCameraDistance(float distance) {
        log();
        super.setCameraDistance(distance);
    }

    @Override
    public float getRotation() {
        log();
        return super.getRotation();
    }

    @Override
    public void setRotation(float rotation) {
        log();
        super.setRotation(rotation);
    }

    @Override
    public float getRotationY() {
        log();
        return super.getRotationY();
    }

    @Override
    public void setRotationY(float rotationY) {
        log();
        super.setRotationY(rotationY);
    }

    @Override
    public float getRotationX() {
        log();
        return super.getRotationX();
    }

    @Override
    public void setRotationX(float rotationX) {
        log();
        super.setRotationX(rotationX);
    }

    @Override
    public float getScaleX() {
        log();
        return super.getScaleX();
    }

    @Override
    public void setScaleX(float scaleX) {
        log();
        super.setScaleX(scaleX);
    }

    @Override
    public float getScaleY() {
        log();
        return super.getScaleY();
    }

    @Override
    public void setScaleY(float scaleY) {
        log();
        super.setScaleY(scaleY);
    }

    @Override
    public float getPivotX() {
        log();
        return super.getPivotX();
    }

    @Override
    public void setPivotX(float pivotX) {
        log();
        super.setPivotX(pivotX);
    }

    @Override
    public float getPivotY() {
        log();
        return super.getPivotY();
    }

    @Override
    public void setPivotY(float pivotY) {
        log();
        super.setPivotY(pivotY);
    }

    @Override
    public float getAlpha() {
        log();
        return super.getAlpha();
    }

    @Override
    public void forceHasOverlappingRendering(boolean hasOverlappingRendering) {
        log();
        super.forceHasOverlappingRendering(hasOverlappingRendering);
    }

    @Override
    public void setAlpha(float alpha) {
        log();
        super.setAlpha(alpha);
    }

    @Override
    public boolean isDirty() {
        log();
        return super.isDirty();
    }

    @Override
    public void setX(float x) {
        log();
        super.setX(x);
    }


    @Override
    public void setY(float y) {
        log();
        super.setY(y);
    }

    @Override
    public float getZ() {
        log();
        return super.getZ();
    }

    @Override
    public void setZ(float z) {
        log();
        super.setZ(z);
    }

    @Override
    public float getElevation() {
        log();
        return super.getElevation();
    }

    @Override
    public void setElevation(float elevation) {
        log();
        super.setElevation(elevation);
    }

    @Override
    public void setTranslationX(float translationX) {
        log();
        super.setTranslationX(translationX);
    }


    @Override
    public void setTranslationY(float translationY) {
        log();
        super.setTranslationY(translationY);
    }


    @Override
    public void setTranslationZ(float translationZ) {
        log();
        super.setTranslationZ(translationZ);
    }

    @Override
    public StateListAnimator getStateListAnimator() {
        log();
        return super.getStateListAnimator();
    }

    @Override
    public void setStateListAnimator(StateListAnimator stateListAnimator) {
        log();
        super.setStateListAnimator(stateListAnimator);
    }

    @Override
    public void setClipToOutline(boolean clipToOutline) {
        log();
        super.setClipToOutline(clipToOutline);
    }

    @Override
    public void setOutlineProvider(ViewOutlineProvider provider) {
        log();
        super.setOutlineProvider(provider);
    }

    @Override
    public ViewOutlineProvider getOutlineProvider() {
        log();
        return super.getOutlineProvider();
    }

    @Override
    public void invalidateOutline() {
        log();
        super.invalidateOutline();
    }

    @Override
    public void getHitRect(Rect outRect) {
        log();
        super.getHitRect(outRect);
    }

    @Override
    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
        log();
        return super.getGlobalVisibleRect(r, globalOffset);
    }

    @Override
    public void offsetTopAndBottom(int offset) {
        log();
        super.offsetTopAndBottom(offset);
    }

    @Override
    public void offsetLeftAndRight(int offset) {
        log();
        super.offsetLeftAndRight(offset);
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        log();
        return super.getLayoutParams();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        log();
        super.setLayoutParams(params);
    }

    @Override
    public void scrollTo(int x, int y) {
        log();
        super.scrollTo(x, y);
    }

    @Override
    public void scrollBy(int x, int y) {
        log();
        super.scrollBy(x, y);
    }

    @Override
    protected boolean awakenScrollBars() {
        log();
        return super.awakenScrollBars();
    }

    @Override
    protected boolean awakenScrollBars(int startDelay) {
        log();
        return super.awakenScrollBars(startDelay);
    }

    @Override
    protected boolean awakenScrollBars(int startDelay, boolean invalidate) {
        log();
        return super.awakenScrollBars(startDelay, invalidate);
    }

    @Override
    public void invalidate(Rect dirty) {
        log();
        super.invalidate(dirty);
    }

    @Override
    public void invalidate(int l, int t, int r, int b) {
        log();
        super.invalidate(l, t, r, b);
    }

    @Override
    public void invalidate() {
        log();
        super.invalidate();
    }

    @Override
    public boolean isOpaque() {
        log();
        return super.isOpaque();
    }

    @Override
    public Handler getHandler() {
        log();
        return super.getHandler();
    }

    @Override
    public boolean post(Runnable action) {
        log();
        return super.post(action);
    }

    @Override
    public boolean postDelayed(Runnable action, long delayMillis) {
        log();
        return super.postDelayed(action, delayMillis);
    }

    @Override
    public void postOnAnimation(Runnable action) {
        log();
        super.postOnAnimation(action);
    }

    @Override
    public void postOnAnimationDelayed(Runnable action, long delayMillis) {
        log();
        super.postOnAnimationDelayed(action, delayMillis);
    }

    @Override
    public boolean removeCallbacks(Runnable action) {
        log();
        return super.removeCallbacks(action);
    }

    @Override
    public void postInvalidate() {
        log();
        super.postInvalidate();
    }

    @Override
    public void postInvalidate(int left, int top, int right, int bottom) {
        log();
        super.postInvalidate(left, top, right, bottom);
    }

    @Override
    public void postInvalidateDelayed(long delayMilliseconds) {
        log();
        super.postInvalidateDelayed(delayMilliseconds);
    }

    @Override
    public void postInvalidateDelayed(long delayMilliseconds, int left, int top, int right, int bottom) {
        log();
        super.postInvalidateDelayed(delayMilliseconds, left, top, right, bottom);
    }

    @Override
    public void postInvalidateOnAnimation() {
        log();
        super.postInvalidateOnAnimation();
    }

    @Override
    public void postInvalidateOnAnimation(int left, int top, int right, int bottom) {
        log();
        super.postInvalidateOnAnimation(left, top, right, bottom);
    }

    @Override
    public boolean isHorizontalFadingEdgeEnabled() {
        log();
        return super.isHorizontalFadingEdgeEnabled();
    }

    @Override
    public void setHorizontalFadingEdgeEnabled(boolean horizontalFadingEdgeEnabled) {
        log();
        super.setHorizontalFadingEdgeEnabled(horizontalFadingEdgeEnabled);
    }

    @Override
    public boolean isVerticalFadingEdgeEnabled() {
        log();
        return super.isVerticalFadingEdgeEnabled();
    }

    @Override
    public void setVerticalFadingEdgeEnabled(boolean verticalFadingEdgeEnabled) {
        log();
        super.setVerticalFadingEdgeEnabled(verticalFadingEdgeEnabled);
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        log();
        return super.getTopFadingEdgeStrength();
    }

    @Override
    protected float getBottomFadingEdgeStrength() {
        log();
        return super.getBottomFadingEdgeStrength();
    }

    @Override
    public boolean isHorizontalScrollBarEnabled() {
        log();
        return super.isHorizontalScrollBarEnabled();
    }

    @Override
    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        log();
        super.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled);
    }

    @Override
    public boolean isVerticalScrollBarEnabled() {
        log();
        return super.isVerticalScrollBarEnabled();
    }

    @Override
    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        log();
        super.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    }

    @Override
    public void setScrollbarFadingEnabled(boolean fadeScrollbars) {
        log();
        super.setScrollbarFadingEnabled(fadeScrollbars);
    }

    @Override
    public boolean isScrollbarFadingEnabled() {
        log();
        return super.isScrollbarFadingEnabled();
    }

    @Override
    public int getScrollBarDefaultDelayBeforeFade() {
        log();
        return super.getScrollBarDefaultDelayBeforeFade();
    }

    @Override
    public void setScrollBarDefaultDelayBeforeFade(int scrollBarDefaultDelayBeforeFade) {
        log();
        super.setScrollBarDefaultDelayBeforeFade(scrollBarDefaultDelayBeforeFade);
    }

    @Override
    public int getScrollBarFadeDuration() {
        log();
        return super.getScrollBarFadeDuration();
    }

    @Override
    public void setScrollBarFadeDuration(int scrollBarFadeDuration) {
        log();
        super.setScrollBarFadeDuration(scrollBarFadeDuration);
    }

    @Override
    public int getScrollBarSize() {
        log();
        return super.getScrollBarSize();
    }

    @Override
    public void setScrollBarSize(int scrollBarSize) {
        log();
        super.setScrollBarSize(scrollBarSize);
    }

    @Override
    public void setScrollBarStyle(int style) {
        log();
        super.setScrollBarStyle(style);
    }

    @Override
    public int getScrollBarStyle() {
        log();
        return super.getScrollBarStyle();
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        log();
        return super.computeHorizontalScrollOffset();
    }

    @Override
    protected int computeHorizontalScrollExtent() {
        log();
        return super.computeHorizontalScrollExtent();
    }

    @Override
    protected int computeVerticalScrollOffset() {
        log();
        return super.computeVerticalScrollOffset();
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        log();
        return super.canScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollVertically(int direction) {
        log();
        return super.canScrollVertically(direction);
    }

    @Override
    public boolean canResolveLayoutDirection() {
        log();
        return super.canResolveLayoutDirection();
    }

    @Override
    public boolean isLayoutDirectionResolved() {
        log();
        return super.isLayoutDirectionResolved();
    }

    @Override
    protected void onDetachedFromWindow() {
        log();
        super.onDetachedFromWindow();
    }

    @Override
    protected int getWindowAttachCount() {
        log();
        return super.getWindowAttachCount();
    }

    @Override
    public IBinder getWindowToken() {
        log();
        return super.getWindowToken();
    }

    @Override
    public WindowId getWindowId() {
        log();
        return super.getWindowId();
    }

    @Override
    public IBinder getApplicationWindowToken() {
        log();
        return super.getApplicationWindowToken();
    }

    @Override
    public Display getDisplay() {
        log();
        return super.getDisplay();
    }

    @Override
    public void onCancelPendingInputEvents() {
        log();
        super.onCancelPendingInputEvents();
    }

    @Override
    public void saveHierarchyState(SparseArray<Parcelable> container) {
        log();
        super.saveHierarchyState(container);
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        log();
        super.dispatchSaveInstanceState(container);
    }

    @Override
    public void restoreHierarchyState(SparseArray<Parcelable> container) {
        log();
        super.restoreHierarchyState(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        log();
        super.dispatchRestoreInstanceState(container);
    }

    @Override
    public long getDrawingTime() {
        log();
        return super.getDrawingTime();
    }

    @Override
    public void setDuplicateParentStateEnabled(boolean enabled) {
        log();
        super.setDuplicateParentStateEnabled(enabled);
    }

    @Override
    public boolean isDuplicateParentStateEnabled() {
        log();
        return super.isDuplicateParentStateEnabled();
    }

    @Override
    public void setLayerType(int layerType, Paint paint) {
        log();
        super.setLayerType(layerType, paint);
    }

    @Override
    public void setLayerPaint(Paint paint) {
        log();
        super.setLayerPaint(paint);
    }

    @Override
    public int getLayerType() {
        log();
        return super.getLayerType();
    }

    @Override
    public void buildLayer() {
        log();
        super.buildLayer();
    }

    @Override
    public void setDrawingCacheEnabled(boolean enabled) {
        log();
        super.setDrawingCacheEnabled(enabled);
    }

    @Override
    public boolean isDrawingCacheEnabled() {
        log();
        return super.isDrawingCacheEnabled();
    }

    @Override
    public Bitmap getDrawingCache() {
        log();
        return super.getDrawingCache();
    }

    @Override
    public Bitmap getDrawingCache(boolean autoScale) {
        log();
        return super.getDrawingCache(autoScale);
    }

    @Override
    public void destroyDrawingCache() {
        log();
        super.destroyDrawingCache();
    }

    @Override
    public void setDrawingCacheBackgroundColor(int color) {
        log();
        super.setDrawingCacheBackgroundColor(color);
    }

    @Override
    public int getDrawingCacheBackgroundColor() {
        log();
        return super.getDrawingCacheBackgroundColor();
    }

    @Override
    public void buildDrawingCache() {
        log();
        super.buildDrawingCache();
    }

    @Override
    public void buildDrawingCache(boolean autoScale) {
        log();
        super.buildDrawingCache(autoScale);
    }

    @Override
    public boolean isInEditMode() {
        log();
        return super.isInEditMode();
    }

    @Override
    public boolean isHardwareAccelerated() {
        log();
        return super.isHardwareAccelerated();
    }

    @Override
    public void setClipBounds(Rect clipBounds) {
        log();
        super.setClipBounds(clipBounds);
    }

    @Override
    public Rect getClipBounds() {
        log();
        return super.getClipBounds();
    }

    @Override
    public boolean getClipBounds(Rect outRect) {
        log();
        return super.getClipBounds(outRect);
    }

    @Override
    public void draw(Canvas canvas) {
        log();
        super.draw(canvas);
    }

    @Override
    public ViewOverlay getOverlay() {
        log();
        return super.getOverlay();
    }

    @Override
    public int getSolidColor() {
        log();
        return super.getSolidColor();
    }

    @Override
    public boolean isLayoutRequested() {
        log();
        return super.isLayoutRequested();
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        log();
        super.layout(l, t, r, b);
    }

    @Override
    protected void onFinishInflate() {
        log();
        super.onFinishInflate();
    }

    @Override
    public Resources getResources() {
        log();
        return super.getResources();
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        log();
        super.scheduleDrawable(who, what, when);
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        log();
        super.unscheduleDrawable(who, what);
    }

    @Override
    public void unscheduleDrawable(Drawable who) {
        log();
        super.unscheduleDrawable(who);
    }

    @Override
    public void dispatchDrawableHotspotChanged(float x, float y) {
        log();
        super.dispatchDrawableHotspotChanged(x, y);
    }

    @Override
    public void refreshDrawableState() {
        log();
        super.refreshDrawableState();
    }

    @Override
    public void setBackgroundColor(int color) {
        log();
        super.setBackgroundColor(color);
    }

    @Override
    public void setBackground(Drawable background) {
        log();
        super.setBackground(background);
    }

    @Override
    public Drawable getBackground() {
        log();
        return super.getBackground();
    }

    @Override
    public void setBackgroundTintList(ColorStateList tint) {
        log();
        super.setBackgroundTintList(tint);
    }

    @Override
    public ColorStateList getBackgroundTintList() {
        log();
        return super.getBackgroundTintList();
    }

    @Override
    public void setBackgroundTintMode(PorterDuff.Mode tintMode) {
        log();
        super.setBackgroundTintMode(tintMode);
    }

    @Override
    public PorterDuff.Mode getBackgroundTintMode() {
        log();
        return super.getBackgroundTintMode();
    }

    @Override
    public Drawable getForeground() {
        log();
        return super.getForeground();
    }

    @Override
    public void setForeground(Drawable foreground) {
        log();
        super.setForeground(foreground);
    }

    @Override
    public int getForegroundGravity() {
        log();
        return super.getForegroundGravity();
    }

    @Override
    public void setForegroundGravity(int gravity) {
        log();
        super.setForegroundGravity(gravity);
    }

    @Override
    public void setForegroundTintList(ColorStateList tint) {
        log();
        super.setForegroundTintList(tint);
    }

    @Override
    public ColorStateList getForegroundTintList() {
        log();
        return super.getForegroundTintList();
    }

    @Override
    public void setForegroundTintMode(PorterDuff.Mode tintMode) {
        log();
        super.setForegroundTintMode(tintMode);
    }

    @Override
    public PorterDuff.Mode getForegroundTintMode() {
        log();
        return super.getForegroundTintMode();
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        log();
        super.onDrawForeground(canvas);
    }

    @Override
    public int getPaddingTop() {
        log();
        return super.getPaddingTop();
    }

    @Override
    public int getPaddingBottom() {
        log();
        return super.getPaddingBottom();
    }

    @Override
    public int getPaddingLeft() {
        log();
        return super.getPaddingLeft();
    }

    @Override
    public int getPaddingStart() {
        log();
        return super.getPaddingStart();
    }

    @Override
    public int getPaddingRight() {
        log();
        return super.getPaddingRight();
    }

    @Override
    public int getPaddingEnd() {
        log();
        return super.getPaddingEnd();
    }

    @Override
    public boolean isPaddingRelative() {
        log();
        return super.isPaddingRelative();
    }

    @Override
    protected void dispatchSetSelected(boolean selected) {
        log();
        super.dispatchSetSelected(selected);
    }

    @Override
    public boolean isSelected() {
        log();
        return super.isSelected();
    }

    @Override
    public void setActivated(boolean activated) {
        log();
        super.setActivated(activated);
    }

    @Override
    protected void dispatchSetActivated(boolean activated) {
        log();
        super.dispatchSetActivated(activated);
    }

    @Override
    public boolean isActivated() {
        log();
        return super.isActivated();
    }

    @Override
    public ViewTreeObserver getViewTreeObserver() {
        log();
        return super.getViewTreeObserver();
    }

    @Override
    public View getRootView() {
        log();
        return super.getRootView();
    }

    @Override
    public void getLocationOnScreen(int[] outLocation) {
        log();
        super.getLocationOnScreen(outLocation);
    }

    @Override
    public void getLocationInWindow(int[] outLocation) {
        log();
        super.getLocationInWindow(outLocation);
    }

    @Override
    public void setId(int id) {
        log();
        super.setId(id);
    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public Object getTag() {
        log();
        return super.getTag();
    }

    @Override
    public void setTag(Object tag) {
        log();
        super.setTag(tag);
    }

    @Override
    public Object getTag(int key) {
        log();
        return super.getTag(key);
    }

    @Override
    public void setTag(int key, Object tag) {
        log();
        super.setTag(key, tag);
    }

    @Override
    public boolean isInLayout() {
        log();
        return super.isInLayout();
    }

    @Override
    public void requestLayout() {
        log();
        super.requestLayout();
    }

    @Override
    public void forceLayout() {
        log();
        super.forceLayout();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        log();
        return super.getSuggestedMinimumHeight();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        log();
        return super.getSuggestedMinimumWidth();
    }

    @Override
    public int getMinimumHeight() {
        log();
        return super.getMinimumHeight();
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        log();
        super.setMinimumHeight(minHeight);
    }

    @Override
    public int getMinimumWidth() {
        log();
        return super.getMinimumWidth();
    }

    @Override
    public void setMinimumWidth(int minWidth) {
        log();
        super.setMinimumWidth(minWidth);
    }

    @Override
    public Animation getAnimation() {
        log();
        return super.getAnimation();
    }

    @Override
    public void startAnimation(Animation animation) {
        log();
        super.startAnimation(animation);
    }

    @Override
    public void clearAnimation() {
        log();
        super.clearAnimation();
    }

    @Override
    public void setAnimation(Animation animation) {
        log();
        super.setAnimation(animation);
    }

    @Override
    protected void onAnimationStart() {
        log();
        super.onAnimationStart();
    }

    @Override
    protected void onAnimationEnd() {
        log();
        super.onAnimationEnd();
    }

    @Override
    protected boolean onSetAlpha(int alpha) {
        log();
        return super.onSetAlpha(alpha);
    }

    @Override
    public void playSoundEffect(int soundConstant) {
        log();
        super.playSoundEffect(soundConstant);
    }

    @Override
    public boolean performHapticFeedback(int feedbackConstant) {
        log();
        return super.performHapticFeedback(feedbackConstant);
    }

    @Override
    public boolean performHapticFeedback(int feedbackConstant, int flags) {
        log();
        return super.performHapticFeedback(feedbackConstant, flags);
    }

    @Override
    public void setSystemUiVisibility(int visibility) {
        log();
        super.setSystemUiVisibility(visibility);
    }

    @Override
    public int getSystemUiVisibility() {
        log();
        return super.getSystemUiVisibility();
    }

    @Override
    public int getWindowSystemUiVisibility() {
        log();
        return super.getWindowSystemUiVisibility();
    }

    @Override
    public void onWindowSystemUiVisibilityChanged(int visible) {
        log();
        super.onWindowSystemUiVisibilityChanged(visible);
    }

    @Override
    public void dispatchWindowSystemUiVisiblityChanged(int visible) {
        log();
        super.dispatchWindowSystemUiVisiblityChanged(visible);
    }

    @Override
    public void setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener l) {
        log();
        super.setOnSystemUiVisibilityChangeListener(l);
    }

    @Override
    public void dispatchSystemUiVisibilityChanged(int visibility) {
        log();
        super.dispatchSystemUiVisibilityChanged(visibility);
    }

    @Override
    public boolean dispatchDragEvent(DragEvent event) {
        log();
        return super.dispatchDragEvent(event);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        log();
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        log();
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    public int getOverScrollMode() {
        log();
        return super.getOverScrollMode();
    }

    @Override
    public void setOverScrollMode(int overScrollMode) {
        log();
        super.setOverScrollMode(overScrollMode);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        log();
        super.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        log();
        return super.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        log();
        return super.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        log();
        super.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        log();
        return super.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        log();
        return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        log();
        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        log();
        return super.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        log();
        return super.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public void setTextDirection(int textDirection) {
        log();
        super.setTextDirection(textDirection);
    }

    @Override
    public int getTextDirection() {
        log();
        return super.getTextDirection();
    }

    @Override
    public boolean canResolveTextDirection() {
        log();
        return super.canResolveTextDirection();
    }

    @Override
    public boolean isTextDirectionResolved() {
        log();
        return super.isTextDirectionResolved();
    }

    @Override
    public void setTextAlignment(int textAlignment) {
        log();
        super.setTextAlignment(textAlignment);
    }

    @Override
    public int getTextAlignment() {
        log();
        return super.getTextAlignment();
    }

    @Override
    public boolean canResolveTextAlignment() {
        log();
        return super.canResolveTextAlignment();
    }

    @Override
    public boolean isTextAlignmentResolved() {
        log();
        return super.isTextAlignmentResolved();
    }

    @Override
    public void setPointerIcon(PointerIcon pointerIcon) {
        log();
        super.setPointerIcon(pointerIcon);
    }

    @Override
    public PointerIcon getPointerIcon() {
        log();
        return super.getPointerIcon();
    }

    @Override
    public boolean hasPointerCapture() {
        log();
        return super.hasPointerCapture();
    }

    @Override
    public void requestPointerCapture() {
        log();
        super.requestPointerCapture();
    }

    @Override
    public void releasePointerCapture() {
        log();
        super.releasePointerCapture();
    }

    @Override
    public void onPointerCaptureChange(boolean hasCapture) {
        log();
        super.onPointerCaptureChange(hasCapture);
    }

    @Override
    public void dispatchPointerCaptureChanged(boolean hasCapture) {
        log();
        super.dispatchPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onCapturedPointerEvent(MotionEvent event) {
        log();
        return super.onCapturedPointerEvent(event);
    }

    @Override
    public void setOnCapturedPointerListener(OnCapturedPointerListener l) {
        log();
        super.setOnCapturedPointerListener(l);
    }

    @Override
    public ViewPropertyAnimator animate() {
        log();
        return super.animate();
    }

    @Override
    public String getTransitionName() {
        log();
        return super.getTransitionName();
    }

    @Override
    public void setTooltipText(CharSequence tooltipText) {
        log();
        super.setTooltipText(tooltipText);
    }

    @Override
    public CharSequence getTooltipText() {
        log();
        return super.getTooltipText();
    }
}
