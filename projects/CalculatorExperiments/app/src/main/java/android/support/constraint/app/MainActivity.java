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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.app.g3d.Graph3D;
import android.text.Html;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.MotionButton;
import androidx.constraintlayout.utils.widget.MotionLabel;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/* This test the visibility*/
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String STACK_STATE_KEY = "STACK_STATE_KEY";
    private static final String SAVE_STATE = "SAVE_STATE";
    MotionLayout mMotionLayout;
    Graph2D graph2D;
    Graph3D graph3D;
    boolean show3d = false;
    boolean show2d = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        setContentView(R.layout.calc);
        mMotionLayout = findView(MotionLayout.class);
        graph2D = findViewById(R.id.graph);
        graph3D = findViewById(R.id.graph3d);
        getStack();
        restoreState();
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
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        try {
            ObjectInputStream os = new ObjectInputStream(bais);
            calcEngine.stack = calcEngine.deserializeStack(os);
            if (show2d = os.readBoolean()) {
                graph2D.setVisibility(View.VISIBLE);
                graph2D.setAlpha(1);
                graph2D.plot(calcEngine.deserializeSymbolic(os));
            } else {
                graph2D.setVisibility(View.GONE);
                graph2D.setAlpha(0);
            }
            if (show3d = os.readBoolean()) {
                graph3D.setVisibility(View.VISIBLE);
                graph2D.setAlpha(1);
                graph3D.plot(calcEngine.deserializeSymbolic(os));
            } else {
                graph3D.setVisibility(View.GONE);
                graph2D.setAlpha(0);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        int fill = Math.min(stack.length, calcEngine.stack.top);
        for (int i = 0; i < fill; i++) {
            stack[i].setText(calcEngine.getStack(calcEngine.stack.top - i - 1));
        }
    }

    byte[] getState() {
        byte[] objectBytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(baos);
            calcEngine.stack.serialize(os);

            os.writeBoolean(show2d);
            if (show2d) {
                graph2D.getPlot().serialize(os);
            }

            os.writeBoolean(show3d);
            if (show3d) {
                graph3D.getPlot().serialize(os);
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
            FileOutputStream outputStream = getApplicationContext().openFileOutput( SAVE_STATE, Context.MODE_PRIVATE);
            outputStream.write(getState());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void  restoreState(){
        try {
            FileInputStream inputStream = getApplicationContext().openFileInput( SAVE_STATE);
            byte[] data = new byte[inputStream.available()];
            int total = 0;
            while(total < data.length) {
              int n =  inputStream.read(data,total,data.length-total);
              if (n == -1) {
                 return;
              }
               total+=n;
            }
            setState(data);
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        switch (key) {
            case "inv":
                isInInverser = !isInInverser;
                invertStrings(isInInverser);
                int run = isInInverser ? R.id.inverse : R.id.un_inverse;
                mMotionLayout.viewTransition(run, findViewById(R.id.adv_inv));
                return;
            case "plot":
                plot();
                return;
            case "save":
                save_plot();
                return;
        }

        String str = key;
        if (isInInverser && view.getTag() != null) {
            str = (String) view.getTag();
        }
        if (str == null) {
            Log.w(TAG, Debug.getLoc() + " null! ");
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

    private void save_plot() {
        String str = "d";
        if (graph2D.getVisibility() == View.VISIBLE) {
            save(graph2D, "calc2d" + (System.nanoTime() % 10000), graph2D.getEquation());
            str = "2D Graph saved";
        } else if (graph3D.getVisibility() == View.VISIBLE) {
            save(graph3D, "calc3d" + (System.nanoTime() % 10000), graph3D.getEquation());
            str = "3D Graph saved";
        } else {
            save(mMotionLayout, "calcScreen" + (System.nanoTime() % 10000), "" + calcEngine.stack.getVar(0));
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
        CalcEngine.Symbolic s = calcEngine.stack.getVar(0);
        if (s == null) {
            show3d = false;
            show2d = false;
            mMotionLayout.transitionToState(R.id.mode_no_graph);
            return;
        }

        int dim = s.dimensions();

        if (dim == 1) {
            show3d = false;
            show2d = true;
            mMotionLayout.transitionToState(R.id.mode2d);
            graph2D.plot(s);
        } else if (dim == 3) {
            show3d = true;
            show2d = false;
            mMotionLayout.transitionToState(R.id.mode3d);
            graph3D.plot(s);
        }

    }

}
