package com.example.android.team49;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
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

import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

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

import static com.google.android.gms.internal.zzagz.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecipesFragment extends Fragment {

    //String yummlyURL = "http://api.yummly.com/v1/api/recipes?_app_id=bd3d9a8a&_app_key=aae41863adcb337b442a93c71fb77344";
    String edamamURL = "https://api.edamam.com/search?app_id=2779832b&app_key=13d9b72fcf298caf37cff7668775a2d4";
    private String instanceId;

    private EditText recipeEdit;
    private Button searchButton;
    private Button chooseButton;
    private ListView recipeListView;
    private String query = "";

    private MobileServiceClient msc;
    private MobileServiceTable<Ingredients> ingredientsTable;
    private ArrayList<Ingredients> results;
    private List<String> ingredients;

    private ProgressDialog progressDialog;


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
        instanceId = InstanceID.getInstance(getContext()).getId();
        getIngredients(instanceId);

        recipeEdit = (EditText) view.findViewById(R.id.recipe_edit_text);
        searchButton = (Button) view.findViewById(R.id.recipe_search_button);
        chooseButton = (Button) view.findViewById(R.id.ingredient_search_button);
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
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList selected = new ArrayList();
                CharSequence[] cs = ingredients.toArray(new CharSequence[ingredients.size()]);
                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Choose the ingredients you want to use")
                        .setMultiChoiceItems(cs, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    selected.add(indexSelected);
                                } else if (selected.contains(indexSelected)) {
                                    selected.remove(Integer.valueOf(indexSelected));
                                }
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO: @SIDAK SEARCH API BASED ON SELECTED ARRAY LIST
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //
                            }
                        }).create();
                dialog.show();
            }
        });




        return view;
    }

    private void getIngredients(final String instanceId){
        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", getContext());
            ingredientsTable = msc.getTable("ingredientstest", Ingredients.class);

            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Getting your items... Please Wait");
                    progressDialog.show();
                }

                @Override
                protected Void doInBackground(Void... params) {

                    try{
                        results = ingredientsTable.where().field("instanceId").eq(instanceId)
                                .execute().get();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ingredients = new ArrayList<>();
                                for(Ingredients i : results){
                                    ingredients.add(i.getName());
                                }
                            }
                        });
                    } catch (final Exception e) {
                        //createAndShowDialogFromTask(e, "Error");
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            };
            runAsyncTask(task);
        } catch (Exception e) {

        }
    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
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
