/*
 * Copyright (C) 2021 The Android Open Source Project
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
package androidx.constraintlayout.core.motion.utils;

import java.util.Arrays;

public class KeyFrameArray<E> {
    int[] keys = new int[101];
    E[] values = (E[]) new Object[101];
    int count;
    private static final int EMPTY = 999;

    public KeyFrameArray() {
        clear();
    }

    public void clear() {
        Arrays.fill(keys, EMPTY);
        Arrays.fill(values, null);
        count = 0;
    }

    public void dump() {
        System.out.println("V: " + Arrays.toString(Arrays.copyOf(keys, count)));
        System.out.print("K: [");
        for (int i = 0; i < count; i++) {
            System.out.print(((i == 0 ? "" : ", ")) + valueAt(i));
        }
        System.out.println("]");
    }

    public int size() {
        return count;
    }

    public E valueAt(int i) {
        return values[keys[i]];
    }

    public int keyAt(int i) {
        return keys[i];
    }

    public void append(int position, E value) {
        if (values[position] != null) {
            remove(position);
        }
        values[position] = value;
        keys[count++] = position;
        Arrays.sort(keys);
    }

    public void remove(int position) {
        values[position] = null;
        for (int j = 0, i = 0; i < count; i++) {
            if (position == keys[i]) {
                keys[i] = EMPTY;
                j++;
            }
            if (i != j) {
                keys[i] = keys[j];
            }
            j++;

        }
        count--;
    }
}
