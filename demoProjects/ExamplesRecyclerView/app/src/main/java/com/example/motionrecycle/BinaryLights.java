package com.example.motionrecycle;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.TransitionAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BinaryLights extends AppCompatActivity {
    class MyModel {
        String name;
        int current = 0;

        void next() {
            current = (current + 1) % mStates.length;
        }

        CustomViewHolder view;
    }

    static int[] mStates = {R.id.state1, R.id.state2, R.id.state3, R.id.state4, R.id.state5, R.id.state6, R.id.state7, R.id.state8, R.id.state9};
    MyModel[] models = new MyModel[200];
    RecyclerView recyclerView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < models.length; i++) {
            models[i] = new MyModel();
            models[i].name = "(" + i + ") ";
        }
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new CustomAdapter(models));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < models.length; i++) {

                    if (models[i].view != null) {
                        models[i].view.upDateProgress();
                    } else if (models[i].current > 0) {
                        models[i].next();
                    }
                }

                handler.postDelayed(this, 2000);
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
            View view = inflater.inflate(R.layout.binary_lights, viewGroup, false);
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
        private int view_number = view_count;
        private static int view_count = 0;
        private final Button button;
        private final MotionLayout mMotionLayout;
        boolean mAnimating = false;
        MyModel mModel;
        MotionLayout.TransitionListener listener = new TransitionAdapter() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                mAnimating = true;
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                mAnimating = false;
            }
        };

        public void click(View e) {
            mModel.next();
            mMotionLayout.transitionToState(mStates[mModel.current]);
            String str = mModel.name + mModel.current;
            button.setText(str);
        }

        public CustomViewHolder(View view) {
            super(view);
            view_count++;
            mMotionLayout = (MotionLayout) view;
            button = view.findViewById(R.id.button);
            button.setOnClickListener(this::click);
            mMotionLayout.jumpToState(R.id.state0);
        }

        public void upDateProgress() {
            if (mModel.current != 0) {
                mModel.next();
                mMotionLayout.transitionToState(mStates[mModel.current]);
                String str = mModel.name + mModel.current;
                button.setText(str);
            }


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
            button.setText(model.name + model.current);
            mMotionLayout.jumpToState(mStates[model.current]);
            mMotionLayout.setTransition(mStates[model.current], mStates[model.current]);

        }
    }
}