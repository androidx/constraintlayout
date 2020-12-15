package androidx.constraintlayout.motion.widget;

/**
 *  Add support to views that do floating point layout.
 *  This can be useful to allow objects within the view to animate smoothly
 */
public interface FloatLayout {
      /**
       * To convert to regular layout 
       * l = (int)(0.5f + lf);
       * You are expected to do your own measure if you need it.
       * This will be called only during animation.
       * @param lf
       * @param tf
       * @param rf
       * @param bf
       */
      void layout(float lf, float tf, float rf, float bf);
}
