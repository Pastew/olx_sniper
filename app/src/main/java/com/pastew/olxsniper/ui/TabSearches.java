package com.pastew.olxsniper.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pastew.olxsniper.R;
import com.pastew.olxsniper.db.Search;
import com.pastew.olxsniper.db.SniperDatabaseManager;

import java.util.ArrayList;
import java.util.List;


public class TabSearches extends Fragment implements SearchRecyclerItemTouchHelper.SearchRecyclerItemTouchHelperListener {

    private SearchesAdapter searchesAdapter;
    private SniperDatabaseManager sniperDatabaseManager;
    private View view;
    private Context context;
    private List<Search> searchesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_searches, container, false);

        context = getContext();
        setupRecyclerView();
        setupSearchDbManager();
        setupFab();
        setupButtons();

        return view;
    }

    private void setupButtons() {
        view.findViewById(R.id.saveAllSearches).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TabSearches.SaveSearchesToDatabaseTask().execute();
            }
        });
    }

    private void setupFab() {
        FloatingActionButton fab = view.findViewById(R.id.searchesFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewSearchInList();
            }
        });
    }

    private void createNewSearchInList() {
        Search search = new Search();
        searchesList.add(search);
        searchesAdapter.notifyItemInserted(searchesList.size() - 1);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.searches_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        searchesList = new ArrayList<>();
        searchesAdapter = new SearchesAdapter(context, searchesList);
        recyclerView.setAdapter(searchesAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
                new SearchRecyclerItemTouchHelper(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                        this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    private void setupSearchDbManager() {
        this.sniperDatabaseManager = new SniperDatabaseManager(context);
    }

    @Override
    public void onResume() {
        searchesAdapter.notifyDataSetChanged();
        new DownloadAndShowSearchesFromDatabaseTask().execute();
        super.onResume();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof SearchesAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String name = searchesList.get(viewHolder.getAdapterPosition()).url;

            // backup of removed item for undo purpose
            final Search deletedItem = searchesList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            searchesAdapter.removeItem(viewHolder.getAdapterPosition());

            // set in database "removed" flag
            new DeleteSearchFromDatabase().execute(deletedItem);

            // showing snack bar with Undo option
            String nameToShow = name.substring(0, Math.min(name.length(), 10));
            Snackbar snackbar = Snackbar.make(view.findViewById(R.id.tabSearchesLayout), "Usunięto " + nameToShow + "(...)", Snackbar.LENGTH_LONG);
            snackbar.setAction("COFNIJ", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    searchesAdapter.restoreItem(deletedItem, deletedIndex);
                    new TabSearches.SaveSearchesToDatabaseTask().execute();
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private class DeleteSearchFromDatabase extends AsyncTask<Search, Void, Void> {
        protected Void doInBackground(Search... searches) {
            sniperDatabaseManager.deleteSearch(searches[0], true);
            return null;
        }
    }

    private class DownloadAndShowSearchesFromDatabaseTask extends AsyncTask<Void, Integer, List<Search>> {
        protected List<Search> doInBackground(Void... params) {
            List<Search> searchesFromDatabase = sniperDatabaseManager.getAllSearches();
            return searchesFromDatabase;
        }

        protected void onPostExecute(List<Search> searchesFromDatabase) {
            if (searchesFromDatabase.size() > 0) {
                searchesList.clear();
                searchesList.addAll(0, searchesFromDatabase);
                searchesAdapter.notifyDataSetChanged();
            } else {
                searchesList.clear();
                searchesAdapter.notifyDataSetChanged();

                Snackbar snackbar = Snackbar
                        .make(getActivity().findViewById(R.id.main_content),
                                String.format("Nie ustawiłeś żadnych wyszukiwań."), Snackbar.LENGTH_LONG)
                        .setAction("Nie klikaj!", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(context, "Miałeś nie klikać!", Toast.LENGTH_SHORT).show();
                            }
                        });
                snackbar.show();
            }
        }
    }

    private class SaveSearchesToDatabaseTask extends AsyncTask<Void, Integer, Void> {
        protected Void doInBackground(Void... params) {
            sniperDatabaseManager.saveAllSearches(searchesList);
            return null;
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(context, "Zapisałem zmiany", Toast.LENGTH_SHORT).show();
        }
    }
}