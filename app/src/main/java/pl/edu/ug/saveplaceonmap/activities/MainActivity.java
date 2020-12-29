package pl.edu.ug.saveplaceonmap.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pl.edu.ug.saveplaceonmap.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void menuHandler(View view) {
        // Otwieranie nowej aktywności
        Intent intent = null;
        switch (view.getId()) {
            case R.id.mapBtn:
                intent = new Intent(MainActivity.this, MapActivity.class);
                break;
            case R.id.locationsBtn:
                intent = new Intent(MainActivity.this, LocationsActivity.class);
                break;
        }
        startActivity(intent);

    }

}
