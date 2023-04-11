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

public class CubicEasing implements MaterialVelocity.Easing {

    private static final float[] STANDARD_COEFFICIENTS = {0.4f, 0.0f, 0.2f, 1f};
    private static final float[] ACCELERATE_COEFFICIENTS = {0.4f, 0.05f, 0.8f, 0.7f};
    private static final float[] DECELERATE_COEFFICIENTS = {0.0f, 0.0f, 0.2f, 0.95f};
    private static final float[] LINEAR_COEFFICIENTS = {1f, 1f, 0f, 0f};
    private static final float[] ANTICIPATE_COEFFICIENTS = {0.36f, 0f, 0.66f, -0.56f};
    private static final float[] OVERSHOOT_COEFFICIENTS = {0.34f, 1.56f, 0.64f, 1f};

    public static final String DECELERATE_NAME = "decelerate";
    public static final String ACCELERATE_NAME = "accelerate";
    public static final String STANDARD_NAME = "standard";
    public static final String LINEAR_NAME = "linear";
    public static final String ANTICIPATE_NAME = "anticipate";
    public static final String OVERSHOOT_NAME = "overshoot";

    // public static final CubicEasing STANDARD = new CubicEasing(STANDARD_COEFFICIENTS);
    // public static final CubicEasing ACCELERATE = new CubicEasing(ACCELERATE_COEFFICIENTS);
    public static final CubicEasing DECELERATE = new CubicEasing(DECELERATE_COEFFICIENTS);
    public static final CubicEasing LINEAR = new CubicEasing(LINEAR_COEFFICIENTS);
    // public static final CubicEasing ANTICIPATE = new CubicEasing(ANTICIPATE_COEFFICIENTS);
    public static final CubicEasing OVERSHOOT = new CubicEasing(OVERSHOOT_COEFFICIENTS);
    public static final CubicEasing EASE_OUT_SINE = new CubicEasing(new float[]{0.61f, 1f, 0.88f, 1f});
    public static final CubicEasing EASE_OUT_CUBIC = new CubicEasing(new float[]{0.33f, 1f, 0.68f, 1f});
    public static final CubicEasing EASE_OUT_QUINT = new CubicEasing(new float[]{0.22f, 1f, 0.36f, 1f});
    public static final CubicEasing EASE_OUT_CIRC = new CubicEasing(new float[]{0.02f, 0.55f, 0.45f, 1f});
    public static final CubicEasing EASE_OUT_QUAD = new CubicEasing(new float[]{0.5f, 1f, 0.89f, 1f});
    public static final CubicEasing EASE_OUT_QUART = new CubicEasing(new float[]{0.25f, 1f, 0.5f, 1f});
    public static final CubicEasing EASE_OUT_EXPO = new CubicEasing(new float[]{0.16f, 1f, 0.3f, 1f});
    public static final CubicEasing EASE_OUT_BACK = new CubicEasing(new float[]{0.34f, 1.56f, 0.64f, 1f});
    public static final MaterialVelocity.Easing EASE_OUT_ELASTIC = new EaseOutElastic();


    static class EaseOutElastic implements MaterialVelocity.Easing {

        double c4 = (2 * Math.PI) / 3;
        double TWENTY_PI = 20 * Math.PI;
        double log8 = Math.log(8);

        @Override
        public double get(double t) {
            if (t <= 0) {
                return 0.0;
            }
            if (t >= 1) {
                return 1.0;
            }
            return Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1;
        }

        @Override
        public double getDiff(double t) {
            double c4 = (2 * Math.PI) / 3;
            if (t < 0 || t > 1) {
                return 0.0;
            }

            double v = 5 * Math.pow(2, 1 - 10 * t) *
                    (log8 * Math.cos((TWENTY_PI * t) / 3) + 2 * Math.PI * Math.sin((TWENTY_PI * t) / 3)) / 3;
            return v;
        }

        @Override
        public EaseOutElastic clone() {
            return new EaseOutElastic();
        }
    }

    ;
    public static final MaterialVelocity.Easing EASE_OUT_BOUNCE = new EaseOutBounce();


    static class EaseOutBounce implements MaterialVelocity.Easing {
        double n1 = 7.5625;
        double d1 = 2.75;

        @Override
        public double get(double t) {

            if (t < 0) {
                return 0;
            }
            if (t < 1 / d1) {
                return (1 / (1 + 1 / d1)) * (n1 * t * t + t);
            } else if (t < 2 / d1) {
                return n1 * (t -= 1.5 / d1) * t + 0.75;
            } else if (t < 2.5 / d1) {
                return n1 * (t -= 2.25 / d1) * t + 0.9375;
            } else if (t <= 1) {
                return n1 * (t -= 2.625 / d1) * t + 0.984375;
            }
            return 1;
        }

