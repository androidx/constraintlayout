/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.constraintlayout.widget;

import android.util.SparseIntArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Shared values
 */
public class SharedValues {
    private HashSet<WeakReference<SharedValuesListener>> mListeners = new HashSet<>();
    private SparseIntArray mValues = new SparseIntArray();

    interface SharedValuesListener {
        void onNewValue(int key, int value);
    }

    public void addListener(SharedValuesListener listener) {
        mListeners.add(new WeakReference<>(listener));
        final int count = mValues.size();
        for (int i = 0; i < count; i++) {
            int key = mValues.keyAt(i);
            int value = mValues.valueAt(i);
            listener.onNewValue(key, value);
        }
    }

    public int getValue(int key) {
        return mValues.get(key, -1);
    }

    public void fireNewValue(int key, int value) {
        boolean needsCleanup = false;
        mValues.put(key, value);
        for (WeakReference<SharedValuesListener> listenerWeakReference : mListeners) {
            SharedValuesListener listener = listenerWeakReference.get();
            if (listener != null) {
                listener.onNewValue(key, value);
            } else {
                needsCleanup = true;
            }
        }
        if (needsCleanup) {
            List<WeakReference<SharedValuesListener>> toRemove = new ArrayList<>();
            for (WeakReference<SharedValuesListener> listenerWeakReference : mListeners) {
                SharedValuesListener listener = listenerWeakReference.get();
                if (listener == null) {
                    toRemove.add(listenerWeakReference);
                }
            }
            mListeners.removeAll(toRemove);
        }
    }
}
