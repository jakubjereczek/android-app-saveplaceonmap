package pl.edu.ug.saveplaceonmap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.Distance;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

import pl.edu.ug.saveplaceonmap.BuildConfig;
import pl.edu.ug.saveplaceonmap.DataManager;
import pl.edu.ug.saveplaceonmap.LocationList;
import pl.edu.ug.saveplaceonmap.MyMarker;
import pl.edu.ug.saveplaceonmap.R;
import pl.edu.ug.saveplaceonmap.models.Category;
import pl.edu.ug.saveplaceonmap.models.Location;

public class MapActivity extends AppCompatActivity {

    MapView map;

    LocationList locationList;
    DataManager dataManager;

    Location locationToAdd = null;
    GeoPoint move = null;

    boolean isLoaded = false;
    boolean tappedLocation = false;
    Context context;

    int id = 0;

    ArrayList<Location> loadedLocations;

    float openOnLocationX, openOnLocationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // W przypadku gdy przechodzimy z widoku dodawnia odebranie obiektu ktory mamy dodać.
        final Bundle b = getIntent().getExtras();
        if (b != null) {
            String locationJsonString = b.getString("location");
            // Przejscie z widoku dodawnia
            if (locationJsonString != null) {
                GsonBuilder builder = new GsonBuilder();
                locationToAdd = builder.create().fromJson(locationJsonString, Location.class);
                move = new GeoPoint(locationToAdd.getX(), locationToAdd.getY());
            }else {
                // Przejscie z widoku listy
                boolean openOnLocation = b.getBoolean("openOnLocation");
                if (openOnLocation) {
                    Log.i("DANE", "Ustawiam x i y"+openOnLocation);

                   move = new GeoPoint(b.getDouble("x"), b.getDouble("y"));

                }
            }

        }

        setContentView(R.layout.activity_map);
        context = this;

        loadedLocations = new ArrayList<Location>();
        if (!isLoaded) {
            loadedLocations = loadingData();
        }

        // Implementacja map
        map = findViewById(R.id.map);
        map.getTileProvider().clearTileCache();
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        final IMapController mapController = map.getController();
        mapController.setZoom(16.0);
        map.setMinZoomLevel(5.0);
        map.setMaxZoomLevel(20.0);

        // skalowanie mapy
        final DisplayMetrics dm = this.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBarOverlay);

        // lokalizacja
        final MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        //myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.enableMyLocation();
        map.getOverlays().add(myLocationNewOverlay);

        // Przejscie do lokalizacji telefonu albo dodanego punktyu
        myLocationNewOverlay.runOnFirstFix(new Runnable () {
            @Override
            public void run () {
                final GeoPoint myLocation = myLocationNewOverlay.getMyLocation();
                if (myLocation != null) {
                    runOnUiThread(new Runnable () {
                        @Override
                        public  void  run () {
                            if (b != null) {
                                // Przejscie do punkty z listy
                                map.getController().animateTo(move);
                                Log.i("DANE", "x"+move.getLatitude()+"Y"+move.getLongitude());
//                                if (openOnLocationX != 0 && openOnLocationY != 0)
//                                    map.getController().animateTo(new GeoPoint(openOnLocationX, openOnLocationY));
//                                //}//else {
//                                    // Wyswietlanie ostatnio dodanego punktu
//                                     if (locationToAdd != null)
//                                        map.getController().animateTo(new GeoPoint(locationToAdd.getX(), locationToAdd.getY()));
                                //}
                            }else{
                                // Przejscie do lokalizacji telefonu
                                map.getController().animateTo(myLocation);
                            }
                        }
                    });
                }
            }
        });

        // punkty na mapie
        ArrayList<Marker> items = new ArrayList<Marker>();
        if (loadedLocations != null) {
            for (int i = 0; i < loadedLocations.size(); i++) {
                final Location location = loadedLocations.get(i);
                MyMarker m = new MyMarker(map, location.getCategory(), context);
                m.setId(Integer.toString(location.getId()));
                m.setPosition(new GeoPoint(location.getX(), location.getY()));
                m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
                m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(final Marker marker, MapView mapView) {
                        final int id = Integer.parseInt(marker.getId());
                        Location myLoc = locationList.findById(id);
                        map.getController().animateTo(new GeoPoint(myLoc.getX(), myLoc.getY()));

                        final Dialog dialog = new Dialog(context);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                        dialog.setContentView(R.layout.information_popup);
                        TextView header = dialog.findViewById(R.id.headerTV);
                        header.setText(myLoc.getTitle());
                        TextView category = dialog.findViewById(R.id.categoryTV);
                        category.setText(Category.getName(context, myLoc.getCategory()));
                        TextView body = dialog.findViewById(R.id.bodyTV);
                        body.setText(myLoc.getDescription());
                        dialog.show();

                        ImageView close = dialog.findViewById(R.id.closeBtn);
                        close.setOnClickListener((new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        }));

                        ImageView del = dialog.findViewById(R.id.deleteBtn);
                        del.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                locationList.locations.remove(locationList.findById(id));
                                for(int i=0;i<map.getOverlays().size();i++){
                                    Overlay overlay= map.getOverlays().get(i);
                                    if(overlay instanceof Marker&&((Marker) overlay).getId().equals(marker.getId())){
                                        map.getOverlays().remove(overlay);
                                    }
                                }
                                dataManager.updateDate(locationList);
                                dialog.cancel();
                            }
                        });
                        return true;
                    }
                });
                m.setIcon();

                map.getOverlays().add(m);
            }
        }

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

                // W przypadku gdy przechodzimy z widoku dodawnia odebranie obiektu ktory mamy dodać.
                final Bundle b = getIntent().getExtras();
                if (b != null) {
                    String locationJsonString = b.getString("location");
                    GsonBuilder builder = new GsonBuilder();
                    locationToAdd = builder.create().fromJson(locationJsonString, Location.class);
                }

                Intent i = null;
                i = new Intent(MapActivity.this, MapAddLocationActivity.class);
                i.putExtra("id", id);
                i.putExtra("x", (float)p.getLatitude());
                i.putExtra("y", (float)p.getLongitude());

                startActivity(i);
                return false;
            }
        };
        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayEvents);

        // kompas
        CompassOverlay compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);



        map.invalidate();
    }

    private ArrayList<Location> loadingData() {
        locationList = new LocationList("Location list");
        // Zarządzanie danymi
        dataManager = new DataManager(this, locationList);

        // Dla starszych wersji niz 10.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Utworzenie folderu jesli nie istnieje
            dataManager.createDir();
            // uzupelnienie pliku
            dataManager.createFile();
        }
        // AKTULIZACJA DANYCH PRZEZ DODANIEM
        locationList.locations = dataManager.getList();

        // Jesli przekazalismy z aktywnosci MapAdd..., to zostanie dodany do listy.
        if (locationToAdd != null) {
            locationList.locations.add(locationToAdd);
        }

        dataManager.updateDate(locationList);
        // Musimy tu pobiera id ostatniego elementu

         //Ustawianie ID na poczatku
        if (locationList.locations == null || locationList.locations.size() == 0) {
            id = 0;
        }else {
            id = locationList.locations.get(locationList.locations.size() - 1).getId() +1;
        }
        isLoaded = true;

        return locationList.locations;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // Pozbycie się blędu z niepoprawnym odsieżaniem przy cofaniu do aktywności.
    @Override
    public void onBackPressed() {
        Intent i = null;
        i = new Intent(MapActivity.this, MainActivity.class);
        startActivity(i);
    }

}

