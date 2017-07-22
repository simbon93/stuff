package simone.bonvicini.travalert.travalert.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by simone on 22/07/2017.
 */

public class ConverterHelper {

    public static int convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    public static int convertPixelsToDp(Context context, float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / Math.round(metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        if (dp == px) {
            dp = px / metrics.density;
        }
        return (int) dp;
    }

}
