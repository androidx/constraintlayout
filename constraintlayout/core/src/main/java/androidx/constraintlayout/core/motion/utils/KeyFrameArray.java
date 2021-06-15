package androidx.constraintlayout.core.motion.utils;

import androidx.constraintlayout.core.motion.CustomAttribute;

import java.util.Arrays;
import java.util.HashSet;

public class KeyFrameArray<E> {
    int[] keys = new int[101];
    E[] values = (E[]) new Object[101];
    int count;

    public KeyFrameArray() {
        clear();
    }

    public void clear() {
        Arrays.fill(keys, 999);
        count = 0;
    }

    public void dump() {
        System.out.println("V: "+Arrays.toString(Arrays.copyOf(keys, count)));
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
        values[position] = value;
            keys[count++] = position;
        Arrays.sort(keys);
    }
}
