package com.lustig.autofitgridrecyclerview.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lustig.autofitgridrecyclerview.R;
import com.lustig.autofitgridrecyclerview.animations.AnimationHelper;

import java.util.ArrayList;
import java.util.List;

public class NumberedAdapter extends RecyclerView.Adapter<NumberedAdapter.TextViewHolder> {

    private List<String> labels;
    private AnimationHelper mHelper;

    public NumberedAdapter(int count) {

        mHelper = new AnimationHelper();

        labels = new ArrayList<String>(count);
        for (int i = 0; i < count; ++i) {
            labels.add(String.valueOf(i));
        }
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TextViewHolder holder, final int position) {

        final String label = labels.get(position);
        holder.textView.setText(label);

        mHelper.addViewToQueue(holder.cardView);

    }

    @Override
    public int getItemCount() {

        return labels.size();
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
}