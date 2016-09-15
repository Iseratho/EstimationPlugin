package ut.org.catrobat.estimationplugin;

import static org.junit.Assert.*;

import org.catrobat.estimationplugin.helper.DateHelper;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class DateHelperUnitTest {

    @Test
    public void testGetStartOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, 0, 31, 23, 59, 59);
        Date input = calendar.getTime();
        Date output = DateHelper.getStartOfMonth(input);
        calendar.set(2000, 0, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        Date expected = calendar.getTime();
        assertEquals(expected, output);
    }

    @Test
    public void testGetEndOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, 0, 1, 0, 0, 0);
        Date input = calendar.getTime();
        Date output = DateHelper.getEndOfMonth(input);
        calendar.set(2000, 0, 31, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        Date expected = calendar.getTime();
        assertEquals(expected, output);
    }

    @Test
    public void testGetStartOfNextMonth() {
    }

    @Test
    public void testGetEndOfNextMonth() {
    }

    // Test is sometimes faulty
    @Test
    public void testDaysUntil() {
        long testval = 5;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (int)testval);
        assertEquals(testval, DateHelper.daysUntil(cal.getTime()));
    }

    @Test
    public void testGetXUnitsEarlierFromNow() {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = LocalDate.of(2016, 9, 10);
        long days = targetDate.until(today, ChronoUnit.DAYS);
        Date output = DateHelper.getXUnitsEarlierFromNow(days, ChronoUnit.DAYS);
        assertEquals(DateHelper.toDate(targetDate), output);
    }

    @Test
    public void testConvertMillisToDays() {
        long millis = 1000 * 60 * 60 * 24 - 1;
        long output = DateHelper.convertMillisToDays(millis);
        assertEquals(0, output);
        millis = 1000 * 60 * 60 * 24 + 1;
        output = DateHelper.convertMillisToDays(millis);
        assertEquals(1, output);
        millis = 86400000L * 34L;
        output = DateHelper.convertMillisToDays(millis);
        assertEquals(34, output);
    }
}
