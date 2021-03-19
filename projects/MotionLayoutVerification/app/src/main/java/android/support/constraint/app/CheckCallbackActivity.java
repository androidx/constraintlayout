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
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.TransitionAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// used with verification_042.xml
public class CheckCallbackActivity extends AppCompatActivity {
    private static final String TAG = "CustomSwipeClick";
    String layout_name;
    MotionLayout mMotionLayout;

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        String prelayout = extra.getString(Utils.KEY);
        setTitle(layout_name = prelayout);
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(prelayout, "layout", ctx.getPackageName());
        setContentView(id);
        mMotionLayout = Utils.findMotionLayout(this);

        populateRecyclerView();

        TextView text = findViewById(R.id.text);
        if (text != null) {
            mMotionLayout.setTransitionListener(new TransitionAdapter() {
                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                    text.setText((currentId == R.id.expanded) ? "cb down" : "cb up");
                    Log.v(TAG, Debug.getLoc() + " "+Debug.getName(getApplicationContext(),currentId));
                }

            });
        }
    }
    // ================================= Recycler support ====================================
    private void populateRecyclerView() {
        RecyclerView rview = Utils.findView(this, RecyclerView.class);
        if (rview == null) {
            return;
        }

        rview.setLayoutManager(new LinearLayoutManager(this));
        rview.setAdapter(new MyAdapter());
    }

    static class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private String[] mDataset = new String[300];
        {
            for (int i = 0; i < mDataset.length; i++) {
                mDataset[i] = "hello world 1234567890  ".substring(0,12+(i%10));
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,  int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(10, 10, 10, 5);
            tv.setTextSize(30);
            RecyclerView.ViewHolder vh = new RecyclerView.ViewHolder(tv){};
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView)holder.itemView).setText(mDataset[position]);
        }

        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }



}
