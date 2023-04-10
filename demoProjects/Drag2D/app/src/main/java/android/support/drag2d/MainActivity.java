package android.support.drag2d;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.drag2d.lib.Velocity2D;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new BallMover(getApplicationContext()));
    }

    static class BallMover extends View {
        Drawable ball;
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        Velocity2D velocity2D = new Velocity2D();
        int ballX;
        int ballY;
        int ballW = 64;
        int ballH = 64;
        public BallMover(Context context) {
            super(context);
            setup(context);
        }

        public BallMover(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            setup(context);
        }

        public BallMover(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setup(context);
        }
        void setup (Context context) {
            ball =   ResourcesCompat.getDrawable(context.getResources(),  R.drawable.volleyball,null);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            if (startAnimationTime != 0) {
                long timeMillis  =    SystemClock.uptimeMillis() - startAnimationTime;
                float time = timeMillis / 1000f;
                ballX = (int)  velocity2D.getX(time);
                ballY = (int)  velocity2D.getY(time);
                if (velocity2D.isStillMoving(time)) {
                    invalidate();
                } else {
                    startAnimationTime = 0;
                }
            }
            ball.setBounds(ballX, ballY, ballW+ballX, ballH+ballY);
            ball.draw(canvas);
        }
        float touchDownX;
        float touchDownY;
        float touchDeltaX,touchDeltaY;
        int ballDownX, ballDownY;
        long startAnimationTime;
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            velocityTracker.addMovement(event);
            switch (event.getAction()) {
                case   MotionEvent.ACTION_DOWN:
                    startAnimationTime = 0;
                    touchDownX = event.getX();
                    touchDownY = event.getY();
                    ballDownX = ballX;
                    ballDownY = ballY;
                    break;
                case   MotionEvent.ACTION_MOVE:
                    touchDeltaX = event.getX() - touchDownX;
                    touchDeltaY = event.getY() - touchDownY;
                    ballX = (int) (ballDownX + touchDeltaX);
                    ballY = (int) (ballDownY + touchDeltaY);
                    invalidate();
                  break;
                case   MotionEvent.ACTION_UP:
                    velocityTracker.computeCurrentVelocity(1000);
                    float velocityX = velocityTracker.getXVelocity();
                    float velocityY = velocityTracker.getXVelocity();
                    System.out.println(velocityX+","+velocityY);
                    startAnimationTime = event.getEventTime();
                    velocity2D.configure(ballX,ballY,velocityX,velocityY,10,10,1,1000,1000,null);
                    break;

            }
            return true;
        }
    }
}