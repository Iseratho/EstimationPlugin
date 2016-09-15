package org.catrobat.estimationplugin.helper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DateHelper {

    public static Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    public static Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    public static Date getStartOfNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        return getStartOfMonth(calendar.getTime());
    }

    public static Date getEndOfNextMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        return getEndOfMonth(calendar.getTime());
    }

    public static List<Date> convertLongCollectionToDateList(Collection<Long> collection) {
        List<Date> dateList = new ArrayList<>();
        for(Long a : collection) {
            dateList.add(new Date(a));
        }
        return dateList;
    }

    public static long daysUntil(Date targetDate) {
        long millis = targetDate.getTime() - (new Date()).getTime();
        return convertMillisToDays(millis);
    }

    public static long convertMillisToDays(long millis) {
        return millis/(1000 * 60 * 60 * 24);
    }

    /* Source from: http://stackoverflow.com/questions/1555262/calculating-the-difference-between-two-java-date-instances */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static Date getXUnitsEarlierFromNow(long amountToSubstract, TemporalUnit temporalUnit) {
        LocalDate localDate = LocalDate.now();
        localDate = localDate.minus(amountToSubstract, temporalUnit);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
