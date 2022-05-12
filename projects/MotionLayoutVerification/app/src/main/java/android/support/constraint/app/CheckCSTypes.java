package android.support.constraint.app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.constraintlayout.motion.widget.MotionLayout;

import java.util.Arrays;

public class CheckCSTypes extends AppCompatActivity {
    private static final String TAG = "CheckCSTypes";
    String layout_name;
    MotionLayout ml;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        String preLayout = extra.getString(Utils.KEY);
        setTitle(layout_name = preLayout);
        Context ctx = getApplicationContext();
        int id = ctx.getResources().getIdentifier(preLayout, "layout", ctx.getPackageName());
        setContentView(id);
        Log.v(TAG, Debug.getLoc()+"");
        ml  = Utils.findMotionLayout(this);
        if (ml == null) return;

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    public void move(View v) {
      String[]types =  ml.getConstraintSet(ml.getCurrentState()).getStateLabels();
      int  id =  v.getId();
        Log.v(TAG, Debug.getLoc()+" "+Debug.getName(getApplicationContext(), id));
          char a = types[0].charAt(0);
        char n = types[1].charAt(0);
        if (id == R.id.moveRight) {

            a =  (a > 'b')? 'a':(char)(a+1);
          }else  if (id == R.id.moveLeft) {
            a =  (a < 'b')? 'c':(char)(a-1);
          }else  if (id == R.id.moveUp) {
            n =  (n > '2')? '1':(char)(n+1);
          }else  if (id == R.id.moveDown) {
            n =  (n < '2')? '3':(char)(n-1);
          }
        types[0] = Character.toString(a);
        types[1] = Character.toString(n);
       int []next  = ml.getMatchingConstraintSetIds(types);
        Log.v(TAG, Debug.getLoc()+" types "+ Arrays.toString(types));

        Log.v(TAG, Debug.getLoc()+" "+Debug.getName(getApplicationContext(),next[0]));
        ml.transitionToState(next[0]);

    }
}
