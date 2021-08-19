package androidx.constraintLayout.desktop.scan;

import java.awt.*;

public class LinkColors {
    Color backgroundColor;
    Color rootBackgroundColor;
    Color startColor;
    Color endColor;
    Color interpolatedColor;
    Color interpolatedSelectedColor;
    Color interpolatedHoverColor;
    Color textColor;
    Color pathColor;
    Color preTransformColor;

    private static LinkColors normal;
    private static LinkColors dark;

    public Color backgroundColor() {
        return backgroundColor;
    }

    public Color rootBackgroundColor() {
        return rootBackgroundColor;
    }

    public Color startColor() {
        return startColor;
    }

    public Color endColor() {
        return endColor;
    }

    public Color interpolatedColor() {
        return interpolatedColor;
    }

    public Color interpolatedSelectedColor() {
        return interpolatedSelectedColor;
    }

    public Color interpolatedHoverColor() {
        return interpolatedHoverColor;
    }

    public Color textColor() {
        return textColor;
    }

    public Color pathColor() {
        return pathColor;
    }

    private LinkColors() {
    }

    public static LinkColors getTheme(boolean darkMode) {
        return darkMode ? getDark() : getNormal();
    }

    private static LinkColors getDark() {
        if (dark != null) {
            return dark;
        }
        return dark = new LinkColors() {
            {
                backgroundColor = new Color(0, 0, 60);
                rootBackgroundColor = new Color(0, 0, 80);
                startColor = new Color(0, 140, 240, 100);
                endColor = new Color(0, 200, 240, 100);
                preTransformColor = new Color(9, 53, 82, 129);
                interpolatedColor = new Color(140, 140, 240, 100);
                interpolatedSelectedColor = new Color(240, 140, 240, 100);
                interpolatedHoverColor = new Color(100, 100, 180, 100);
                textColor = new Color(200, 200, 200);
                pathColor = new Color(200, 200, 200);
            }
        };
    }

    private static LinkColors getNormal() {
        if (normal != null) {
            return normal;
        }
        return normal = new LinkColors() {
            {
                backgroundColor = new Color(200, 200, 200);
                rootBackgroundColor = new Color(255, 255, 255);
                startColor = new Color(0, 50, 100, 100);

                endColor = new Color(34, 125, 239, 100);
                preTransformColor = new Color(224, 235, 248, 129);
                interpolatedColor = new Color(0, 90, 200, 40);
                interpolatedSelectedColor = new Color(255, 128, 128, 100);
                interpolatedHoverColor = new Color(10, 190, 200, 40);
                textColor = new Color(0, 90, 200, 200);
                pathColor = new Color(0, 90, 200, 240);
            }
        };
    }
}
