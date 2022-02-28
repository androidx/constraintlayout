/*
 * Copyright (C) 2020 The Android Open Source Project
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
package androidx.constraintlayout.core;

import java.util.Arrays;

/**
 * Store a set of variables and their values in an array-based linked list coupled
 * with a custom hashmap.
 */
public class SolverVariableValues implements ArrayRow.ArrayRowVariables {

    private static final boolean DEBUG = false;
    private static final boolean HASH = true;
    private static float sEpsilon = 0.001f;
    private final int mNone = -1;
    private int mSize = 16;
    private int mHashSize = 16;

    int[] keys = new int[mSize];
    int[] nextKeys = new int[mSize];

    int[] variables = new int[mSize];
    float[] values = new float[mSize];
    int[] previous = new int[mSize];
    int[] next = new int[mSize];
    int mCount = 0;
    int head = -1;

    private final ArrayRow mRow; // our owner
    // pointer to the system-wide cache, allowing access to SolverVariables
    protected final Cache mCache;

    SolverVariableValues(ArrayRow row, Cache cache) {
        mRow = row;
        mCache = cache;
        clear();
    }

    @Override
    public int getCurrentSize() {
        return mCount;
    }

    @Override
    public SolverVariable getVariable(int index) {
        final int count = mCount;
        if (count == 0) {
            return null;
        }
        int j = head;
        for (int i = 0; i < count; i++) {
            if (i == index && j != mNone) {
                return mCache.mIndexedVariables[variables[j]];
            }
            j = next[j];
            if (j == mNone) {
                break;
            }
        }
        return null;
    }

    @Override
    public float getVariableValue(int index) {
        final int count = mCount;
        int j = head;
        for (int i = 0; i < count; i++) {
            if (i == index) {
                return values[j];
            }
            j = next[j];
            if (j == mNone) {
                break;
            }
        }
        return 0;
    }

    @Override
    public boolean contains(SolverVariable variable) {
        return indexOf(variable) != mNone;
    }

    @Override
    public int indexOf(SolverVariable variable) {
        if (mCount == 0 || variable == null) {
            return mNone;
        }
        int id = variable.id;
        int key = id % mHashSize;
        key = keys[key];
        if (key == mNone) {
            return mNone;
        }
        if (variables[key] == id) {
            return key;
        }
        while (nextKeys[key] != mNone && variables[nextKeys[key]] != id) {
            key = nextKeys[key];
        }
        if (nextKeys[key] == mNone) {
            return mNone;
        }
        if (variables[nextKeys[key]] == id) {
            return nextKeys[key];
        }
        return mNone;
    }

    @Override
    public float get(SolverVariable variable) {
        final int index = indexOf(variable);
        if (index != mNone) {
            return values[index];
        }
        return 0;
    }

    @Override
    public void display() {
        final int count = mCount;
        System.out.print("{ ");
        for (int i = 0; i < count; i++) {
            SolverVariable v = getVariable(i);
            if (v == null) {
                continue;
            }
            System.out.print(v + " = " + getVariableValue(i) + " ");
        }
        System.out.println(" }");
    }

    @Override
    public String toString() {
        String str = hashCode() + " { ";
        final int count = mCount;
        for (int i = 0; i < count; i++) {
            SolverVariable v = getVariable(i);
            if (v == null) {
                continue;
            }
            str += v + " = " + getVariableValue(i) + " ";
            int index = indexOf(v);
            str += "[p: ";
            if (previous[index] != mNone) {
                str += mCache.mIndexedVariables[variables[previous[index]]];
            } else {
                str += "none";
            }
            str += ", n: ";
            if (next[index] != mNone) {
                str += mCache.mIndexedVariables[variables[next[index]]];
            } else {
                str += "none";
            }
            str += "]";
        }
        str += " }";
        return str;
    }

    @Override
    public void clear() {
        if (DEBUG) {
            System.out.println(this + " <clear>");
        }
        final int count = mCount;
        for (int i = 0; i < count; i++) {
            SolverVariable v = getVariable(i);
            if (v != null) {
                v.removeFromRow(mRow);
            }
        }
        for (int i = 0; i < mSize; i++) {
            variables[i] = mNone;
            nextKeys[i] = mNone;
        }
        for (int i = 0; i < mHashSize; i++) {
            keys[i] = mNone;
        }
        mCount = 0;
        head = -1;
    }

