package com.example.motionrecycle;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.TransitionAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

public class MotionRecycler2 extends AppCompatActivity {
    class MyModel {
        String name;
        long countdown = 0;
        CustomViewHolder view;
    }

    MyModel[] models = new MyModel[200];
    RecyclerView recyclerView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < models.length; i++) {
            models[i] = new MyModel();
            models[i].name = "done";
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new CustomAdapter(models));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < models.length; i++) {
                    if (models[i].view != null)
                        models[i].view.upDateProgress();
                }
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    // ========================= The RecyclerView adapter =====================
    static class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        private MyModel[] mModels;

        public CustomAdapter(MyModel[] models) {
            mModels = models;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.timer_item, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder viewHolder, final int position) {
            if (viewHolder.mModel != null) {
                viewHolder.mModel.view = null;
            }
            viewHolder.bind(mModels[position]);
        }

        @Override
        public int getItemCount() {
            return mModels.length;
        }
    }

    // ========================= The View Holder adapter =====================
    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final Button button;
        private final MotionLayout mMotionLayout;
        MyModel mModel;
        boolean setting = false;
        DecimalFormat decimalFormat = new DecimalFormat("##.000");
        TransitionAdapter adapter = new TransitionAdapter() {
            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {
                if (setting ) {
                    return;
                }

                mModel.countdown = calcDuration(progress) + System.currentTimeMillis();
                upDateProgress();
            }
        };

        public void click(View e) {
            mModel.countdown = System.currentTimeMillis();
            mMotionLayout.jumpToState(R.id.start);
        }
        public CustomViewHolder(View view) {
            super(view);
            mMotionLayout = (MotionLayout) view;
            button = view.findViewById(R.id.button);
            button.setOnClickListener(this::click);
        }

        public void upDateProgress() {
            long duration = mModel.countdown -  System.currentTimeMillis();
            if (duration <= 0) {
                button.setText("done");
                return;
            }
            setting = true;
            mMotionLayout.setProgress(calcProgress(duration));
            setting = false;
            button.setText(decimalFormat.format(duration * 1E-3f));
        }

        private float calcProgress(long duration) {
            return duration * 1E-5f;
        }

        private long calcDuration(float progress) {
            return (long) (progress * 1E5);
        }

        public void bind(MyModel model) {
            mModel = model;
            model.view = this;
            mMotionLayout.setTransitionListener(adapter);
            button.setText(model.name);
            if (mModel.countdown < System.currentTimeMillis()) {
                mMotionLayout.jumpToState(R.id.start);
            } else {
                upDateProgress();
            }
        }
    }
}