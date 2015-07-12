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

    String albumName;
    String imageUrl;
    String name;

    // constructor
    public TrackParcelable(String albumName, String name, String imageUrl) {
        this.albumName = albumName;
        this.imageUrl = imageUrl;
        this.name = name;
    }

    // private constructor
    private TrackParcelable(Parcel in) {
        albumName = in.readString();
        imageUrl = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return albumName + " " + name + " " + imageUrl;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        // second argument can be used as a list index when parceling a list object
        // must decode in same order used by encode
        parcel.writeString(albumName);
        parcel.writeString(imageUrl);
        parcel.writeString(name);
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
