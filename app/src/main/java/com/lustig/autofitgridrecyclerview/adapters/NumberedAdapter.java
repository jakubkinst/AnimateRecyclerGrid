package com.lustig.autofitgridrecyclerview.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lustig.autofitgridrecyclerview.R;
import com.lustig.autofitgridrecyclerview.animations.AnimationHelper;
import com.lustig.autofitgridrecyclerview.recyclers.AutoFitRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NumberedAdapter extends RecyclerView.Adapter<NumberedAdapter.TextViewHolder> {

    private List<String> labels;
    private AnimationHelper mHelper;

    private AutoFitRecyclerView mRecyclerView;

    private int mColumns = -1;
    private int mRows = -1;

    private GridLayoutManager mManager;

    public NumberedAdapter(int count) {

        Log.d("Lustig", "begin NumberAdapter constructor");

        mHelper = new AnimationHelper();

        labels = new ArrayList<String>(count);
        for (int i = 0; i < count; ++i) {
            labels.add(String.valueOf(i));
        }

        Log.d("Lustig", "end NumberAdapter constructor");

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        Log.d("Lustig", "begin onAttached");

        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = (AutoFitRecyclerView) recyclerView;
        mManager = ((GridLayoutManager) mRecyclerView.getLayoutManager());

        Log.d("Lustig", "end onAttached");
    }
    public class TextViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView textView;

        public TextViewHolder(View itemView) {

            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card);
            textView = (TextView) itemView.findViewById(R.id.text);
        }
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TextViewHolder holder, final int position) {

        Log.d("Lustig", "begin onBindViewHolder");

        Log.d("Lustig", "member columsn: " + mColumns);

        Log.d("Lustig", "first visible " + mManager.findFirstVisibleItemPosition());
        Log.d("Lustig", "last completely visible" + mManager.findLastCompletelyVisibleItemPosition());


        Log.d("Lustig", "columns: " + mRecyclerView.getNumColumns() + "");

        /* If mColumns has been set, the code will skip this block */
        if (mColumns == -1) {

            Log.d("Lustig", "setting adapter numColumns");

//
//
//            int firstVisiblePosition =
//            int lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition();
//
//            /* Test to see if we get the right height */
//            int height = lastVisiblePosition - firstVisiblePosition;
//
////            Log.d("Lustig", "is the height " + height + "?");


            mColumns = mRecyclerView.getNumColumns();

            Log.d("Lustig", "mColumns now = " + mColumns);
        }

        final String label = labels.get(position);
        holder.textView.setText(label);

        mHelper.addViewToQueue(holder.cardView);

        Log.d("Lustig", "columns: " + mRecyclerView.getNumColumns() + "");

        Log.d("Lustig", "end onBindViewHolder");

    }

    @Override
    public int getItemCount() {

        Log.d("Lustig", "getItemCount");
        return labels.size();


    }

}