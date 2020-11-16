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
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.constraintlayout.motion.utils.Easing;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.ConstraintLayout;
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
    public static final String CUSTOM_ATTRIBUTE = "CustomAttribute";
    public static final String CUSTOM_METHOD = "CustomMethod";

    private static final int UNSET = -1;
    private int mId;
    // Transition can be up or down of manually fired
    private final int ONSTATETRANSITION_ACTION_DOWN = 1;
    private final int ONSTATETRANSITION_ACTION_UP = 2;
    private int mOnStateTransition = UNSET;
    private boolean mDisabled = false;
    private int mPathMotionArc = 0;
    private int mViewTransitionMode;
    private static final int VIEWTRANSITIONMODE_CURRENTSTATE = 0;
    private static final int VIEWTRANSITIONMODE_ALLSTATES = 1;
    private static final int VIEWTRANSITIONMODE_NOSTATE = 2;
    KeyFrames mKeyFrames;
    ConstraintSet.Constraint mConstraintDelta;
    private int mDuration = UNSET;
    private int mTargetId;
    private String mTargetString;

    // interpolator code
    private static final int SPLINE_STRING = -1;
    private static final int INTERPOLATOR_REFRENCE_ID = -2;
    private int mDefaultInterpolator = 0;
    private String mDefaultInterpolatorString = null;
    private int mDefaultInterpolatorID = -1;
    static final int EASE_IN_OUT = 0;
    static final int EASE_IN = 1;
    static final int EASE_OUT = 2;
    static final int LINEAR = 3;
    static final int ANTICIPATE = 4;
    static final int BOUNCE = 5;
    Context mContext;
    private int mSetsTag = UNSET;
    private int mClearsTag = UNSET;
    private int mIfTagSet = UNSET;
    private int mIfTagNotSet = UNSET;

    public String toString() {
        return "ViewTransition(" + Debug.getName(mContext, mId) + ")";
    }

    public Interpolator getInterpolator(Context context) {
        switch (mDefaultInterpolator) {
            case SPLINE_STRING:
                final Easing easing = Easing.getInterpolator(mDefaultInterpolatorString);
                return new Interpolator() {
                    @Override
                    public float getInterpolation(float v) {
                        return (float) easing.get(v);
                    }
                };
            case INTERPOLATOR_REFRENCE_ID:
                return AnimationUtils.loadInterpolator(context,
                        mDefaultInterpolatorID);
            case EASE_IN_OUT:
                return new AccelerateDecelerateInterpolator();
            case EASE_IN:
                return new AccelerateInterpolator();
            case EASE_OUT:
                return new DecelerateInterpolator();
            case LINEAR:
                return null;
            case ANTICIPATE:
                return new AnticipateInterpolator();
            case BOUNCE:
                return new BounceInterpolator();
        }
        return null;
    }

    public ViewTransition(Context context, XmlPullParser parser) {
        mContext = context;
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
                        switch (tagName) {
                            case VIEW_TRANSITION_TAG:
                                parseViewTransitionTags(context, parser);
                                break;
                            case KEY_FRAME_SET_TAG:
                                mKeyFrames = new KeyFrames(context, parser);
                                break;
                            case CONSTRAINT_OVERRIDE:
                                mConstraintDelta = ConstraintSet.buildDelta(context, parser);
                                break;
                            case CUSTOM_ATTRIBUTE:
                            case CUSTOM_METHOD:
                                ConstraintAttribute.parse(context, parser, mConstraintDelta.mCustomConstraints);
                                break;
                            default:
                                Log.e(TAG, Debug.getLoc()+" unknown tag "+tagName);
                                Log.e(TAG,  ".xml:"+parser.getLineNumber());
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
            } else if (attr == R.styleable.ViewTransition_onStateTransition) {
                mOnStateTransition = a.getInt(attr, mOnStateTransition);
            } else if (attr == R.styleable.ViewTransition_transitionDisable) {
                mDisabled = a.getBoolean(attr, mDisabled);
            } else if (attr == R.styleable.ViewTransition_pathMotionArc) {
                mPathMotionArc = a.getInt(attr, mPathMotionArc);
            } else if (attr == R.styleable.ViewTransition_duration) {
                mDuration = a.getInt(attr, mDuration);
            } else if (attr == R.styleable.ViewTransition_viewTransitionMode) {
                mViewTransitionMode = a.getInt(attr, mViewTransitionMode);
            } else if (attr == R.styleable.ViewTransition_motionInterpolator) {
                TypedValue type = a.peekValue(attr);
                if (type.type == TypedValue.TYPE_REFERENCE) {
                    mDefaultInterpolatorID = a.getResourceId(attr, -1);
                    if (mDefaultInterpolatorID != UNSET) {
                        mDefaultInterpolator = INTERPOLATOR_REFRENCE_ID;
                    }
                } else if (type.type == TypedValue.TYPE_STRING) {
                    mDefaultInterpolatorString = a.getString(attr);
                    if (mDefaultInterpolatorString.indexOf("/") > 0) {
                        mDefaultInterpolatorID = a.getResourceId(attr, UNSET);
                        mDefaultInterpolator = INTERPOLATOR_REFRENCE_ID;
                    } else {
                        mDefaultInterpolator = SPLINE_STRING;
                    }
                } else {
                    mDefaultInterpolator = a.getInteger(attr, mDefaultInterpolator);
                }
            } else if (attr == R.styleable.ViewTransition_setsTag) {
                mSetsTag = a.getResourceId(attr, mSetsTag);
            } else if (attr == R.styleable.ViewTransition_clearsTag) {
                mClearsTag = a.getResourceId(attr, mClearsTag);
            } else if (attr == R.styleable.ViewTransition_ifTagSet) {
                mIfTagSet = a.getResourceId(attr, mIfTagSet);
            } else if (attr == R.styleable.ViewTransition_ifTagNotSet) {
                mIfTagNotSet = a.getResourceId(attr, mIfTagNotSet);
            }
        }
        a.recycle();
    }

    public void applyIndependentTransition(ViewTransitionController controller, MotionLayout motionLayout, int fromId, ConstraintSet current, View view) {
        MotionController motionController = new MotionController(view);
        motionController.setBothStates(view);
        mKeyFrames.addAllFrames(motionController);
        motionController.setup(motionLayout.getWidth(), motionLayout.getHeight(), mDuration, System.nanoTime());
        new Animate(controller, motionController, mDuration, getInterpolator(motionLayout.getContext()), mSetsTag, mClearsTag);
    }

    static class Animate {
        private final int mSetsTag;
        private final int mClearsTag;
        long mStart;
        MotionController mMC;
        int mDuration;
        KeyCache mCache = new KeyCache();
        ViewTransitionController mVtController;
        Interpolator mInterpolator;

        Animate(ViewTransitionController controller,
                MotionController motionController,
                int duration,
                Interpolator interpolator, int setTag, int clearTag) {
            mVtController = controller;
            mMC = motionController;
            mDuration = duration;
            mStart = System.nanoTime();
            mVtController.addAnimation(this);
            mInterpolator = interpolator;
            mSetsTag = setTag;
            mClearsTag = clearTag;
            mutate();
        }

        void mutate() {
            long current = System.nanoTime();
            long elapse = current - mStart;
            float position = ((float) (elapse * 1E-6)) / mDuration;
            float ipos = (mInterpolator == null) ? position : mInterpolator.getInterpolation(position);
            boolean repaint = mMC.interpolate(mMC.mView, ipos, current, mCache);
            if (position >= 1) {
                if (mSetsTag != UNSET) {
                    mMC.getView().setTag(mSetsTag, System.nanoTime());
                }
                if (mClearsTag != UNSET) {
                    mMC.getView().setTag(mClearsTag, null);
                }
                mVtController.removeAnimation(this);
            }
            if (position < 1f || repaint) {
                mVtController.invalidate();
            }
        }
    }


    public void applyTransition(ViewTransitionController controller,
                                MotionLayout layout,
                                int fromId,
                                ConstraintSet current,
                                View... views) {
        if (mDisabled) {
            return;
        }
        if (mViewTransitionMode == VIEWTRANSITIONMODE_NOSTATE) {
            applyIndependentTransition(controller, layout, fromId, current, views[0]);
            return;
        }
        if (mViewTransitionMode == VIEWTRANSITIONMODE_ALLSTATES) {
            int[] ids = layout.getConstraintSetIds();
            for (int i = 0; i < ids.length; i++) {
                int id = ids[i];
                if (id == fromId) {
                    continue;
                }
                ConstraintSet cset = layout.getConstraintSet(id);
                for (View view : views) {
                    ConstraintSet.Constraint constraint = cset.getConstraint(view.getId());
                    if (mConstraintDelta != null) {
                        mConstraintDelta.applyDelta(constraint);
                        constraint.mCustomConstraints.putAll(mConstraintDelta.mCustomConstraints);
                    }
                }
            }
        }

        ConstraintSet transformedState = new ConstraintSet();
        transformedState.clone(current);
        for (View view : views) {
            ConstraintSet.Constraint constraint = transformedState.getConstraint(view.getId());
            if (mConstraintDelta != null) {
                mConstraintDelta.applyDelta(constraint);
                constraint.mCustomConstraints.putAll(mConstraintDelta.mCustomConstraints);
            }
        }

        layout.updateState(fromId, transformedState);
        layout.updateState(R.id.view_transition, current);
        layout.setState(R.id.view_transition, -1, -1);
        MotionScene.Transition tmpTransition = new MotionScene.Transition(-1, layout.mScene, R.id.view_transition, fromId);
        for (View view : views) {
            updateTransition(tmpTransition, view);
        }
        layout.setTransition(tmpTransition);
        layout.transitionToEnd(() -> {
            if (mSetsTag != UNSET) {
                for (View view : views) {
                    view.setTag(mSetsTag, System.nanoTime());
                }
            }
            if (mClearsTag != UNSET) {
                for (View view : views) {
                    view.setTag(mClearsTag, null);
                }
            }
        });
    }

    private void updateTransition(MotionScene.Transition transition, View view) {
        if (mDuration != -1) {
            transition.setDuration(mDuration);
        }
        transition.setPathMotionArc(mPathMotionArc);
        transition.setInterpolatorInfo(mDefaultInterpolator, mDefaultInterpolatorString, mDefaultInterpolatorID);
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

    boolean matchesView(View view) {
        if (view == null) {
            return false;
        }
        if (mTargetId == -1 && mTargetString == null) {
            return false;
        }
        if (!checkTags(view)) {
            return false;
        }
        if (view.getId() == mTargetId) {
            return true;
        }
        if (mTargetString == null) {
            return false;
        }
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof ConstraintLayout.LayoutParams) {
            String tag = ((ConstraintLayout.LayoutParams) (view.getLayoutParams())).constraintTag;
            if (tag != null && tag.matches(mTargetString)) {
                return true;
            }
        }
        return false;
    }

    public boolean supports(int action) {
        if (mOnStateTransition == ONSTATETRANSITION_ACTION_DOWN) {
            return action == MotionEvent.ACTION_DOWN;
        }
        if (mOnStateTransition == ONSTATETRANSITION_ACTION_UP) {
            return action == MotionEvent.ACTION_UP;
        }
        return false;
    }

    public boolean isEnabled() {
        return !mDisabled;
    }

    public void setEnable(boolean enable) {
        this.mDisabled = !enable;
    }

    boolean checkTags(View view) {

        boolean set = (mIfTagSet == UNSET) ? true : (null != view.getTag(mIfTagSet));
        boolean notSet = (mIfTagNotSet == UNSET) ? true : null == view.getTag(mIfTagNotSet);
        return set && notSet;
    }
}
