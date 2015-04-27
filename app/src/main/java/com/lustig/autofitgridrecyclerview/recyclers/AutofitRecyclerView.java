package com.lustig.autofitgridrecyclerview.recyclers;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

public class AutoFitRecyclerView extends RecyclerView {

    private static final String TAG = "Autofit";

    private GridLayoutManager manager;
    private int columnWidth = -1;
    private int rowHeight = -1;

    private int mNumColumns = -1;
    private int mNumRows = -1;

    public AutoFitRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public AutoFitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoFitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            int[] attrsArray = {
                    android.R.attr.columnWidth,
            };
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            columnWidth = array.getDimensionPixelSize(0, -1);

            Log.d("Lustig", "columnWidth: " + columnWidth);

            array.recycle();
        }

        manager = new GridLayoutManager(getContext(), 1);
        setLayoutManager(manager);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        Log.d("Lustig", "start onMeasure");

        super.onMeasure(widthSpec, heightSpec);
        if (columnWidth > 0) {
            mNumColumns = Math.max(1, getMeasuredWidth() / columnWidth);
            manager.setSpanCount(mNumColumns);

            Log.d("Lustig", "onMeasure: span count = " + mNumColumns);
        }

        Log.d("Lustig", "end onMeasure");
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    @Override
    protected void onFinishInflate() {

        Log.d("Lustig", "done inflating");

        Log.d("Lustig", ""+ manager.findLastVisibleItemPosition());

        super.onFinishInflate();

    }
}

