package com.example.android.team49;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseMethodFragment extends Fragment {

    private TextView openScanner;
    private TextView openDataEntry;

    public ChooseMethodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_method, container, false);

        openScanner = (TextView) view.findViewById(R.id.open_scanner);
        openDataEntry = (TextView) view.findViewById(R.id.open_data_entry);

        openScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScanner();
            }
        });

        openDataEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDataEntry();
            }
        });

        return view;
    }

    private void openScanner(){
        Intent i = new Intent(this.getContext(), ScanActivity.class);
        startActivity(i);
    }

    private void openDataEntry(){
        Intent i = new Intent(this.getContext(), DataEntryActivity.class);
        startActivity(i);
    }

}
