package pl.edu.ug.saveplaceonmap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;

import pl.edu.ug.saveplaceonmap.R;
import pl.edu.ug.saveplaceonmap.models.Category;
import pl.edu.ug.saveplaceonmap.models.Location;

public class MapAddLocationActivity extends AppCompatActivity {

    EditText title;
    EditText description;
    Spinner category;
    Button button;
    TextView errorMessage;

    float x, y;
    int id;

    String selectedCategory = null;
    int selectedCategoryId = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_add_location);
        Context context = this;
        final String[] categories = new String[]{
                Category.getName(context, Category.FUN),
                Category.getName(context, Category.MY_FAVOURITE_PLACES),
                Category.getName(context, Category.HOME),
                Category.getName(context, Category.GROCERIES),
                Category.getName(context, Category.RESTAURANTS),
                Category.getName(context, Category.PLACES)
        };

        Bundle b = getIntent().getExtras();
        ActionBar actionBar = getActionBar();
        x = b.getFloat("x");
        y = b.getFloat("y");
        id = b.getInt("id");
        title = findViewById(R.id.titleET);
        description = findViewById(R.id.descriptionET);
        category = findViewById(R.id.categorySpinner);
        errorMessage = findViewById(R.id.errorMessageTV);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        category.setAdapter(adapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
                selectedCategoryId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });
        button = findViewById(R.id.button);
    }

    public void addHandler(View view) {
        String titleString = title.getText().toString(),
                descriptionString = description.getText().toString();
        errorMessage.setText("");
        if (!titleString.isEmpty() && !descriptionString.isEmpty() && !selectedCategory.isEmpty()) {
            Category category = Category.getById(selectedCategoryId);
            Location location = new Location(id, x,y,titleString,descriptionString, category);

            // przekonwertowanie obiektu na string json
            GsonBuilder builder = new GsonBuilder();
            String locationJsonString = builder.create().toJson(location);

            Intent i = null;
            i = new Intent(MapAddLocationActivity.this, MapActivity.class);
            i.putExtra("location", locationJsonString);
            startActivity(i);

        }else {
            // Wiadomość o niepoprawnym formularzu
            int incorrectFields = 0;
            String[] fields = new String[3];
            if (titleString.isEmpty() || titleString.length() < 3) {
                fields[0] = getApplicationContext().getResources().getString(R.string.location_to_add_title);
                incorrectFields++;
            }
            if (descriptionString.isEmpty() || descriptionString.length() < 3) {
                fields[1] = getApplicationContext().getResources().getString(R.string.location_to_add_des);
                incorrectFields++;
            }
            if (selectedCategory.isEmpty() || selectedCategory.length() < 3) {
                fields[2] = getApplicationContext().getResources().getString(R.string.location_to_add_category);
                incorrectFields++;
            }
            String errorMessageBuilder = "";
            if (incorrectFields > 1) {
                errorMessageBuilder = getApplicationContext().getResources().getString(R.string.location_to_add_field);
            }else{
                errorMessageBuilder =getApplicationContext().getResources().getString(R.string.location_to_add_fields);
            }
            for (int i = 0; i<fields.length; i++) {
                if (fields[i] != "" && fields[i] != null) {
                    errorMessageBuilder += " "+fields[i];
                    if (i<(incorrectFields - 1))
                        errorMessageBuilder += ",";
                }
            }
            if (incorrectFields > 1) {
                errorMessageBuilder += " "+getApplicationContext().getResources().getString(R.string.location_to_error_msg);
            }else{
                errorMessageBuilder += " "+getApplicationContext().getResources().getString(R.string.location_to_error_msg_many);
            }
            errorMessage.setText(errorMessageBuilder);
        }

    }

}
