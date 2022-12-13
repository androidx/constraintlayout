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

package androidx.constraintlayout.validation;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.support.constraintlayout.validation.R;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;


public class CheckDumpJson extends AppCompatActivity {
    private static final String TAG = "CheckDumpJson";
    String layout_name;
    ConstraintLayout mLayout;
    String []mAllLayout;
    int current = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAllLayout =  getLayouts("check_.*");
        setLayout(mAllLayout[current]);
    }

    void setLayout(String name) {
        setTitle(layout_name = name);
        Context ctx = getApplicationContext();
        Log.v("MAIN", current+" : "+name);
        int  id = ctx.getResources().getIdentifier(name, "layout", ctx.getPackageName());
        setContentView(id);
        mLayout = findConstraintLayout(this);
        if (mLayout == null) {
            current++;
            if (current < mAllLayout.length)
                setLayout(mAllLayout[current]);
            else
                finish();
        } else {
            mLayout.postDelayed(() -> dumpJson(), 1000);
        }
    }
    ArrayList<String> allFiles = new ArrayList<>();
    private void dumpJson() {
      Log.v("MAIN", current+" : "+mAllLayout[current]);
      try {


      String   fileName = ConstraintLayoutToJason.writeJSonToFile(mLayout, layout_name);

        allFiles.add(fileName);
      } catch (Exception e) {
          Log.v("MAIN", e.getMessage()+" "+current+" :ERROR  .("+mAllLayout[current]+".xml:01) ");

      }
        current++;
        if (current < mAllLayout.length)
            setLayout(mAllLayout[current]);
        else {
            for (String file : allFiles) {
                Log.v("MAIN", "git pull "+file);
            }
            finish();
        }
    }

    private static String[] getLayouts(String match) {
        ArrayList<String> list = new ArrayList<>();
        Field[] f = R.layout.class.getDeclaredFields();
        Arrays.sort(f, (f1, f2) -> {
            int v =   f1.getName().compareTo(f2.getName());
            return v;
        });

        for (int i = 0; i < f.length; i++) {
            String name = f[i].getName();
            if (match == null || name.matches(match)) {
                list.add(name);
            } else {
                Log.v(TAG,"reject "+name);
            }
        }
        return list.toArray(new String[0]);
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

}
