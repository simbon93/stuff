package simone.bonvicini.travalert.travalert.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {

    private static SimpleDateFormat mNotificationFormat = new SimpleDateFormat("dd MMMM", Locale.getDefault());


    public static String formatDate(Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("cccc dd MMMM", Locale.getDefault());

        return date != null ? dateFormatter.format(date) : "";
    }

    public static String formatCompleteDate(Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("cccc dd MMMM HH:mm:ss", Locale.getDefault());

        return date != null ? dateFormatter.format(date) : "";
    }

    public static String formatTime(Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

        return date != null ? dateFormatter.format(date) : "";
    }

    public static String formatTime(Date date, boolean is24h) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat(is24h ? "HH:mm" : "hh:mm", Locale.getDefault());

        return date != null ? dateFormatter.format(date) : "";
    }

    public static String formatHours(Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH", Locale.getDefault());

        return date != null ? dateFormatter.format(date) : "";
    }

    public static String formatHours(Date date, boolean is24h) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat(is24h ? "HH" : "hh", Locale.getDefault());

        return date != null ? dateFormatter.format(date) : "";
    }

    public static String formatMinutes(Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("mm", Locale.getDefault());

        return date != null ? dateFormatter.format(date) : "";
    }

    public static String formatDayOfWeek(Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("cccc", Locale.getDefault());

        return date != null ? dateFormatter.format(date) : "";
    }

    public static String formatMonth(Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM", Locale.getDefault());

        return date != null ? dateFormatter.format(date) : "";
    }

    public static String getNotificationTime(Date date) {

        if (date == null) {
            return "";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (isToday(date)) {

            if (isSameHour(date)) {

                if (isSameMinute(date)) {

                    return computeDeltaSecond(date);
                }

                return computeDeltaMinute(date);
            }

            return computeDeltaHour(date);
        }

        return formatNotification(date);
    }

    private static boolean isToday(Date date) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isSameHour(Date date) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        return isToday(date) && cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY);
    }

    private static boolean isSameMinute(Date date) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        return isSameHour(date) && cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE);
    }

    private static String computeDeltaHour(Date date) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        int delta = cal1.get(Calendar.HOUR_OF_DAY) - cal2.get(Calendar.HOUR_OF_DAY);
        String formattedHours = delta > 1 ? " hours" : " hour";
        return delta + " " + formattedHours + " ago";
    }

    private static String computeDeltaMinute(Date date) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        int delta = cal1.get(Calendar.MINUTE) - cal2.get(Calendar.MINUTE);
        String formattedMins = delta > 1 ? " minutes" : " minute";
        return delta + " " + formattedMins + " ago";
    }

    private static String computeDeltaSecond(Date date) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        int delta = cal1.get(Calendar.SECOND) - cal2.get(Calendar.SECOND);

        if (delta >= 0 && delta <= 2) {
            return "Now";
        }

        if (delta >= 2 && delta <= 10) {
            return "Few seconds ago";
        }

        return delta + " seconds ago";
    }

    public static String formatNotification(Date date) {

        if (date == null)
            return "";

        return mNotificationFormat.format(date);
    }

    public static String formatAmPm(Date date) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("a", Locale.getDefault());

        String result = date != null ? dateFormatter.format(date) : "";

        if (result.contains(".")) {
            return result.replaceAll(".", "").toUpperCase();
        }

        return result;
    }
}
