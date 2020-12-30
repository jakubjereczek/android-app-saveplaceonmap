package pl.edu.ug.saveplaceonmap.models;

public enum Category {
    FUN(""),
    MY_FAVOURITE_PLACES("jedno z moich ukochanych miejsc"),
    HOME("moj dom");

    private String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Category findByDescription(String description) {
        switch (description) {
            case "miejsce rozrywkowe":
                return FUN;
            case "moj dom":
                return HOME;
        }
        return null;
    }
}


