package be.doji.productivity.trambucore.model.tracker;

import be.doji.productivity.trambucore.TrackMeConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeLogTest {

    @Test public void testToStringFull() {
        TimeLog timelog = new TimeLog();
        Calendar startDate = new GregorianCalendar(2017, Calendar.DECEMBER, 18, 18, 00, 00);
        Calendar endDate = new GregorianCalendar(2017, Calendar.DECEMBER, 18, 20, 00, 00);
        timelog.setStartTime(startDate.getTime());
        timelog.setEndTime(endDate.getTime());
        String stringifiedLog = timelog.toString();
        Assert.assertNotNull(stringifiedLog);
        Assert.assertTrue(StringUtils.contains(stringifiedLog,
                TrackMeConstants.INDICATOR_LOGPOINT_START + TrackMeConstants.getDateFormat()
                        .format(startDate.getTime())));
        Assert.assertTrue(StringUtils.contains(stringifiedLog,
                TrackMeConstants.INDICATOR_LOGPOINT_END + TrackMeConstants.getDateFormat()
                        .format(endDate.getTime())));
    }

    @Test public void testToStringNoEndtime() {
        TimeLog timelog = new TimeLog();
        Calendar startDate = new GregorianCalendar(2017, Calendar.DECEMBER, 18, 18, 00, 00);
        timelog.setStartTime(startDate.getTime());
        String stringifiedLog = timelog.toString();
        Assert.assertNotNull(stringifiedLog);
        Assert.assertTrue(StringUtils.contains(stringifiedLog,
                TrackMeConstants.INDICATOR_LOGPOINT_START + TrackMeConstants.getDateFormat()
                        .format(startDate.getTime())));
        Assert.assertFalse(StringUtils.contains(stringifiedLog,
                TrackMeConstants.INDICATOR_LOGPOINT_END));
    }
}
