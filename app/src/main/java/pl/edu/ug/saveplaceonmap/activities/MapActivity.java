package pl.edu.ug.saveplaceonmap.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pl.edu.ug.saveplaceonmap.BuildConfig;
import pl.edu.ug.saveplaceonmap.DataManager;
import pl.edu.ug.saveplaceonmap.LocationList;
import pl.edu.ug.saveplaceonmap.R;
import pl.edu.ug.saveplaceonmap.models.Category;
import pl.edu.ug.saveplaceonmap.models.Location;

public class MapActivity extends AppCompatActivity {

    MapView map;
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    LocationList locationList;
    DataManager dataManager;

    Location locationToAdd = null;

    boolean isLoaded = false;
    boolean tappedLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Konieczne jest zastosowanie tego mechanizmu dla niektórych z permissions, nie wystarczy tylko AndroidManifest.
        if (!hasPermissions(PERMISSIONS)) {
            Toast.makeText(this, "Aby skorzystać z map musisz wyrazic zgodę", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        // W przypadku gdy przechodzimy z widoku dodawnia odebranie obiektu ktory mamy dodać.
        Bundle b = getIntent().getExtras();
        if (b != null) {
            String locationJsonString = b.getString("location");
            if (locationJsonString != null) {
                GsonBuilder builder = new GsonBuilder();
                locationToAdd = builder.create().fromJson(locationJsonString, Location.class);
            }
        }

        ArrayList<Location> loadedLocations = new ArrayList<Location>();
        if (!isLoaded) {
            loadedLocations = loadingData();
        }

        // Implementacja map
        map = (MapView) findViewById(R.id.map);
        map.getTileProvider().clearTileCache();
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        // Tutaj będzie wczytywanie lokalizacji telefonu, ale na razie jest na sztywno GD
        GeoPoint startPoint = new GeoPoint(54.35, 18.64);
        mapController.setZoom(15.0);
        mapController.setCenter(startPoint);

        // skalowanie mapy
        final DisplayMetrics dm = this.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBarOverlay);

        // punkty na mapie
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        for (int i=0; i<loadedLocations.size(); i++) {
            Location location = (Location) loadedLocations.get(i);
            items.add(new OverlayItem(location.getTitle(), location.getDescription(), new GeoPoint(location.getX(),location.getY()))); // Lat/Lon decimal degrees

        }

        //the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        tappedLocation = true;
                        return false;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, this);
        mOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(mOverlay);

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            // Nacisniecie na mape
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return true;
            }

            // Przytrzymanie = wyswietlenia okna z dodaniem
            @Override
            public boolean longPressHelper(GeoPoint p) {
                // Otworzenie Activity z dodawaniem + przekazanie x,y.
                Intent i = null;
                i = new Intent(MapActivity.this, MapAddLocationActivity.class);
                i.putExtra("x", (float)p.getLatitude());
                i.putExtra("y", (float)p.getLongitude());

                startActivity(i);
                return false;
            }
        };
        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayEvents);


        map.invalidate();
    }

    private boolean hasPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private ArrayList<Location> loadingData() {

        locationList = new LocationList("Location list");
        LocationList locationList = new LocationList("Location list");

        // Zarządzanie danymi
        dataManager = new DataManager(this, locationList);
        // Utworzenie folderu jesli nie istnieje
        dataManager.createDir();
        // uzupelnienie pliku
        dataManager.createFile();

        // AKTULIZACJA PRZEZ DODANIEM
        locationList.locations = dataManager.getList();

        // Jesli przekazalismy z aktywnosci MapAdd, to zostanie dodany do listy.
        if (locationToAdd != null) {
            locationList.locations.add(locationToAdd);
        }

        dataManager.updateDate(locationList);

        Log.i("Locations", "Lista pobranych danych: "+locationList.locations.size());

        isLoaded = true;
        return locationList.locations;

    }
}
