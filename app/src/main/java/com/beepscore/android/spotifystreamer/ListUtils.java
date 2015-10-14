package com.beepscore.android.spotifystreamer;


import java.util.List;

/**
 * Created by stevebaker on 10/13/15.
 */
public class ListUtils {

    /**
     * @param list A list, treated as if the end wraps around to the start
     * @param index
     * @return next index
     * return null if list is null or empty
     * return 0 if index is last element
     */
    public static Integer indexNextWraps(List list, int index) {
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        if (index == (list.size() - 1)) {
            // at last element. Wrap ahead to first element
            return 0;
        }
        return index + 1;
    }

}
