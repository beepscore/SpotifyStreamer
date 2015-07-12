package com.beepscore.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class ArtistParcelable is similar to Artist, but is Parcelable.
 * It has a subset of Artist properties.
 * Can't have a property that is type Artist??
 * https://developer.android.com/reference/android/os/Parcelable.html
 * Created by stevebaker on 7/11/15.
 */
public class ArtistParcelable implements Parcelable {

    String id;
    String imageUrl;
    String name;

    // constructor
    public ArtistParcelable(String id, String name, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
    }

    // private constructor
    private ArtistParcelable(Parcel in) {
        id = in.readString();
        imageUrl = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return id + " " + name + " " + imageUrl;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        // second argument can be used as a list index when parceling a list object
        // must decode in same order used by encode
        parcel.writeString(id);
        parcel.writeString(imageUrl);
        parcel.writeString(name);
    }

    public static final Parcelable.Creator<ArtistParcelable> CREATOR =
            new Parcelable.Creator<ArtistParcelable>() {

                @Override
                public ArtistParcelable createFromParcel(Parcel parcel) {
                    return new ArtistParcelable(parcel);
                }

                @Override
                public ArtistParcelable[] newArray(int i) {
                    return new ArtistParcelable[i];
                }
            };

}
