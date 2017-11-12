package com.pastew.olxsniper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final Context context;
    private List<Offer> offerList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleTextView;
        public TextView cityTextView;
        public TextView priceTextView;
        public TextView addedDateTextView;
        public ImageView linkImageView;
        public CardView cardView;

        public ViewHolder(View v) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            linkImageView = v.findViewById(R.id.linkImageView);
            cityTextView = v.findViewById(R.id.cityTextView);
            priceTextView = v.findViewById(R.id.priceTextView);
            addedDateTextView = v.findViewById(R.id.dateTextView);
            cardView = v.findViewById(R.id.cardView);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context mContext, List<Offer> offerList) {
        this.context = mContext;
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
        String price = offerList.get(position).price;
        String link = offerList.get(position).link;
        String city = offerList.get(position).city;
        String addedDate = offerList.get(position).addedDate;
        boolean wasSeenByUser = offerList.get(position).wasSeenByUser;

        holder.titleTextView.setText(title);
        holder.priceTextView.setText(price);
        holder.cityTextView.setText(city);
        holder.cityTextView.setText(city);
        holder.addedDateTextView.setText(addedDate);

        holder.linkImageView.setTag(link);
        holder.linkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(v.getTag().toString()));
                context.startActivity(i);
            }
        });

        if (wasSeenByUser)
            holder.cardView.setCardBackgroundColor(
                    context.getResources().getColor(R.color.offerWasSeenColor));
        else
            holder.cardView.setCardBackgroundColor(
                    context.getResources().getColor(R.color.offerWasNotSeenColor));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return offerList.size();
    }

}