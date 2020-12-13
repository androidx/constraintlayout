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

import android.util.SparseArray;
import android.util.SparseIntArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Shared values
 */
public class SharedValues {
    public static final int UNSET = -1;

    private SparseIntArray mValues = new SparseIntArray();
    private HashMap<Integer, HashSet<WeakReference<SharedValuesListener>>> mValuesListeners = new HashMap<>();

    public interface SharedValuesListener {
        void onNewValue(int key, int newValue, int oldValue);
    }

    public void addListener(SharedValuesListener listener) {
        for (Integer key : mValuesListeners.keySet()) {
            addListener(listener, key);
        }
    }

    public void addListener(SharedValuesListener listener, int key) {
        HashSet<WeakReference<SharedValuesListener>> listeners = mValuesListeners.get(key);
        if (listeners == null) {
            listeners = new HashSet<>();
            mValuesListeners.put(key, listeners);
        }
        listeners.add(new WeakReference<>(listener));
    }

    public void removeListener(SharedValuesListener listener, int key) {
        HashSet<WeakReference<SharedValuesListener>> listeners = mValuesListeners.get(key);
        if (listeners == null) {
            return;
        }
        List<WeakReference<SharedValuesListener>> toRemove = new ArrayList<>();
        for (WeakReference<SharedValuesListener> listenerWeakReference : listeners) {
            SharedValuesListener l = listenerWeakReference.get();
            if (l == null || l == listener) {
                toRemove.add(listenerWeakReference);
            }
        }
        listeners.removeAll(toRemove);
    }

    public void removeListener(SharedValuesListener listener) {
        for (Integer key : mValuesListeners.keySet()) {
            removeListener(listener, key);
        }
    }

    public void clearListeners() {
        mValuesListeners.clear();
    }

    public int getValue(int key) {
        return mValues.get(key, UNSET);
    }

    public void fireNewValue(int key, int value) {
        System.out.println("fire new value!");
        boolean needsCleanup = false;
        int previousValue = mValues.get(key, UNSET);
        if (previousValue != UNSET && previousValue == value) {
            // don't send the value to listeners if it's the same one.
            return;
        }
        mValues.put(key, value);
        HashSet<WeakReference<SharedValuesListener>> listeners = mValuesListeners.get(key);
        if (listeners == null) {
            return;
        }

        for (WeakReference<SharedValuesListener> listenerWeakReference : listeners) {
            SharedValuesListener l = listenerWeakReference.get();
            if (l != null) {
                l.onNewValue(key, value, previousValue);
            } else {
                needsCleanup = true;
            }
        }

        if (needsCleanup) {
            List<WeakReference<SharedValuesListener>> toRemove = new ArrayList<>();
            for (WeakReference<SharedValuesListener> listenerWeakReference : listeners) {
                SharedValuesListener listener = listenerWeakReference.get();
                if (listener == null) {
                    toRemove.add(listenerWeakReference);
                }
            }
            listeners.removeAll(toRemove);
        }
    }
}
