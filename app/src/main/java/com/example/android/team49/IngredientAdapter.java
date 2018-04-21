package com.example.android.team49;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Custom Adapter to load Ingredient list in the {@link RecipesFragment} when the "Ingredients" button is clicked on a recipe
 *
 * @author Sidak Pasricha
 */

public class IngredientAdapter extends ArrayAdapter<IngredientPresence> {

    public IngredientAdapter(Context context, ArrayList<IngredientPresence> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        final IngredientPresence ip = getItem(position);

        TextView nameText = (TextView) v.findViewById(android.R.id.text1);
        nameText.setText(ip.getName());
        if(!ip.isPresent()) {
            nameText.setTextColor(v.getResources().getColor(R.color.red));
        }

        return v;
    }
}
