package pl.edu.ug.saveplaceonmap.models;

public class Location {

    private final int id;
    private final double x;
    private final double y;
    private final String title;
    private final String description;
    private final Category category;

    public Location(int id, double x, double y, String title, String description, Category category) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.title = title;
        this.description = description;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

}
