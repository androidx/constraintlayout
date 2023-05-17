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
package android.support.drag2d.lib;

import java.util.Arrays;

public class Velocity2D {
    MaterialVelocity2D mvX = new MaterialVelocity2D();
    MaterialVelocity2D mvY = new MaterialVelocity2D();

    public void configure(float posX,
                          float posY,
                          float velocityX,
                          float velocityY,
                          float destinationX,
                          float destinationY,
                          float duration,
                          float maxV,
                          float maxA, MaterialVelocity.Easing easing) {
        double speed = Math.hypot(velocityX, velocityY);
        if (speed > maxV) {
            velocityX *= maxV / speed;
            velocityY *= maxV / speed;
        }
        mvX.config(posX, destinationX, velocityX, duration, maxA, maxV, easing);
        mvY.config(posY, destinationY, velocityY, duration, maxA, maxV, easing);

        mvX.sync(mvY);
    }

    private void checkCurves() {
        System.out.println(" --------x-------");
        dump(mvX);
        System.out.println(" -------y--------");

        dump(mvY);
        System.out.println("  ");

    }

    private void dump(MaterialVelocity2D mv) {
        System.out.println(" duration " + mv.getDuration());
        System.out.println(" travel " + mv.getStartPos() + " -> " + mv.getEndPos());
        System.out.println(" NumberOfStages " + mv.mNumberOfStages);
        System.out.print("vel  ");

        for (int i = 0; i < mv.mNumberOfStages; i++) {
            System.out.print(" | " + mv.mStage[i].mStartV + "  -> " + mv.mStage[i].mEndV);
        }
        System.out.println();
        System.out.print("pos  ");

        for (int i = 0; i < mv.mNumberOfStages; i++) {
            System.out.print(" | " + mv.mStage[i].mStartPos + "  -> " + mv.mStage[i].mEndPos);
        }
        System.out.println();
        System.out.print("pos* ");
        for (int i = 0; i < mv.mNumberOfStages; i++) {
            float t1 = mv.mStage[i].mStartTime + 0.001f;
            float t2 = mv.mStage[i].mEndTime - 0.001f;
            System.out.print(" | " + mv.getPos(t1) + "  -> " + mv.getPos(t2));
        }
        System.out.println();
        System.out.print("time ");

        for (int i = 0; i < mv.mNumberOfStages; i++) {
            System.out.print(" | " + mv.mStage[i].mStartTime + "  -> " + mv.mStage[i].mEndTime);
        }
        System.out.println();
        System.out.print("dist ");

        for (int i = 0; i < mv.mNumberOfStages; i++) {
            float dist = mv.mStage[i].mEndPos - mv.mStage[i].mStartPos;
            float dist2 = (mv.mStage[i].mStartV + mv.mStage[i].mEndV) * (mv.mStage[i].mEndTime - mv.mStage[i].mStartTime) / 2;
            System.out.print(" | " + dist + "  == " + dist2);
        }
        System.out.println();


    }

    public float getX(float t) {
        return mvX.getPos(t);
    }

    public float getY(float t) {
        return mvY.getPos(t);
    }

    public boolean isStillMoving(float t) {
        return mvX.getDuration() > t || mvY.getDuration() > t;
    }

    public float getDuration() {
        return Math.max(mvX.getDuration(), mvY.mDuration);
    }

    public int getPointOffsetX(int len, float fraction) {
        int lines = (len  - 5 * 4)/8;
        int off = (int) (((len - 20) / 8) * fraction);

        if (off >= lines) {
             off = lines-2;
        }
        return 20 + 4*off;
    }

