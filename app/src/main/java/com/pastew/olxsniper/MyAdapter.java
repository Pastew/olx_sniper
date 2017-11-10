package com.pastew.olxsniper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final Context mContent;
    private List<Offer> offerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.info_text);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context mContext, List<Offer> offerList) {
        this.mContent = mContext;
        this.offerList = offerList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer, parent, false);
        // set the view's size, margins, paddings and layout parameters
        // ...
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String title = offerList.get(position).title;
        BigDecimal price = offerList.get(position).price;
        String link =offerList.get(position).link;
        String city = offerList.get(position).city;
        Date addedDate = offerList.get(position).addedDate;

        holder.mTextView.setText(String.format(
                "Title: %s\nPrice: %s zł\nLink: %s\nCity: %s\nAddedDate: %s",
                title, price, link, city, addedDate ));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return offerList.size();
    }
}