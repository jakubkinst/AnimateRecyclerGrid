package com.lustig.autofitgridrecyclerview.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.lustig.autofitgridrecyclerview.R;
import com.lustig.autofitgridrecyclerview.adapters.NumberedAdapter;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new NumberedAdapter(100));
    }
}
