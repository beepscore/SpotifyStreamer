package com.beepscore.android.spotifystreamer;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by stevebaker on 7/7/15.
 * Reference
 * https://github.com/udacity/android-custom-arrayadapter
 */
public class TracksArrayAdapter extends ArrayAdapter<TrackParcelable> {

    TrackViewHolder trackViewHolder;

    /**
     * Custom constructor (it doesn't mirror a superclass constructor).
     * @param context The current context. Used to inflate the layout file.
     * @param tracksList A List of TrackParcelable objects to display
     */
    public TracksArrayAdapter(Activity context, List<TrackParcelable> tracksList) {
        // Initialize the ArrayAdapter's internal storage for the context and the list.
        // ArrayAdapter uses the second argument when populating a single TextView.
        // ArtistsArrayAdapter is not going to use the second argument, so it can be any value. Here, we used 0.
        super(context, 0, tracksList);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the TrackParcelable object from the ArrayAdapter at the appropriate position
        TrackParcelable trackParcelable = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tracks_list_item, parent, false);
            trackViewHolder = getConfiguredTrackViewHolder(convertView);
        } else {
            // this convertView already has the layout inflated from a previous call to getView
            trackViewHolder = (TrackViewHolder) convertView.getTag();
        }

        if (trackParcelable.imageNarrowestUrl == null
                || trackParcelable.imageNarrowestUrl.equals("")) {
            // show placeholder image
            Picasso.with(getContext()).load(R.mipmap.ic_launcher).into(trackViewHolder.imageView);
        } else {
            Picasso.with(getContext()).load(trackParcelable.imageNarrowestUrl).into(trackViewHolder.imageView);
        }

        trackViewHolder.trackNameView.setText(trackParcelable.name);
        trackViewHolder.albumNameView.setText(trackParcelable.albumName);

        return convertView;
    }

    private TrackViewHolder  getConfiguredTrackViewHolder(View convertView) {
        // Use ViewHolder pattern to reduce repeated findViewById and make scrolling smoother.
        // Based on snippet from Udacity reviewer
        // http://developer.android.com/training/improving-layouts/smooth-scrolling.html
        TrackViewHolder holder = new TrackViewHolder();
        holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_imageview);
        holder.trackNameView = (TextView) convertView.findViewById(R.id.list_item_track);
        holder.albumNameView = (TextView) convertView.findViewById(R.id.list_item_album);
        convertView.setTag(holder);
        return holder;
    }
}
