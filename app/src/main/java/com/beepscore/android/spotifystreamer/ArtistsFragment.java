package com.beepscore.android.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * Shows artists
 */
public class ArtistsFragment extends Fragment {

    public ArtistsFragment() {
    }

    ArrayAdapter<String> adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View artistsView = inflater.inflate(R.layout.fragment_main, container, false);

        List<String> list = new ArrayList<String>();

        // TODO: replace fake list elements
        list.add("foo");
        list.add("bar");
        list.add(getActivity().getString(R.string.baz));
        list.add(getActivity().getString(R.string.dash));
        list.add(getActivity().getString(R.string.dash));

        // adapter creates views for each list item
        adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item,
                R.id.list_item_textview,
                list);

        ListView listView = (ListView)artistsView.findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        return artistsView;
    }
}
