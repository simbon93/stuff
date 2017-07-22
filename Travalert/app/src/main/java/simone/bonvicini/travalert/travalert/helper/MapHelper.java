package simone.bonvicini.travalert.travalert.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import simone.bonvicini.travalert.travalert.R;

/**
 * Created by simone on 23/06/2017.
 */

public class MapHelper {

    public static int getZoomLevel(Circle circle) {

        int zoomLevel = 11;
        if (circle != null) {
            double radius = circle.getRadius() + circle.getRadius() / 2;
            double scale = radius / 500;
            zoomLevel = (int) (15.5 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    public static CircleOptions getCircleOptions(int radius, LatLng location, Context context) {

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(location);
        circleOptions.radius(radius);
        circleOptions.strokeColor(ContextCompat.getColor(context,R.color.area_color));
        circleOptions.fillColor(ContextCompat.getColor(context, R.color.area_color_transparent));
        circleOptions.strokeWidth(2);
        return circleOptions;
    }

    public static BitmapDescriptor getMapIcon(int id,Context context) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context,id);
        int h = ((int) ConverterHelper.convertDpToPixel(context,42));
        int w = ((int) ConverterHelper.convertDpToPixel(context, 42));
        vectorDrawable.setBounds(0, 0, w, h);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }
}
