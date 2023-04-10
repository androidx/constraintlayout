package android.support.drag2d.lib;

public class MaterialVelocity2D extends MaterialVelocity {

    public MaterialVelocity2D() {
        oneDimension = false;
    }

    public void fixed(MaterialVelocity2D slower) {

        float currentPos = mStartPos;
        float destination = mEndPos;
        float currentVelocity = mStartVel;
        float duration = slower.mDuration;
        float dir = Math.signum(destination - currentPos);
        mDuration = slower.mDuration;
        int stages = mNumberOfStages = slower.mNumberOfStages;
        float t1 = mDuration / 2;
        if (stages == 1) {
            stages = mNumberOfStages = 2;
        } else {
            t1 = slower.mStage[0].getEndTime();
        }
        if (currentVelocity == 0.0) { // so we do not need to div by 0 all o ver the place
            currentVelocity = 0.0001f * dir;
        }
        switch (stages) {
            case 1:
                fixeRampDown(currentPos, destination, currentVelocity, duration);
                break;
            case 2:
                fixeRampUpRampDown(currentPos,
                        destination,
                        currentVelocity,
                        duration,
                        t1);
                break;
            case 3:
                fixe3Ramp(currentPos, destination, currentVelocity,
                        duration,
                        slower.mStage[0].getEndTime(),
                        slower.mStage[1].getEndTime());
                break;
        }
        slower.configureEasingAdapter();
        configureEasingAdapter( );

    }

    private void fixeRampDown(float currentPos, float destination, float currentVelocity,
                              float duration) {
        float distance = 2 * duration / currentVelocity;
        float timeToDestination = 2 * ((destination - currentPos) / currentVelocity);
        mNumberOfStages = 1;
        mStage[0].setUp(currentVelocity, currentPos, 0, 0, destination, timeToDestination);
        mDuration = timeToDestination;

    }


    private void fixeRampUpRampDown(float currentPos,
                                    float destination,
                                    float currentVelocity,
                                    float duration,
                                    float t1) {


        float maxV = ((destination - currentPos) * 2 - currentVelocity * t1) / duration;
        float d1 = currentPos + (currentVelocity + maxV) * t1 / 2;

        mNumberOfStages = 2;
        mStage[0].setUp(currentVelocity, currentPos, 0, maxV, d1, t1);
        mStage[1].setUp(maxV, d1, t1, 0, destination, duration);
        mDuration = duration;

    }

    public void sync(MaterialVelocity2D m) {
        if (isDominant(m)) {
            fixed(m);
        } else {
            m.fixed(this);
        }

    }

    public boolean isDominant(MaterialVelocity2D m) {
        if (m.mNumberOfStages == mNumberOfStages) {
            return mDuration < m.mDuration;
        }
        return mNumberOfStages < m.mNumberOfStages;
    }

    private void fixe3Ramp(float currentPos, float destination, float currentVelocity,
                           float duration, float t1, float t2) {

        float distance = destination - currentPos;
        float dt1 = t1;
        float dt2 = t2 - t1;
        float dt3 = duration - t2;
        float v1 = (2 * distance - currentVelocity * dt1) / (dt1 + 2 * dt2 + dt3);
        float peakV1 = v1, peakV2 = v1;

        float d1 = (currentVelocity + peakV1) * t1 / 2;
        float d2 = (peakV1 + peakV2) * (t2 - t1) / 2;
        mNumberOfStages = 3;
        mStage[0].setUp(currentVelocity, currentPos, 0, peakV1, currentPos + d1, t1);
        mStage[1].setUp(peakV1, currentPos + d1, t1, peakV2, currentPos + d1 + d2, t2);
        mStage[2].setUp(peakV2, currentPos + d1 + d2, t2, 0, destination, duration);
        mDuration = duration;
    }

}
