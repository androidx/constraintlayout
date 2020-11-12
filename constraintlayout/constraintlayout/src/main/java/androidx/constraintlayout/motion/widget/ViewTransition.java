package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class ViewTransition {
    private static String TAG = "ViewTransition";
    ConstraintSet set;
    public static final String VIEW_TRANSITION_TAG = "ViewTransition";
    public static final String KEY_FRAME_SET_TAG = "KeyFrameSet";
    public static final String CONSTRAINT_OVERRIDE = "ConstraintOverride";
    private static final int UNSET = -1;
    int mId;
    String mApplyTransitionTo;
    String mExcludeTransitionFor;
    int mOnStateTransition = UNSET;
    boolean mTransitionDisable;
    boolean mPathMotionArc;
    int mAutoViewTransition;
    KeyFrames mKeyFrames;
    ConstraintSet.Constraint mConstraintDelta;
    private int mDuration = UNSET;
    private static final int UNSET = -1;
    private static final int STATE_PRESSED = 1;
    private static final int STATE_FOCUSED = 2;
    private static final int STATE_SELECTED = 3;
    private static final int STATE_CHECKED = 4;
    private static final int STATE_ENABLED = 5;
    private static final int STATE_WINDOWFOCUSED = 6;
    private static final int STATE_VISIBLE = 7;
    private static final int STATE_GONE = 8;
    private static final int STATE_INVISIBLE = 9;
    private static final int STATE_UNFOCUSED = 10;
    private static final int STATE_UNSELECTED = 11;
    private static final int STATE_UNCHECKED = 12;
    private static final int STATE_DISABLED = 13;

    public ViewTransition(Context context, XmlPullParser parser) {

        String tagName = null;
        try {
            Key key = null;
            for (int eventType = parser.getEventType();
                 eventType != XmlResourceParser.END_DOCUMENT;
                 eventType = parser.next()) {
                switch (eventType) {
                    case XmlResourceParser.START_DOCUMENT:
                        break;
                    case XmlResourceParser.START_TAG:
                        tagName = parser.getName();
                        Log.v(TAG, Debug.getLoc() + "START " + tagName);
                        switch (tagName) {
                            case VIEW_TRANSITION_TAG:
                                parseViewTransitionTags(context, parser);
                                break;
                            case KEY_FRAME_SET_TAG:
                                mKeyFrames = new KeyFrames(context, parser);
                                break;
                            case CONSTRAINT_OVERRIDE:
                                mConstraintDelta = ConstraintSet.buildDelta(context, parser);
                                mConstraintDelta.printDelta(TAG);
                                break;
                        }

                        break;
                    case XmlResourceParser.END_TAG:
                        if (VIEW_TRANSITION_TAG.equals(parser.getName())) {
                            return;
                        }
                        break;
                    case XmlResourceParser.TEXT:
                        break;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseViewTransitionTags(Context context, XmlPullParser parser) {
        AttributeSet attrs = Xml.asAttributeSet(parser);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewTransition);
        final int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.ViewTransition_android_id) {
                mId = a.getResourceId(attr, mId);
                Log.v(TAG, Debug.getLoc() + "mId = " + mId);
            } else if (attr == R.styleable.ViewTransition_applyTransitionTo) {
                mApplyTransitionTo = a.getString(attr);
                Log.v(TAG, Debug.getLoc() + "mApplyTransitionTo = " + mApplyTransitionTo);
            } else if (attr == R.styleable.ViewTransition_excludeTransitionFor) {
                mExcludeTransitionFor = a.getString(attr);
                Log.v(TAG, Debug.getLoc() + "mExcludeTransitionFor = " + mExcludeTransitionFor);
            } else if (attr == R.styleable.ViewTransition_onStateTransition) {
                mOnStateTransition = a.getInt(attr, mOnStateTransition);
                Log.v(TAG, Debug.getLoc() + "mTransition_onState = " + mOnStateTransition);
            } else if (attr == R.styleable.ViewTransition_transitionDisable) {
                mTransitionDisable = a.getBoolean(attr, mTransitionDisable);
                Log.v(TAG, Debug.getLoc() + "mTransitionDisable = " + mTransitionDisable);
            } else if (attr == R.styleable.ViewTransition_pathMotionArc) {
                mPathMotionArc = a.getBoolean(attr, mPathMotionArc);
                Log.v(TAG, Debug.getLoc() + "mPathMotionArc = " + mPathMotionArc);
            } else if (attr == R.styleable.ViewTransition_autoViewTransition) {
                mAutoViewTransition = a.getInt(attr, mAutoViewTransition);
                Log.v(TAG, Debug.getLoc() + "mAutoViewTransition = " + mAutoViewTransition);
            } else if (attr == R.styleable.ViewTransition_duration) {
                mDuration = a.getInt(attr, mDuration);
                Log.v(TAG, Debug.getLoc() + "mAutoViewTransition = " + mAutoViewTransition);
            }


        }
        a.recycle();
    }

    private void updateTransition(MotionScene.Transition transition) {
        if (mDuration != -1) {
            transition.setDuration(mDuration);
        }
    }
    public void attacheListener(View view) {
       switch (mOnStateTransition) {
           case STATE_PRESSED:
           break;
           case STATE_UNFOCUSED:

           case STATE_FOCUSED:
               view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                   @Override
                   public void onFocusChange(View view, boolean b) {
                        event(STATE_FOCUSED,view);
                   }
               });
           break;
           case  STATE_UNSELECTED:
           case STATE_SELECTED:
           break;
           case STATE_UNCHECKED :
           case STATE_CHECKED:
           break;
           case STATE_DISABLED:
           case STATE_ENABLED:
           break;
           case STATE_WINDOWFOCUSED:
           break;
           case STATE_VISIBLE:
           break;
           case STATE_GONE:
           break;
           case STATE_INVISIBLE:
           break;
       }
    }
    void event(int type, View view) {

    }

    public void applyTransition(MotionLayout mMotionLayout, int fromid, ConstraintSet current, View... view) {
        ConstraintSet transformedState = new ConstraintSet();
        transformedState.clone(current);
        for (View view1 : view) {
            Log.v(TAG, Debug.getLoc() + " view transition " + Debug.getName(view1));
            ConstraintSet.Constraint constraint = transformedState.getConstraint(view1.getId());
            mConstraintDelta.applyDelta(constraint);
        }
        mMotionLayout.updateState(fromid, transformedState);
        mMotionLayout.updateState(R.id.view_transition, current);
        mMotionLayout.setState(R.id.view_transition, -1, -1);
        MotionScene.Transition tmpTransition = new MotionScene.Transition(-1, mMotionLayout.mScene, R.id.view_transition, fromid);
        updateTransition(tmpTransition);

        mMotionLayout.setTransition(tmpTransition);
//

        mMotionLayout.transitionToEnd();

    }
}
