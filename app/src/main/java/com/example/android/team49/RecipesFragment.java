package com.example.android.team49;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipesFragment extends Fragment {

    final int MAX_RESULTS = 10;
    final int PAGE_NUMBER = 0;
    final String MAX_RESULTS_QUERY = "&maxResult=" + MAX_RESULTS + "&start=" +PAGE_NUMBER;

    String yummlyURL = "http://api.yummly.com/v1/api/recipes?_app_id=bd3d9a8a&_app_key=aae41863adcb337b442a93c71fb77344";
    TextView textView;


    public RecipesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        textView = (TextView) view.findViewById(R.id.recipe_text);

        textView.setText(getDataFromYummly("Chicken Tikka").toString());

        return view;
    }

    private Recipe getDataFromYummly(String query){
        Recipe test = new Recipe("", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), "", 0, "", "");
        query = query.replace(" ", "+");
        try{
            URL url = new URL(yummlyURL + "&q=" + query + MAX_RESULTS_QUERY);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) conn.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            JsonArray matches = rootobj.getAsJsonArray("matches");
            JsonObject recipe = matches.get(0).getAsJsonObject();
            JsonObject imageURLJson = recipe.get("imageUrlsBySize").getAsJsonObject();
            JsonObject attributes = recipe.get("attributes").getAsJsonObject();
            JsonElement ingredientJson = recipe.get("ingredients");
            JsonElement courseJson = attributes.get("course");
            JsonElement cuisineJson = attributes.get("cuisine");


            Type listType = new TypeToken<List<String>>() {}.getType(); //GetType for JSON format Lists
            String recipeName = recipe.get("recipeName").getAsString();
            String recipeID = recipe.get("id").getAsString();
            int recipeRating = Integer.parseInt(recipe.get("rating").getAsString());
            String recipeSource = recipe.get("sourceDisplayName").getAsString();
            String imageURL = imageURLJson.get("90").getAsString();
            ArrayList<String> recipeIngredients = new Gson().fromJson(ingredientJson, listType);
            ArrayList<String> recipeCourses = new Gson().fromJson(courseJson, listType);
            ArrayList<String> recipeCuisine = new Gson().fromJson(cuisineJson, listType);

            test = new Recipe(recipeName, recipeIngredients, recipeCourses, recipeCuisine,imageURL, recipeRating, recipeSource, recipeID);


        } catch (Exception e){
            e.printStackTrace();
        }

        return test;
    }

}
