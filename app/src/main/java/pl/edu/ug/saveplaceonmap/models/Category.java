package pl.edu.ug.saveplaceonmap.models;

import android.content.Context;
import android.content.res.Resources;

import pl.edu.ug.saveplaceonmap.R;
import pl.edu.ug.saveplaceonmap.utils.Consts;

public enum Category {
    FUN(R.string.FUN, Consts.FUN_ID),
    MY_FAVOURITE_PLACES(R.string.MY_FAVOURITE_PLACES, Consts.MY_FAVOURITE_PLACES_ID),
    HOME(R.string.HOME, Consts.HOME_ID),
    GROCERIES(R.string.GROCERIES, Consts.GROCERIES_ID),
    RESTAURANTS(R.string.RESTAURANTS, Consts.RESTAURANTS_ID),
    PLACES(R.string.PLACES, Consts.PLACES_ID);

    private int idResources;
    private int id;

    Category(int idResources, int id) {
        this.idResources = idResources;
        this.id = id;
    }

    public int getResourceId() {
        return idResources;
    }

    public static Category getById(int id) {
        for(Category category : values()) {
            if(category.id == id) {
                return category;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public static String getName(Context context, Category category) {
        Resources res = context.getResources();
        String categoryName = res.getString(category.getResourceId());
        return categoryName;
    }

}

