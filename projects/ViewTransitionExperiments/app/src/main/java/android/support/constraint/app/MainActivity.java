package android.support.constraint.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Class []demos = { Demo00vt.class};

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < demos.length; i++) {
            Button button = new Button(this);
            button.setText(demos[i].getSimpleName());
            button.setTag(demos[i]);
            linearLayout.addView(button);
            button.setOnClickListener(this);
        }
        scrollView.addView(linearLayout);
        setContentView(scrollView);
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this,(Class) view.getTag()));
    }
}
