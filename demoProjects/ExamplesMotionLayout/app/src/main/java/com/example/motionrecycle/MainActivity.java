package com.example.motionrecycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity {
    private String KEY = "layout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        if (extra != null && extra.containsKey(KEY)) {
            setContentView(extra.getInt(KEY));
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    public void launch(View view) {
        Object tag = view.getTag();
        if (tag instanceof String) {
            String layout = ((String) tag);
            layout = layout.substring(layout.lastIndexOf('/') + 1, layout.lastIndexOf('.'));
            for (Field declaredField : R.layout.class.getDeclaredFields()) {
                if (layout.equals(declaredField.getName())) {
                    try {
                        Intent intent = new Intent(this, this.getClass());
                        int val = declaredField.getInt(null);
                        intent.putExtra(KEY, val);
                        startActivity(intent);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}