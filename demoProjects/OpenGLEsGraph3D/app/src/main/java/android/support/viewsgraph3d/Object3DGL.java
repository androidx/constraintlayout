/*
 * Copyright (C) 2023 The Android Open Source Project
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
package android.support.viewsgraph3d;

import static android.opengl.GLES10.GL_BLEND;
import static android.opengl.GLES10.GL_LINE_SMOOTH_HINT;
import static android.opengl.GLES10.GL_NICEST;

import android.support.constraintLayout.extlib.graph3d.Object3D;
import android.util.Log;

import androidx.constraintlayout.motion.widget.Debug;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Object3DGL extends Object3D {
    protected FloatBuffer mVertexBuffer;
    protected FloatBuffer mNormalBuffer;
    protected FloatBuffer mColorBuffer;
    protected ShortBuffer mIndexBuffer;
    protected float[] colors;
    private static final boolean DEBUG = false;

    @Override
    public void makeVert(int n) {
        vert = new float[n * 3];
        tVert = new float[n * 3];
        normal = new float[n * 3];
        colors = new float[n * 4];
    }
int len = 0;
    protected void setupGLBuffers() {
        Log.v("MAIN", Debug.getLoc() + ">>>> setupGLBuffers ");
        if (len == vert.length) {
            return;
        }
        len = vert.length;
        ByteBuffer byteBuf = ByteBuffer.allocateDirect(vert.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mVertexBuffer = byteBuf.asFloatBuffer();
        mVertexBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(vert.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mNormalBuffer = byteBuf.asFloatBuffer();
        mNormalBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        mColorBuffer = byteBuf.asFloatBuffer();
        mColorBuffer.position(0);

        byteBuf = ByteBuffer.allocateDirect(index.length * 2);
        byteBuf.order(ByteOrder.nativeOrder());
        mIndexBuffer = byteBuf.asShortBuffer();
        mIndexBuffer.position(0);
    }

    protected void fillBuffers() {
        mVertexBuffer.position(0);
        mVertexBuffer.put(vert);
        mVertexBuffer.position(0);

        mNormalBuffer.position(0);
        mNormalBuffer.put(normal);
        mNormalBuffer.position(0);

        mColorBuffer.position(0);
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        mIndexBuffer.position(0);
        mIndexBuffer.put(index);
        mIndexBuffer.position(0);
    }


    public void draw(GL10 gl) {
        //gl.glFrontFace(GL10.GL_CW);
        if (mVertexBuffer == null) {
            Log.v("MAIN", Debug.getLoc() + ">>> drawing null");
            return;
        }
        if (DEBUG)
            Log.v("MAIN", Debug.getLoc() + ">>> drawing " + index.length / 3);

        setMaterial(gl);
       // gl.glEnable(GL10.GL_NORMALIZE);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        gl.glEnable(GL_BLEND);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexBuffer);
        gl.glNormalPointer(  GL10.GL_FLOAT, 0,  mNormalBuffer);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
       gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

        gl.glDrawElements(GL10.GL_TRIANGLES, index.length, GL10.GL_UNSIGNED_SHORT,
                mIndexBuffer);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
      gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }


    private void setMaterial(GL10 gl) {
        float shininess = 30;
        float[] ambient = { 0.9f, 0.6f, .6f, 1 };
        float[] diffuse = { 0.3f, 0.4f, .9f, 1 };
        float[] specular = { 0.8f, 0.8f, 0.8f, 1 };

        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specular, 0);
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininess);
    }
}
