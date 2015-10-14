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

    public void testIndexNextWrapsListOneElement() {
        List list = new ArrayList();
        list.add("a");

        // cast to avoid error "reference to assertEquals is ambiguous"
        assertEquals((Integer)0, ListUtils.indexNextWraps(list, 0));
    }

    public void testIndexNextWraps() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");

        // cast to avoid error "reference to assertEquals is ambiguous"
        assertEquals((Integer)1, ListUtils.indexNextWraps(list, 0));
        assertEquals((Integer)2, ListUtils.indexNextWraps(list, 1));
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

    public void testIndexPreviousWrapsListNull() {
        assertEquals(null, ListUtils.indexPreviousWraps(null, 3));
    }

    public void testIndexPreviousWrapsListEmpty() {
        List list = new ArrayList();
        assertEquals(null, ListUtils.indexPreviousWraps(list, 0));
    }

    public void testIndexPreviousWrapsListOneElement() {
        List list = new ArrayList();
        list.add("a");

        // cast to avoid error "reference to assertEquals is ambiguous"
        assertEquals((Integer)0, ListUtils.indexPreviousWraps(list, 0));
    }

    public void testIndexPreviousWraps() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");

        // cast to avoid error "reference to assertEquals is ambiguous"
        assertEquals((Integer)0, ListUtils.indexPreviousWraps(list, 1));
        assertEquals((Integer)1, ListUtils.indexPreviousWraps(list, 2));
        assertEquals((Integer)2, ListUtils.indexPreviousWraps(list, 3));
    }

    public void testIndexPreviousWrapsFirst() {
        List list = new ArrayList();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");

        // cast to avoid error "reference to assertEquals is ambiguous"
        assertEquals((Integer)3, ListUtils.indexPreviousWraps(list, 0));
    }
}
