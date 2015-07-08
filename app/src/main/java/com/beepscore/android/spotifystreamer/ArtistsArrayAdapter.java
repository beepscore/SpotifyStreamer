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

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by stevebaker on 7/6/15.
 * Reference
 * https://github.com/udacity/android-custom-arrayadapter
 */
public class ArtistsArrayAdapter extends ArrayAdapter<Artist> {

    /**
     * Custom constructor (it doesn't mirror a superclass constructor).
     * @param context The current context. Used to inflate the layout file.
     * @param artistsList A List of Artist objects to display
     */
    public ArtistsArrayAdapter(Activity context, List<Artist> artistsList) {
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
        // Gets the Artist object from the ArrayAdapter at the appropriate position
        Artist artist = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.artists_list_item, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_imageview);
        loadArtistImageView(artist, imageView);

        TextView artistNameView = (TextView) convertView.findViewById(R.id.list_item_textview);
        artistNameView.setText(artist.name);

        return convertView;
    }

    private void loadArtistImageView(Artist artist, ImageView imageView) {
        // https://github.com/kaaes/spotify-web-api-android/blob/master/src/main/java/kaaes/spotify/webapi/android/models/Image.java
        if (artist.images.size() > 0) {
            // get the last image because images is sorted decreasing size
            Image artistLastImage = artist.images.get(artist.images.size() - 1);
            String artistLastImageUrlString = artistLastImage.url;
            Picasso.with(getContext()).load(artistLastImageUrlString).into(imageView);
        }
    }

}
