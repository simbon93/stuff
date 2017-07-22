package simone.bonvicini.travalert.travalert.helper;

import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by simone on 27/06/2017.
 */

public class MetricsHelper {

    public static void setMetricsListener(final View v, final OnMetricsListener listener) {

        final ViewTreeObserver observer = v.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (listener != null) {
                    listener.onMeasured(v, v.getHeight(), v.getWidth());
                }

                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    //interface

    public interface OnMetricsListener {

        void onMeasured(View v, int height, int width);
    }

}
