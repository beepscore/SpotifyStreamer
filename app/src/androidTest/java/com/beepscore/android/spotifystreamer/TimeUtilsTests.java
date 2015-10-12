package com.beepscore.android.spotifystreamer;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Created by stevebaker on 10/11/15.
 */
public class TimeUtilsTests extends ApplicationTestCase<Application> {

    public TimeUtilsTests() {
        super(Application.class);
    }

    public void testMinutesSecondsStringFromMsecZero() {
        assertEquals("0:00", TimeUtils.minutesSecondsStringFromMsec(0));
    }

    public void testMinutesSecondsStringFromMsec() {
        assertEquals("0:00", TimeUtils.minutesSecondsStringFromMsec(1));
        assertEquals("0:01", TimeUtils.minutesSecondsStringFromMsec(1000));
        assertEquals("1:00", TimeUtils.minutesSecondsStringFromMsec(60000));
        assertEquals("9:00", TimeUtils.minutesSecondsStringFromMsec(540000));
        assertEquals("10:00", TimeUtils.minutesSecondsStringFromMsec(600000));
    }

    public void testGetPercentProgressZero() {
        assertEquals(0, TimeUtils.getPercentProgress(0, 0));
    }

    public void testGetPercentProgressNegative() {
        assertEquals(0, TimeUtils.getPercentProgress(-3, 0));
    }

    public void testGetPercentProgress() {
        assertEquals(80, TimeUtils.getPercentProgress(4, 5));
    }

    public void testGetPercentProgress100() {
        assertEquals(100, TimeUtils.getPercentProgress(5, 5));
    }

    public void testGetPercentProgressGreaterThan100() {
        assertEquals(400, TimeUtils.getPercentProgress(20, 5));
    }
}