        @Override
        public double getDiff(double t) {
            double result;
            if (t < 0) {
                return 0;
            }
            if (t < 1 / d1) {
                return 2 * n1 * (t) / (1 + 1 / d1) + 1 / (1 + 1 / d1);
            } else if (t < 2 / d1) {
                return 2 * n1 * (t - 1.5 / d1);
            } else if (t < 2.5 / d1) {
                return 2 * n1 * (t - 2.25 / d1);
            } else if (t <= 1) {
                return 2 * n1 * (t - 2.625 / d1);
            }
            return 0;

        }

        @Override
        public EaseOutBounce clone() {
            return new EaseOutBounce();
        }
    }

    ;


    private static double sError = 0.001;
    private static double sDError = 0.0001;
    private String mConfigString;
    double mX1, mY1, mX2, mY2;

    CubicEasing(String configString) {
        // done this way for efficiency
        mConfigString = configString;
        int start = configString.indexOf('(');
        int off1 = configString.indexOf(',', start);
        mX1 = Double.parseDouble(configString.substring(start + 1, off1).trim());
        int off2 = configString.indexOf(',', off1 + 1);
        mY1 = Double.parseDouble(configString.substring(off1 + 1, off2).trim());
        int off3 = configString.indexOf(',', off2 + 1);
        mX2 = Double.parseDouble(configString.substring(off2 + 1, off3).trim());
        int end = configString.indexOf(')', off3 + 1);
        mY2 = Double.parseDouble(configString.substring(off3 + 1, end).trim());
    }

    CubicEasing(float[] c) {
        this(c[0], c[1], c[2], c[3]);
    }

    CubicEasing(double x1, double y1, double x2, double y2) {
        setup(x1, y1, x2, y2);
    }


    public CubicEasing clone() {
        return new CubicEasing(mX1, mY1, mX2, mY2);
    }

    void setup(double x1, double y1, double x2, double y2) {
        this.mX1 = x1;
        this.mY1 = y1;
        this.mX2 = x2;
        this.mY2 = y2;
    }

    private double getX(double t) {
        double t1 = 1 - t;
        // no need for because start at 0,0 double f0 = (1 - t) * (1 - t) * (1 - t);
        double f1 = 3 * t1 * t1 * t;
        double f2 = 3 * t1 * t * t;
        double f3 = t * t * t;
        return mX1 * f1 + mX2 * f2 + f3;
    }

    private double getY(double t) {
        double t1 = 1 - t;
        // no need for because start at 0,0 double f0 = (1 - t) * (1 - t) * (1 - t);
        double f1 = 3 * t1 * t1 * t;
        double f2 = 3 * t1 * t * t;
        double f3 = t * t * t;
        return mY1 * f1 + mY2 * f2 + f3;
    }


    private double getDiffX(double t) {
        double t1 = 1 - t;
        return 3 * t1 * t1 * mX1 + 6 * t1 * t * (mX2 - mX1) + 3 * t * t * (1 - mX2);
    }


    private double getDiffY(double t) {
        double t1 = 1 - t;
        return 3 * t1 * t1 * mY1 + 6 * t1 * t * (mY2 - mY1) + 3 * t * t * (1 - mY2);
    }

    /**
     * binary search for the region
     * and linear interpolate the answer
     */

    public double getDiff(double x) {
        double t = 0.5;
        double range = 0.5;
        while (range > sDError) {
            double tx = getX(t);
            range *= 0.5;
            if (tx < x) {
                t += range;
            } else {
                t -= range;
            }
        }

        double x1 = getX(t - range);
        double x2 = getX(t + range);
        double y1 = getY(t - range);
        double y2 = getY(t + range);
        return (y2 - y1) / (x2 - x1);
    }

    /**
     * binary search for the region
     * and linear interpolate the answer
     */

    public double get(double x) {
        if (x <= 0.0) {
            return 0;
        }
        if (x >= 1.0) {
            return 1.0;
        }
        double t = 0.5;
        double range = 0.5;
        while (range > sError) {
            double tx = getX(t);
            range *= 0.5;
            if (tx < x) {
                t += range;
            } else {
                t -= range;
            }
        }

        double x1 = getX(t - range);
        double x2 = getX(t + range);
        double y1 = getY(t - range);
        double y2 = getY(t + range);

        return (y2 - y1) * (x - x1) / (x2 - x1) + y1;
    }
}