    public int getPointOffsetY(int len, float fraction) {

        int lines = (len  - 5 * 4)/8;
        int off = (int) (((len - 20) / 8) * fraction);

        if (off >= lines) {
            off = lines-2;
        }
        return 20 + 4*(lines+off);
    }
    public void getCurvesSegments(float[]t1,float[]t2) {
        Arrays.fill(t1,Float.NaN);
        Arrays.fill(t2,Float.NaN);
        for (int i = 0; i <  mvY.mNumberOfStages; i++) {
           t2[i] = mvY.mStage[i].mStartTime;

        }
        for (int i = 0; i <  mvX.mNumberOfStages; i++) {
            t1[i] = mvX.mStage[i].mStartTime;

        }
    }
    /**
     * This builds a curves that can be displayed on the screen for debugging
     *
     * @param points        in the form (x1,y1,x2,y2),... as supported by canvas.drawLines()
     * @param w
     * @param h
     * @param velocity_mode
     */
    public void getCurves(float[] points, int w, int h, boolean velocity_mode) {
        int len = points.length;
        float duration = getDuration();
        int lines = (len  - 5 * 4)/8;
        int p = 0;

        int inset = 40;
        int regionW = w - inset * 2;
        int regionH = h - inset * 2;
        points[p++] = inset;
        points[p++] = inset;
        points[p++] = inset;
        points[p++] = inset + regionH;

        points[p++] = inset + regionW;
        points[p++] = inset;
        points[p++] = inset + regionW;
        points[p++] = inset + regionH;

        points[p++] = inset;
        points[p++] = inset + regionH;
        points[p++] = inset + regionW;
        points[p++] = inset + regionH;

        float min = 0, max = 1;
        float v;
        if (velocity_mode) {
            float startX = mvX.getStartV();
            float startY = mvY.getStartV();
            float endX = 0;
            float endY = 0;
            for (int i = 0; i < lines; i++) {
                float t = i * duration / lines;
                v = (mvY.getV(t) - startY) / (endY - startY);
                min = Math.min(v, min);
                max = Math.max(v, max);
                v = (mvX.getV(t) - startX) / (endX - startX);
                min = Math.min(v, min);
                max = Math.max(v, max);
            }

            float y0 = inset + regionH - regionH * ((0.0f - min) / (max - min));
            points[p++] = inset;
            points[p++] = y0;
            points[p++] = inset + regionW;
            points[p++] = y0;
            y0 = inset + regionH - regionH * ((1.0f - min) / (max - min));
            points[p++] = inset;
            points[p++] = y0;
            points[p++] = inset + regionW;
            points[p++] = y0;


            for (int i = 0; i < lines; i++) {
                float t = i * duration / lines;
                float t2 = (i + 1) * duration / lines;
                float xp1 = i / (float) lines;
                float xp2 = (i + 1) / (float) lines;
                points[p++] = inset + regionW * (xp1);
                points[p++] = inset + regionH - regionH * ((mvY.getV(t) - startY) / (endY - startY) - min) / (max - min);
                points[p++] = inset + regionW * (xp2);
                points[p++] = inset + regionH - regionH * ((mvY.getV(t2) - startY) / (endY - startY) - min) / (max - min);
            }
            for (int i = 0; i < lines; i++) {
                float t = i * duration / lines;
                float t2 = (i + 1) * duration / lines;
                float xp1 = i / (float) lines;
                float xp2 = (i + 1) / (float) lines;
                points[p++] = inset + regionW * (xp1);
                points[p++] = inset + regionH - regionH * ((mvX.getV(t) - startX) / (endX - startX) - min) / (max - min);
                points[p++] = inset + regionW * (xp2);
                points[p++] = inset + regionH - regionH * ((mvX.getV(t2) - startX) / (endX - startX) - min) / (max - min);
            }

        } else {
            float startX = mvX.getStartPos();
            float startY = mvY.getStartPos();
            float endX = mvX.getEndPos();
            float endY = mvY.getEndPos();
            for (int i = 0; i < lines; i++) {
                float t = i * duration / lines;
                v = (mvY.getPos(t) - startY) / (endY - startY);
                min = Math.min(v, min);
                max = Math.max(v, max);
                v = (mvX.getPos(t) - startX) / (endX - startX);
                min = Math.min(v, min);
                max = Math.max(v, max);
            }

            float y0 = inset + regionH - regionH * ((0.0f - min) / (max - min));
            points[p++] = inset;
            points[p++] = y0;
            points[p++] = inset + regionW;
            points[p++] = y0;
            y0 = inset + regionH - regionH * ((1.0f - min) / (max - min));
            points[p++] = inset;
            points[p++] = y0;
            points[p++] = inset + regionW;
            points[p++] = y0;


            for (int i = 0; i < lines; i++) {
                float t = i * duration / lines;
                float t2 = (i + 1) * duration / lines;
                float xp1 = i / (float) lines;
                float xp2 = (i + 1) / (float) lines;
                points[p++] = inset + regionW * (xp1);
                points[p++] = inset + regionH - regionH * ((mvY.getPos(t) - startY) / (endY - startY) - min) / (max - min);
                points[p++] = inset + regionW * (xp2);
                points[p++] = inset + regionH - regionH * ((mvY.getPos(t2) - startY) / (endY - startY) - min) / (max - min);
            }
            for (int i = 0; i < lines; i++) {
                float t = i * duration / lines;
                float t2 = (i + 1) * duration / lines;
                float xp1 = i / (float) lines;
                float xp2 = (i + 1) / (float) lines;
                points[p++] = inset + regionW * (xp1);
                points[p++] = inset + regionH - regionH * ((mvX.getPos(t) - startX) / (endX - startX) - min) / (max - min);
                points[p++] = inset + regionW * (xp2);
                points[p++] = inset + regionH - regionH * ((mvX.getPos(t2) - startX) / (endX - startX) - min) / (max - min);
            }

        }
    }
}
