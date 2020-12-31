package pl.edu.ug.saveplaceonmap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.GsonBuilder;

import pl.edu.ug.saveplaceonmap.R;
import pl.edu.ug.saveplaceonmap.models.Category;
import pl.edu.ug.saveplaceonmap.models.Location;

public class MapAddLocationActivity extends AppCompatActivity {

    EditText title;
    EditText description;
    Spinner category;
    Button button;

    float x, y;

    String selectedCategory = null;

    private String[] categories = new String[] { Category.FUN.getDescription(), Category.MY_FAVOURITE_PLACES.getDescription(), Category.HOME.getDescription() };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_add_location);
        Bundle b = getIntent().getExtras();
        x = b.getFloat("x");
        y = b.getFloat("y");
        title = findViewById(R.id.titleET);
        description = findViewById(R.id.descriptionET);
        category = findViewById(R.id.categorySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categories);
        category.setAdapter(adapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do uzupelnienia
                selectedCategory = categories[0];
            }

        });

        button = findViewById(R.id.button);
    }


    public void addHandler(View view) {
        String titleString = title.getText().toString(),
                descriptionString = description.getText().toString();
        Log.i("dane", titleString + "" +descriptionString);
        Log.i("dane", selectedCategory.toString());

        if (!titleString.isEmpty() && !descriptionString.isEmpty() && !selectedCategory.isEmpty()) {
            Log.i("dane", "yest");
            Category category = Category.findByDescription(selectedCategory);
            Location location = new Location(x,y,titleString,descriptionString, category);

            // przekonwertowanie obiektu na string json
            GsonBuilder builder = new GsonBuilder();
            String locationJsonString = builder.create().toJson(location);

            Intent i = null;
            i = new Intent(MapAddLocationActivity.this, MapActivity.class);
            i.putExtra("location", locationJsonString);
            startActivity(i);

        }else {
            Log.i("dane", "not");

            // komunikat o niepoprawnych danych
        }

    }

}
