package com.example.motionrecycle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Random;

public class RecyclerToDetailView extends AppCompatActivity {
    int[] mColor = new int[200];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_to_detail);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        MotionLayout base = findViewById(R.id.base_motionLayout);
        Random random = new Random();
        for (int i = 0; i < mColor.length; i++) {
            mColor[i] = random.nextInt(0xFFFFFF) | 0xFF000000;
        }
        recyclerView.setAdapter(new CustomAdapter(recyclerView, mColor, base));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    static class ColorBitmap {
        private final Paint paint = new Paint();
        private final float[] hsv = new float[3];
        private final Canvas canvas;
        private final float y_offset;
        private final Rect bounds = new Rect();
        public final Bitmap bitmap;
        public int color;

        ColorBitmap() {
            bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            paint.setTextSize(64);
            y_offset = paint.getFontMetrics().descent;
        }

        void update(int color) {
            this.color = color;
            paint.setColor(color);
            canvas.drawRoundRect(0, 0, 256, 256, 128, 128, paint);
            Color.colorToHSV(color, hsv);
            hsv[0] = (hsv[0] + 180) % 360;
            hsv[1] = 1 - hsv[1];
            hsv[2] = 1 - hsv[2];
            paint.setColor(Color.HSVToColor(hsv));
            String s = Integer.toHexString(color).substring(2);
            paint.getTextBounds(s, 0, 6, bounds);
            canvas.drawText(s, (256 - bounds.right) / 2f, 128 + y_offset, paint);
        }
    }

    // ========================= The RecyclerView adapter =====================
    static class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        private final MotionLayout mBaseMotionLayout;
        private final int[] mLocalDataSet;
        RecyclerView mRecyclerView;

        public CustomAdapter(RecyclerView recyclerView, int[] colors, MotionLayout base) {
            mBaseMotionLayout = base;
            mRecyclerView = recyclerView;
            mLocalDataSet = colors;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.recycler_to_detail_item, viewGroup, false);

            return new CustomViewHolder(view, mBaseMotionLayout);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder viewHolder, final int position) {
            viewHolder.bind(position, mLocalDataSet[position]);
        }

        @Override
        public int getItemCount() {
            return mLocalDataSet.length;
        }
    }

    // ========================= The View Holder adapter =====================
    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final ImageFilterButton button;
        private final ImageView imageView;
        private final MotionLayout mMotionLayout;
        private final MotionLayout mBaseMotionLayout;
        ColorBitmap colorBitmap = new ColorBitmap();
        private int mPosition = -1;

        public CustomViewHolder(View view, MotionLayout baseMotionLayout) {
            super(view);
            mBaseMotionLayout = baseMotionLayout;
            mMotionLayout = (MotionLayout) view;
            button = view.findViewById(R.id.button);
            imageView = view.findViewById(R.id.image);
            button.setOnClickListener(this::click);
        }

        // use CustomViewHolder to bind expand version
        private CustomViewHolder(View view, MotionLayout baseMotionLayout, int color) {
            super(view);
            mBaseMotionLayout = baseMotionLayout;
            mMotionLayout = (MotionLayout) view;
            button = view.findViewById(R.id.button);
            imageView = view.findViewById(R.id.image);
            colorBitmap.update(color);
            button.setOnClickListener((v) -> mBaseMotionLayout.transitionToStart());
        }

        public void click(View v) {
            CustomViewHolder detailed = (CustomViewHolder) mBaseMotionLayout.getTag(R.id.base_motionLayout);
            MotionLayout child = mBaseMotionLayout.findViewById(R.id.detail);
            if (detailed == null) {
                detailed = new CustomViewHolder(child, mBaseMotionLayout, colorBitmap.color);
                mBaseMotionLayout.setTag(R.id.base_motionLayout, detailed);
            }
            int[] pos = new int[2];
            detailed.bind(mPosition, colorBitmap.color);
            ConstraintSet cs = mBaseMotionLayout.getConstraintSet(R.id.start);
            mBaseMotionLayout.getLocationInWindow(pos);
            int x = pos[0], y = pos[1];
            mMotionLayout.getLocationInWindow(pos);
            pos[0] -= x;
            pos[1] -= y;
            cs.setMargin(R.id.detail, ConstraintSet.TOP, pos[1]);
            mBaseMotionLayout.updateState(R.id.start, cs);
            mBaseMotionLayout.transitionToEnd();
        }

        public void bind(int position, int color) {
            mPosition = position;
            colorBitmap.update(color);
            imageView.setImageBitmap(colorBitmap.bitmap);
        }
    }
}