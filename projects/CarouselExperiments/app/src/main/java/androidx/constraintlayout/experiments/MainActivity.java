package androidx.constraintlayout.experiments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.constraintlayout.helper.widget.Flow;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.motion.widget.TransitionAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private String KEY = "layout";
    String layout_name;
    String s = AppCompatActivity.class.getName();
    private static boolean REVERSE = false;
    private final String LAYOUTS_MATCHES = "demo_\\d+_.*";
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
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.hide();
        }
        setContentView(id);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFfd401d));
        RecyclerView rv = findView(RecyclerView.class);
        populateRecyclerView(rv);

        ViewGroup root = ((ViewGroup) findViewById(android.R.id.content).getRootView());
        View mlView = findViewById(R.id.motionLayout);
        mMotionLayout = (mlView != null) ? (MotionLayout) mlView : findMotionLayout(root);


        if (mMotionLayout != null) {
            ArrayList<MotionScene.Transition> transition = mMotionLayout.getDefinedTransitions();
            int[] tids = new int[transition.size()];
            int count = 0;
            for (MotionScene.Transition t : transition) {
                int tid = t.getId();
                if (tid != -1) {
                    tids[count++] = tid;
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
                    Log.v(TAG, Debug.getLoc() + " ===================  " + str + " ===================  ");
                    start = System.nanoTime();
                }

                @Override
                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                    Log.v(TAG, Debug.getLoc() +
                            "               " + Debug.getName(getApplicationContext(), startId) +
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
                    Log.v(TAG, Debug.getLoc() +
                            " =============x======  " + str + " <<<<<<<<<<<<< " + pad(dur) + " " + pad(motionLayout.getProgress()));

                }
            };
            tids = Arrays.copyOf(tids, count);
            Log.v(TAG, Debug.getLoc() + " Transitions list  " + Arrays.toString(tids) + " " + Debug.getName(getApplicationContext(), tids));
            int[] cids = mMotionLayout.getConstraintSetIds();
            Log.v(TAG, Debug.getLoc() + " ContraintSets  list  " + Arrays.toString(cids) + " " + Debug.getName(getApplicationContext(), cids));
            mMotionLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                Log.v(TAG, Debug.getLocation() + " GlobalLayoutListener");
            });

            Carousel carousel = findViewById(R.id.carousel);
            TextView label = findViewById(R.id.label);
            TextView text = findViewById(R.id.text);
            if (carousel != null) {
                int images[] = {
                        R.drawable.bryce_canyon,
                        R.drawable.cathedral_rock,
                        R.drawable.death_valley,
                        R.drawable.fitzgerald_marine_reserve,
                        R.drawable.goldengate,
                        R.drawable.golden_gate_bridge,
                        R.drawable.shipwreck_1,
                        R.drawable.shipwreck_2,
                        R.drawable.grand_canyon,
                        R.drawable.horseshoe_bend,
                        R.drawable.muir_beach,
                        R.drawable.rainbow_falls,
                        //R.drawable.rockaway_beach,
                        //R.drawable.sf_coast,
                };
                int colors[] = {
                        Color.parseColor("#9C4B8F"),
                        Color.parseColor("#945693"),
                        Color.parseColor("#8C6096"),
                        Color.parseColor("#846B9A"),
                        Color.parseColor("#7C769E"),
                        Color.parseColor("#7480A2"),
                        Color.parseColor("#6D8BA5"),
                        Color.parseColor("#6595A9"),
                        Color.parseColor("#5DA0AD"),
                        Color.parseColor("#55ABB1"),
                        Color.parseColor("#4DB5B4"),
                        Color.parseColor("#45C0B8"),
                };
                mNumImages = images.length;
                if (layout_name.equals("demo_050_carousel")) {
                    mNumImages = 1;
                }
                carousel.setAdapter(new Carousel.Adapter() {
                    @Override
                    public int count() {
                        return getNumImages();
                    }

                    @Override
                    public void populate(View view, int index) {
                        if (view instanceof ImageView) {
                            ImageView imageView = (ImageView) view;
                            imageView.setImageResource(images[index]);
                        } else if (view instanceof TextView) {
                            TextView textView = (TextView) view;
                            textView.setText("#" + (index + 1));
                            textView.setBackgroundColor(colors[index]);
                        }
                    }

                    @Override
                    public void onNewItem(int index) {
                        if (label != null) {
                            label.setText("#" + (index + 1));
                        }
                    }
                });
                Button buttonAdd = findViewById(R.id.add);
                if (buttonAdd != null) {
                    buttonAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mNumImages++;
                            if (text != null) {
                                text.setText("" + mNumImages + " images");
                            }
                            carousel.refresh();
                        }
                    });
                }
                Button buttonRemove = findViewById(R.id.remove);
                if (buttonRemove != null) {
                    buttonRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mNumImages = 0;
                            if (text != null) {
                                text.setText("" + mNumImages + " images");
                            }
                            carousel.refresh();
                        }
                    });
                }
            }
        }

        if (extra.containsKey(SAVE_KEY)) {
            restoreMotion(extra);
        } else {
            Log.v(TAG, Debug.getLoc() + " no saved key");
        }
    }

    int mNumImages = 10;

    private int getNumImages() {
        return mNumImages;
    }

    private void setupCounter(TextView view) {
        view.postDelayed(() -> {
            move(view);
        }, 30);
        Log.v(TAG, ">>>>>>>>>>>>>>>> move1");
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

    public void toggleColor(View view) {
        float r = (float) Math.random();
        int color = Color.rgb((int) (255 * (1 - r)), (int) (255 - r * r), (int) (255 * r));
        view.setBackgroundColor(color);
    }

    private void normalMenuStartUp() {
        String[] layouts = getLayouts(new MainActivity.Test() {
            @Override
            public boolean test(String s) {
                return s.matches(LAYOUTS_MATCHES);
            }
        });
        ScrollView sv = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < layouts.length; i++) {
            Button button = new Button(this);
            button.setText(layouts[i]);
            button.setTag(layouts[i]);
            linearLayout.addView(button);
            button.setOnClickListener(this);
        }
        sv.addView(linearLayout);
        setContentView(sv);
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
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
        toneGen1.release();
    }

    interface Test {
        boolean test(String s);
    }

    private String[] getLayouts(MainActivity.Test filter) {
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

    public void launch(String id) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(KEY, id);
        startActivity(intent);
    }

    // ########################### save restore motion ##########################

    private static String SAVE_KEY = "saveMotion";

    private void restoreMotion(Bundle extra) {
        Log.v(TAG, Debug.getLoc() + ">>>>>>>>>>>>>> RESTOR");

        mMotionLayout.setTransitionState(extra.getBundle(SAVE_KEY));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, Debug.getLoc() + ">>>>>>>>>>>>>> RESTOR");
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
        Log.v(TAG, Debug.getLoc() + ">>>>>>>>>>>>>> SAVE INSTANCE");
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
            Log.v(TAG, " >>> " + str[i]);

        }
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(new MainActivity.MyAdapter(str));
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

    static class MyAdapter extends RecyclerView.Adapter<MainActivity.MyAdapter.MyViewHolder> {
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
        public MainActivity.MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
            // create a new view
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(5, 5, 5, 5);
            tv.setTextSize(20);
            MainActivity.MyAdapter.MyViewHolder vh = new MainActivity.MyAdapter.MyViewHolder(tv);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MainActivity.MyAdapter.MyViewHolder holder, int position) {

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
                Log.v(TAG, Debug.getLoc() + " settin layout manager");
            }
            Log.v(TAG, Debug.getLoc() + " scroll");
            sv.scrollToPosition(0);
        }
    }
}
