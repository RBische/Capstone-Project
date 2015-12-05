package fr.bischof.raphael.gothiite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import fr.bischof.raphael.gothiite.R;

/**
 * Adapter to show icon choices
 * Created by biche on 30/09/2015.
 */
public class IconAdapter extends ArrayAdapter<String> {

    public IconAdapter(Context context) {
        super(context, 0, context.getResources().getStringArray(R.array.icon_resource_name));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_image, parent, false);
        }
        ((ImageView)convertView.findViewById(R.id.ivIcon)).setImageResource(getContext().getResources().getIdentifier(getItem(position), "drawable", getContext().getPackageName()));
        return convertView;
    }
}
