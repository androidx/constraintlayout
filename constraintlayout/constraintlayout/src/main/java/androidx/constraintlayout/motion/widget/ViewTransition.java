package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

public class ViewTransition {
    private static String TAG = "ViewTransition";
    ConstraintSet set;
    public static final String VIEW_TRANSITION_TAG = "ViewTransition";
    public static final String KEY_FRAME_SET_TAG = "KeyFrameSet";
    public static final String CONSTRAINT_OVERRIDE = "ConstraintOverride";
    private static final int UNSET = -1;
    private int mId;

    private int mOnStateTransition = UNSET;
    private boolean mTransitionDisable;
    private boolean mPathMotionArc;
    private int mViewTransitionMode;
    private static final int VIEWTRANSITIONMODE_CURRENTSTATE = 0;
    private static final int VIEWTRANSITIONMODE_ALLSTATES = 1;
    KeyFrames mKeyFrames;
    ConstraintSet.Constraint mConstraintDelta;
    private int mDuration = UNSET;
    private int mTargetId;
    private String mTargetString;


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
                Log.v(TAG, Debug.getLoc() + "mId = " + getId());
            } else if (attr == R.styleable.ViewTransition_motionTarget) {
                if (MotionLayout.IS_IN_EDIT_MODE) {
                     mTargetId = a.getResourceId(attr, mTargetId);
                    if (mTargetId == -1) {
                        mTargetString = a.getString(attr);
                    }
                } else {
                    if (a.peekValue(attr).type == TypedValue.TYPE_STRING) {
                        mTargetString = a.getString(attr);
                    } else {
                        mTargetId = a.getResourceId(attr, mTargetId);
                    }
                }
            }  else if (attr == R.styleable.ViewTransition_onStateTransition) {
                mOnStateTransition = a.getInt(attr, mOnStateTransition);
                Log.v(TAG, Debug.getLoc() + "mTransition_onState = " + mOnStateTransition);
            } else if (attr == R.styleable.ViewTransition_transitionDisable) {
                mTransitionDisable = a.getBoolean(attr, mTransitionDisable);
                Log.v(TAG, Debug.getLoc() + "mTransitionDisable = " + mTransitionDisable);
            } else if (attr == R.styleable.ViewTransition_pathMotionArc) {
                mPathMotionArc = a.getBoolean(attr, mPathMotionArc);
                Log.v(TAG, Debug.getLoc() + "mPathMotionArc = " + mPathMotionArc);
            }   else if (attr == R.styleable.ViewTransition_duration) {
                mDuration = a.getInt(attr, mDuration);
                Log.v(TAG, Debug.getLoc() + "mDuration = " + mDuration);
            } else if (attr == R.styleable.ViewTransition_viewTransitionMode) {
                mViewTransitionMode = a.getInt(attr, mViewTransitionMode);
                Log.v(TAG, Debug.getLoc() + "mViewTransitionMode = " + mViewTransitionMode);
            }


        }
        a.recycle();
    }

    void event(int type, View view) {

    }

    public void applyTransition(MotionLayout mMotionLayout, int fromid, ConstraintSet current, View... views) {
        if (mViewTransitionMode == VIEWTRANSITIONMODE_ALLSTATES) {
           int []ids =  mMotionLayout.getConstraintSetIds();
            for (int i = 0; i < ids.length; i++) {
                int id = ids[i];
                if (id == fromid) {
                    continue;
                }
                ConstraintSet cset = mMotionLayout.getConstraintSet(id);
                for (View view : views) {
                    ConstraintSet.Constraint constraint = cset.getConstraint(view.getId());
                    mConstraintDelta.applyDelta(constraint);
                }
            }
        }
            ConstraintSet transformedState = new ConstraintSet();
            transformedState.clone(current);
            for (View view : views) {
                Log.v(TAG, Debug.getLoc() + " view transition " + Debug.getName(view));
                ConstraintSet.Constraint constraint = transformedState.getConstraint(view.getId());
                mConstraintDelta.applyDelta(constraint);
            }

        mMotionLayout.updateState(fromid, transformedState);
        mMotionLayout.updateState(R.id.view_transition, current);
        mMotionLayout.setState(R.id.view_transition, -1, -1);
        MotionScene.Transition tmpTransition = new MotionScene.Transition(-1, mMotionLayout.mScene, R.id.view_transition, fromid);
        for (View view : views) {
            updateTransition(tmpTransition, view);
        }
        mMotionLayout.setTransition(tmpTransition);
        mMotionLayout.transitionToEnd();

    }


    private void updateTransition(MotionScene.Transition transition, View view) {
        Log.v(TAG, Debug.getLoc());

        if (mDuration != -1) {
            Log.v(TAG, Debug.getLoc() + " setting duration  " + mDuration);
            transition.setDuration(mDuration);
        }
        int id = view.getId();
        if (mKeyFrames != null) {
            ArrayList<Key> keys = mKeyFrames.getKeyFramesForView(KeyFrames.UNSET);
            KeyFrames keyFrames = new KeyFrames();
            for (Key key : keys) {
                keyFrames.addKey(key.clone().setViewId(id));
            }

            transition.addtKeyFrame(keyFrames);
        }
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }
}
