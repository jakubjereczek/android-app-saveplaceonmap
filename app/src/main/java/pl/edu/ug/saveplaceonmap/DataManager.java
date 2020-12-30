package pl.edu.ug.saveplaceonmap;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import pl.edu.ug.saveplaceonmap.models.Location;

public class DataManager {

    Context context;
    private String pathDir = (Environment.getExternalStorageDirectory().getPath() + File.separator + "/data/");
    private String pathFile = (Environment.getExternalStorageDirectory().getPath() + File.separator + "/data/locations2.json");
    File file;
    File folder;

    LocationList locationList;

    public DataManager(Context context, LocationList locationList) {
        this.context = context;
        this.locationList = locationList;

        file = new File(pathFile);
        folder = new File(pathDir);
    }

    public void createFile() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createDir() {
        if (!folder.exists()) {
            try {
                folder.mkdir();
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // NADPISYWANIE PLIKU
    public void updateDate(LocationList locationList) {
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

    // Pobieranie z pliku
    public ArrayList<Location> getList() {
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
            return locs;
        }catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
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