    private void increaseSize() {
        int size = this.mSize * 2;
        variables = Arrays.copyOf(variables, size);
        values = Arrays.copyOf(values, size);
        previous = Arrays.copyOf(previous, size);
        next = Arrays.copyOf(next, size);
        nextKeys = Arrays.copyOf(nextKeys, size);
        for (int i = this.mSize; i < size; i++) {
            variables[i] = mNone;
            nextKeys[i] = mNone;
        }
        this.mSize = size;
    }

    private void addToHashMap(SolverVariable variable, int index) {
        if (DEBUG) {
            System.out.println(this.hashCode() + " hash add " + variable.id + " @ " + index);
        }
        int hash = variable.id % mHashSize;
        int key = keys[hash];
        if (key == mNone) {
            keys[hash] = index;
            if (DEBUG) {
                System.out.println(this.hashCode() + " hash add "
                        + variable.id + " @ " + index + " directly on keys " + hash);
            }
        } else {
            while (nextKeys[key] != mNone) {
                key = nextKeys[key];
            }
            nextKeys[key] = index;
            if (DEBUG) {
                System.out.println(this.hashCode() + " hash add "
                        + variable.id + " @ " + index + " as nextkey of " + key);
            }
        }
        nextKeys[index] = mNone;
        if (DEBUG) {
            displayHash();
        }
    }

    private void displayHash() {
        for (int i = 0; i < mHashSize; i++) {
            if (keys[i] != mNone) {
                String str = this.hashCode() + " hash [" + i + "] => ";
                int key = keys[i];
                boolean done = false;
                while (!done) {
                    str += " " + variables[key];
                    if (nextKeys[key] != mNone) {
                        key = nextKeys[key];
                    } else {
                        done = true;
                    }
                }
                System.out.println(str);
            }
        }
    }
    private void removeFromHashMap(SolverVariable variable) {
        if (DEBUG) {
            System.out.println(this.hashCode() + " hash remove " + variable.id);
        }
        int hash = variable.id % mHashSize;
        int key = keys[hash];
        if (key == mNone) {
            if (DEBUG) {
                displayHash();
            }
            return;
        }
        int id = variable.id;
        // let's first find it
        if (variables[key] == id) {
            keys[hash] = nextKeys[key];
            nextKeys[key] = mNone;
        } else {
            while (nextKeys[key] != mNone && variables[nextKeys[key]] != id)  {
                key = nextKeys[key];
            }
            int currentKey = nextKeys[key];
            if (currentKey != mNone && variables[currentKey] == id) {
                nextKeys[key] = nextKeys[currentKey];
                nextKeys[currentKey] = mNone;
            }
        }
        if (DEBUG) {
            displayHash();
        }
    }

    private void addVariable(int index, SolverVariable variable, float value) {
        variables[index] = variable.id;
        values[index] = value;
        previous[index] = mNone;
        next[index] = mNone;
        variable.addToRow(mRow);
        variable.usageInRowCount++;
        mCount++;
    }

    private int findEmptySlot() {
        for (int i = 0; i < mSize; i++) {
            if (variables[i] == mNone) {
                return i;
            }
        }
        return -1;
    }

    private void insertVariable(int index, SolverVariable variable, float value) {
        int availableSlot = findEmptySlot();
        addVariable(availableSlot, variable, value);
        if (index != mNone) {
            previous[availableSlot] = index;
            next[availableSlot] = next[index];
            next[index] = availableSlot;
        } else {
            previous[availableSlot] = mNone;
            if (mCount > 0) {
                next[availableSlot] = head;
                head = availableSlot;
            } else {
                next[availableSlot] = mNone;
            }
        }
        if (next[availableSlot] != mNone) {
            previous[next[availableSlot]] = availableSlot;
        }
        addToHashMap(variable, availableSlot);
    }

