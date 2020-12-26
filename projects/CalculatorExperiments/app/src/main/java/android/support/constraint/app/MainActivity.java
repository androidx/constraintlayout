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

import android.os.Bundle;

import android.support.constraint.app.g3d.Graph3D;
import android.text.Html;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.MotionButton;
import androidx.constraintlayout.utils.widget.MotionLabel;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

/* This test the visibility*/
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    MotionLayout mMotionLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        setContentView(R.layout.calc);
        mMotionLayout = findView(MotionLayout.class);
        getStack();
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


    // =================================================================================


    CalcEngine calcEngine = new CalcEngine();

    MotionLabel[] stack = new MotionLabel[4];

    private void getStack() {
        stack[0] = findViewById(R.id.line0);
        stack[1] = findViewById(R.id.line1);
        stack[2] = findViewById(R.id.line2);
        stack[3] = findViewById(R.id.line3);
    }

    boolean isInInverser = false;

    public void key(View view) {
        String key = ((Button) view).getText().toString();
        if ("inv".equals(key)) {
            isInInverser = !isInInverser;
            invertStrings(isInInverser);
            int run = isInInverser ? R.id.inverse : R.id.un_inverse;
              mMotionLayout.viewTransition(run,findViewById(R.id.adv_inv));
            return;
        }
        if ("plot".equals(key)) {
            plot();
            return;
        }
        String str = key;
        if (isInInverser && view.getTag() != null) {
            str = (String) view.getTag();
        }
        if (str == null) {
            Log.v(TAG, Debug.getLoc() + " null! ");
            return;
        }
        String s = calcEngine.key(str);
        int k = 0;
        if (s.length() != 0) {
            stack[k++].setText(s);
        }
        for (int i = k; i < stack.length; i++) {
            stack[i].setText(calcEngine.getStack(i - k));
        }

        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
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

    HashMap<String, String> inverse = new HashMap<>();
    HashMap<String, String> normal = new HashMap<>();

    {
        normal.put("cos-1", "cos");
        normal.put("sin-1", "sin");
        normal.put("tan-1", "tan");

        inverse.put("cos-1", "cos<sup><small>-1</small></sup>");
        inverse.put("sin-1", "sin<sup><small>-1</small></sup>");
        inverse.put("tan-1", "tan<sup><small>-1</small></sup>");
    }

    private void swtchString(String tag, MotionButton v, boolean invert) {
        if (invert) {
            v.setText(Html.fromHtml(inverse.get(tag)));
        } else {
            v.setText(normal.get(tag));
        }
    }

    private void plot() {
        CalcEngine.Symbolic s = calcEngine.stack.getVar(0);
        Graph2D graph2D = findViewById(R.id.graph);
        Graph3D graph3D = findViewById(R.id.graph3d);
            if (s == null) {
                graph2D.setAlpha(0);
                graph2D.setVisibility(View.GONE);
                graph3D.setAlpha(0);
                graph3D.setVisibility(View.GONE);
                return;
            }
        int dim = s.dimensions();

        if (dim == 1) {
            graph2D.setVisibility(View.VISIBLE);
            graph2D.setAlpha(1);
            graph3D.setAlpha(0);
            graph3D.setVisibility(View.GONE);
            graph2D.plot(s);
        } else if (dim == 3) {
            graph3D.setVisibility(View.VISIBLE);
            graph3D.setAlpha(1);
            graph2D.setAlpha(0);
            graph2D.setVisibility(View.GONE);
            graph3D.plot(s);
        }

    }

    public void exp(View view) {
        ExtendedFloatingActionButton fab =  findViewById(R.id.fab);
        ViewGroup.LayoutParams p = fab.getLayoutParams();
        Log.v(TAG,Debug.getLoc()+" "+p.width+" , "+p.height+ "  "+fab.isExtended());

        if (fab.isExtended()) {
            fab.shrink();
        } else {
            fab.extend();
        }

    }
}
