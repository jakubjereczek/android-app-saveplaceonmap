package pl.edu.ug.saveplaceonmap.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import pl.edu.ug.saveplaceonmap.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grandPermissions();
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

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        grandPermissions();
    }

    public void grandPermissions() {
        // Konieczne jest zastosowanie tego mechanizmu dla niektórych z permissions, nie wystarczy tylko AndroidManifest.
        if (!hasPermissions(PERMISSIONS)) {
            Log.i("AAA", "Nadajemy permissions"+ PERMISSIONS.toString());
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }
    }

    private boolean hasPermissions(String... permissions) {
        Log.i("AAA", "Ustawianie permissions");
        if (permissions != null) {
            for (String permission : permissions) {
                Log.i("PERM", "Ustawianie permissions "+permission);

                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
