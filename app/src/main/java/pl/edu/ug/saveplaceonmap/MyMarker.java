package pl.edu.ug.saveplaceonmap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.widget.Toast;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import pl.edu.ug.saveplaceonmap.models.Category;
import pl.edu.ug.saveplaceonmap.utils.Consts;

public class MyMarker extends Marker {

    Category category;
    Context context;

    public MyMarker(MapView mapView, Category category, Context context) {
        super(mapView);
        this.category = category;
        this.context = context;
    }

    public void setIcon(){
        if (category!=null) {
            mIcon=setIconByCategory(category.getDescription());
        }
    }

    private Drawable setIconByCategory(String category) {
        Drawable drawable;
        switch (category) {
            case Consts.FUN_DESCRIBE:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    drawable = context.getDrawable(R.drawable.burger);
//                } else {
//                    drawable = context.getResources().getDrawable(R.drawable.burger);
//                }
                drawable = context.getDrawable(R.drawable.star);
                break;
            case Consts.MY_FAVOURITE_PLACES_DESCRIBE:
                drawable = context.getDrawable(R.drawable.heart);
                break;
            case Consts.HOME_DESCRIBE:
                drawable = context.getDrawable(R.drawable.home);
                break;
            case Consts.GROCERIES_DESCRIBE:
                drawable = context.getDrawable(R.drawable.bag);
                break;
            case Consts.RESTAURANTS_DESCRIBE:
                drawable = context.getDrawable(R.drawable.burger);
                break;
            case Consts.PLACES_DESCRIBE:
                drawable = context.getDrawable(R.drawable.fun);
                break;
            default:
                drawable = context.getDrawable(R.drawable.star);
                break;

        }
        return drawable;
    }
}
