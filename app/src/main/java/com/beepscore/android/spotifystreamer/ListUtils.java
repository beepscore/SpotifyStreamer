package com.beepscore.android.spotifystreamer;


import java.util.List;

/**
 * Created by stevebaker on 10/13/15.
 */
public class ListUtils {

    /**
     * @param list A List (e.g. an ArrayList), treated as if the end wraps forward to the start
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
            // at last element. Wrap forward to first element
            return 0;
        }
        return index + 1;
    }

    /**
     * @param list A List (e.g. an ArrayList), treated as if the start wraps backward to the end
     * @param index
     * @return previous index
     * return null if list is null or empty
     * return last element if index is 0
     */
    public static Integer indexPreviousWraps(List list, int index) {
        if ((list == null) || (list.size() == 0)) {
            return null;
        }
        if (index == 0) {
            // at first element. Wrap backward to last element
            return (list.size() - 1);
        }
        return index - 1;
    }
}
