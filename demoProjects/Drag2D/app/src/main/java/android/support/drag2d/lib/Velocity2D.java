package android.support.drag2d.lib;

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
            System.out.println(" rescale velocity to "+velocityX +" ," + velocityY);
        }
        mvX.config(posX, destinationX, velocityX, duration, maxA, maxV, easing);
        mvY.config(posY, destinationY, velocityY, duration, maxA, maxV, easing);
        System.out.println(" p duration "+mvX.getDuration()+" "+mvY.getDuration());

        mvX.sync(mvY);
        System.out.println(" a duration "+mvX.getDuration()+" "+mvY.getDuration());
        checkCurves();
    }
    private void checkCurves() {
        System.out.println(" --------x-------");
        dump(mvX);
        System.out.println(" -------y--------");

        dump(mvY);
        System.out.println("  ");

    }
    private void dump(MaterialVelocity2D mv) {
        System.out.println(" duration "+mv.getDuration());
        System.out.println(" travel "+mv.getStartPos() + " -> "+ mv.getEndPos());
        System.out.println(" NumberOfStages "+mv.mNumberOfStages);
        System.out.print( "vel  ");

        for (int i = 0; i < mv.mNumberOfStages; i++) {
            System.out.print(" | "+ mv.mStage[i].mStartV+"  -> "+ mv.mStage[i].mEndV);
        }
        System.out.println();
        System.out.print( "pos  ");

        for (int i = 0; i < mv.mNumberOfStages; i++) {
            System.out.print(" | "+ mv.mStage[i].mStartPos+"  -> "+ mv.mStage[i].mEndPos);
        }
        System.out.println();
        System.out.print( "pos* ");
        for (int i = 0; i < mv.mNumberOfStages; i++) {
            float t1 = mv.mStage[i].mStartTime+0.001f;
            float t2 = mv.mStage[i].mEndTime-0.001f;
            System.out.print(" | "+ mv.getPos(t1)+"  -> "+ mv.getPos(t2));
        }
        System.out.println();
        System.out.print( "time ");

        for (int i = 0; i < mv.mNumberOfStages; i++) {
            System.out.print(" | "+ mv.mStage[i].mStartTime+"  -> "+ mv.mStage[i].mEndTime);
        }
        System.out.println();
        System.out.print( "dist ");

        for (int i = 0; i < mv.mNumberOfStages; i++) {
            float dist = mv.mStage[i].mEndPos - mv.mStage[i].mStartPos;
            float dist2 =  (mv.mStage[i].mStartV + mv.mStage[i].mEndV) *  (mv.mStage[i].mEndTime - mv.mStage[i].mStartTime) /2;
            System.out.print(" | "+ dist+"  == "+ dist2);
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
        return mvX.getDuration() > t;
    }

    public void getCurves(float[] points, int w, int h) {
        int len = points.length;
        float duration = mvX.getDuration();
        int lines = len / 8 - 5 * 4;
        int p = 0;
        float startX = mvX.getStartPos();
        float startY = mvY.getStartPos();
        float endX = mvX.getEndPos();
        float endY = mvY.getEndPos();
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




        float min = 0, max = 1;
        float v;
        for (int i = 0; i < lines; i++) {
            float t = i * duration / lines;
             v =  (mvY.getPos(t) - startY) / (endY - startY);
             min = Math.min(v,min);
             max = Math.max(v,max);
             v =  (mvX.getPos(t) - startX) / (endX - startX);
            min = Math.min(v,min);
            max = Math.max(v,max);
        }

        float y0 = inset +regionH  - regionH *((0.0f-min)/(max-min));
        points[p++] = inset;
        points[p++] = y0;
        points[p++] = inset + regionW;
        points[p++] = y0;
        y0 = inset + regionH - regionH  *((1.0f-min)/(max-min));
        points[p++] = inset;
        points[p++] = y0;
        points[p++] = inset + regionW;
        points[p++] = y0;

        points[p++] = inset;
        points[p++] = inset + regionH;
        points[p++] = inset + regionW;
        points[p++] = inset + regionH;


        for (int i = 0; i < lines; i++) {
            float t = i * duration / lines;
            float t2 = (i + 1) * duration / lines;
            float xp1 = i / (float) lines;
            float xp2 = (i + 1) / (float) lines;
            points[p++] = inset + regionW * (xp1);
            points[p++] = inset + regionH  - regionH * ((mvY.getPos(t) - startY) / (endY - startY) - min)/(max-min);
            points[p++] = inset + regionW * (xp2);
            points[p++] = inset + regionH  - regionH * ((mvY.getPos(t2) - startY) / (endY - startY)- min)/(max-min);
        }
        for (int i = 0; i < lines; i++) {
            float t = i * duration / lines;
            float t2 = (i + 1) * duration / lines;
            float xp1 = i / (float) lines;
            float xp2 = (i + 1) / (float) lines;
            points[p++] = inset + regionW * (xp1);
            points[p++] = inset + regionH  - regionH  * ((mvX.getPos(t) - startX) / (endX - startX)- min)/(max-min);
            points[p++] = inset + regionW * (xp2);
            points[p++] = inset + regionH  - regionH  * ((mvX.getPos(t2) - startX) / (endX - startX)- min)/(max-min);
        }

    }
}
