package pl.edu.ug.saveplaceonmap.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;

import java.io.File;
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

    LocationList locationList;
    DataManager dataManager;

    Location locationToAdd = null;

    boolean isLoaded = false;
    boolean tappedLocation = false;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
//        GeoPoint startPoint;
        // W przypadku gdy przechodzimy z widoku dodawnia odebranie obiektu ktory mamy dodać.
        Bundle b = getIntent().getExtras();
        if (b != null) {
            String locationJsonString = b.getString("location");
            //  if (locationJsonString != null) {
            GsonBuilder builder = new GsonBuilder();
            locationToAdd = builder.create().fromJson(locationJsonString, Location.class);
            // Przejscie do lokalizacji swiezo dodanej
           // startPoint = new GeoPoint(locationToAdd.getX(), locationToAdd.getY());
            // }
        }

        setContentView(R.layout.activity_map);
        context = this;

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
        mapController.setZoom(15.0);
       // mapController.setCenter(startPoint);

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
            items.add(new OverlayItem(location.getTitle(), "Opis: "+location.getDescription()+"\n Kategoria: "+location.getCategory().getDescription(), new GeoPoint(location.getX(),location.getY())));
        }

        //the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(context, "Nacisnieto na zakladne "+item.getTitle(), Toast.LENGTH_LONG).show();

                        //do something
                        tappedLocation = true;

                        return false;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Toast.makeText(context, "Przytrzymano na zakladce "+item.getTitle(), Toast.LENGTH_LONG).show();

                        return false;
                    }
                }, this);
        mOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(mOverlay);

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            // Nacisniecie na mape
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
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

        // lokalizacja
        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        //myLocationNewOverlay.setEnableAutoStop(false);
        myLocationNewOverlay.enableFollowLocation(); ///////////////////
        myLocationNewOverlay.enableMyLocation();
        map.getOverlays().add(myLocationNewOverlay);

        // kompas
        CompassOverlay compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        GeoPoint startPoint;
        // Przejscie do dodanego elementu
        if (b != null) {
            // Wyswietlanie ostatnio dodanego punktu
            startPoint = new GeoPoint(locationToAdd.getX(), locationToAdd.getY());
        }else {
            // przejscie do mojej loc
            if (myLocationNewOverlay.getMyLocation() == null) {
                startPoint = null;
            }else {
                // Wyswietlanie mojej lokaliacji
                startPoint = myLocationNewOverlay.getMyLocation();
            }
        }
        //mapController.setCenter(startPoint);

        map.invalidate();
    }

    private ArrayList<Location> loadingData() {
        locationList = new LocationList("Location list");
        LocationList locationList = new LocationList("Location list");

        // Zarządzanie danymi
        dataManager = new DataManager(this, locationList);

        // Dla starszych wersji niz 10.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Utworzenie folderu jesli nie istnieje
            dataManager.createDir();
            // uzupelnienie pliku
            dataManager.createFile();
        }
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

    //TODO
    // W tym cyklu zycia odswiezamy puntky na mapie po wyjsciu do pola dodawania. Zapobiega bledu wycofywaniu.
    // Albo zmiana obslugi cofania w klasie MapAd..
    @Override
    protected void onResume() {
        super.onResume();
     //   Toast.makeText(this, "POWROCONO DO EKRENU", Toast.LENGTH_SHORT).show();
    }


}
