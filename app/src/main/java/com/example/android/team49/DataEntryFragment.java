package com.example.android.team49;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import static com.google.android.gms.internal.zzagz.runOnUiThread;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataEntryFragment extends Fragment {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "DataEntryActivity";
    private String barcodeValue = "";

    private EditText itemBarcode;
    private EditText itemName;
    private ImageView itemImage;
    private NumberPicker numberPicker;
    private int barcodeNumber = 0;

    private Button datePick;
    private DatePickerDialog datePickerDialog;

    private Button addItem;
    private ProgressDialog progressDialog;

    private MobileServiceClient msc;
    private MobileServiceTable<Ingredients> ingredientsTable;
    private List<Ingredients> results;

    public DataEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_data_entry, container, false);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        itemBarcode = (EditText) view.findViewById(R.id.item_barcode);
        itemName = (EditText) view.findViewById(R.id.item_name);
        itemImage = (ImageView) view.findViewById(R.id.item_image);
        numberPicker = (NumberPicker) view.findViewById(R.id.item_quantity);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);

        addItem = (Button) view.findViewById(R.id.confirm_add_item);

        //Picking Date
        datePick = (Button) view.findViewById(R.id.item_exp);
        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDatePicker().show();
            }
        });

        //Handling Barcode Scanning
        final Button barcodeInput = (Button) view.findViewById(R.id.button_barcode_input);
        barcodeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ScanActivity.class);
                startActivityForResult(i, RC_BARCODE_CAPTURE);
            }
        });

        //Handling Item Adding to DB
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    barcodeNumber = Integer.parseInt(itemBarcode.getText().toString());
                } catch (NumberFormatException n){
                    barcodeNumber = 0;
                }
                String name = itemName.getText().toString();
                String date = datePick.getText().toString().replace(" ", "");
                int quantity = numberPicker.getValue();
                if(name.equals("") || date.equalsIgnoreCase(getString(R.string.pick_a_date).replace(" ", "")) || date.equals("")){
                    Toast.makeText(getContext(), "Please Fill out all required information properly", Toast.LENGTH_SHORT).show();
                } else {
                    addItemToDB(barcodeNumber, name, date, quantity);
                    view.clearFocus();
                }
            }
        });

        return view;
    }

    private void addItemToDB(final int barcodeNumber, final String name, String expDate, int quantity) {
        String instanceID = InstanceID.getInstance(getContext()).getId();
        Ingredients ingredient = new Ingredients(instanceID, name, barcodeNumber, expDate, quantity);
        Log.d(TAG, "addItemToDB: "+ingredient.toString());

        try {
            msc = new MobileServiceClient("https://smartfridgeteam49.azurewebsites.net", getContext());
            ingredientsTable = msc.getTable("ingredientstest", Ingredients.class);
            ingredientsTable.insert(ingredient);

            @SuppressLint("StaticFieldLeak") final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Adding item to Stock... Please Wait");
                    progressDialog.show();
                }

                @Override
                protected Void doInBackground(Void... params) {

                    try{
                        results = ingredientsTable.where().field("name").eq(name)
                                        .execute().get();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                    Toast.makeText(getContext(), "Item added to Inventory!", Toast.LENGTH_LONG).show();
                                    itemName.setText("");
                                    datePick.setText(R.string.pick_a_date);
                                    itemBarcode.setText("");

                            }
                        });
                    } catch (final Exception e) {
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

        ViewFragment.results.add(ingredient);

    }

    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    public Dialog getDatePicker() {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String date = dayOfMonth + " / " + month + " / " + year;
                datePick.setText(date);
            }
        }, year, month, day);

        return datePickerDialog;
    }

    /**Asynchronous Task to Handle API Call **/

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    class setDataFromAPI extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("https://world.openfoodfacts.org/api/v0/product/" +barcodeValue+ ".json");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                // Convert to a JSON object to print data
                JsonParser jp = new JsonParser(); //from gson
                JsonElement root = jp.parse(new InputStreamReader((InputStream) conn.getContent())); //Convert the input stream to a json element
                JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                JsonObject product = rootobj.getAsJsonObject("product");
                String productName = product.get("product_name").toString().replace("\"", "");//just grab the name
                itemBarcode.setText(barcodeValue);
                itemName.setText(productName);

                if(product.get("image_front_small_url") != null) {
                    String productImageLink = product.get("image_front_small_url").toString().replace("\"", "");
                    Log.d(TAG, "Name read: " + productName + " image URL : " + productImageLink);
                    new DownloadImageTask((ImageView) getView().findViewById(R.id.item_image))
                            .execute(productImageLink);
                } else {
                    itemImage.setImageResource(R.drawable.ic_broken_image_black_24dp);
                }

            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Item not found - Please enter data manually");
                        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
                        itemName.setText("");
                        datePick.setText(R.string.pick_a_date);
                        itemBarcode.setText("");

                    }
                });
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be resultCancelled if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(ScanActivity.BarcodeObject);
                    barcodeValue = barcode.displayValue;
                    Log.d(TAG, "Barcode read: " + barcodeValue);
                    new setDataFromAPI().doInBackground();
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
