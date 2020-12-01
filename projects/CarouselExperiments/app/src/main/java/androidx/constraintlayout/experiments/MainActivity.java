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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Carousel;
import androidx.constraintlayout.motion.widget.MotionLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    String layout_name;
    MotionLayout mMotionLayout;

    int numImages;

    ////////////////////////////////////////////////////////////////
    // Some data for the Carousel examples...

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

    // Array from Activities with more examples
    Class activitiesDemo[] = {
            CarouselHelperActivity.class
    };

    ////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        if (extra == null) {
            Loader.normalMenuStartUp(this, activitiesDemo);
            return;
        }
        setupActivity(extra);
        setupCarousel();
    }

    private void setupActivity(Bundle extra) {
        String prelayout = extra.getString(Loader.KEY);
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

        ViewGroup root = ((ViewGroup) findViewById(android.R.id.content).getRootView());
        View mlView = findViewById(R.id.motionLayout);
        mMotionLayout = (mlView != null) ? (MotionLayout) mlView : Loader.findMotionLayout(root);
    }

    ////////////////////////////////////////////////////////////////
    // Setup the Carousel adapter
    ////////////////////////////////////////////////////////////////

    private void setupCarousel() {
        Carousel carousel = findViewById(R.id.carousel);
        TextView label = findViewById(R.id.label);
        if (carousel == null) {
            return;
        }
        numImages = images.length;

        if (layout_name.equals("demo_050_carousel")) {
            numImages = 1;
            setupCarouselDemo50(carousel);
        }

        carousel.setAdapter(new Carousel.Adapter() {
            @Override
            public int count() {
                return numImages;
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
    }

    // add / remove elements dynamically
    private void setupCarouselDemo50(Carousel carousel) {
        TextView text = findViewById(R.id.text);
        Button buttonAdd = findViewById(R.id.add);
        if (buttonAdd != null) {
            buttonAdd.setOnClickListener(view -> {
                numImages++;
                if (text != null) {
                    text.setText("" + numImages + " images");
                }
                carousel.refresh();
            });
        }
        Button buttonRemove = findViewById(R.id.remove);
        if (buttonRemove != null) {
            buttonRemove.setOnClickListener(view -> {
                numImages = 0;
                if (text != null) {
                    text.setText("" + numImages + " images");
                }
                carousel.refresh();
            });
        }
    }

}
