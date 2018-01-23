package com.pastew.olxsniper;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pastew.olxsniper.db.Search;

import java.util.List;

public class SearchesAdapter extends RecyclerView.Adapter<SearchesAdapter.ViewHolder> {
    private final Context context;
    private List<Search> searchList;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder{ // TODO: TO powinno być static!!! Popraw. Ref: https://stackoverflow.com/questions/31302341/what-difference-between-static-and-non-static-viewholder-in-recyclerview-adapter
        // each data item is just a string in this case
        public EditText textEditText;
        public EditText cityEditText;
        public EditText priceMinEditText;
        public EditText priceMaxEditText;
        public Spinner categorySpinner;
        public CardView cardView;

        private Context c;

        public ViewHolder(View v, Context context) {
            super(v);
            textEditText = v.findViewById(R.id.searchTextEditText);

            textEditText.addTextChangedListener(new TextWatcher() {

                // the user's changes are saved here
                public void onTextChanged(CharSequence c, int start, int before, int count) {
                }

                public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                    // this space intentionally left blank
                }

                public void afterTextChanged(Editable c) {
                    searchList.get(getAdapterPosition()).text = textEditText.getText().toString();
                    searchList.get(getAdapterPosition()).getLink();
                }
            });

//            cityEditText = v.findViewById(R.id.searchCityEditText);
            priceMinEditText = v.findViewById(R.id.searchPriceMinEditText);
            priceMaxEditText = v.findViewById(R.id.searchPriceMaxEditText);
//            categorySpinner = v.findViewById(R.id.searchCategorySpinner);
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
                    saveSearchToDatabase(p);
                    return true;
                }
            });
        }

        private void saveSearchToDatabase(int position) {
            Toast.makeText(c, "LongClick: " + searchList.get(position).text , Toast.LENGTH_SHORT).show();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchesAdapter(Context mContext, List<Search> searchList) {
        this.context = mContext;
        this.searchList = searchList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SearchesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search, parent, false);
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
        Search search = searchList.get(position);

        holder.textEditText.setText(search.text);
//        holder.cityEditText.setText(search.city);
        holder.priceMinEditText.setText(Integer.toString(search.priceMin));
        holder.priceMaxEditText.setText(Integer.toString(search.priceMax));
//        holder.categorySpinner.setSelection(search.category);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public void removeItem(int position) {
        searchList.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(Search search, int position) {
        searchList.add(position, search);
        // notify item added by position
        notifyItemInserted(position);
    }

}