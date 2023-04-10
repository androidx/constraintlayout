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
                          float maxA, MaterialVelocity.Easing easing){
        mvX.config(posX,destinationX,velocityX,duration,maxA,maxV,easing);
        mvY.config(posY,destinationY,velocityY,duration,maxA,maxV,easing);
        mvX.sync(mvY);
    }
    public float getX(float t) {
       return mvX.getPos(t);
    }
    public float getY(float t) {
        return mvY.getPos(t);
    }
    public boolean isStillMoving(float t){
       return mvX.getDuration() > t;
    }
}
