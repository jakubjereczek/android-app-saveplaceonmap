package pl.edu.ug.saveplaceonmap.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pl.edu.ug.saveplaceonmap.DataManager;
import pl.edu.ug.saveplaceonmap.ListLocationAdapter;
import pl.edu.ug.saveplaceonmap.LocationList;
import pl.edu.ug.saveplaceonmap.R;
import pl.edu.ug.saveplaceonmap.models.Location;

public class LocationsActivity extends AppCompatActivity {

    LocationList locationList;
    ListView listView;

    TextView locationTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        listView = findViewById(R.id.locationListView);
        locationTitle = findViewById(R.id.locationTitle);
        locationList = new LocationList("Location list");
        DataManager dataManager = new DataManager(this, locationList);
        locationList.locations = dataManager.getList();
        if (locationList.locations != null) {
            Log.i("TAK", "Ustawiam listView - Visible, Title - Gone");
//            listView.setVisibility(View.VISIBLE);
//            locationTitle.setVisibility(View.GONE);
            loadData();
        }else {
            Log.i("TAK", "Ustawiam listView - Gone, Title - Visible");
//            listView.setVisibility(View.GONE);
//            locationTitle.setVisibility(View.VISIBLE);
        }
    }

    public void loadData() {
        if (locationList.locations.size() > 0) {
            listView.setVisibility(View.VISIBLE);
            locationTitle.setVisibility(View.GONE);
            ListLocationAdapter listLocationAdapter = new ListLocationAdapter(this, locationList.locations);
            listView.setAdapter(listLocationAdapter);
        }else {
            listView.setVisibility(View.GONE);
            locationTitle.setVisibility(View.VISIBLE);
        }
    }

}