    @Override
    public void put(SolverVariable variable, float value) {
        if (DEBUG) {
            System.out.println(this + " <put> " + variable.id + " = " + value);
        }
        if (value > -sEpsilon && value < sEpsilon) {
            remove(variable, true);
            return;
        }
        if (mCount == 0) {
            addVariable(0, variable, value);
            addToHashMap(variable, 0);
            head = 0;
        } else {
            final int index = indexOf(variable);
            if (index != mNone) {
                values[index] = value;
            } else {
                if (mCount + 1 >= mSize) {
                    increaseSize();
                }
                final int count = mCount;
                int previousItem = -1;
                int j = head;
                for (int i = 0; i < count; i++) {
                    if (variables[j] == variable.id) {
                        values[j] = value;
                        return;
                    }
                    if (variables[j] < variable.id) {
                        previousItem = j;
                    }
                    j = next[j];
                    if (j == mNone) {
                        break;
                    }
                }
                insertVariable(previousItem, variable, value);
            }
        }
    }

    @Override
    public int sizeInBytes() {
        return 0;
    }

    @Override
    public float remove(SolverVariable v, boolean removeFromDefinition) {
        if (DEBUG) {
            System.out.println(this + " <remove> " + v.id);
        }
        int index = indexOf(v);
        if (index == mNone) {
            return 0;
        }
        removeFromHashMap(v);
        float value = values[index];
        if (head == index) {
            head = next[index];
        }
        variables[index] = mNone;
        if (previous[index] != mNone) {
            next[previous[index]] = next[index];
        }
        if (next[index] != mNone) {
            previous[next[index]] = previous[index];
        }
        mCount--;
        v.usageInRowCount--;
        if (removeFromDefinition) {
            v.removeFromRow(mRow);
        }
        return value;
    }

    @Override
    public void add(SolverVariable v, float value, boolean removeFromDefinition) {
        if (DEBUG) {
            System.out.println(this + " <add> " + v.id + " = " + value);
        }
        if (value > -sEpsilon && value < sEpsilon) {
            return;
        }
        final int index = indexOf(v);
        if (index == mNone) {
            put(v, value);
        } else {
            values[index] += value;
            if (values[index] > -sEpsilon && values[index] < sEpsilon) {
                values[index] = 0;
                remove(v, removeFromDefinition);
            }
        }
    }

    @Override
    public float use(ArrayRow def, boolean removeFromDefinition) {
        float value = get(def.variable);
        remove(def.variable, removeFromDefinition);
        if (false) {
            ArrayRow.ArrayRowVariables definitionVariables = def.variables;
            int definitionSize = definitionVariables.getCurrentSize();
            for (int i = 0; i < definitionSize; i++) {
                SolverVariable definitionVariable = definitionVariables.getVariable(i);
                float definitionValue = definitionVariables.get(definitionVariable);
                this.add(definitionVariable, definitionValue * value, removeFromDefinition);
            }
            return value;
        }
        SolverVariableValues definition = (SolverVariableValues) def.variables;
        final int definitionSize = definition.getCurrentSize();
        int j = definition.head;
        if (false) {
            for (int i = 0; i < definitionSize; i++) {
                float definitionValue = definition.values[j];
                SolverVariable definitionVariable =
                        mCache.mIndexedVariables[definition.variables[j]];
                add(definitionVariable, definitionValue * value, removeFromDefinition);
                j = definition.next[j];
                if (j == mNone) {
                    break;
                }
            }
        } else {
            j = 0;
            for (int i = 0; j < definitionSize; i++) {
                if (definition.variables[i] != mNone) {
                    float definitionValue = definition.values[i];
                    SolverVariable definitionVariable =
                            mCache.mIndexedVariables[definition.variables[i]];
                    add(definitionVariable, definitionValue * value, removeFromDefinition);
                    j++;
                }
            }
        }
        return value;
    }

    @Override
    public void invert() {
        final int count = mCount;
        int j = head;
        for (int i = 0; i < count; i++) {
            values[j] *= -1;
            j = next[j];
            if (j == mNone) {
                break;
            }
        }
    }

    @Override
    public void divideByAmount(float amount) {
        final int count = mCount;
        int j = head;
        for (int i = 0; i < count; i++) {
            values[j] /= amount;
            j = next[j];
            if (j == mNone) {
                break;
            }
        }
    }

}
