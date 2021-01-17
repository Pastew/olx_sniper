package com.pastew.olxsniper.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pastew.olxsniper.R;
import com.pastew.olxsniper.db.Search;
import com.pastew.olxsniper.db.SniperDatabaseManager;

import java.util.ArrayList;
import java.util.List;


public class TabSearches extends Fragment {

    private SearchesAdapter searchesAdapter;
    List<Search> searchList;
    private SniperDatabaseManager sniperDatabaseManager;
    private View view;
    private Context context;


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
                updateAllSearchesFromEditTexts();
                new TabSearches.SaveSearchesToDatabaseTask().execute();
            }
        });
    }

    private void updateAllSearchesFromEditTexts() {

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
        searchList.add(search);
        searchesAdapter.notifyItemInserted(searchList.size()-1);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = view.findViewById(R.id.searches_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        searchList = new ArrayList<>();
        searchesAdapter = new SearchesAdapter(context, searchList);
        recyclerView.setAdapter(searchesAdapter);

//        ItemTouchHelper.SimpleCallback itemTouchHelperCallback =
//                new RecyclerItemTouchHelper(0,
//                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
//                        this);

//        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }

    private void setupSearchDbManager() {
        this.sniperDatabaseManager = new SniperDatabaseManager(context);
    }

    @Override
    public void onResume() {
        searchesAdapter.notifyDataSetChanged();
        new TabSearches.DownloadSearchesFromDatabaseTask().execute();
        super.onResume();
    }

    private class DownloadSearchesFromDatabaseTask extends AsyncTask<Void, Integer, List<Search>> {
        protected List<Search> doInBackground(Void... params) {
            List<Search> searchesFromDatabase = sniperDatabaseManager.getAllSearches();
            return searchesFromDatabase;
        }

        protected void onPostExecute(List<Search> searches) {
            if (searches.size() > 0) {
                searchList.addAll(0, searches);
                searchesAdapter.notifyDataSetChanged();

            } else {
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
            sniperDatabaseManager.saveAllSearches(searchList);
            return null;
        }

        protected void onPostExecute(Void... params) {
            Toast.makeText(context, "Zapisałem zmiany", Toast.LENGTH_SHORT).show();
        }
    }
}