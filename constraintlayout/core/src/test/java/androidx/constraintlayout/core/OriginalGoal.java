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

package androidx.constraintlayout.core;

import java.util.ArrayList;

/**
 * Represents a goal to minimize
 */
public class OriginalGoal {

    static int MAX = 6;

    class GoalElement {
        float[] strengths = new float[MAX];
        SolverVariable variable;
        void clearStrengths() {
            for (int i = 0; i < MAX; i++) {
                strengths[i] = 0;
            }
        }
        public String toString() {
            String representation = variable + "[";
            for (int j = 0; j < strengths.length; j++) {
                representation += strengths[j];
                if (j < strengths.length - 1) {
                    representation += ", ";
                } else {
                    representation += "] ";
                }
            }
            return representation;
        }
    }

    ArrayList<GoalElement> variables = new ArrayList<>();

    public SolverVariable getPivotCandidate() {
        final int count = variables.size();
        SolverVariable candidate = null;
        int strength = 0;

        for (int i = 0; i < count; i++) {
            GoalElement element = variables.get(i);
//            System.out.println("get pivot, looking at " + element);
            for (int k = MAX - 1; k >= 0; k--) {
                float value = element.strengths[k];
                if (candidate == null && value < 0 && (k >= strength)) {
                    strength = k;
                    candidate = element.variable;
//                    System.out.println("-> k: " + k + " strength: " + strength + " v: " + value + " candidate " + candidate);
                }
                if (value > 0 && k > strength) {
//                    System.out.println("-> reset, k: " + k + " strength: " + strength + " v: " + value + " candidate " + candidate);
                    strength = k;
                    candidate = null;
                }
            }
        }
        return candidate;
    }

    public void updateFromSystemErrors(LinearSystem system) {
        for (int i = 1; i < system.mNumColumns; i++) {
            SolverVariable variable = system.mCache.mIndexedVariables[i];
            if (variable.mType != SolverVariable.Type.ERROR) {
                continue;
            }
            GoalElement element = new GoalElement();
            element.variable = variable;
            element.strengths[variable.strength] = 1;
            variables.add(element);
        }
    }

    public void updateFromSystem(LinearSystem system) {
        variables.clear();
        updateFromSystemErrors(system);
        final int count = variables.size();
        for (int i = 0; i < count; i++) {
            GoalElement element = variables.get(i);
            if (element.variable.definitionId != -1) {
                ArrayRow definition = system.getRow(element.variable.definitionId);
                ArrayLinkedVariables variables = (ArrayLinkedVariables) (Object) definition.variables;
                int size = variables.currentSize;
                for (int j = 0; j < size; j++) {
                    SolverVariable var = variables.getVariable(j);
                    float value = variables.getVariableValue(j);
                    add(element, var, value);
                }
                element.clearStrengths();
            }
        }
    }

    public GoalElement getElement(SolverVariable variable) {
        final int count = variables.size();
        for (int i = 0; i < count; i++) {
            GoalElement element = variables.get(i);
            if (element.variable == variable) {
                return element;
            }
        }
        GoalElement element = new GoalElement();
        element.variable = variable;
        element.strengths[variable.strength] = 1;
        variables.add(element);
        return element;
    }

    public void add(GoalElement element, SolverVariable variable, float value) {
        GoalElement addition = getElement(variable);
        for (int i = 0; i < MAX; i++) {
            addition.strengths[i] += element.strengths[i] * value;
        }
    }

    public String toString() {
        String representation = "OriginalGoal: ";
        final int count = variables.size();
        for (int i = 0; i < count; i++) {
            GoalElement element = variables.get(i);
            representation += element.variable + "[";
            for (int j = 0; j < element.strengths.length; j++) {
                representation += element.strengths[j];
                if (j < element.strengths.length - 1) {
                    representation += ", ";
                } else {
                    representation += "], ";
                }
            }
        }
        return representation;
    }

}
