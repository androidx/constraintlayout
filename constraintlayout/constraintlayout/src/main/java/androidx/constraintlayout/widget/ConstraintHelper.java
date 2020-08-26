package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.solver.widgets.Helper;
import androidx.constraintlayout.solver.widgets.HelperWidget;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @hide
 * <b>Added in 1.1</b>
 * <p>
 *     This class manages a set of referenced widgets. HelperWidget objects can be created to act upon the set
 *     of referenced widgets. The difference between {@code ConstraintHelper} and {@code ViewGroup} is that
 *     multiple {@code ConstraintHelper} can reference the same widgets.
 * <p>
 *     Widgets are referenced by being added to a comma separated list of ids, e.g:
 *     <pre>
 *     {@code
 *         <androidx.constraintlayout.widget.Barrier
 *              android:id="@+id/barrier"
 *              android:layout_width="wrap_content"
 *              android:layout_height="wrap_content"
 *              app:barrierDirection="start"
 *              app:constraint_referenced_ids="button1,button2" />
 *     }
 *     </pre>
 * </p>
 */
public abstract class ConstraintHelper extends View {

    /**
     * @hide
     */
    protected int[] mIds = new int[32];
    /**
     * @hide
     */
    protected int mCount;

    /**
     * @hide
     */
    protected Context myContext;
    /**
     * @hide
     */
    protected Helper mHelperWidget;
    /**
     * @hide
     */
    protected boolean mUseViewMeasure = false;
    /**
     * @hide
     */
    protected String mReferenceIds;

    /**
     * @hide
     */
    private View[] mViews = null;

    private HashMap<Integer, String> mMap = new HashMap<>();

    public ConstraintHelper(Context context) {
        super(context);
        myContext = context;
        init(null);
    }

