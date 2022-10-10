package com.example.motionrecycle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    String[] mData = new String[200];
    private int[] mState = new int[mData.length];

    {
        for (int i = 0; i < mData.length; i++) {
            mData[i] = "This is data " + i;
            mState[i] = -1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new CustomAdapter(mData, mState));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // ========================= The RecyclerView adapter =====================
    static class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        private String[] mLocalDataSet;
        private int[] mState;

        public CustomAdapter(String[] dataSet, int[] stats) {
            mLocalDataSet = dataSet;
            mState = stats;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.motion_item, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder viewHolder, final int position) {
            int oldPos = viewHolder.getCurrentPosition();
            if (oldPos != -1) {
                mState[oldPos] = viewHolder.getState();
            }
            viewHolder.bind(position, mLocalDataSet[position], mState[position]);
        }

        @Override
        public int getItemCount() {
            return mLocalDataSet.length;
        }
    }

    // ========================= The View Holder adapter =====================
    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final Button textView;
        private final MotionLayout mMotionLayout;
        private int mPosition = -1;

        public CustomViewHolder(View view) {
            super(view);
            mMotionLayout = (MotionLayout) view;
            textView = (Button) view.findViewById(R.id.button);
        }

        public int getState() {
            if (mPosition == -1) {
                return -1;
            }
            return mMotionLayout.getCurrentState();
        }

        public int getCurrentPosition() {
            return mPosition;
        }

        public void bind(int position, String text, int state) {
            mPosition = position;
            textView.setText(text);
            if (state != -1) {
                mMotionLayout.jumpToState(state);
            } else {
                mMotionLayout.jumpToState(R.id.start);
            }
        }
    }
}