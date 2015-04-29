package com.lustig.autofitgridrecyclerview.adapters;

import android.content.Context;
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

    private int mNumColumns = -1;

    private GridLayoutManager mManager;

    private View[][] mViewsToAnimate;

    private boolean[] mChildHasBeenRevealed;

    private boolean mAllVisible = false;

    private boolean mHasAnimated = false;

    private Context mContext;

    public NumberedAdapter(int count, Context context) {

        mContext = context;

        mChildHasBeenRevealed = new boolean[count];

        Log.d("Lustig", "begin NumberAdapter constructor");

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

        mHelper = new AnimationHelper(mRecyclerView);
    }


    public class TextViewHolder extends RecyclerView.ViewHolder {

        public final CardView cardView;

        public final TextView textView;

        public TextViewHolder(View itemView) {

            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card);

            textView = (TextView) itemView.findViewById(R.id.text);

//            cardView.setOnClickListener(
//                    new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
//                            makeViewsInvisible();
//
//                            mHelper = new AnimationHelper(mViewsToAnimate, mRecyclerView);
//                        }
//                    });

        }
    }

    protected void makeViewsInvisible() {

        for (int i = 0; i < mRecyclerView.getChildCount(); i++) {
            mRecyclerView.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TextViewHolder holder, final int position) {

        Log.d("Lustig", "onBindViewHolder: " + position);

        /* If mNumColumns has been set, the code will skip this block */
        if (mNumColumns == -1) {

            Log.d("Lustig", "setting adapter numColumns");

            mNumColumns = mRecyclerView.getNumColumns();

            Log.d("Lustig", "mNumColumns now = " + mNumColumns);
        }

        final String label = labels.get(position);
        holder.textView.setText(label);

        if (mHasAnimated) {
            holder.cardView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {

        return labels.size();

    }

    public int getNumColumns() {
        return mNumColumns;
    }

}