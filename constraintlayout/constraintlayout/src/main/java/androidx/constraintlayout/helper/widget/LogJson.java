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

package androidx.constraintlayout.helper.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

/**
 * This is a class to help with logging the constraints in Jason
 * This is used for debugging purposes
 */
public class LogJson extends ConstraintHelper {
    private static final String TAG = "JSON5";
    private int mDuration = 10000;
    private int mMode = 0;
    private String mLogToFile = null;
    private boolean mLogConsole = true;

    public static final int LOG_PERIODIC = 1;
    public static final int LOG_DELAYED = 2;
    public static final int LOG_LAYOUT = 3;
    public static final int LOG_API = 4;
    private boolean mPeriodic = false;

    public LogJson(Context context) {
        super(context);
    }

    public LogJson(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLogJson(attrs);

    }

    public LogJson(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLogJson(attrs);
    }


    private void initLogJson(AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.LogJson);
            final int count = a.getIndexCount();
            for (int i = 0; i < count; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.LogJson_logDuration) {
                    mDuration = a.getInt(attr, mDuration);
                } else if (attr == R.styleable.LogJson_logMode) {
                    mMode = a.getInt(attr, mMode);
                } else if (attr == R.styleable.LogJson_logTo) {
                    TypedValue v = a.peekValue(attr);
                    if (v.type == TypedValue.TYPE_STRING) {
                        String value = a.getString(attr);
                    } else {
                        int value = a.getInt(attr, 0);
                        mLogConsole = value == 2;
                    }
                }
            }
            a.recycle();
        }
        setVisibility(GONE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        switch (mMode) {
            case LOG_PERIODIC:
                mPeriodic = true;
                this.postDelayed(this::periodic, mDuration);
                break;
            case LOG_DELAYED:
                this.postDelayed(this::writeLog, mDuration);
                break;
            case LOG_LAYOUT:

                break;
            case LOG_API:
                break;
        }
    }

    /**
     * Set the duration of periodic logging of constraints
     * @param duration
     */
    public void setPeriodicDuration(int duration) {
        mDuration = duration;
    }

    /**
     * Start sampling periodically sampling
     */
    public void periodicStart() {
        mPeriodic = true;
        this.postDelayed(this::periodic, mDuration);
    }

    /**
     * Stop sampling periodically sampling
     */
    public void periodicStop() {
        mPeriodic = false;
    }

    private void periodic() {
        if (mPeriodic) {
            writeLog();
            this.postDelayed(this::periodic, mDuration);
        }
    }

    /**
     * This writes a JSON5 representation of the constraintSet
     */
    public void writeLog() {
        try {
            StringWriter writer = new StringWriter();
            ConstraintSet c = new ConstraintSet();
            c.writeState(writer, (ConstraintLayout) this.getParent(), 0);
            String str = writer.toString();
            if (mLogToFile == null) {
                if (mLogConsole) {
                    System.out.println(str);
                } else {
                    logBigString(str);
                }
            } else {
                toFile(str, mLogToFile);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This writes the JSON5 description of the constraintLayout to a file named fileName.json5
     * in the download directory which can be pulled with:
     * "adb pull "/storage/emulated/0/Download/" ."
     *
     * @param str
     * @param fileName
     * @return
     */
    private static String toFile(String str, String fileName) {
        FileOutputStream outputStream;
        if (!fileName.endsWith(".json5")) {
            fileName += ".json5";
        }
        try {
            File down =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(down, fileName);
            outputStream = new FileOutputStream(file);
            outputStream.write(str.getBytes());
            outputStream.close();
            return file.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LogConditional")
    private void logBigString(String str) {
        int len = str.length();
        for (int i = 0; i < len; i++) {
            int k = str.indexOf("\n", i);
            if (k == -1) {
                Log.v(TAG, str.substring(i));
                break;
            }
            Log.v(TAG, str.substring(i, k));
            i = k;
        }
    }
}
