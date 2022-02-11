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

package android.support.constraint.calc;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.constraint.calc.g3d.Graph3D;
import android.text.Html;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.MotionButton;
import androidx.constraintlayout.utils.widget.MotionLabel;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Consumer;
import androidx.window.java.layout.WindowInfoTrackerCallbackAdapter;
import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoTracker;
import androidx.window.layout.WindowLayoutInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/* This test the visibility*/
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final boolean EXPERIMENTAL = false;
    private static final String STACK_STATE_KEY = "STACK_STATE_KEY";
    private static final String SAVE_STATE = "SAVE_STATE";
    int mGraphMode = 0;
    MotionLayout mMotionLayout;
    private WindowInfoTrackerCallbackAdapter windowInfoTracker;
    private Boolean mIsPostureTabletop = false;
    private final LayoutStateChangeCallback layoutStateChangeCallback =
            new LayoutStateChangeCallback();
    CalcEngine mCalcEngine = new CalcEngine();
    MotionLabel[] mStack = new MotionLabel[4];
    boolean mIsInInverseMode = false;
    Graph2D mGraph2D;
    Graph3D mGraph3D;
    boolean mShow3d = false;
    boolean mShow2d = false;
    HashMap<String, String> mInverseMap = new HashMap<>();
    HashMap<String, String> mNormalMap = new HashMap<>();
    HashMap<String, String> mUiToString = new HashMap<>();

    {
        mNormalMap.put("cos-1", "cos");
        mNormalMap.put("sin-1", "sin");
        mNormalMap.put("tan-1", "tan");
        mNormalMap.put("save", "plot");
        mUiToString.put("✕", "*");

        mInverseMap.put("cos-1", "cos<sup><small>-1</small></sup>");
        mInverseMap.put("sin-1", "sin<sup><small>-1</small></sup>");
        mInverseMap.put("tan-1", "tan<sup><small>-1</small></sup>");
        mInverseMap.put("save", "save");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fold);
        mMotionLayout = findView(MotionLayout.class);
        mGraph2D = findViewById(R.id.graph);
        mGraph3D = findViewById(R.id.graph3d);
        getStack();
        restoreState();
        regester_for_clipboard();

        windowInfoTracker = new WindowInfoTrackerCallbackAdapter(
                WindowInfoTracker.getOrCreate(this));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.v(TAG, Debug.getLoc() + " State = " + Debug.getName(getApplicationContext(), mGraphMode));
        Log.v(TAG, Debug.getLoc() + " mMotionLayout = " + mMotionLayout);

        if (mIsPostureTabletop) {
            if (mGraphMode == R.id.mode2d) {
                mMotionLayout.post(() -> mMotionLayout.transitionToState(R.id.mode2d_fold));
            } else if (mGraphMode == R.id.mode3d) {
                mMotionLayout.post(() -> mMotionLayout.transitionToState(R.id.mode3d_fold));
            }
        } else {
            if (mGraphMode == R.id.mode2d) {
                mMotionLayout.post(() -> mMotionLayout.transitionToState(R.id.mode2d));
            } else if (mGraphMode == R.id.mode3d) {
                mMotionLayout.post(() -> mMotionLayout.transitionToState(R.id.mode3d));
            }
        }
    }

    // ================================= Recycler support ====================================
    private <T extends View> T findView(Class c) {
        ViewGroup group = ((ViewGroup) findViewById(android.R.id.content).getRootView());
        ArrayList<ViewGroup> groups = new ArrayList<>();
        groups.add(group);
        while (!groups.isEmpty()) {
            ViewGroup vg = groups.remove(0);
            int n = vg.getChildCount();
            for (int i = 0; i < n; i++) {
                View view = vg.getChildAt(i);
                if (c.isAssignableFrom(view.getClass())) {
                    return (T) view;
                }
                if (view instanceof ViewGroup) {
                    groups.add((ViewGroup) view);
                }
            }
        }
        return (T) null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        windowInfoTracker.addWindowLayoutInfoListener(
                this, Runnable::run, layoutStateChangeCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        windowInfoTracker.removeWindowLayoutInfoListener(layoutStateChangeCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mMotionLayout.setFocusable(true);
        mMotionLayout.requestFocus();
        readClipboard();
    }

    // ====================================STATE Management ========================================
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        byte[] stateBytes = savedInstanceState.getByteArray(STACK_STATE_KEY);
        setState(stateBytes);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putByteArray(STACK_STATE_KEY, getState());
        super.onSaveInstanceState(outState);
    }

    void setState(byte[] data) {

        mMotionLayout.transitionToState(mGraphMode = R.id.mode_no_graph);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try {
            ObjectInputStream os = new ObjectInputStream(bais);
            mCalcEngine.stack = mCalcEngine.deserializeStack(os);
            if (mShow2d = os.readBoolean()) {
                mGraph2D.setVisibility(View.VISIBLE);
                mGraph2D.setAlpha(1);
                CalcEngine.Symbolic sym = mCalcEngine.deserializeSymbolic(os);
                mGraph2D.deserializeSymbolic(sym, os);
                mMotionLayout.transitionToState(mGraphMode = R.id.mode2d);
                Log.v(TAG, Debug.getLoc() + " State =  mode2d");

            } else {
                mGraph2D.setVisibility(View.GONE);
                mGraph2D.setAlpha(0);
            }
            if (mShow3d = os.readBoolean()) {
                mGraph3D.setVisibility(View.VISIBLE);
                mGraph2D.setAlpha(1);
                mMotionLayout.transitionToState(mGraphMode = R.id.mode3d);
                Log.v(TAG, Debug.getLoc() + " State = mode3d");

                CalcEngine.Symbolic sym = mCalcEngine.deserializeSymbolic(os);
                mGraph3D.deserializeSymbolic(sym, os);
            } else {
                mGraph3D.setVisibility(View.GONE);
                mGraph2D.setAlpha(0);
                mMotionLayout.transitionToState(mGraphMode = R.id.mode_no_graph);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getApplicationContext().deleteFile(SAVE_STATE);

        }
        int fill = Math.min(mStack.length, mCalcEngine.stack.top);
        for (int i = 0; i < fill; i++) {
            mStack[i].setText(mCalcEngine.getStack(i));
        }
    }

    byte[] getState() {
        byte[] objectBytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(baos);
            mCalcEngine.stack.serialize(os);

            os.writeBoolean(mShow2d);
            if (mShow2d) {
                mGraph2D.serialize(os);
            }

            os.writeBoolean(mShow3d);
            if (mShow3d) {
                mGraph3D.serialize(os);
            }
            os.flush();
            objectBytes = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return objectBytes;
    }

    protected void onPause() {
        super.onPause();
        try {
            FileOutputStream outputStream = getApplicationContext().openFileOutput(SAVE_STATE, Context.MODE_PRIVATE);
            outputStream.write(getState());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void restoreState() {
        try {
            FileInputStream inputStream = getApplicationContext().openFileInput(SAVE_STATE);
            byte[] data = new byte[inputStream.available()];
            int total = 0;
            while (total < data.length) {
                int n = inputStream.read(data, total, data.length - total);
                if (n == -1) {
                    return;
                }
                total += n;
            }
            setState(data);
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // =================================================================================

    private void getStack() {
        mStack[0] = findViewById(R.id.line0);
        mStack[1] = findViewById(R.id.line1);
        mStack[2] = findViewById(R.id.line2);
        mStack[3] = findViewById(R.id.line3);
    }

    public void key(View view) {
        String key = ((Button) view).getText().toString();
        if (mUiToString.containsKey(key)) {
            key = mUiToString.get(key);
        }
        switch (key) {
            case "inv":
                mIsInInverseMode = !mIsInInverseMode;
                invertStrings(mIsInInverseMode);
                int run = mIsInInverseMode ? R.id.inverse : R.id.un_inverse;
                mMotionLayout.viewTransition(run, findViewById(R.id.adv_inv));
                return;
            case "plot":
                plot();
                return;
            case "save":
                save_plot();
                return;
            case "copy":
                serializeToCopyBuffer();
                return;
        }

        String str = key;
        if (mIsInInverseMode && view.getTag() != null) {
            str = (String) view.getTag();
        }
        if (str == null) {
            Log.w(TAG, Debug.getLoc() + " null! ");
            return;
        }
        String s = mCalcEngine.key(str);
        int k = 0;
        if (s.length() != 0) {
            mStack[k++].setText(s);
        }
        for (int i = k; i < mStack.length; i++) {
            mStack[i].setText(mCalcEngine.getStack(i - k));
        }

        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
    }

    public void readClipboard() {
        if (EXPERIMENTAL) {
            ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            {
                ClipData clipData = clipBoard.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    ClipData.Item item = clipData.getItemAt(0);
                    String text = item.getText().toString();
                    CalcEngine.Symbolic op = mCalcEngine.deserializeString(text);
                    Log.v(TAG, Debug.getLoc() + " \"" + op.toString() + "\"");
                }
            }
        }
    }

    public void regester_for_clipboard() {
        if (EXPERIMENTAL) {
            ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            try {
                ClipData clipData = clipBoard.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    ClipData.Item item = clipData.getItemAt(0);
                    String text = item.getText().toString();
                    CalcEngine.Symbolic op = mCalcEngine.deserializeString(text);
                    Log.v(TAG, Debug.getLoc() + " \"" + op.toString() + "\"");
                }
            } catch (Exception ex) {

            }

            clipBoard.addPrimaryClipChangedListener(() -> {
                try {
                    ClipData clipData = clipBoard.getPrimaryClip();
                    ClipData.Item item = clipData.getItemAt(0);
                    String text = item.getText().toString();
                    CalcEngine.Symbolic op = mCalcEngine.deserializeString(text);
                    Log.v(TAG, Debug.getLoc() + " \"" + op.toString() + "\"");
                    Log.v(TAG, Debug.getLoc() + " \"" + text + "\"");
                } catch (Exception ex) {

                }
            });
        }
    }

    private void serializeToCopyBuffer() {
        CalcEngine.Symbolic s = mCalcEngine.stack.getVar(0);
        StringBuffer buffer = new StringBuffer();
        s.toSerialString(buffer);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("calc", buffer);
        clipboard.setPrimaryClip(clip);
    }

    void invertStrings(boolean invert) {
        int count = mMotionLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = mMotionLayout.getChildAt(i);
            if (v.getTag() != null && v instanceof MotionButton) {
                swtchString((String) v.getTag(), (MotionButton) v, invert);
            }

        }
    }

    private void swtchString(String tag, MotionButton v, boolean invert) {
        if (invert) {
            Log.v(TAG, Debug.getLoc() + " \"" + tag + "\"");
            v.setText(Html.fromHtml(mInverseMap.get(tag)));
        } else {
            v.setText(mNormalMap.get(tag));
        }
    }

    private void save_plot() {
        String str = "d";
        if (mGraph2D.getVisibility() == View.VISIBLE) {
            save(mGraph2D, "calc2d" + (System.nanoTime() % 10000), mGraph2D.getEquation());
            str = "2D Graph saved";
        } else if (mGraph3D.getVisibility() == View.VISIBLE) {
            save(mGraph3D, "calc3d" + (System.nanoTime() % 10000), mGraph3D.getEquation());
            str = "3D Graph saved";
        } else {
            save(mMotionLayout, "calcScreen" + (System.nanoTime() % 10000), "" + mCalcEngine.stack.getVar(0));
            str = "screen saved";
        }
        Toast toast = Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void save(View view, String title, String description) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        view.setBackgroundColor(0xFFFFFFFF);
        view.draw(canvas);
        view.setBackgroundColor(0x0);
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, title, description);
    }

    private void plot() {
        boolean fold = mIsPostureTabletop;
        CalcEngine.Symbolic s = mCalcEngine.stack.getVar(0);
        if (s == null) {
            mShow3d = false;
            mShow2d = false;
            mGraphMode = R.id.mode_no_graph;
            mMotionLayout.transitionToState(mGraphMode);
            return;
        }

        int dim = s.dimensions();

        if ((dim & 3) == 1) {
            mShow3d = false;
            mShow2d = true;
            mGraphMode = (fold) ? R.id.mode2d_fold : R.id.mode2d;
            mGraph2D.plot(s);
        } else if ((dim & 3) == 3) {
            mShow3d = true;
            mShow2d = false;
            mGraphMode = (fold) ? R.id.mode3d_fold : R.id.mode3d;

            mGraph3D.plot(s);
        } else {
            mGraphMode = (fold) ? R.id.mode_no_graph_fold : R.id.mode_no_graph;
        }
        mMotionLayout.transitionToState(mGraphMode);
    }

    public void showMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.skin_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "design2":
                        reloadLayout(R.layout.design2);
                        break;
                    case "full":
                        if (mShow2d) {
                            mMotionLayout.transitionToState(R.id.mode2d_full);
                        } else if (mShow3d) {
                            mMotionLayout.transitionToState(R.id.mode3d_full);
                        } else {
                            mMotionLayout.transitionToState(R.id.mode_no_graph);
                        }
                        break;
                    case "normal":
                        if (mShow2d) {
                            mMotionLayout.transitionToState(R.id.mode2d);
                        } else if (mShow3d) {
                            mMotionLayout.transitionToState(R.id.mode3d);
                        } else {
                            mMotionLayout.transitionToState(R.id.mode_no_graph);
                        }
                        break;
                    default:
                        reloadLayout(R.layout.calc);
                }
                return true;
            }
        });
        popup.show();
    }

    private void reloadLayout(int calc) {
        byte[] data = getState();
        mMotionLayout.setVisibility(View.GONE);
        setContentView(calc);
        mMotionLayout = findView(MotionLayout.class);
        mGraph2D = findViewById(R.id.graph);
        mGraph3D = findViewById(R.id.graph3d);
        getStack();
        setState(data);
    }

    class LayoutStateChangeCallback implements Consumer<WindowLayoutInfo> {
        @Override
        public void accept(WindowLayoutInfo windowLayoutInfo) {
            int fold = 0;
            for (DisplayFeature feature : windowLayoutInfo.getDisplayFeatures()) {
                FoldingFeature foldingFeature = (FoldingFeature) feature;
                Rect splitRect = getFeatureBoundsInWindow(feature, mMotionLayout, true);
                if (splitRect != null) {
                    fold = mMotionLayout.getHeight() - splitRect.top;
                }
                if (isTableTopMode(foldingFeature)) {
                    mIsPostureTabletop = true;
                    Log.v(TAG, Debug.getLoc() + " POSTURE: TABLETOP " + fold);
                    if (mMotionLayout.getCurrentState() == R.id.mode2d) {
                        mMotionLayout.transitionToState(R.id.mode2d_fold);
                    } else if (mMotionLayout.getCurrentState() == R.id.mode3d) {
                        mMotionLayout.transitionToState(R.id.mode3d_fold);
                    } else if (mMotionLayout.getCurrentState() == R.id.mode_no_graph) {
                        mMotionLayout.transitionToState(R.id.mode_no_graph_fold);
                    }
                    ConstraintLayout.getSharedValues().fireNewValue(R.id.fold, fold);
                } else {
                    Log.v(TAG, Debug.getLoc() + " POSTURE NOT TABLETOP " + fold);
                    mIsPostureTabletop = false;
                    if (mMotionLayout.getCurrentState() == R.id.mode2d_fold) {
                        mMotionLayout.transitionToState(R.id.mode2d);
                    } else if (mMotionLayout.getCurrentState() == R.id.mode3d_fold) {
                        mMotionLayout.transitionToState(R.id.mode3d);
                    } else if (mMotionLayout.getCurrentState() == R.id.mode_no_graph_fold) {
                        mMotionLayout.transitionToState(R.id.mode_no_graph);
                    }
                    ConstraintLayout.getSharedValues().fireNewValue(R.id.fold, 0);
                }
            }
        }

        Boolean isTableTopMode(FoldingFeature foldFeature) {
            return foldFeature.getState() == FoldingFeature.State.HALF_OPENED &&
                    foldFeature.getOrientation() == FoldingFeature.Orientation.HORIZONTAL;
        }

        /**
         * Get the bounds of the display feature translated to the View's coordinate space and current
         * position in the window. This will also include view padding in the calculations.
         */
        Rect getFeatureBoundsInWindow(
                DisplayFeature displayFeature,
                View view,
                Boolean includePadding
        ) {
            // The the location of the view in window to be in the same coordinate space as the feature.
            int[] viewLocationInWindow = new int[2];
            view.getLocationInWindow(viewLocationInWindow);

            // Intersect the feature rectangle in window with view rectangle to clip the bounds.
            Rect viewRect = new Rect(
                    viewLocationInWindow[0], viewLocationInWindow[1],
                    viewLocationInWindow[0] + view.getWidth(), viewLocationInWindow[1] + view.getHeight()
            );

            // Include padding if needed
            if (includePadding) {
                viewRect.left += view.getPaddingLeft();
                viewRect.top += view.getPaddingTop();
                viewRect.right -= view.getPaddingRight();
                viewRect.bottom -= view.getPaddingBottom();
            }

            Rect featureRectInView = new Rect(displayFeature.getBounds());
            boolean intersects = featureRectInView.intersect(viewRect);

            //Checks to see if the display feature overlaps with our view at all
            if ((featureRectInView.width() == 0 && featureRectInView.height() == 0) ||
                    !intersects) {
                return null;
            }

            // Offset the feature coordinates to view coordinate space start point
            featureRectInView.offset(-viewLocationInWindow[0], -viewLocationInWindow[1]);
            return featureRectInView;
        }
    }


}
