package com.rdb.haxd.Presenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.rdb.haxd.Model.Hacker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Randy Bruner on 6/7/2017.
 */

public class HackerFragment extends android.support.v4.app.ListFragment {
    private List<Hacker> hackers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        Backendless.initApp(getContext(), "73B8E514-FB28-B17E-FF84-3BF6B88BD000", "929DA8BC-4FDE-CFAF-FF66-0B4B156DCB00", "v1");
        //create our list of hackers
        hackers = new ArrayList<>();
        populateList();



        return rootView;
    }

    private void populateList() {
        Backendless.Persistence.of(Hacker.class).find(new AsyncCallback<BackendlessCollection<Hacker>>() {
            @Override
            public void handleResponse(BackendlessCollection<Hacker> response) {
                hackers = response.getData();
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

        Iterator<Hacker> iter = hackers.iterator();
        while (iter.hasNext()) {
            Hacker h = iter.next();

            if (h.getObjectId().equals(Hacker.currentUser().getObjectId())) {
                iter.remove();
            }
        }
        //sort player list
        //Collections.sort(hackers, new HackerComparatorName());

        //fill the custom adapter
        HackerAdapter adapter = new HackerAdapter(getActivity(), hackers);

        //set the listView's adapter
        setListAdapter(adapter);
    }
}
