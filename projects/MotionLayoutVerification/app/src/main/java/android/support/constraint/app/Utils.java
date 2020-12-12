package android.support.constraint.app;

import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
    public static String KEY = "layout";

    static MotionLayout findMotionLayout(AppCompatActivity activity) {
        ViewGroup group = ((ViewGroup) activity.findViewById(android.R.id.content).getRootView());
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

   static String[] getLayouts(String  match) {
        ArrayList<String> list = new ArrayList<>();
        Field[] f = R.layout.class.getDeclaredFields();
        boolean REVERSE = false;
        String SHOW_FIRST = null;
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
            if (match == null || name.matches(match)) {
                list.add(name);
            }
        }
        return list.toArray(new String[0]);
    }
}
