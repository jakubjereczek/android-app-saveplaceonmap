package pl.edu.ug.saveplaceonmap;

import android.util.Log;

import java.util.ArrayList;

import pl.edu.ug.saveplaceonmap.models.Location;

public class LocationList {

    private final String name;
    public ArrayList<Location> locations;

    public LocationList(String name) {
        this.name = name;
        locations = new ArrayList<>();
    }

    public Location findById(int id) {
        for (Location loc : this.locations) {
            if (loc.getId() == id) {
                return loc;
            }
        }
        return null;
    }
}
