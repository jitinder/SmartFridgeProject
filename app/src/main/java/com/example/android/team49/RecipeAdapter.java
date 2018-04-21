package com.example.android.team49;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.iid.InstanceID;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Custom Adapter to Load Recipes in the {@link RecipesFragment} ListView
 *
 * @author          Sidak Pasricha
 */

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private Context context;
    private ArrayList<Recipe> recipes;
    private HashMap<Integer, Bitmap> images = new HashMap<>();

    private ImageView recipeImage;
    private TextView recipeName;
    private TextView recipeSourceName;
    private Button recipeSource;
    private Button recipeIngredients;

    public RecipeAdapter(Context context, ArrayList<Recipe> recipes){
        super(context, R.layout.recipe_list_view, recipes);
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public View getView(int position, final View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.recipe_list_view, null);
        }


        final Recipe r = getItem(position);

        recipeImage = (ImageView) v.findViewById(R.id.recipe_image_view);
        recipeName = (TextView) v.findViewById(R.id.recipe_name_view);
        recipeSourceName = (TextView) v.findViewById(R.id.recipe_source_view);
        recipeSource = (Button) v.findViewById(R.id.recipe_website_button);
        recipeIngredients = (Button) v.findViewById(R.id.recipe_ingredients_button);

        if (r != null) {
            new ImageLoadTask(r.getImageURL(), recipeImage, position).execute(); //Set Recipe Image
            recipeName.setText(r.getName());
            recipeSourceName.setText(r.getSource());
            recipeSource.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = r.getSourceURL();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            });
            recipeIngredients.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Ingredients for: \n" +r.getName());
                    final ArrayList<IngredientPresence> ingredients = new ArrayList<>();
                    for(int i = 0; i < r.getIngredients().size(); i++){
                        ingredients.add(new IngredientPresence(r.getIngredients().get(i), checkIngredientPresence(r.getIngredients().get(i))));
                        System.out.println(checkIngredientPresence(r.getIngredients().get(i)));
                    }
                    builder.setAdapter(new IngredientAdapter(context, ingredients), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = ingredients.get(which).getName();
                            String url = "https://www.amazon.co.uk/s?url=search-alias%3Daps&field-keywords=" + name.replace(" ", "+");
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            context.startActivity(i);
                        }
                    });
                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        return v;
    }

    private boolean checkIngredientPresence(String ingName){
        String[] split = ingName.split(" ");
        for(int i = 0; i < split.length; i++){
            split[i].replace(" ", "");
            split[i] = split[i].toLowerCase();
        }
        for(int i = 0; i < split.length; i++){
            for(int j = 0; j < ViewFragment.results.size(); j++){
                System.out.print(split[i]);
                System.out.println(ViewFragment.results.get(j).getName().toLowerCase());
                if(split[i].equals(ViewFragment.results.get(j).getName().toLowerCase())
                        || split[i].contains(ViewFragment.results.get(j).getName().toLowerCase())){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Used to Load image for the Card
     */
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;
        private int position;

        public ImageLoadTask(String url, ImageView imageView, int position) {
            this.url = url;
            this.imageView = imageView;
            this.position = position;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                if(images.get(position) != null){
                    return images.get(position);
                } else {
                    URL urlConnection = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlConnection
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                    images.put(position, myBitmap);
                    return myBitmap;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if(result == null) {
                imageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.background));
            } else {
                imageView.setImageBitmap(result);
            }
        }

    }
}
