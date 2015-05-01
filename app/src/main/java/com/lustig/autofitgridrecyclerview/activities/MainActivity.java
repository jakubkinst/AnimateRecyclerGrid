package com.lustig.autofitgridrecyclerview.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.lustig.autofitgridrecyclerview.R;
import com.lustig.autofitgridrecyclerview.adapters.NumberedAdapter;
import com.lustig.autofitgridrecyclerview.recyclers.AutoFitRecyclerView;

public class MainActivity extends Activity {

    AutoFitRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (AutoFitRecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(new NumberedAdapter(100));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            mRecyclerView.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        super.onConfigurationChanged(newConfig);
    }
}
