package pl.edu.ug.saveplaceonmap;

import java.util.ArrayList;

import pl.edu.ug.saveplaceonmap.models.Location;

public class LocationList {

    private String name;
    public ArrayList<Location> locations;

    public LocationList(String name) {
        this.name = name;
        locations = new ArrayList<>();
    }
}
