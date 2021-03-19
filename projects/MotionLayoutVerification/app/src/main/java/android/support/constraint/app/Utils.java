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

import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
    public static String KEY = "layout";

    static MotionLayout findMotionLayout(AppCompatActivity activity) {
        ViewGroup group = ((ViewGroup) activity.findViewById(android.R.id.content).getRootView());
        ArrayList<ViewGroup> groups = new ArrayList<>();
        groups.add(group);
        while (!groups.isEmpty()) {
            ViewGroup vg = groups.remove(0);
            int n = vg.getChildCount();
            for (int i = 0; i < n; i++) {
                View view = vg.getChildAt(i);
                if (view instanceof MotionLayout) {
                    return (MotionLayout) view;
                }
                if (view instanceof ViewGroup) {
                    groups.add((ViewGroup) view);
                }
            }
        }
        return null;
    }
    static  <T extends View> T findView(AppCompatActivity activity, Class c) {
        ViewGroup group = ((ViewGroup) activity.findViewById(android.R.id.content).getRootView());
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

    static ConstraintLayout findConstraintLayout(AppCompatActivity activity) {
        ViewGroup group = ((ViewGroup) activity.findViewById(android.R.id.content).getRootView());
        ArrayList<ViewGroup> groups = new ArrayList<>();
        groups.add(group);
        while (!groups.isEmpty()) {
            ViewGroup vg = groups.remove(0);
            int n = vg.getChildCount();
            for (int i = 0; i < n; i++) {
                View view = vg.getChildAt(i);
                if (view instanceof ConstraintLayout) {
                    return (ConstraintLayout) view;
                }
                if (view instanceof ViewGroup) {
                    groups.add((ViewGroup) view);
                }
            }
        }
        return null;
    }
   static String[] getLayouts(String  match) {
        ArrayList<String> list = new ArrayList<>();
        Field[] f = R.layout.class.getDeclaredFields();
        boolean REVERSE = false;
        String SHOW_FIRST = null;
        Arrays.sort(f, (f1, f2) -> {
            int v = (REVERSE ? -1 : 1) * f1.getName().compareTo(f2.getName());
            if (SHOW_FIRST == null) {
                return v;
            }
            if (f1.getName().matches(SHOW_FIRST)) {
                return -1;
            }
            if (f2.getName().matches(SHOW_FIRST)) {
                return +1;
            }
            return v;
        });
        for (int i = 0; i < f.length; i++) {
            String name = f[i].getName();
            if (match == null || name.matches(match)) {
                list.add(name);
            }
        }
        return list.toArray(new String[0]);
    }
}
