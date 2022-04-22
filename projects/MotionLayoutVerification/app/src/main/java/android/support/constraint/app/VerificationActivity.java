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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.motion.widget.TransitionAdapter;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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

/* This test the visibility*/
public class VerificationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Verification00";
    private static final boolean DEBUG_LAYOUT = false;
    private String KEY = "layout";
    private static final boolean DEBUG = false;
    String layout_name;
    HashMap<String, Class<? extends Activity>> activity_map = new HashMap<>();

    {
        activity_map.put("verification_400", CheckSharedValues.class);
        activity_map.put("verification_035", RotationActivity.class);
        activity_map.put("verification_036", RotationActivity.class);
        activity_map.put("verification_038", RotationUsingRotateTo.class);
        activity_map.put("verification_039", RotationAngular.class);
        activity_map.put("verification_350", CustomSwipeClick.class);
        activity_map.put("constraint_set_01", ConstraintSetVerify.class);
        activity_map.put("verification_042", CheckCallbackActivity.class);
        activity_map.put("verification_057", CheckSetStates.class);
        activity_map.put("verification_502", ButtonDriveAnimate.class);
        activity_map.put("bug_004", OnCreateTransiton.class);
        activity_map.put("verification_503", FullScreenActivity.class);
        activity_map.put("v_000", ParseLayouts.class);
        activity_map.put("verification_800", CheckSetProgress.class);
        activity_map.put("basic_cl_001", CheckDumpJson.class);
        activity_map.put("basic_cl_002", CheckDumpJson.class);
        activity_map.put("verification_091", CheckCSTypes.class);
        //  activity_map.put("check_cl01", CheckCLPlugin.class);
        //  activity_map.put("verification_037", RotationToolbar.class);
        //  activity_map.put("verification_038", RotationRotateToToolbar.class);

    }

    String s = AppCompatActivity.class.getName();

    private static boolean REVERSE = false;


    private static final String RUN_FIRST = "verification_091";// (true) ? "verification_801" : "bug_005";
    private final String LAYOUTS_MATCHES = "[bcv].*_.*";

    private static String SHOW_FIRST = "";
    MotionLayout mMotionLayout;
    private Flow mFlow;

    MotionLayout findMotionLayout(ViewGroup group) {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            normalMenuStartUp();
            return;
        }
        String prelayout = extra.getString(KEY);
        layout_name = prelayout;
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setTitle("layout:"+layout_name);

        setContentView(id);
        focusJump();
        ActionBar actionBar = getSupportActionBar();
        RecyclerView rv = findView(RecyclerView.class);
        populateRecyclerView(rv);

        ViewGroup root = ((ViewGroup) findViewById(android.R.id.content).getRootView());
        View mlView = findViewById(R.id.motionLayout);
        mMotionLayout = (mlView != null) ? (MotionLayout) mlView : findMotionLayout(root);
        if (mMotionLayout != null || DEBUG_LAYOUT) {
            ConstraintSet set = new ConstraintSet();
            set.clone(mMotionLayout);
            StringWriter writer = new StringWriter();
            try {
                log("-----------------------------------------------------------------------");

                set.writeState(writer, mMotionLayout, 1);

                logBigString( writer.toString());

                log("-----------------------------------------------------------------------");
                log("-----------------------------------------------------------------------");
                set.writeState(writer, mMotionLayout, 0);
                logBigString( writer.toString());

                log("-----------------------------------------------------------------------");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        View view = findViewById(R.id.flow);
        if (view != null) {
            setupFlow((Flow) view);
        }
        view = findViewById(R.id.counter);
        if (view != null) {
            setupCounter((TextView) view);
        }
        if (mMotionLayout != null) {
            ArrayList<MotionScene.Transition> transition = mMotionLayout.getDefinedTransitions();
            int[] tids = new int[0];
            int count = 0;
            if (transition != null) {
                tids = new int[transition.size()];
                for (MotionScene.Transition t : transition) {
                    int tid = t.getId();
                    if (tid != -1) {
                        tids[count++] = tid;
                    }
                }
            }

            TransitionAdapter adapter = new TransitionAdapter() {
                long start = System.nanoTime();
                int mSid, mEid;
                final DecimalFormat df = new DecimalFormat("##0.000");

                String pad(String s) {
                    s = "           " + s;
                    return s.substring(s.length() - 7);
                }

                String pad(float v) {
                    return pad(df.format(v));
                }

                @Override
                public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                    String str = Debug.getName(getApplicationContext(), startId) +
                            "->" + Debug.getName(getApplicationContext(), endId);
                    log(" ===================  " + str + " ===================  ");
                    start = System.nanoTime();
                }

                @Override
                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                    log("               " + Debug.getName(getApplicationContext(), startId) +
                            "->" + Debug.getName(getApplicationContext(), endId) + " " + df.format(progress) + " --------- " + df.format(motionLayout.getVelocity()));

                    @SuppressWarnings("unused")
                    float dur = (System.nanoTime() - start) * 1E-6f;

                    mSid = startId;
                    mEid = endId;
                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {

                    float dur = (System.nanoTime() - start) * 1E-6f;
                    String str = Debug.getName(getApplicationContext(), currentId);
                    log(" =============x======  " + str + " <<<<<<<<<<<<< " + pad(dur) + " " + pad(motionLayout.getProgress()));

                }
            };
            tids = Arrays.copyOf(tids, count);
            log(" Transitions list  " + Arrays.toString(tids) + " " + Debug.getName(getApplicationContext(), tids));
            int[] cids = mMotionLayout.getConstraintSetIds();
            log(" ContraintSets  list  " + Arrays.toString(cids) + " " + Debug.getName(getApplicationContext(), cids));
            mMotionLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                log(" GlobalLayoutListener");
            });
            mMotionLayout.setTransitionListener(new TransitionAdapter() {
                long start = System.nanoTime();
                int mSid, mEid;

                @Override
                public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                    String str = Debug.getName(getApplicationContext(), startId) +
                            "->" + Debug.getName(getApplicationContext(), endId);
                    log(" ===================  " + str + " ===================  ");
                    start = System.nanoTime();
                }

                final DecimalFormat df = new DecimalFormat("##0.000");

                String pad(String s) {
                    s = "           " + s;
                    return s.substring(s.length() - 7);
                }

                String pad(float v) {
                    return pad(df.format(v));
                }

                @Override
                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
//                   log(
//                            "               " + Debug.getName(getApplicationContext(), startId) +
//                            "->" + Debug.getName(getApplicationContext(), endId) + " " + df.format(progress) + " --------- " + df.format(motionLayout.getVelocity()));

                    float dur = (System.nanoTime() - start) * 1E-6f;
                    mSid = startId;
                    mEid = endId;
                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                    float dur = (System.nanoTime() - start) * 1E-6f;
                    String str = Debug.getName(getApplicationContext(), currentId);
                    log(
                            " =============x======  " + str + " <<<<<<<<<<<<< " + pad(dur) + " " + pad(motionLayout.getProgress()));
                    if (mFlow != null) {
                        motionLayout.post(() -> {
                            addToFlow(mSid, mEid, currentId);
                        });
                    }
                }

                @Override
                public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                    log(
                            " ------------------  " + Debug.getName(getApplicationContext(), triggerId) +
                                    " " + positive + " progress " + progress);
                }

            });
        }

        if (extra.containsKey(SAVE_KEY)) {
            restoreMotion(extra);
        } else {
            log(Debug.getLoc() + " no saved key");
        }
    }

    private void setupCounter(TextView view) {
        view.postDelayed(() -> {
            move(view);
        }, 30);
        log(">>>>>>>>>>>>>>>> move1");
    }

    long mTime = System.nanoTime();


    @SuppressLint("SetTextI18n")
    public void move(TextView view) {
        long t = System.nanoTime() - mTime;
        view.setText(Float.toString(t * 1E-9f));
        view.postDelayed(() -> {
            move(view);
        }, 30);
    }
    // ########################### FLOW TESTING ##########################

    boolean dir = true;
    int last_gone = -1;

    private void addToFlow(int startState, int endState, int currentId) {

        if (mFlow == null) {
            return;
        }
        ConstraintSet start = mMotionLayout.getConstraintSet(startState);
        ConstraintSet end = mMotionLayout.getConstraintSet(endState);
        int[] ids = ((startState == currentId) ? start : end).getReferencedIds(mFlow.getId());
        log(" getReferencedIds " + Debug.getName(this.getApplicationContext(), ids));

        int len = ids.length;
        if (len > 20) {
            dir = false;
        } else if (len < 10) {
            dir = true;
        }
        if (dir) {
            DebugButton textView = new DebugButton(this);
            int id = 567 + len;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                id = View.generateViewId();
            }
            log(" new id  = " + id);
            textView.setId(id);
            textView.setText("(" + ids.length + ")");
            ids = Arrays.copyOf(ids, ids.length + 1);
            ids[ids.length - 1] = id;

            log(" addView = " + id);
            log(" constrainHeight  = " + id);
            int size = 191;
            MotionLayout.LayoutParams params = new MotionLayout.LayoutParams(size, size);
            textView.setLayoutParams(params);
            mMotionLayout.addView(textView, params);
            textView.setVisibility(View.VISIBLE);
            textView.setBackgroundColor(0xFFAAFFAA);
            textView.setTextColor(0xFF000000);

            start.constrainHeight(id, size);
            end.constrainHeight(id, size);
            start.constrainWidth(id, size);
            end.constrainWidth(id, size);
            log("");

            if (currentId == startState) {
                end.setReferencedIds(mFlow.getId(), ids);
                end.setVisibility(id, ConstraintSet.VISIBLE);
                mMotionLayout.updateState(endState, end);
                mMotionLayout.updateState(startState, start);
            } else {
                start.setReferencedIds(mFlow.getId(), ids);
                start.setVisibility(id, ConstraintSet.VISIBLE);
                mMotionLayout.updateState(startState, start);
                mMotionLayout.updateState(endState, end);
            }
            mMotionLayout.requestLayout();
            mMotionLayout.rebuildScene();
            mMotionLayout.invalidate();
        } else {
            log("");

            TextView textView = (TextView) findViewById(ids[ids.length - 1]);
            if (textView == null) {
                Log.e(TAG, "could not find " + Debug.getName(getApplicationContext(), ids[ids.length - 1]));
                return;
            }
            log("");
            if (last_gone != -1) {
                View view = findViewById(last_gone);
                mMotionLayout.removeView(view);
            }
            ids = Arrays.copyOf(ids, ids.length - 1);
            if (currentId == startState) {
                end.setVisibility(last_gone = textView.getId(), ConstraintSet.GONE);
                ids = removeId(ids, last_gone);
                end.setReferencedIds(mFlow.getId(), ids);
            } else {
                start.setVisibility(last_gone = textView.getId(), ConstraintSet.GONE);
                ids = removeId(ids, last_gone);
                start.setReferencedIds(mFlow.getId(), ids);
            }
            mMotionLayout.updateState(endState, end);
            mMotionLayout.updateState(startState, start);
            mMotionLayout.rebuildScene();
            log("");
            mMotionLayout.invalidate();
            Button button = mMotionLayout.findViewById(R.id.button2);
            button.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                int current = button.getVisibility();

                @Override
                public void onGlobalLayout() {
                    int newVis = button.getVisibility();
                    if (newVis != current) {
                        switch (newVis) {
                            case View.VISIBLE:
                                break;
                            case View.INVISIBLE:
                                break;
                            case View.GONE:
                                mMotionLayout.viewTransition(R.id.spin, button);
                        }
                    }
                }
            });
        }
        log("");

        int count = mMotionLayout.getChildCount();
        log("");
        String str = "[";
        for (int i = 0; i < count; i++) {
            View child = mMotionLayout.getChildAt(i);
            if (i > 0) str += ",";
            str += Debug.getName(getApplicationContext(), child.getId()) + "(" + child.getWidth() + " x " + child.getHeight() + ")";
        }
        log(" children =  " + str);
        log(" " + Debug.getName(this.getApplicationContext(), ids));
    }

    int[] removeId(int[] ids, int id_to_remove) {
        if (id_to_remove == -1) return ids;
        boolean found = false;
        for (int i = 0, k = 0; i < ids.length; i++) {
            ids[k] = ids[i];
            if (ids[k] != id_to_remove) {
                k++;
            } else {
                found = true;
            }
        }
        return found ? Arrays.copyOf(ids, ids.length - 1) : ids;
    }

    private void setupFlow(Flow flow) {
        mFlow = flow;
    }

    // ###########################END FLOW TESTING ##########################
    public void toggleDirection(View view) {
        float v = mMotionLayout.getVelocity();
        float p = mMotionLayout.getProgress();
        log("vel = " + v + " p= " + p);
        if (p == 1) {
            mMotionLayout.transitionToStart();
        } else if (p == 0) {
            mMotionLayout.transitionToEnd();
        } else if (v > 0) {
            mMotionLayout.transitionToStart();
        } else if (v < 0) {
            mMotionLayout.transitionToEnd();
        }
    }

    public void toggleColor(View view) {
        float r = (float) Math.random();
        int color = Color.rgb((int) (255 * (1 - r)), (int) (255 - r * r), (int) (255 * r));
        view.setBackgroundColor(color);
    }

    private void normalMenuStartUp() {
        String[] layouts = getLayouts(new VerificationActivity.Test() {
            @Override
            public boolean test(String s) {
                return s.matches(LAYOUTS_MATCHES);
            }
        });
        ScrollView sv = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        Button runFirst = null;
        Button button = null;
        for (int i = 0; i < layouts.length; i++) {
            button = new Button(this);
            button.setText(layouts[i]);
            button.setTag(layouts[i]);
            if (layouts[i].equals(RUN_FIRST)) {
                runFirst = button;
                Log.d(TAG, Debug.getLoc() + " auto run " + layouts[i]);
            }
            linearLayout.addView(button);
            button.setOnClickListener(this);
        }
        sv.addView(linearLayout);

        setContentView(sv);
        if (runFirst != null) {
            runFirst.callOnClick();
        }
    }

    public void jumpToMe(View v) {
        String str = (String) v.getTag();
        if (str == null || mMotionLayout == null) {
            return;
        }
        int id = getResources().getIdentifier(str, "id", getPackageName());
        mMotionLayout.transitionToState(id);
    }

    public void click(View view) {
        log(">>>>>>>>>>>>>>>>>>. ON CLICK!");
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
        toneGen1.release();
    }

    public void hideAll(View view) {
        Log.v(TAG, Debug.getLoc() + " --------------------- ");
        //  mMotionLayout.viewTransition(R.id.vt_hide_all, view);
    }

    public void hideCurrent(View view) {
        Log.v(TAG, Debug.getLoc() + " --------------------- ");
        //  mMotionLayout.viewTransition(R.id.vt_hide_current, view);
    }

    public void addToFlow2(View view) {
        Flow flow = mMotionLayout.findViewById(R.id.flow2);
        Button button = new Button(getApplicationContext());
        button.setText("AA");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            button.setId(View.generateViewId());
        }
        mMotionLayout.addView(button);
        button.setLayoutParams(new MotionLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        flow.addView(button);
        int[] ids = flow.getReferencedIds();
        Log.v(TAG, Debug.getLoc() + " ides =" + ids.length);
        {
            int[] idss = mMotionLayout.getConstraintSetIds();
            for (int id : idss) {
                ConstraintSet cset = mMotionLayout.getConstraintSet(id);
                cset.constrainWidth(button.getId(), ConstraintSet.WRAP_CONTENT);
                cset.constrainHeight(button.getId(), ConstraintSet.WRAP_CONTENT);
                cset.setReferencedIds(R.id.flow2, ids);
            }
        }
    }

    int rotate = 0;

    /**
     * Use to test the animated change api
     *
     * @param view
     */
    public void twistViews(View view) {
        rotate++;
        int current = mMotionLayout.getCurrentState();
        ConstraintSet cset = mMotionLayout.cloneConstraintSet(current);
        int[] id = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6};
        for (int i : id) {
            cset.setRotation(i, ((rotate & 1) == 0) ? 90 : 0);
        }
        int r = (rotate + 1);
        String str = (((r & 1) == 0) ? "rot 90 " : "rot 0 ") +
                (((r & 2) == 0) ? "then start" : "then end");
        ((Button) view).setText(str);
        mMotionLayout.updateStateAnimate(current, cset, 200);
        mMotionLayout.scheduleTransitionTo(((rotate & 2) == 0) ? R.id.start : R.id.end);
    }

    public void redirect(View view) {
        String str = (String) view.getTag();
        int id = getResources().getIdentifier(str, "id", getApplicationContext().getPackageName());
        if (id == mMotionLayout.getCurrentState()) {
            Log.v(TAG, Debug.getLoc() + " return to start");
            MotionScene.Transition t = mMotionLayout.getDefinedTransitions().get(0);
            mMotionLayout.transitionToState(t.getStartConstraintSetId(), 10000);
        } else {
            Log.v(TAG, Debug.getLoc() + " jump to " + str);

            mMotionLayout.transitionToState(id, 0);
        }
    }

    interface Test {
        boolean test(String s);
    }

    private String[] getLayouts(VerificationActivity.Test filter) {
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

    int color = 0xFF32323;

    public void changeColor(View v) {
        v.setBackgroundColor(color);
        color = 0xFF000000 | (int) (Math.random() * 0xFFFFFF);
    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag();
        launch((String) view.getTag());
    }

    void focusJump() {
        if (!"verification_045".startsWith(layout_name)) {
            return;
        }
        MotionLayout ml = findViewById(R.id.base);
        int[] ids = {R.id.editTextText1, R.id.editTextText, R.id.editTextText2, R.id.editTextText3};
        int[] states = {R.id.start, R.id.tx1, R.id.tx2, R.id.tx3};
        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            final int state = states[i];
            EditText text = findViewById(id);
            text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        log((((System.nanoTime() / 1000) % 100000) / 1000f) + " jump to " + Debug.getName(getApplicationContext(), state));
                        ml.transitionToState(state);
                    }
                }
            });
        }
    }

    public void launch(String id) {
        Intent intent;
        if (activity_map.containsKey(id)) {
            Log.v(TAG, Debug.getLoc() + " launch in activity " + activity_map.get(id).getSimpleName());
            intent = new Intent(this, activity_map.get(id));
        } else {
            intent = new Intent(this, VerificationActivity.class);
        }
        intent.putExtra(KEY, id);
        startActivity(intent);
    }

    // ########################### save restore motion ##########################

    private static String SAVE_KEY = "saveMotion";

    private void restoreMotion(Bundle extra) {
        log(">>>>>>>>>>>>>> RESTOR");

        mMotionLayout.setTransitionState(extra.getBundle(SAVE_KEY));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        log(">>>>>>>>>>>>>> RESTOR");
        if (savedInstanceState.containsKey(SAVE_KEY)) {
            restoreMotion(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMotionLayout == null) {
            return;
        }
        /// get Transition State
        outState.putBundle(SAVE_KEY, mMotionLayout.getTransitionState());
        log(">>>>>>>>>>>>>> SAVE INSTANCE");
    }

    MotionLayout dynamicLayout;
    int show_id;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (dynamicLayout != null) {
            dynamicLayout.transitionToState(show_id, dynamicLayout.getWidth(), dynamicLayout.getHeight());
        }
    }

    // ================================= Recycler support ====================================
    private void populateRecyclerView(RecyclerView view) {
        if (view == null) {
            return;
        }
        String[] str = new String[300];
        Random random = new Random();
        for (int i = 0; i < str.length; i++) {
            str[i] = randomString(32, random);
            log(" >>> " + str[i]);

        }
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(new VerificationActivity.MyAdapter(str));
    }

    String randomString(int length, Random random) {
        char[] lut = "etaoinsrhdlucmfywgbvkxqjz".toCharArray();

        String ret = "";
        byte[] buff = new byte[1024];
        random.nextBytes(buff);
        for (; ; ) {
            int worlen = ((char) (random.nextGaussian() * 4 + 7.5)) % 12;
            for (int i = 0; i < worlen; i++) {

                ret += lut[Math.abs(buff[ret.length()]) % lut.length];
            }
            if (ret.length() > length) {
                return ret;
            }
            ret += " ";
        }
    }

    static class MyAdapter extends RecyclerView.Adapter<VerificationActivity.MyAdapter.MyViewHolder> {
        private String[] mDataset;

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextView;

            public MyViewHolder(TextView v) {
                super(v);
                mTextView = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public VerificationActivity.MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                              int viewType) {
            // create a new view
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(5, 5, 5, 5);
            tv.setTextSize(20);
            VerificationActivity.MyAdapter.MyViewHolder vh = new VerificationActivity.MyAdapter.MyViewHolder(tv);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(VerificationActivity.MyAdapter.MyViewHolder holder, int position) {

            holder.mTextView.setText(mDataset[position]);

        }

        @Override
        public int getItemCount() {
            return mDataset.length;
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

    public void scrollup(View v) {
        RecyclerView sv = findView(RecyclerView.class);
        if (sv != null) {
            if (sv.getLayoutManager() == null) {
                LinearLayoutManager mgr = new LinearLayoutManager(this);
                sv.setLayoutManager(mgr);
                log(" settin layout manager");
            }
            log(" scroll");
            sv.scrollToPosition(0);
        }
    }

    public static void log(String str) {
        if (!DEBUG) {
            return;
        }
        StackTraceElement s = new Throwable().getStackTrace()[1];
        Log.v(TAG, ".(" + s.getFileName() + ":" + s.getLineNumber() + ") " + s.getMethodName() + "()" + str);
    }

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
