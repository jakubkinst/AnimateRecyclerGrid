package com.lustig.autofitgridrecyclerview.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.ViewTreeObserver;

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

        recyclerView.setAdapter(new NumberedAdapter(100));

        Log.d("MainActivity", "end of onCreate");

        final GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();

        ViewTreeObserver vto = recyclerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                                          @Override
                                          public void onGlobalLayout() {

                                              Log.d("MainActivity", "" + manager.findFirstCompletelyVisibleItemPosition());
                                              Log.d("MainActivity", "" + manager.findLastCompletelyVisibleItemPosition());

                                              recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                          }
                                      });


    }

}