    public ConstraintHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
        myContext = context;
        init(attrs);
    }

    public ConstraintHelper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        myContext = context;
        init(attrs);
    }

    /**
     * @hide
     */
    protected void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            final int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.ConstraintLayout_Layout_constraint_referenced_ids) {
                    mReferenceIds = a.getString(attr);
                    setIds(mReferenceIds);
                }
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mReferenceIds != null) {
            setIds(mReferenceIds);
        }
    }

    /**
     * Add a view to the helper. The referenced view need to be a child of the helper's parent.
     * The view also need to have its id set in order to be added.
     *
     * @param view
     */
    public void addView(View view) {
        if (view == this) {
            return;
        }
        if (view.getId() == -1) {
            Log.e("ConstraintHelper", "Views added to a ConstraintHelper need to have an id");
            return;
        }
        if (view.getParent() == null) {
            Log.e("ConstraintHelper", "Views added to a ConstraintHelper need to have a parent");
            return;
        }
        mReferenceIds = null;
        addRscID(view.getId());
        requestLayout();
    }

    /**
     * Remove a given view from the helper.
     *
     * @param view
     */
    public void removeView(View view) {
        int id = view.getId();
        if (id == -1) {
            return;
        }
        mReferenceIds = null;
        for (int i = 0; i < mCount; i++) {
            if (mIds[i] == id) {
                for (int j = i; j < mCount -1; j++) {
                    mIds[j] = mIds[j + 1];
                }
                mIds[mCount - 1] = 0;
                mCount--;
                break;
            }
        }
        requestLayout();
    }

    /**
     * Helpers typically reference a collection of ids
     * @return ids referenced
     */
    public int[] getReferencedIds() {
        return Arrays.copyOf(mIds, mCount);
    }

    /**
     * Helpers typically reference a collection of ids
     * @return ids referenced
     */
    public void setReferencedIds(int[] ids) {
        mReferenceIds = null;
        mCount = 0;
        for (int i = 0; i < ids.length; i++) {
            addRscID(ids[i]);
        }
    }

    /**
     * @hide
     */
    private void addRscID(int id) {
        if (id == getId()) {
            return;
        }
        if (mCount + 1 > mIds.length) {
            mIds = Arrays.copyOf(mIds, mIds.length * 2);
        }
        mIds[mCount] = id;
        mCount++;
    }

    /**
     * @hide
     */
    @Override
    public void onDraw(Canvas canvas) {
        // Nothing
    }

    /**
     * @hide
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mUseViewMeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    /**
     * @hide
     * Allows a helper to replace the default ConstraintWidget in LayoutParams by its own subclass
     */
    public void validateParams() {
        if (mHelperWidget == null) {
            return;
        }
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params instanceof ConstraintLayout.LayoutParams) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) params;
            layoutParams.widget = (ConstraintWidget) mHelperWidget;
        }
    }

    /**
     * @hide
     */
    private void addID(String idString) {
        if (idString == null || idString.length() == 0) {
            return;
        }
        if (myContext == null) {
            return;
        }

        idString = idString.trim();

        ConstraintLayout parent = null;
        if (getParent() instanceof ConstraintLayout) {
            parent = (ConstraintLayout) getParent();
        }
        int rscId = findId(idString);
        if (rscId != 0) {
            mMap.put(rscId, idString); // let's remember the idString used, as we may need it for dynamic modules
            addRscID(rscId);
        } else {
            Log.w("ConstraintHelper", "Could not find id of \""+idString+"\"");
        }
    }

    /**
     * Attempt to find the id given a reference string
     * @param referenceId
     * @return
     */
    private int findId(String referenceId) {
        ConstraintLayout parent = null;
        if (getParent() instanceof ConstraintLayout) {
            parent = (ConstraintLayout) getParent();
        }
        int rscId = 0;

        // First, if we are in design mode let's get the cached information
        if (isInEditMode() && parent != null) {
            Object value = parent.getDesignInformation(0, referenceId);
            if (value instanceof Integer) {
                rscId = (Integer) value;
            }
        }

        // ... if not, let's check our siblings
        if (rscId == 0 && parent != null) {
            // TODO: cache this in ConstraintLayout
            rscId = findId(parent, referenceId);
        }

        if (rscId == 0) {
            try {
                Class res = R.id.class;
                Field field = res.getField(referenceId);
                rscId = field.getInt(null);
            } catch (Exception e) {
                // Do nothing
            }
        }

        if (rscId == 0) {
            // this will first try to parse the string id as a number (!) in ResourcesImpl, so
            // let's try that last...
            rscId = myContext.getResources().getIdentifier(referenceId, "id",
                    myContext.getPackageName());
        }

        return rscId;
    }

    /**
     * Iterate through the container's children to find a matching id.
     * Slow path, seems necessary to handle dynamic modules resolution...
     *
     * @param container
     * @param idString
     * @return
     */
    private int findId(ConstraintLayout container, String idString) {
        if (idString == null || container == null) {
            return 0;
        }
        Resources resources = myContext.getResources();
        if (resources == null) {
            return 0;
        }
        final int count = container.getChildCount();
        for (int j = 0; j < count; j++) {
            View child = container.getChildAt(j);
            if (child.getId() != -1) {
                String res = null;
                try {
                    res = resources.getResourceEntryName(child.getId());
                } catch (android.content.res.Resources.NotFoundException e) {
                    // nothing
                }
                if (idString.equals(res)) {
                    return child.getId();
                }
            }
        }
        return 0;
    }

    /**
     * @hide
     */
    protected void setIds(String idList) {
        mReferenceIds = idList;
        if (idList == null) {
            return;
        }
        int begin = 0;
        mCount = 0;
        while (true) {
            int end = idList.indexOf(',', begin);
            if (end == -1) {
                addID(idList.substring(begin));
                break;
            }
            addID(idList.substring(begin, end));
            begin = end + 1;
        }
    }

    /**
     * @hide
     * @param container
     */
    protected void applyLayoutFeatures(ConstraintLayout container) {
        int visibility = getVisibility();
        float elevation = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            elevation = getElevation();
        }
        for (int i = 0; i < mCount; i++) {
            int id = mIds[i];
            View view = container.getViewById(id);
            if (view != null) {
                view.setVisibility(visibility);
                if (elevation > 0 && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    view.setTranslationZ(view.getTranslationZ() + elevation);
                }
            }
        }
    }

    /**
     * @hide
     */
    protected void applyLayoutFeatures() {
        ViewParent parent = getParent();
        if (parent != null && parent instanceof ConstraintLayout) {
            applyLayoutFeatures((ConstraintLayout) parent);
        }
    }

    /**
     * @hide
     * Allows a helper a chance to update its internal object pre layout or set up connections for the pointed elements
     *
     * @param container
     */
    public void updatePreLayout(ConstraintLayout container) {
        if (isInEditMode()) {
            setIds(mReferenceIds);
        }
        if (mHelperWidget == null) {
            return;
        }
        mHelperWidget.removeAllIds();
        for (int i = 0; i < mCount; i++) {
            int id = mIds[i];
            View view = container.getViewById(id);
            if (view == null) {
                // hm -- we couldn't find the view.
                // It might still be there though, but with the wrong id (with dynamic modules)
                String candidate = mMap.get(id);
                int foundId = findId(container, candidate);
                if (foundId != 0) {
                    mIds[i] = foundId;
                    mMap.put(foundId, candidate);
                    view = container.getViewById(foundId);
                }
            }
            if (view != null) {
                mHelperWidget.add(container.getViewWidget(view));
            }
        }
        mHelperWidget.updateConstraints(container.mLayoutWidget);
    }

    public void updatePreLayout(ConstraintWidgetContainer container,
                                Helper helper,
                                SparseArray<ConstraintWidget> map) {
        helper.removeAllIds();
        for (int i = 0; i < mCount; i++) {
            int id = mIds[i];
            helper.add(map.get(id));
        }
    }

    protected View [] getViews(ConstraintLayout layout) {

        if (mViews == null || mViews.length != mCount) {
            mViews = new View[mCount];
        }

        for (int i = 0; i < mCount; i++) {
            int id = mIds[i];
            mViews[i] = layout.getViewById(id);
        }
        return mViews;
    }

    /**
     * @hide
     * Allows a helper a chance to update its internal object post layout or set up connections for the pointed elements
     *
     * @param container
     */
    public void updatePostLayout(ConstraintLayout container) {
        // Do nothing
    }

    /**
     * @hide
     * @param container
     */
    public void updatePostMeasure(ConstraintLayout container) {
        // Do nothing
    }

    public void updatePostConstraints(ConstraintLayout constainer) {
        // Do nothing
    }

    public void updatePreDraw(ConstraintLayout container) {
        // Do nothing
    }

    public void loadParameters(ConstraintSet.Constraint constraint, HelperWidget child, ConstraintLayout.LayoutParams layoutParams, SparseArray<ConstraintWidget> mapIdToWidget) {
        // TODO: we need to rethink this -- the list of referenced views shouldn't be resolved at updatePreLayout stage,
        // as this makes changing referenced views tricky at runtime
        if (constraint.layout.mReferenceIds != null) {
            setReferencedIds(constraint.layout.mReferenceIds);
        } else if (constraint.layout.mReferenceIdString != null
            && constraint.layout.mReferenceIdString.length() > 0) {
            constraint.layout.mReferenceIds = convertReferenceString(this,
                    constraint.layout.mReferenceIdString);
        }
        child.removeAllIds();
        if (constraint.layout.mReferenceIds != null) {
            for (int i = 0; i < constraint.layout.mReferenceIds.length; i++) {
                int id = constraint.layout.mReferenceIds[i];
                ConstraintWidget widget = mapIdToWidget.get(id);
                if (widget != null) {
                    child.add(widget);
                }
            }
        }
    }

    private int[] convertReferenceString(View view, String referenceIdString) {
        String[] split = referenceIdString.split(",");
        Context context = view.getContext();
        int[] rscIds = new int[split.length];
        int count = 0;
        for (int i = 0; i < split.length; i++) {
            String idString = split[i];
            idString = idString.trim();
            int id = findId(idString);
            if (id != 0) {
                rscIds[count++] = id;
            }
        }
        if (count != split.length) {
            rscIds = Arrays.copyOf(rscIds, count);
        }
        return rscIds;
    }

    public void resolveRtl(ConstraintWidget widget, boolean isRtl) {
        // nothing here
    }
}
