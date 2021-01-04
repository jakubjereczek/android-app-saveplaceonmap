package pl.edu.ug.saveplaceonmap.models;

import pl.edu.ug.saveplaceonmap.utils.Consts;

public enum Category {
    FUN(""+ Consts.FUN_DESCRIBE),
    MY_FAVOURITE_PLACES(""+Consts.MY_FAVOURITE_PLACES_DESCRIBE),
    HOME(""+Consts.HOME_DESCRIBE),
    GROCERIES(""+ Consts.GROCERIES_DESCRIBE),
    RESTAURANTS(""+Consts.RESTAURANTS_DESCRIBE),
    PLACES(""+Consts.PLACES_DESCRIBE);

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Category findByDescription(String description) {
        switch (description) {
            case Consts.FUN_DESCRIBE:
                return FUN;
            case Consts.MY_FAVOURITE_PLACES_DESCRIBE:
                return MY_FAVOURITE_PLACES;
            case Consts.HOME_DESCRIBE:
                return HOME;
            case Consts.GROCERIES_DESCRIBE:
                return GROCERIES;
            case Consts.RESTAURANTS_DESCRIBE:
                return RESTAURANTS;
            case Consts.PLACES_DESCRIBE:
                return PLACES;
        }
        return null;
    }



}


