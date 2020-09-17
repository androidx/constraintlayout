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

package android.support.constraint.app;

import java.text.DecimalFormat;

public class StackTrak {

    private StackTrak mParent;
    private StackTrak mChild;
    private StackTrak mSibling;
    private String mFileName;
    private int mLineNumber;
    private String mClassName;
    private String mMethodName;
    private long mNanotime = -1;

    StackTrak(StackTraceElement element, StackTrak parent) {
        mFileName = element.getFileName();
        mLineNumber = element.getLineNumber();
        mClassName = element.getClassName();
        mMethodName = element.getMethodName();
        mParent = parent;
    }
    StackTrak(StackTraceElement element, StackTrak parent,long nanotime) {
        mFileName = element.getFileName();
        mLineNumber = element.getLineNumber();
        mClassName = element.getClassName();
        mMethodName = element.getMethodName();
        mParent = parent;
        mNanotime = nanotime;
    }

    boolean equal(StackTraceElement element) {
        return mFileName .equals( element.getFileName() )&&
                mLineNumber == element.getLineNumber() &&
                mClassName.equals( element.getClassName()) &&
                mMethodName.equals( element.getMethodName());
    }


    StackTrak findCommonAnsestor(StackTraceElement[] stack, int n) {
        for (int i = n; i < stack.length; i++) {
            StackTraceElement stackTraceElement = stack[i];
            if (equal(stack[i])) {
                return this;
            }
        }
        return mParent == null ? this : mParent.findCommonAnsestor(stack, n);
    }

    static StackTrak buildTree(StackTraceElement[] stack, int n) {
        StackTrak ret = null, cursor = null;
        for (int i = stack.length - 1; i >= n; i--) {
            cursor = new StackTrak(stack[i], cursor, (n==i)?System.nanoTime():-1);
            if (ret == null) {
                ret = cursor;
            }
        }
        return ret;
    }

    public String addToTree(StackTraceElement[] st, int min) {
        StackTrak node = this;
        StackTrak lastMatch = node;

        int lastMatchIndex = st.length - 1;
        while (lastMatchIndex >= min && node != null) {
            boolean eq = node.equal(st[lastMatchIndex]);
            if (eq) {
                lastMatch = node;
                node = node.mChild;
            } else {
                StackTrak sis = node.mSibling;
                while (sis != null) {
                    if (sis.equal(st[lastMatchIndex])) {
                        lastMatch = sis;
                        node = sis.mChild;
                        break;
                    }
                    sis = sis.mSibling;
                }
                if (sis == null) {
                    break;
                }

            }
            lastMatchIndex--;
        }
        String ret = "adding to" + mFileName + "." + mMethodName + "()  " + st[lastMatchIndex].getFileName() + "." + st[lastMatchIndex].getMethodName() + "()";
        lastMatch.addBranch(st, lastMatchIndex, min);
        return ret;
    }

    private void addBranch(StackTraceElement[] st, int lastMatchIndex, int min) {
        if (lastMatchIndex >= min) {
            StackTrak toAdd = new StackTrak(st[lastMatchIndex], this, (lastMatchIndex!=min)?-1:System.nanoTime());
            toAdd.mSibling = this.mChild;
            this.mChild = toAdd;
            toAdd.addBranch(st, lastMatchIndex - 1, min);
        }
    }

    StackTrak findFirstBranch() {
        if (mSibling != null) {
            return mParent;
        }
        if (mChild != null) {
            return mChild.findFirstBranch();
        }
        return null;
    }

    public String toString() {
        int[] leaf_count = new int[1];
        leaf_count[0] = 1;
        return toString(">", leaf_count);
    }


    public String toString(String indent, int[] lcount) {
        String str = indent + " -  .(" + mFileName + ":" + mLineNumber + ") " + mMethodName + "()" + ((mChild == null) ? (" " + (lcount[0]++) + " "+getTime(mNanotime)) : "") + "\n";
        if (mChild != null) {
            str += mChild.toString(indent + " |  ", lcount);
        }
        if (mSibling != null) {
            str += mSibling.toString(indent, lcount);
        }
        return str;
    }
    private static DecimalFormat df = new DecimalFormat("0.000");

    public static String getTime(long time){

        time -= 10_000_000_000L*(time / 10_000_000_000L);
        return df.format((10_000L*(time / 10_000L))*1E-9f) ;
    }
}
