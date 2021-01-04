package pl.edu.ug.saveplaceonmap;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import pl.edu.ug.saveplaceonmap.models.Location;

public class DataManager {

    Context context;
    // < 10
    private final String pathDir = (Environment.getExternalStorageDirectory().getPath() + File.separator + "/data/");
    private final String pathFile = (Environment.getExternalStorageDirectory().getPath() + File.separator + "/data/locations2.json");
    private final String filename = "locs.json";
    private final String filepath = "dane";


    File file;
    File folder;

    LocationList locationList;

    public DataManager(Context context, LocationList locationList) {
        this.context = context;
        this.locationList = locationList;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            file = new File(pathFile);
            folder = new File(pathDir);
        }
//        if (!isExternalStorageEnabledForRW()) {
//
//        }
    }

    public boolean isExternalStorageEnabledForRW() {
//        String extStorageState = Environment.getExternalStorageState();
//        if (extStorageState.equals(Environment.MEDIA_MOUNTED)) {
//            return true;
//        }
        return false;
    }

    // < 10
    public void createFile() {
        Log.i("MOJE", "Tworzenie pliku");
        try {
            if (!file.exists()) {
                Log.i("MOJE", "Tworzenie plik");

                file.createNewFile();
            }
        }catch (Exception e){
            Log.i("MOJE", "Blad przy tworzeniu pliku"+e.getMessage());

            e.printStackTrace();
        }
    }
    // < 10
    public void createDir() {
        Log.i("MOJE", "Tworzenie folderu");

        if (!folder.exists()) {
            try {
                Log.i("MOJE", "Utworzono folder");

                folder.mkdir();
            }catch (Exception ex) {
                Log.i("MOJE", "Blad przy tworzeniu folderu"+ex.getMessage());

                ex.printStackTrace();
            }
        }
    }

    // NADPISYWANIE PLIKU
    public void updateDate(LocationList locationList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File myExternalFile = new File(context.getExternalFilesDir(filepath), filename) ;
            FileOutputStream fos = null;

            String data = parseToJSON(locationList);

            try {
                fos = new FileOutputStream(myExternalFile);
                if (data != null) {
                    fos.write(data.getBytes());
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.i("MOJE", "Dodano");

        // < 10
        }else {
            try {
            FileOutputStream fileOutputStream = new FileOutputStream(pathFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            String data = parseToJSON(locationList);

            if (data != null) {
                outputStreamWriter.write(data);
            }

            outputStreamWriter.close();
            fileOutputStream.close();

            }catch (Exception ex) {

            }
        }

    }

    // Pobieranie z pliku
    public ArrayList<Location> getList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FileReader fr = null;
            File myExternalFile = new File(context.getExternalFilesDir(filepath), filename);
            StringBuilder stringBuilder = new StringBuilder();
            try {
                fr = new FileReader(myExternalFile);
                BufferedReader bf = new BufferedReader(fr);
                String line = bf.readLine();
                while(line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bf.readLine();
                }
                String data = stringBuilder.toString();
                ArrayList<Location> locs = parseToList(data);
                //Gdy ArrayList jest pusta (metoda zwraca null zwracam pustą ArrayListe
                if (locs == null) {
                    locs = new ArrayList<>();
                }
                return locs;
            }catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        // < 10
        }else {
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);
                StringBuilder stringBuilder = new StringBuilder();


                String line = bufferedReader.readLine();
                while (line != null) {
                    Log.i("X", line);
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }

                String data = stringBuilder.toString();

                ArrayList<Location> locs = parseToList(data);
                // Gdy ArrayList jest pusta (metoda zwraca null zwracam pustą ArrayListe
                if (locs == null) {
                    locs = new ArrayList<>();
                }
                bufferedReader.close();
                fileReader.close();
                return locs;
            }catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

    }

    private String parseToJSON(LocationList locationList) {
        Gson gson = new Gson();
        String json = gson.toJson(locationList);
        return json;
    }

    private ArrayList<Location> parseToList(String json) {
        if (json != null)
        {
            Gson gson = new Gson();
            LocationList target = gson.fromJson(json, LocationList.class);
            return target.locations;
        }
        return null;
    }
}
