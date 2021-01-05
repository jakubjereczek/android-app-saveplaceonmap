package pl.edu.ug.saveplaceonmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import pl.edu.ug.saveplaceonmap.activities.MapActivity;
import pl.edu.ug.saveplaceonmap.activities.MapAddLocationActivity;
import pl.edu.ug.saveplaceonmap.models.Category;
import pl.edu.ug.saveplaceonmap.models.Location;

public class ListLocationAdapter extends ArrayAdapter<Location> {

    Context context;
    private ArrayList<Location> locs = new ArrayList<>();

    public ListLocationAdapter(@NonNull Context context, ArrayList<Location> objects) {
        super(context, 0, objects);
        this.locs = objects;
        this.context = context;
    }

    @Override
    public Location getItem(int position) {
        return locs.get(position);
    }

    @Override
    public int getCount() {
        return locs.size();
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.location_box,parent,false);

        final Location loc = getItem(position);

        ImageView image = listItem.findViewById(R.id.info_image);
        Drawable icon = (MyMarker.setIconByCategoryId(getItem(position).getCategory().getId(), context));
        image.setImageDrawable(icon);
        TextView title = listItem.findViewById(R.id.info_title);
        title.setText(loc.getTitle());
        TextView category = listItem.findViewById(R.id.info_category);
        category.setText(Category.getName(context, loc.getCategory()));
        TextView description = listItem.findViewById(R.id.info_description);
        description.setText(loc.getDescription());
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MapActivity.class);
                i.putExtra("openOnLocation", true);
                i.putExtra("x", loc.getX());
                i.putExtra("y", loc.getY());
                context.startActivity(i);
            }
        });
        return listItem;
    }
}
