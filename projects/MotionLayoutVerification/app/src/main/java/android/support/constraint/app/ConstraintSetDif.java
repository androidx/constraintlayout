/*
 * Copyright (C) 2021 The Android Open Source Project
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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.TransitionAdapter;
import androidx.constraintlayout.widget.ConstraintSet;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Test transitionToState bug
 */
public class ConstraintSetDif extends AppCompatActivity {
    private static final String TAG = "ConstraintSetDif";
    static HashSet<String> isID = new HashSet<>(Arrays.asList(
            "leftToLeft",
            "leftToRight",
            "rightToLeft",
            "rightToRight",
            "topToTop",
            "topToBottom",
            "bottomToTop",
            "bottomToBottom",
            "baselineToBaseline",
            "baselineToTop",
            "baselineToBottom",
            "startToEnd",
            "startToStart",
            "endToStart",
            "endToEnd",
            "circleConstraint"
    ));
    String layout_name;
    MotionLayout mMotionLayout;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        String prelayout = extra.getString(Utils.KEY);
        layout_name = prelayout;
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());
        setContentView(id);
        mMotionLayout = Utils.findMotionLayout(this);
        mMotionLayout.transitionToState(R.id.end);
        mMotionLayout.setTransitionListener(new TransitionAdapter() {
            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                Log.v(TAG, Debug.getLoc() + " ");
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                Log.v(TAG, Debug.getLoc() + " " + progress);
            }
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mMotionLayout.postDelayed(() -> check(), 1000);
    }

    void check() {
        int[] ids = mMotionLayout.getConstraintSetIds();
        if (ids.length < 2) {
            Log.v(TAG, "=================================== Nothing to Compare");
        }
        ConstraintSet[] cset = new ConstraintSet[ids.length];

        for (int i = 0; i < ids.length; i++) {
            cset[i] = mMotionLayout.getConstraintSet(ids[i]);
            Log.v(TAG, "== csets " + i + ":" + Debug.getName(getApplicationContext(), ids[i]));
        }
        int count = mMotionLayout.getChildCount();
        int[] vId = new int[count];
        for (int i = 0; i < count; i++) {
            vId[i] = mMotionLayout.getChildAt(i).getId();
        }
        int base = cset.length / 2;
        int baseId = ids[base];
        String baseName = Debug.getName(getApplicationContext(), ids[base]);

        for (int j = 1; j < cset.length; j++) {

            String constraintSetName = Debug.getName(getApplicationContext(), ids[j]);
            logDiff(cset[base], cset[j], baseName, constraintSetName, vId);

        }


    }

    void logDiff(ConstraintSet cs_a, ConstraintSet cs_b, String aName, String bName, int[] vId) {
        int base = vId.length / 2;
        int baseId = vId[base];

        for (int i = 0; i < vId.length; i++) {
            String viewName = Debug.getName(getApplicationContext(), vId[i]);

            ConstraintSet.Constraint cca = cs_a.getConstraint(vId[i]);
            ConstraintSet.Constraint ccb = cs_b.getConstraint(vId[i]);
            String a = aName + "." + viewName;
            String b = bName + "." + viewName;
            String cmp = aName + "/" + bName;
            String str = diffFields(cca.layout, ccb.layout, a + ".layout", b + ".layout");
            if (str != null) {
                Log.v(TAG, "------ " + cmp + "." + viewName + ".layout  -------" + str + "\n");
            }
            str = diffFields(cca.motion, ccb.motion, a + ".motion", b + ".layout");
            if (str != null) {
                Log.v(TAG, "------ " + cmp + "." + viewName + ".motion  -------" + str + "\n");
            }
            str = diffFields(cca.transform, ccb.transform, a + ".transform", b + ".layout");
            if (str != null) {
                Log.v(TAG, "------ " + cmp + "." + b + ".transform  -------" + str + "\n");
            }
            str = diffFields(cca.propertySet, ccb.propertySet, a + ".propertySet", b + ".layout");
            if (str != null) {
                Log.v(TAG, "------ " + cmp + "." + a + ".propertySet  -------" + str + "\n");
            }

            Log.v(TAG, "------ " + cmp + "." + viewName + ".\n");

        }
    }

    String diffFields(Object a, Object b, String aName, String bName) {
        if (a == null) {
            return aName + " is null";
        }
        if (b == null) {
            return bName + " is null";
        }
        Class aClass = a.getClass();
        Class bClass = b.getClass();
        if (aClass != bClass) {
            return aName + " and " + bName + " not of the same class " + aClass.getSimpleName() + " vs " + bClass.getSimpleName();
        }
        Field[] fields = aClass.getDeclaredFields();
        String ret = "\n              " + aName + " - " + bName + "\n";
        boolean found = false;
        try {
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                Object aVal = f.get(a);
                Object bVal = f.get(b);

                if (aVal != bVal && (aVal != null || bVal != null) && !aVal.equals(bVal)) {
                    if (isID.contains(f.getName())) {
                        String aViewName = viewIdName((Integer) aVal);
                        String bViewName = viewIdName((Integer) bVal);
                        ret += padd(f.getName(), 15) + " : " + aViewName + " != " + bViewName + "\n";
                    } else {
                        ret += padd(f.getName(), 15) + " : " + aVal + " != " + bVal + "\n";
                    }
                    found = true;
                } else {
                    //  ret+=f.getName() +"\n";
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (found)
            return ret;
        return null;
    }

    String viewIdName(int id) {
        try {
            switch (id) {
                case -1:
                    return "unset";
                case 0:
                    return "parent";
                default:
                    return getApplicationContext().getResources().getResourceEntryName(id);
            }
        } catch (Exception ex) {
            return "?" + id;
        }
    }

    String padd(String s, int n) {
        if (s.length() < n) {
            s = (s + "                                                  ").substring(0, n);
        }
        return s;
    }

}
