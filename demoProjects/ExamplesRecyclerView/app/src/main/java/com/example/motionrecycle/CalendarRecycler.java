package com.example.motionrecycle;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarRecycler extends AppCompatActivity {


    RecyclerView recyclerView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("ConstraintLayout in Recycler View");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(new CustomAdapter(200));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // ========================= The RecyclerView adapter =====================
    static class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder> {
        int mCount;

        public CustomAdapter(int count) {
            mCount = count;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.calendar, viewGroup, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomViewHolder viewHolder, final int position) {
            viewHolder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mCount;
        }
    }

    // ========================= The View Holder adapter =====================
    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout mLayout;
        static SimpleDateFormat sDateFormat = new SimpleDateFormat("LLLL yyyy");

        static int[] entries = {
                R.id.day0, R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6,
                R.id.date0, R.id.date1, R.id.date2, R.id.date3, R.id.date4, R.id.date5, R.id.date6,
                R.id.date7, R.id.date8, R.id.date9, R.id.date10, R.id.date11, R.id.date12, R.id.date13,
                R.id.date14, R.id.date15, R.id.date16, R.id.date17, R.id.date18, R.id.date19, R.id.date20,
                R.id.date21, R.id.date22, R.id.date23, R.id.date24, R.id.date25, R.id.date26, R.id.date27,
                R.id.date28, R.id.date29, R.id.date30, R.id.date31, R.id.date32, R.id.date33, R.id.date34,
                R.id.date35, R.id.date36, R.id.date37, R.id.date38, R.id.date39, R.id.date40, R.id.date41
        };
        TextView[] mTextView = new TextView[entries.length];
        TextView mTitle;

        public CustomViewHolder(View view) {
            super(view);
            mLayout = (ConstraintLayout) view.findViewById(R.id.cl_view);
            mTitle = mLayout.findViewById(R.id.title);
            for (int i = 0; i < entries.length; i++) {
                mTextView[i] = mLayout.findViewById(entries[i]);
            }
        }

        public void bind(int month_offset) {
            ArrayList<String> dates = new ArrayList<>();
            int count = 0;
            String[] title = {"S", "M", "T", "W", "T", "F", "S"};
            for (String s : title) {
                mTextView[count++].setText(s);
            }
            Calendar cal = Calendar.getInstance();

            long time = cal.getTimeInMillis();
            cal.add(Calendar.MONTH, month_offset);
            mTitle.setText(sDateFormat.format(cal.getTime()));
            for (int pos = 0; pos < 42; pos++) {
                cal.setTimeInMillis(time);
                cal.add(Calendar.MONTH, month_offset);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                int offset = cal.get(Calendar.DAY_OF_WEEK) - 1;
                int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                if (offset > pos || pos - offset >= lastDay) {
                    mTextView[count++].setText("");
                } else {
                    String str = Integer.toString(pos - offset + 1);
                    mTextView[count++].setText(str);
                }
            }
        }

    }
}