package com.example.motionrecycle;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.TransitionAdapter;
import androidx.constraintlayout.utils.widget.MotionLabel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TwoRecycler extends AppCompatActivity {
    MotionLayout motionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_two);
        setTitle("Collapsing Horizontal & Vertical RecyclerView");
        motionLayout = findViewById(R.id.motion_layout_container);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_vertical);
        configure(recyclerView, false);
        recyclerView = findViewById(R.id.recycler_view_horizontal_view);
        configure(recyclerView, true);
    }

    public void configure(RecyclerView rv, boolean horizontal) {
        rv.setAdapter( new RecyclerView.Adapter<SimpleViewHolder>() {
            final int type = (horizontal) ? R.layout.row_item_horizontal : R.layout.lock_recycler_item;

            @NonNull
            @Override
            public SimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(type, parent, false);
                return new SimpleViewHolder(view, rv);
            }

            @Override
            public void onBindViewHolder(@NonNull SimpleViewHolder holder, int position) {
                if (horizontal)
                    ((MotionLabel) (holder.label)).setText("Group " + position);
                else
                    ((Button) (holder.label)).setText("item " + position);
            }

            @Override
            public int getItemCount() {
                return 200;
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this,
                (horizontal) ? RecyclerView.HORIZONTAL : RecyclerView.VERTICAL, false));
    }

    static class SimpleViewHolder extends RecyclerView.ViewHolder {
        View label;
        RecyclerView mRecyclerView;
        TransitionAdapter adapter = new TransitionAdapter() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {
                mRecyclerView.suppressLayout(true);
            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                mRecyclerView.suppressLayout(false);
            }
        };

        public SimpleViewHolder(View itemView, RecyclerView recyclerView) {
            super(itemView);
            mRecyclerView = recyclerView;
            label = itemView.findViewById(R.id.button);
            if (itemView instanceof MotionLayout) {
                ((MotionLayout) itemView).setTransitionListener(adapter);
            }
        }
    }
}

