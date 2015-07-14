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
 * Created by stevebaker on 7/6/15.
 * Reference
 * https://github.com/udacity/android-custom-arrayadapter
 */
public class ArtistsArrayAdapter extends ArrayAdapter<ArtistParcelable> {

    ArtistViewHolder artistViewHolder;

    /**
     * Custom constructor (it doesn't mirror a superclass constructor).
     * @param context The current context. Used to inflate the layout file.
     * @param artistsList A List of ArtistParcelable objects to display
     */
    public ArtistsArrayAdapter(Activity context, List<ArtistParcelable> artistsList) {
        // Initialize the ArrayAdapter's internal storage for the context and the list.
        // ArrayAdapter uses the second argument when populating a single TextView.
        // ArtistsArrayAdapter is not going to use the second argument, so it can be any value. Here, we used 0.
        super(context, 0, artistsList);
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
        // Gets the ArtistParcelable object from the ArrayAdapter at the appropriate position
        ArtistParcelable artistParcelable = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.artists_list_item, parent, false);
            artistViewHolder = getConfiguredArtistViewHolder(convertView);
        } else {
            // this convertView already has the layout inflated from a previous call to getView
            artistViewHolder = (ArtistViewHolder) convertView.getTag();
        }

        if (artistParcelable.imageUrl == null
                || artistParcelable.imageUrl.equals("")) {
            // show placeholder image
            Picasso.with(getContext()).load(R.mipmap.ic_launcher).into(artistViewHolder.imageView);
        } else {
            Picasso.with(getContext()).load(artistParcelable.imageUrl).into(artistViewHolder.imageView);
        }

        artistViewHolder.nameView.setText(artistParcelable.name);

        return convertView;
    }

    private ArtistViewHolder  getConfiguredArtistViewHolder(View convertView) {
        // Use ViewHolder pattern to reduce repeated findViewById and make scrolling smoother.
        // Based on snippet from Udacity reviewer
        // http://developer.android.com/training/improving-layouts/smooth-scrolling.html
        ArtistViewHolder holder = new ArtistViewHolder();
        holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_imageview);
        holder.nameView = (TextView) convertView.findViewById(R.id.list_item_textview);
        convertView.setTag(holder);
        return holder;
    }

}
