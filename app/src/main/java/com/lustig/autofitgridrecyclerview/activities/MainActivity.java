package com.lustig.autofitgridrecyclerview.activities;

import android.app.Activity;
import android.os.Bundle;

import com.lustig.autofitgridrecyclerview.R;
import com.lustig.autofitgridrecyclerview.adapters.NumberedAdapter;
import com.lustig.autofitgridrecyclerview.recyclers.AutoFitRecyclerView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AutoFitRecyclerView recyclerView = (AutoFitRecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(new NumberedAdapter(100, this));



    }

}
