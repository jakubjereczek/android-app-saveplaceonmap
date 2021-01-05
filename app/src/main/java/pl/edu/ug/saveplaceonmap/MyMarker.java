package pl.edu.ug.saveplaceonmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.widget.Toast;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import pl.edu.ug.saveplaceonmap.models.Category;
import pl.edu.ug.saveplaceonmap.utils.Consts;

public class  MyMarker extends Marker {

    Category category;
    Context context;

    public MyMarker(MapView mapView, Category category, Context context) {
        super(mapView);
        this.category = category;
        this.context = context;
    }

    public void setIcon(){
        if (category!=null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) setIconByCategoryId(category.getId(), context);
            Bitmap bitmap = bitmapDrawable.getBitmap();
            Drawable dr = new BitmapDrawable(context.getResources(),
                    Bitmap.createScaledBitmap(bitmap, (int) (48.0f * context.getResources().getDisplayMetrics().density), (int) (48.0f * context.getResources().getDisplayMetrics().density), true));
            mIcon= dr;
            //mIcon = setIconByCategory(category.getDescription());
        }
    }

    static Drawable setIconByCategoryId(int categoryId, Context context) {
        Drawable drawable;
        switch (categoryId) {
            case Consts.FUN_ID:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    drawable = context.getDrawable(R.drawable.burger);
//                } else {
//                    drawable = context.getResources().getDrawable(R.drawable.burger);
//                }
                drawable = context.getDrawable(R.drawable.star);
                break;
            case Consts.MY_FAVOURITE_PLACES_ID:
                drawable = context.getDrawable(R.drawable.heart);
                break;
            case Consts.HOME_ID:
                drawable = context.getDrawable(R.drawable.home);
                break;
            case Consts.GROCERIES_ID:
                drawable = context.getDrawable(R.drawable.bag);
                break;
            case Consts.RESTAURANTS_ID:
                drawable = context.getDrawable(R.drawable.burger);
                break;
            case Consts.PLACES_ID:
                drawable = context.getDrawable(R.drawable.fun);
                break;
            default:
                drawable = context.getDrawable(R.drawable.star);
                break;

        }
        return drawable;
    }
}
