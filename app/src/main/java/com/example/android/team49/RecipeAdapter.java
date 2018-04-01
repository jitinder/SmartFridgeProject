package com.example.android.team49;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sidak Pasricha on 31-Mar-18.
 */

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    private Context context;
    private ArrayList<Recipe> recipes;
    private HashMap<Integer, Bitmap> images = new HashMap<>();

    private ImageView recipeImage;
    private TextView recipeName;
    private TextView recipeSourceName;
    private Button recipeSource;

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

        recipeImage = (ImageView) v.findViewById(R.id.recipe_image_view);
        recipeName = (TextView) v.findViewById(R.id.recipe_name_view);
        recipeSourceName = (TextView) v.findViewById(R.id.recipe_source_view);
        recipeSource = (Button) v.findViewById(R.id.recipe_website_button);

        final Recipe r = getItem(position);
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
        }

        return v;
    }

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
