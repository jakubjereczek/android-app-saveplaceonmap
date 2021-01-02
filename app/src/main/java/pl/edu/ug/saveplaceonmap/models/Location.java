package pl.edu.ug.saveplaceonmap.models;

public class Location {

    private int id;
    private float x;
    private float y;
    private String title;
    private String description;
    private Category category;

    public Location(int id, float x, float y, String title, String description, Category category) {
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

    public float getX() {
        return x;
    }

    public float getY() {
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
