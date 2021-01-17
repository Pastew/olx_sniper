package com.pastew.olxsniper.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pastew.olxsniper.R;
import com.pastew.olxsniper.Utils;
import com.pastew.olxsniper.db.Offer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {
    private final Context context;
    private List<Offer> offerList;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public TextView titleTextView;
        public TextView cityTextView;
        public TextView priceTextView;
        public TextView addedDateTextView;
        public ImageView linkImageView;
        public CardView cardView;

        private Context c;

        public ViewHolder(View v, Context context) {
            super(v);
            titleTextView = v.findViewById(R.id.titleTextView);
            linkImageView = v.findViewById(R.id.linkImageView);
            cityTextView = v.findViewById(R.id.cityTextView);
            priceTextView = v.findViewById(R.id.priceTextView);
            addedDateTextView = v.findViewById(R.id.dateTextView);
            cardView = v.findViewById(R.id.cardView);
            this.c = context;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int p=getLayoutPosition();
                    System.out.println("click: "+p);
                    Toast.makeText(c, "Click", Toast.LENGTH_SHORT).show();
                }
            });

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int p=getLayoutPosition();
                    System.out.println("LongClick: "+p);
                    Toast.makeText(c, "Long Click", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OffersAdapter(Context mContext, List<Offer> offerList) {
        this.context = mContext;
        this.offerList = offerList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OffersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offer, parent, false);
        // set the view's size, margins, paddings and layout parameters
        // ...
        ViewHolder vh = new ViewHolder(v, context);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Offer offer = offerList.get(position);

        holder.titleTextView.setText(offer.title);
        holder.priceTextView.setText(offer.price);
        holder.cityTextView.setText(offer.city);

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
        Date resultDate = new Date(offer.date);
        String addedDate = sdf.format(resultDate);
        holder.addedDateTextView.setText(addedDate);

        holder.linkImageView.setTag(offer.link);
        holder.linkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(v.getTag().toString()));
                context.startActivity(i);
            }
        });

        boolean wasSeenByUser = Utils.checkIfOfferWasSeenByUser(context, offer);
        if (wasSeenByUser) {
            //holder.cardView.setCardBackgroundColor(
            //        context.getResources().getColor(R.color.offerWasSeenColor));
            TextViewCompat.setTextAppearance(holder.titleTextView, R.style.titleSeenByUser);
        } else{
            TextViewCompat.setTextAppearance(holder.titleTextView, R.style.titleNotSeenByUser);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public void removeItem(int position) {
        offerList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Offer offer, int position) {
        offerList.add(position, offer);
        // notify item added by position
        notifyItemInserted(position);
    }

}