package com.beepscore.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Class TrackParcelable is similar to Track, but is Parcelable.
 * It has a subset of Track properties.
 * Can't have a property that is type Track??
 * https://developer.android.com/reference/android/os/Parcelable.html
 * Created by stevebaker on 7/11/15.
 */
public class TrackParcelable implements Parcelable {

    String artistName;
    String albumName;
    String name;
    String imageUrl;

    // constructor
    public TrackParcelable(String artistName,
                           String albumName, String name, String imageUrl) {
        this.artistName = artistName;
        this.albumName = albumName;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // private constructor
    private TrackParcelable(Parcel in) {
        artistName = in.readString();
        albumName = in.readString();
        name = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        final String separator = " ";
        return artistName
                + separator + albumName
                + separator + name
                + separator + imageUrl;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        // second argument can be used as a list index when parceling a list object
        // must decode in same order used by encode
        parcel.writeString(artistName);
        parcel.writeString(albumName);
        parcel.writeString(name);
        parcel.writeString(imageUrl);
    }

    public static final Parcelable.Creator<TrackParcelable> CREATOR =
            new Parcelable.Creator<TrackParcelable>() {

                @Override
                public TrackParcelable createFromParcel(Parcel parcel) {
                    return new TrackParcelable(parcel);
                }

                @Override
                public TrackParcelable[] newArray(int i) {
                    return new TrackParcelable[i];
                }
            };

}
