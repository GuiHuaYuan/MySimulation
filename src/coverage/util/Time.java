package coverage.util;

//import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Time// implements  Cloneable, Serializable
{
    private GregorianCalendar calendar = null;
    //修正儒略时
    private double mjd;
    //修正儒略星历时间
    private double mjde;

    private final static TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone("UTC"); // default internal timezone

    public Time(String strTime) {

        Pattern p = Pattern.compile("\\d{1,4}");
        Matcher m = p.matcher(strTime);
        List strList = new ArrayList();
        while (m.find()) {
            strList.add(m.group());
        }
        int year = 2000, month = 1, day = 1, hour = 0, minute = 0, second = 0, millsecond = 0;
        if (strList.size() == 6 || strList.size() == 7) {
            year = Integer.parseInt((String) strList.get(0));
            month = Integer.parseInt((String) strList.get(1));
            day = Integer.parseInt((String) strList.get(2));
            hour = Integer.parseInt((String) strList.get(3));
            minute = Integer.parseInt((String) strList.get(4));
            second = Integer.parseInt((String) strList.get(5));
            millsecond = strList.size() == 6 ? 0 : Integer.parseInt((String) strList.get(6));
        } else {
            System.out.println("Wrong Time Format!");
        }
        //TimeZone tz=TimeZone.getTimeZone("UTC");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        GregorianCalendar gc = new GregorianCalendar(tz);
        gc.set(Calendar.YEAR, year);
        gc.set(Calendar.MONTH, month - 1);
        gc.set(Calendar.DATE, day);
        gc.set(Calendar.HOUR_OF_DAY, hour);
        gc.set(Calendar.MINUTE, minute);
        gc.set(Calendar.SECOND, second);
        gc.set(Calendar.MILLISECOND, millsecond);
        long d=gc.getTimeInMillis();
        this.init(gc.getTimeInMillis());
    }

    private void init(long time) {
        this.calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        this.calendar.setTimeInMillis(time);
        updateTimeMeasures();

    }

    public String toGMTTime() {
        String res = String.format("%02d-%02d-%02d %02d:%02d:%02d.%03d", this.calendar.get(Calendar.YEAR), 1 + this.calendar.get(Calendar.MONTH), this.calendar.get(Calendar.DAY_OF_MONTH),
                this.calendar.get(Calendar.HOUR_OF_DAY), this.calendar.get(Calendar.MINUTE), this.calendar.get(Calendar.SECOND), this.calendar.get(Calendar.MILLISECOND));
        return ("GMT:    " + res);
    }

    public String toBJTime() {

        //GregorianCalendar g = new GregorianCalendar(TimeZone.getTimeZone("Asia/Shanghai"));
        GregorianCalendar g = new GregorianCalendar(TimeZone.getTimeZone("Asia/Shanghai"));
        g.setTimeInMillis(this.calendar.getTimeInMillis());
        String res = String.format("%02d-%02d-%02d %02d:%02d:%02d.%03d", g.get(Calendar.YEAR), 1 + g.get(Calendar.MONTH), g.get(Calendar.DAY_OF_MONTH),
                g.get(Calendar.HOUR_OF_DAY), g.get(Calendar.MINUTE), g.get(Calendar.SECOND), g.get(Calendar.MILLISECOND));
        return (res);
    }

    public int getBJ(int field) {
        GregorianCalendar g = new GregorianCalendar(TimeZone.getTimeZone("Asia/Shanghai"));
        g.setTimeInMillis(this.calendar.getTimeInMillis());
        return g.get(field);
    }

    public void setBJ(int field,int value) {
        
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        GregorianCalendar gc = new GregorianCalendar(tz);
        gc.setTimeInMillis(this.calendar.getTimeInMillis());
        gc.set(field,value);
        this.init(gc.getTimeInMillis());
    }

    public int[] getMonthXun() {
        int[] MonthXun = new int[2];
        MonthXun[0] = 1 + getBJ(Calendar.MONTH);
        int days = getBJ(Calendar.DAY_OF_MONTH);
        if (days <= 10) {
            MonthXun[1] = 0;
        } else if (days <= 20) {
            MonthXun[1] = 1;
        } else {
            MonthXun[1] = 2;
        }
        return MonthXun;
    }

    /**
     * 构造函数
     *
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 小时
     * @param min 分钟
     * @param sec 秒钟
     */
    private Time(int year, int month, int day, int hour, int min, double sec) {
        int secInt = new Double(Math.floor(sec)).intValue();
        int millisec = new Double(Math.round((sec - Math.floor(sec)) * 1000.0))
                .intValue();
        this.calendar = new GregorianCalendar(DEFAULT_TIMEZONE);
        this.calendar.set(Calendar.YEAR, year);
        this.calendar.set(Calendar.MONTH, month - 1);
        this.calendar.set(Calendar.DATE, day);
        this.calendar.set(Calendar.HOUR_OF_DAY, hour);
        this.calendar.set(Calendar.MINUTE, min);
        this.calendar.set(Calendar.SECOND, secInt);
        this.calendar.set(Calendar.MILLISECOND, millisec);
        updateTimeMeasures();// update other time formats
    }

    private Time(int year, int month, int day) {
        this.calendar = new GregorianCalendar(DEFAULT_TIMEZONE); // set default timezone
        this.calendar.set(Calendar.YEAR, year);
        this.calendar.set(Calendar.MONTH, month - 1);
        this.calendar.set(Calendar.DATE, day);
        this.calendar.set(Calendar.HOUR_OF_DAY, 0);
        this.calendar.set(Calendar.MINUTE, 0);
        this.calendar.set(Calendar.SECOND, 0);
        this.calendar.set(Calendar.MILLISECOND, 0);
        updateTimeMeasures();// update other time formats
    }

    /**
     * Updates the time to current system time
     */
    public void update2CurrentTime() {

        this.calendar.setTimeInMillis(System.currentTimeMillis());// currentTime =new GregorianCalendar(tz);
        // currentTime.setTime( new Date() );
        updateTimeMeasures();// update other time formats
    }
    


    /**
     * 以毫秒时间设置时间
     *
     * @param milliseconds 从Calendar.getTimeInMillis()开始的毫秒数
     */
    public void set(long milliseconds) {
        this.calendar.setTimeInMillis(milliseconds);
        updateTimeMeasures();
    }

    /**
     * 增加固定单位的时间间隔
     *
     * @param unit 时间单位
     * @param value 时间间隔
     */
    public void add(int unit, int value) {
        this.calendar.add(unit, value);
        updateTimeMeasures();
    }

    /**
     * 增加秒钟
     *
     * @param seconds 增加秒数
     */
    public void addSeconds(double seconds) {
        int millis2Add = new Double(Math.round(seconds * 1000)).intValue();
        this.calendar.add(Calendar.MILLISECOND, millis2Add);
        updateTimeMeasures();
    }

    /**
     * 更新儒略时与儒略星历日期
     */
    private void updateTimeMeasures() {
        mjd = calcMjd(this.calendar);
        mjde = mjd + deltaT(mjd);
    }

    /**
     * 获取儒略时 (UT)
     *
     * @return 返回儒略时
     */
    public double getJulianDate() {
        return mjd + 2400000.5;
    }

    /**
     * 获取修正儒略时 (儒略时减去2400000.5) (UT)
     *
     * @return 返回修正儒略时
     */
    public double getMJD() {
        return mjd;
    }

    /**
     * 获取修正儒略星历时 (儒略时减去2400000.5) (TT)
     *
     * @return 返回修正儒略星历时
     */
    public double getMJDE() {
        return mjde;
    }



    /**
     * Returns the specified field
     *
     * @param field int The specified field
     * @return int The field value
     */
    public final int get(int field) {
        return this.calendar.get(field);
    }

   // ============================== STATIC Functions ====================================
    /**
     * 通过日历计算修正儒略时
     *
     * @param cal 日历
     * @return 修正儒略时 (UT)
     */
    public static double calcMjd(Calendar cal) {
        double sec = cal.get(Calendar.SECOND) + cal.get(Calendar.MILLISECOND) / 1000.0;
        return calcMjd(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), sec);
    }

    /**
     * 通过日历获取的年、月、日、时、分与秒，计算修正儒略时
     *
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 小时
     * @param min 分钟
     * @param sec 秒钟
     * @return 修正儒略时 (UT)
     */
    public static double calcMjd(int year, int month, int day, int hour, int min, double sec) {
        long MjdMidnight;
        double FracOfDay;
        int b;
        if (month <= 2) {
            month += 12;
            --year;
        }
        if ((10000L * year + 100L * month + day) <= 15821004L) {
            b = -2 + ((year + 4716) / 4) - 1179; // Julian calendar
        } else {
            b = (year / 400) - (year / 100) + (year / 4); // Gregorian calendar
        }
        MjdMidnight = 365L * year - 679004L + b + (int) (30.6001 * (month + 1)) + day;
        FracOfDay = (hour + min / 60.0 + sec / 3600.0) / 24.0;
        return MjdMidnight + FracOfDay;
    }

    public static double deltaT(double givenMJD) {
        double theEpoch; /* Julian Epoch */

        double t; /* Time parameter used in the equations. */

        double D; /* The return value. */

        givenMJD -= 50000;

        theEpoch = 2000. + (givenMJD - 1545.) / 365.25;

        /*
         * For 1987 to 2015 we use a graphical linear fit to the annual
         * tabulation from USNO/RAL, 2001, Astronomical Almanach 2003, p.K9. We
         * use this up to 2015 about as far into the future as it is based on
         * data in the past. The result is slightly higher than the predictions
         * from that source.
         */
        if (1987 <= theEpoch && 2015 >= theEpoch) {
            t = (theEpoch - 2002.);
            D = 9.2 * t / 15. + 65.;
            D /= 86400.;
        } /*
         * For 1900 to 1987 we use the equation from Schmadl and Zech as quoted
         * in Meeus, 1991, Astronomical Algorithms, p.74. This is precise within
         * 1.0 second.
         */ else if (1900 <= theEpoch && 1987 > theEpoch) {
            t = (theEpoch - 1900.) / 100.;
            D = -0.212591 * t * t * t * t * t * t * t + 0.677066 * t * t * t
                    * t * t * t - 0.861938 * t * t * t * t * t + 0.553040 * t
                    * t * t * t - 0.181133 * t * t * t + 0.025184 * t * t
                    + 0.000297 * t - 0.000020;
        } /*
         * For 1800 to 1900 we use the equation from Schmadl and Zech as quoted
         * in Meeus, 1991, Astronomical Algorithms, p.74. This is precise within
         * 1.0 second.
         */ else if (1800 <= theEpoch && 1900 > theEpoch) {
            t = (theEpoch - 1900.) / 100.;
            D = 2.043794 * t * t * t * t * t * t * t * t * t * t + 11.636204
                    * t * t * t * t * t * t * t * t * t + 28.316289 * t * t * t
                    * t * t * t * t * t + 38.291999 * t * t * t * t * t * t * t
                    + 31.332267 * t * t * t * t * t * t + 15.845535 * t * t * t
                    * t * t + 4.867575 * t * t * t * t + 0.865736 * t * t * t
                    + 0.083563 * t * t + 0.003844 * t - 0.000009;
        } /*
         * For 948 to 1600 we use the equation from Stephenson and Houlden as
         * quoted in Meeus, 1991, Astronomical Algorithms, p.73.
         */ else if (948 <= theEpoch && 1600 >= theEpoch) {
            t = (theEpoch - 1850.) / 100.;
            D = 22.5 * t * t;
            D /= 86400.;
        } /*
         * Before 948 we use the equation from Stephenson and Houlden as quoted
         * in Meeus, 1991, Astronomical Algorithms, p.73.
         */ else if (948 > theEpoch) {
            t = (theEpoch - 948.) / 100.;
            D = 46.5 * t * t - 405. * t + 1830.;
            D /= 86400.;
        } /*
         * Else (between 1600 and 1800 and after 2010) we use the equation from
         * Morrison and Stephenson, quoted as eqation 9.1 in Meeus, 1991,
         * Astronomical Algorithms, p.73.
         */ else {
            t = theEpoch - 1810.;
            D = 0.00325 * t * t - 15.;
            D /= 86400.;
        }

        return D; // in days
    } // deltaT

    public GregorianCalendar getGregorianCalendar() {
        return this.calendar;
    }

    /**
     * 儒略时转换为 Gerorian日历
     *
     * @param julianTime 儒略时
     * @return
     */
    public static GregorianCalendar convertJD2Calendar(double julianTime) {
        return convertJD2Calendar(julianTime, DEFAULT_TIMEZONE);
    }

    public static GregorianCalendar convertJD2Calendar(double julianTime, TimeZone tz) {
        /**
         * Calculate calendar date for Julian date field this.jd
         */
        Double jd2 = new Double(julianTime + 0.5);
        long I = jd2.longValue();
        double F = jd2.doubleValue() - (double) I;
        long A = 0;
        long B = 0;

        if (I > 2299160) {
            Double a1 = new Double(((double) I - 1867216.25) / 36524.25);
            A = a1.longValue();
            Double a3 = new Double((double) A / 4.0);
            B = I + 1 + A - a3.longValue();
        } else {
            B = I;
        }

        double C = (double) B + 1524;
        Double d1 = new Double((C - 122.1) / 365.25);
        long D = d1.longValue();
        Double e1 = new Double(365.25 * (double) D);
        long E = e1.longValue();
        Double g1 = new Double((double) (C - E) / 30.6001);
        long G = g1.longValue();
        Double h = new Double((double) G * 30.6001);
        long da = (long) C - E - h.longValue();

        Integer date = new Integer((int) da); // DATE

        Integer month;
        Integer year;

        if (G < 14L) {
            month = new Integer((int) (G - 2L));
        } else {
            month = new Integer((int) (G - 14L));
        }

        if (month.intValue() > 1) {
            year = new Integer((int) (D - 4716L));
        } else {
            year = new Integer((int) (D - 4715L));
        }

        // Calculate fractional part as hours, minutes, and seconds
        Double dhr = new Double(24.0 * F);
        Integer hour = new Integer(dhr.intValue());
        Double dmin = new Double(
                (dhr.doubleValue() - (double) dhr.longValue()) * 60.0);
        Integer minute = new Integer(dmin.intValue());

        Double dsec = new Double(
                (dmin.doubleValue() - (double) dmin.longValue()) * 60.0);
        Integer second = new Integer(dsec.intValue());

        // int ms = (int)((dsec.doubleValue() - (double) second.longValue()) *
        // 1000.0);
        // rounding fix - e-mailed to SEG by Hani A. Altwaijry 28 May 2009
        int ms = (int) Math.round((dsec.doubleValue() - (double) second
                .longValue()) * 1000.0);

        // create Calendar object
        GregorianCalendar newTime = new GregorianCalendar(tz); // set default
        newTime.set(Calendar.YEAR, year);
        newTime.set(Calendar.MONTH, month);
        newTime.set(Calendar.DATE, date);
        newTime.set(Calendar.HOUR_OF_DAY, hour);
        newTime.set(Calendar.MINUTE, minute);
        newTime.set(Calendar.SECOND, second);
        newTime.set(Calendar.MILLISECOND, ms);

        return newTime;
    }

    /**
     * 生成输入时间的新实例
     *
     * @param time 输入时间
     * @return
     */
    public Time clone() {
        GregorianCalendar calendar = (GregorianCalendar) this.getGregorianCalendar().clone();
        return new Time(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }


    /**
     * 时间比较
     *
     * @since 2012-03-15
     * @param time 比较时间
     * @return 1，晚于输入时间<br>
     * 0，等于输入时间<br>
     * -1，早于输入时间
     */
    public int compareTo(Time time) {
        return this.getDate().compareTo(time.getDate());
    }

    /**
     * 是否等于输入时间
     *
     * @param time 输入时间
     * @return
     */
    public boolean equal(Time time) {
        return this.getDate().compareTo(time.getDate()) == 0;
    }

    /**
     * 是否早于输入时间
     *
     * @param time 输入时间
     * @return
     */
    public boolean before(Time time) {
        return this.getDate().before(time.getDate());
    }

    /**
     * 是否早于或等于输入时间
     *
     * @param time 输入时间
     * @return
     */
    public boolean beforeOrEqual(Time time) {
        return this.compareTo(time) <= 0 ? true : false;
    }

    /**
     * 是否晚于输入时间
     *
     * @param time 输入时间
     * @return
     */
    public boolean after(Time time) {
        return this.compareTo(time) > 0 ? true : false;
    }

    /**
     * 是否晚于或等于输入时间
     *
     * @param time 输入时间
     * @return
     */
    public boolean afterOrEqual(Time time) {
        return this.compareTo(time) >= 0 ? true : false;
    }

    public int distance(Time time) {
        return (int) (this.getGregorianCalendar().getTimeInMillis() - time.getGregorianCalendar().getTimeInMillis()) / 1000;
    }

    /**
     * 返回Date类型时间
     *
     * @since 2012-03-15
     * @return
     */
    public Date getDate() {
        return this.getGregorianCalendar().getTime();
    }

    public static boolean timeIntersect(double[] t1, double[] t2) {
        if ((t1[0] >= t2[0] && t1[0] <= t2[1]) || (t2[0] >= t1[0] && t2[0] <= t1[1])) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param t1
     * @param t2
     * @param sec
     * @return 如果间隔小于sec，说明相交，返回true，否则返回false
     */
    public static boolean timeInterval(double[] t1, double[] t2, double sec) {
        double juSec = sec / 3600.0;
        if ((t1[0] - t2[0] >= juSec && t1[0] - t2[1] <= juSec) || (t2[0] - t1[0] >= juSec && t2[0] - t1[1] <= juSec)) {
            return true;
        }
        return false;
    }
}
