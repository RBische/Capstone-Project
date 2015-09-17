package fr.bischof.raphael.gothiite.dateformat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Classic date format
 * Created by biche on 17/09/2015.
 */
public class DateToShowFormat extends SimpleDateFormat {
    public DateToShowFormat() {
        super("dd/MM/yyyy");
    }

    public boolean isDateToday(String date) throws ParseException {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        // and get that as a Date
        Date today = c.getTime();

        // test your condition
        Date d = this.parse(date);
        return !d.before(today);
    }
}