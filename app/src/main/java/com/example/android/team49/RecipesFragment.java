package com.example.android.team49;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipesFragment extends Fragment {

    //String yummlyURL = "http://api.yummly.com/v1/api/recipes?_app_id=bd3d9a8a&_app_key=aae41863adcb337b442a93c71fb77344";
    String edamamURL = "https://api.edamam.com/search?app_id=2779832b&app_key=13d9b72fcf298caf37cff7668775a2d4";
    private EditText recipeEdit;
    private Button searchButton;
    private ListView recipeListView;
    private String query = "";


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

        recipeEdit = (EditText) view.findViewById(R.id.recipe_edit_text);
        searchButton = (Button) view.findViewById(R.id.recipe_search_button);
        recipeListView = (ListView) view.findViewById(R.id.recipe_list_view);
        recipeListView.setVisibility(View.INVISIBLE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Fetching Recipes... Please Wait");
                progressDialog.show();
                query = recipeEdit.getText().toString();
                if(!query.equalsIgnoreCase("")){
                    recipeListView.setAdapter(new RecipeAdapter(getContext(), getDataFromEdamam(query)));
                    recipeListView.setVisibility(View.VISIBLE);
                } else {
                    recipeListView.setVisibility(View.INVISIBLE);
                }
                progressDialog.dismiss();
            }
        });


        return view;
    }

    private ArrayList<Recipe> getDataFromEdamam(String query){
        ArrayList<Recipe> toReturn = new ArrayList<>();

        try {
            URL url = new URL(edamamURL + "&q=" + query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) conn.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            int count = rootobj.get("count").getAsInt();
            if(count > 10){
                count = 10;
            }
            for(int i = 0; i < count; i++){
                JsonArray hits = rootobj.getAsJsonArray("hits");
                JsonObject recipeRoot = hits.get(i).getAsJsonObject();
                JsonObject recipeData = recipeRoot.get("recipe").getAsJsonObject();
                String recipeName = recipeData.get("label").getAsString();
                String recipeImageURL = recipeData.get("image").getAsString();
                String recipeSource = recipeData.get("source").getAsString();
                String recipeSourceURL = recipeData.get("url").getAsString();
                Gson gson = new Gson();
                Type type = new TypeToken<List<String>>(){}.getType();
                ArrayList<String> recipeIngredients = gson.fromJson(recipeData.getAsJsonArray("ingredientLines"), type);
                Recipe recipe = new Recipe(recipeName, recipeSource, recipeImageURL, recipeSourceURL, recipeIngredients);
                toReturn.add(recipe);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return toReturn;
    }

    private ArrayList<Recipe> getDataFromEdamam(String query, ArrayList<String> included){
        ArrayList<Recipe> toReturn = new ArrayList<>();

        for(int i = 0; i < included.size(); i++){
            query = query + " " + included.get(i);
        }

        try {
            URL url = new URL(edamamURL + "&q=" + query);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser(); //from gson
            JsonElement root = jp.parse(new InputStreamReader((InputStream) conn.getContent())); //Convert the input stream to a json element
            JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
            int count = rootobj.get("count").getAsInt();
            if(count > 10){
                count = 10;
            }
            for(int i = 0; i < count; i++){
                JsonArray hits = rootobj.getAsJsonArray("hits");
                JsonObject recipeRoot = hits.get(i).getAsJsonObject();
                JsonObject recipeData = recipeRoot.get("recipe").getAsJsonObject();
                String recipeName = recipeData.get("label").getAsString();
                String recipeImageURL = recipeData.get("image").getAsString();
                String recipeSource = recipeData.get("source").getAsString();
                String recipeSourceURL = recipeData.get("url").getAsString();
                Gson gson = new Gson();
                Type type = new TypeToken<List<String>>(){}.getType();
                ArrayList<String> recipeIngredients = gson.fromJson(recipeData.getAsJsonArray("ingredientLines"), type);
                Recipe recipe = new Recipe(recipeName, recipeSource, recipeImageURL, recipeSourceURL, recipeIngredients);
                toReturn.add(recipe);
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return toReturn;
    }

}
