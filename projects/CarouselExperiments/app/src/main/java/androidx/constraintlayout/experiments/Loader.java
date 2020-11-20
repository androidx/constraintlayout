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

package androidx.constraintlayout.experiments;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.constraintlayout.motion.widget.MotionLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utility class to load layouts dynamically
 */
public class Loader {
    static String KEY = "layout";
    private static boolean REVERSE = false;
    private static final String LAYOUTS_MATCHES = "demo_\\d+_.*";
    private static String SHOW_FIRST = "";

    static MotionLayout findMotionLayout(ViewGroup group) {
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

    static void normalMenuStartUp(MainActivity mainActivity) {
        String[] layouts = getLayouts(s -> s.matches(LAYOUTS_MATCHES));
        ScrollView sv = new ScrollView(mainActivity);
        LinearLayout linearLayout = new LinearLayout(mainActivity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < layouts.length; i++) {
            Button button = new Button(mainActivity);
            button.setText(layouts[i]);
            button.setTag(layouts[i]);
            linearLayout.addView(button);
            button.setOnClickListener(view -> launch(mainActivity, (String) view.getTag()));
        }
        sv.addView(linearLayout);
        mainActivity.setContentView(sv);
    }

    private static String[] getLayouts(Test filter) {
        ArrayList<String> list = new ArrayList<>();
        Field[] f = R.layout.class.getDeclaredFields();

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
            if (filter == null || filter.test(name)) {
                list.add(name);
            }
        }
        return list.toArray(new String[0]);
    }

    public static void launch(MainActivity mainActivity, String id) {
        Intent intent = new Intent(mainActivity, MainActivity.class);
        intent.putExtra(KEY, id);
        mainActivity.startActivity(intent);
    }

    interface Test {
        boolean test(String s);
    }
}
