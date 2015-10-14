package com.beepscore.android.spotifystreamer;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stevebaker on 10/13/15.
 */
public class ListUtilsTests extends ApplicationTestCase<Application> {

    public ListUtilsTests() {
        super(Application.class);
    }

    public void testIndexNextWrapsListNull() {
        assertEquals(null, ListUtils.indexNextWraps(null, 3));
    }

    public void testIndexNextWrapsListEmpty() {
        List list = new ArrayList();

        assertEquals(null, ListUtils.indexNextWraps(list, 0));
    }

    public void testIndexNextWraps() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");

        // cast to avoid error "reference to assertEquals is ambiguous"
        assertEquals((Integer)1, ListUtils.indexNextWraps(list, 0));
        assertEquals((Integer)3, ListUtils.indexNextWraps(list, 2));
    }

    public void testIndexNextWrapsLast() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");

        // cast to avoid error "reference to assertEquals is ambiguous"
        assertEquals((Integer)0, ListUtils.indexNextWraps(list, 3));
    }
}
