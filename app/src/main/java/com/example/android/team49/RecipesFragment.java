package com.example.android.team49;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A {@link Fragment} that allows searching for Recipes and displays them in a List of CardViews.
 * @authors         Abdirahman Mohamed, Venet Kukran
 */
public class RecipesFragment extends Fragment {
    String edamamURL = "https://api.edamam.com/search?app_id=2779832b&app_key=13d9b72fcf298caf37cff7668775a2d4";
    private String instanceId;

    private EditText recipeEdit;
    private Button searchButton;
    private Button chooseButton;
    private ListView recipeListView;
    private String query = "";

    private List<String> ingredients = new ArrayList<>();
    private ArrayList<String> chosen;

    private ProgressDialog progressRecipe;
    private ArrayList<Recipe> toReturn = new ArrayList<>();

    private ViewFlipper viewFlipper;


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
        viewFlipper = (ViewFlipper) view.findViewById(R.id.recipe_view_flipper);
        recipeEdit = (EditText) view.findViewById(R.id.recipe_edit_text);
        searchButton = (Button) view.findViewById(R.id.recipe_search_button);
        chooseButton = (Button) view.findViewById(R.id.ingredient_search_button);
        recipeListView = (ListView) view.findViewById(R.id.recipe_list_view);
        //getIngredients(instanceId);
        if(ViewFragment.results != null) {
            chooseButton.setVisibility(View.VISIBLE);
            for (int i = 0; i < ViewFragment.results.size(); i++) {
                if (!ingredients.contains(ViewFragment.results.get(i).getName())) {
                    ingredients.add(ViewFragment.results.get(i).getName());
                }
            }
        } else {
            chooseButton.setVisibility(View.INVISIBLE);
        }

