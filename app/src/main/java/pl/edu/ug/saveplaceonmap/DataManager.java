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
    private final String pathFile = (Environment.getExternalStorageDirectory().getPath() + File.separator + "/data/locs.json");
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
    }

    // < 10
    public void createFile() { try {
            if (!file.exists()) {
                file.createNewFile();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // < 10
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
                ex.printStackTrace();
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
