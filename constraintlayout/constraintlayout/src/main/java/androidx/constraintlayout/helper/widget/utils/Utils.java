package androidx.constraintlayout.helper.widget.utils;

public class Utils {
    public static int[] removeElementFromArray(int[] array, int index) {
        int[] newArray = new int[array.length - 1];

        for (int i = 0, k = 0; i < array.length; i++) {
            if (i == index) {
                continue;
            }
            newArray[k++] = array[i];
        }
        return newArray;
    }

    public static float[] removeElementFromArray(float[] array, int index) {
        float[] newArray = new float[array.length - 1];

        for (int i = 0, k = 0; i < array.length; i++) {
            if (i == index) {
                continue;
            }
            newArray[k++] = array[i];
        }
        return newArray;
    }
}