        if(savedInstanceState == null) {
            recipeListView.setVisibility(View.INVISIBLE);
        } else {
            recipeListView.setVisibility(View.VISIBLE);
            recipeListView.setAdapter(new RecipeAdapter(getContext(), (ArrayList<Recipe>) savedInstanceState.getSerializable("Recipes_list")));
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = recipeEdit.getText().toString();
               // progressRecipe.show();
                if (!query.equalsIgnoreCase("")) {
                    setDataFromEdamam(query);
                } else {
                    recipeListView.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Please Enter a valid keyword", Toast.LENGTH_SHORT).show();
                }

            }
        });

        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Integer> selected = new ArrayList<Integer>();
                CharSequence[] cs = ingredients.toArray(new CharSequence[ingredients.size()]);
                if (cs.length != 0) {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("Choose the ingredients you want to use")
                            .setMultiChoiceItems(cs, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                    if (isChecked) {
                                        selected.add(indexSelected);
                                    } else if (selected.contains(indexSelected)) {
                                        selected.remove(indexSelected);
                                    }
                                    chosen = new ArrayList<>();
                                    for (int i : selected) {
                                        chosen.add(ingredients.get(i));
                                    }
                                }
                            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        getDataFromEdamam(chosen);
                                    } catch (NullPointerException n) {
                                        //
                                    }

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //
                                }
                            }).create();
                    dialog.show();
                } else {
                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle("No Ingredients Found")
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create();
                    dialog.show();
                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("Recipes_list", toReturn);
    }

    private AsyncTask<Void, Void, ArrayList<Recipe>> runAsyncTaskForRecipeList(AsyncTask<Void, Void, ArrayList<Recipe>> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    /**
     * Fetches data from the API from the "Search By Name" and displays it in the ListView
     * @param query         The Query passed to the API
     */
    private void setDataFromEdamam(final String query){

        @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, ArrayList<Recipe>> task = new AsyncTask<Void, Void, ArrayList<Recipe>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressRecipe = new ProgressDialog(getContext());
                progressRecipe.setMessage("Fetching those recipes for you...");
                progressRecipe.show();
            }

            @Override
            protected ArrayList<Recipe> doInBackground(Void... voids) {
                try{
                    URL url = new URL(edamamURL + "&q=" + query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // Convert to a JSON object to print data
                    JsonParser jp = new JsonParser(); //from gson
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) conn.getContent())); //Convert the input stream to a json element
                    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                    int count = rootobj.get("count").getAsInt();
                    if (count > 10) {
                        count = 10;
                    }
                    for (int i = 0; i < count; i++) {
                        JsonArray hits = rootobj.getAsJsonArray("hits");
                        JsonObject recipeRoot = hits.get(i).getAsJsonObject();
                        JsonObject recipeData = recipeRoot.get("recipe").getAsJsonObject();
                        String recipeName = recipeData.get("label").getAsString();
                        String recipeImageURL = recipeData.get("image").getAsString();
                        String recipeSource = recipeData.get("source").getAsString();
                        String recipeSourceURL = recipeData.get("url").getAsString();
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<String>>() {
                        }.getType();
                        ArrayList<String> recipeIngredients = gson.fromJson(recipeData.getAsJsonArray("ingredientLines"), type);
                        Recipe recipe = new Recipe(recipeName, recipeSource, recipeImageURL, recipeSourceURL, recipeIngredients);
                        toReturn.add(recipe);
                    }

                } catch(
                        Exception e)

                {
                    e.printStackTrace();
                }

                return toReturn;
            }

            @Override
            protected void onPostExecute(ArrayList toReturn) {
                super.onPostExecute(toReturn);
                if(toReturn.isEmpty()){
                    viewFlipper.setDisplayedChild(1);
                } else {
                    viewFlipper.setDisplayedChild(0);
                    recipeListView.setAdapter(new RecipeAdapter(getContext(), toReturn));
                    recipeListView.setVisibility(View.VISIBLE);
                }
                if(progressRecipe.isShowing()){
                    progressRecipe.dismiss();
                }
            }
        };
        runAsyncTaskForRecipeList(task);
    }

    /**
     * Fetches data from the API from the List of Ingredients and displays it in the ListView
     * @param included         The list of Ingredient Names selected by the user.
     */
    private void getDataFromEdamam(ArrayList<String> included){
        String totalQuery = "";

        for(int i = 0; i < included.size(); i++){
            totalQuery = totalQuery + included.get(i) + " ";
        }

        final String query = totalQuery;

        @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, ArrayList<Recipe>> task = new AsyncTask<Void, Void, ArrayList<Recipe>>() {

            private ArrayList<Recipe> toReturn = new ArrayList<>();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressRecipe = new ProgressDialog(getContext());
                progressRecipe.setMessage("Fetching those recipes for you...");
                progressRecipe.show();
            }

            @Override
            protected ArrayList<Recipe> doInBackground(Void... voids) {
                try{
                    URL url = new URL(edamamURL + "&q=" + query);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // Convert to a JSON object to print data
                    JsonParser jp = new JsonParser(); //from gson
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) conn.getContent())); //Convert the input stream to a json element
                    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                    int count = rootobj.get("count").getAsInt();
                    if (count > 10) {
                        count = 10;
                    }
                    for (int i = 0; i < count; i++) {
                        JsonArray hits = rootobj.getAsJsonArray("hits");
                        JsonObject recipeRoot = hits.get(i).getAsJsonObject();
                        JsonObject recipeData = recipeRoot.get("recipe").getAsJsonObject();
                        String recipeName = recipeData.get("label").getAsString();
                        String recipeImageURL = recipeData.get("image").getAsString();
                        String recipeSource = recipeData.get("source").getAsString();
                        String recipeSourceURL = recipeData.get("url").getAsString();
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<String>>() {
                        }.getType();
                        ArrayList<String> recipeIngredients = gson.fromJson(recipeData.getAsJsonArray("ingredientLines"), type);
                        Recipe recipe = new Recipe(recipeName, recipeSource, recipeImageURL, recipeSourceURL, recipeIngredients);
                        toReturn.add(recipe);
                    }

                } catch(
                        Exception e)

                {
                    e.printStackTrace();
                }

                return toReturn;
            }

            @Override
            protected void onPostExecute(ArrayList toReturn) {
                super.onPostExecute(toReturn);
                if(toReturn.isEmpty()){
                    viewFlipper.setDisplayedChild(1);
                } else {
                    viewFlipper.setDisplayedChild(0);
                    recipeListView.setAdapter(new RecipeAdapter(getContext(), toReturn));
                    recipeListView.setVisibility(View.VISIBLE);
                }
                if(progressRecipe.isShowing()){
                    progressRecipe.dismiss();
                }
            }
        };
        runAsyncTaskForRecipeList(task);
    }

}
