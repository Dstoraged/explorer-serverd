package com.imooc.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateUtil {
    private static final String[] PARSEPATTERNS = new String[] {
            "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss",
            "yyyy.MM.dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm",
            "yyyy.MM.dd HH:mm", "yyyy-MM-dd HH", "yyyy/MM/dd HH",
            "yyyy.MM.dd HH", "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd" };
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static int getYear(){
        Calendar cal =Calendar.getInstance();
        int year =cal.get(Calendar.YEAR);
        return year;
    }


    public static int getFirstBlock(long timeStamp){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeStamp);
        String dateTime=sdf.format(date);
        String year =dateTime.substring(0,4);
        int first=Integer.parseInt(year);
        return first;
    }










    /**
     *
     * @param date
     * @return
     */
    public static Date stringToDate(String date) {
        try {
            return DateUtils.parseDate(date, PARSEPATTERNS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param date
     * @param parsePatterns
     * @return
     */
    public static Date stringToDate(String date, String... parsePatterns) {
        try {
            return DateUtils.parseDate(date, parsePatterns);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String dateToString(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        }
        return "";
    }

    /**
     *  yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date) {
        return dateToString(date, PATTERN);
    }

    /**
     *
     * @param date
     * @param n
     * @return
     */
    public static Date addDay(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, n);
        return calendar.getTime();
    }

    /**
     *
     * @param date
     * @param n
     * @return
     */
    public static Date addMonth(Date date, int n) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, n);
        return calendar.getTime();
    }

    /**
     *
     * @return
     */
    public static Date firstDayOfMonth() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }


    /**
     *
     * @return
     */
    public final static Date addMinToDate(Date date, int min) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, min);
        return c.getTime();
    }

    /**
     *
     * @return
     */
    public final static Date addDaysToDate(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    /**
     *
     * @return
     */
    public final static Date addMonthsToDate(Date date, int months) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }
    public final static int compareDate(Date date1,Date date2,int type){
        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sd2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c1=Calendar.getInstance();
        Calendar c2=Calendar.getInstance();
        Date newDate1= null;
        Date newDate2= null;
        try {
            if(type==0){
                newDate1 = sd1.parse(sd1.format(date1));
                newDate2 = sd1.parse(sd1.format(date2));
            }else if(type==1){
                newDate1 = sd2.parse(sd2.format(date1));
                newDate2 = sd2.parse(sd2.format(date2));
            }else{
                newDate1 = date1;
                newDate2 = date2;
            }
            c1.setTime(newDate1);
            c2.setTime(newDate2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c1.compareTo(c2);
    }
    public final static String simpleMapToJsonStr(Map<String ,String > map){
        if(map==null||map.isEmpty()){
            return "null";
        }
        String jsonStr = "{";
        Set<?> keySet = map.keySet();
        for (Object key : keySet) {
            jsonStr += "\""+key+"\":\""+map.get(key)+"\",";
        }
        jsonStr = jsonStr.substring(1,jsonStr.length()-2);
        jsonStr += "}";
        return jsonStr;
    }
    /**
     * @return
     */
    public final static int getDaysBetweenDate(String date1, String date2) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        try {
            d1 = sd.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date d2 = null;
        try {
            d2 = sd.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c1 = Calendar.getInstance();

        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        long diff = (c2.getTimeInMillis() - c1.getTimeInMillis())
                / (1000 * 60 * 60 * 24);
        return ((Long) diff).intValue();
    }

    /**
     *
     * @return
     */
    public final static Integer getDaysBetweenDate(Date date1, Date date2) {
        Date d1 = date1;
        Date d2 = date2;
        Calendar c1 = Calendar.getInstance();

        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        long diff = (c2.getTimeInMillis() - c1.getTimeInMillis())
                / (1000 * 60 * 60 * 24);
        return ((Long) diff).intValue();
    }

    /**
     */
    public final static Date parseDate(String dateString, String dateFormate) {
        SimpleDateFormat sd = new SimpleDateFormat(dateFormate);
        Date date = null;
        try {
            date = sd.parse(dateString);
        } catch (Exception ex) {

        }
        return date;
    }

    /**
     *
     * @return
     */
    public static Map<Integer, Integer> getMonthAndDaysBetweenDate(
            String date1, String date2) {
        Map<Integer, Integer> map = new HashMap();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        try {
            d1 = sd.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date d2 = null;
        try {
            d2 = sd.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int months = 0;
        int days = 0;
        int y1 = d1.getYear();
        int y2 = d2.getYear();
        int dm1 = d2.getMonth();
        int dm2 = d2.getMonth();
        int dd1 = d1.getDate();
        int dd2 = d2.getDate();
        if (d1.getTime() < d2.getTime()) {
            months = d2.getMonth() - d1.getMonth() + (y2 - y1) * 12;
            if (dd2 < dd1) {
                months = months - 1;
            }
            days = getDaysBetweenDate(
                    getFormatDateTime(
                            addMonthsToDate(
                                    DateUtil.parseDate(date1, "yyyy-MM-dd"),
                                    months), "yyyy-MM-dd"), date2);
            map.put(1, months);
            map.put(2, days);
        }
        return map;
    }

    /**
     * @param dateFormat
     * @return
     */
    public final static String getFormatDateTime(Date date, String dateFormat) {

        SimpleDateFormat sf = null;
        try {
            sf = new SimpleDateFormat(dateFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return sf.format(date);
    }


    /**
     * @param dateFormat
     * @return
     */
    public final static String getFormatDateTime(String date, String dateFormat) {

        SimpleDateFormat sf = null;
        try {
            sf = new SimpleDateFormat(dateFormat);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        return sf.format(date);
    }


    public static String getNewDate() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);
        return time;
    }


    public static Date dateAddMinute(Date date,int minute){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }
    public static Date dateAddSecond(Date date,int second){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND,second);
        return cal.getTime();
    }

    public static int compareDateMinute(Date date1,Date date2){
        Calendar dateOne=Calendar.getInstance();
        dateOne.setTime(date1);	//
        Calendar dateTwo=Calendar.getInstance();
        dateTwo.setTime(date2);	//
        long timeOne=dateOne.getTimeInMillis();
        long timeTwo=dateTwo.getTimeInMillis();
        long minute=(timeOne-timeTwo)/(1000*60);//
        return Long.valueOf(minute).intValue();
    }

    public static int compareDateMinuteSpace(Date date1,Date date2,int space){
        Calendar dateOne=Calendar.getInstance();
        dateOne.setTime(date1);	//
        Calendar dateTwo=Calendar.getInstance();
        dateTwo.setTime(date2);	//
        long timeOne=dateOne.getTimeInMillis();
        long timeTwo=dateTwo.getTimeInMillis();
        long minute=(timeOne-timeTwo)/(1000*60*space);//minute
        return Long.valueOf(minute).intValue();
    }

    public static Map<String,List<String[]>> GetTimeInterval(Date startDate,Date endDate){
        Map<String,List<String[]>>  map = new HashMap<String, List<String[]>>();

        long startTime = startDate.getTime();
        long endTime = endDate.getTime();

        List<String[]> min1List = new ArrayList<String[]>();
        long tempTime = startTime;
        while (tempTime< endTime) {
            String[] arr = new String[2];
            arr[0] = dateToString(new Date(tempTime), "yyyy-MM-dd HH:mm:ss");
            tempTime = addMinToDate(new Date(tempTime), 1).getTime();
            arr[1] = dateToString(new Date(tempTime), "yyyy-MM-dd HH:mm:ss");
            min1List.add(arr);
        }
        map.put("1min", min1List);

        List<String[]> min5List = new ArrayList<String[]>();
        long tempTime5 = startTime;
        while (tempTime5< endTime) {
            String[] arr = new String[2];
            arr[0] = dateToString(new Date(tempTime5), "yyyy-MM-dd HH:mm:ss");
            tempTime5 = addMinToDate(new Date(tempTime5), 5).getTime();
            arr[1] = dateToString(new Date(tempTime5), "yyyy-MM-dd HH:mm:ss");
            min5List.add(arr);
        }
        map.put("5min", min5List);

        List<String[]> min15List = new ArrayList<String[]>();
        long tempTime15 = startTime;
        while (tempTime15< endTime) {
            String[] arr = new String[2];
            arr[0] = dateToString(new Date(tempTime15), "yyyy-MM-dd HH:mm:ss");
            tempTime15 = addMinToDate(new Date(tempTime15), 15).getTime();
            arr[1] = dateToString(new Date(tempTime15), "yyyy-MM-dd HH:mm:ss");
            min15List.add(arr);
        }
        map.put("15min", min15List);

        List<String[]> min30List = new ArrayList<String[]>();
        long tempTime30 = startTime;
        while (tempTime30< endTime) {
            String[] arr = new String[2];
            arr[0] = dateToString(new Date(tempTime30), "yyyy-MM-dd HH:mm:ss");
            tempTime30 = addMinToDate(new Date(tempTime30), 30).getTime();
            arr[1] = dateToString(new Date(tempTime30), "yyyy-MM-dd HH:mm:ss");
            min30List.add(arr);
        }
        map.put("30min", min30List);

        List<String[]> min60List = new ArrayList<String[]>();
        long tempTime60 = startTime;
        while (tempTime60< endTime) {
            String[] arr = new String[2];
            arr[0] = dateToString(new Date(tempTime60), "yyyy-MM-dd HH:mm:ss");
            tempTime60 = addMinToDate(new Date(tempTime60), 60).getTime();
            arr[1] = dateToString(new Date(tempTime60), "yyyy-MM-dd HH:mm:ss");
            min60List.add(arr);
        }
        map.put("60min", min60List);


        return map;
    }

    public static Map<String,Date> getPeriodDate(Date date){
        Map<String,Date> map = new HashMap<String, Date>();
        //"1min","5min","15min","30min","60min","1day","1week","1mon"
        map.put("1min", stringToDate(getFormatDateTime(date, "yyyy-MM-dd HH:mm")));
        map.put("5min", getPeriodMin(date,5));
        map.put("15min", getPeriodMin(date,15));
        map.put("30min", getPeriodMin(date,30));
        map.put("60min", stringToDate(getFormatDateTime(date, "yyyy-MM-dd HH")));
        map.put("1day", stringToDate(getFormatDateTime(date, "yyyy-MM-dd")));
        Calendar cweek = Calendar.getInstance(Locale.CHINA);
        cweek.setTime(date);
        cweek.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        cweek.set(Calendar.HOUR_OF_DAY, 0);
        cweek.set(Calendar.MINUTE, 0);
        cweek.set(Calendar.SECOND, 0);
        map.put("1week", cweek.getTime());
        Calendar cmon = Calendar.getInstance(Locale.CHINA);
        cmon.setTime(date);
        cmon.set(Calendar.DAY_OF_MONTH, 1);
        cmon.set(Calendar.HOUR_OF_DAY, 0);
        cmon.set(Calendar.MINUTE, 0);
        cweek.set(Calendar.SECOND, 0);
        map.put("1mon", cmon.getTime());
        return map;
    }

    public static Map<String,Date> getPeriodDate2(Date date){
        Map<String,Date> map = new HashMap<String, Date>();
        map.put("1min", stringToDate(getFormatDateTime(date, "yyyy-MM-dd HH:mm")));
        map.put("5min", getPeriodMin(date,5));
        map.put("15min", getPeriodMin(date,15));
        map.put("30min", getPeriodMin(date,30));
        map.put("60min", stringToDate(getFormatDateTime(date, "yyyy-MM-dd HH")));
        map.put("1day", stringToDate(getFormatDateTime(date, "yyyy-MM-dd")));
        Calendar cweek = Calendar.getInstance(Locale.CHINA);
        cweek.setTime(date);
        cweek.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        cweek.set(Calendar.HOUR_OF_DAY, 0);
        cweek.set(Calendar.MINUTE, 0);
        cweek.set(Calendar.SECOND, 0);
        map.put("1week", cweek.getTime());
        Calendar cmon = Calendar.getInstance(Locale.CHINA);
        cmon.setTime(date);
        cmon.set(Calendar.DAY_OF_MONTH, 1);
        cmon.set(Calendar.HOUR_OF_DAY, 0);
        cmon.set(Calendar.MINUTE, 0);
        cmon.set(Calendar.SECOND, 0);
        map.put("1mon", cmon.getTime());
        return map;
    }

    public static Date getPeriodMin(Date date , int index){
        int num =  Integer.valueOf(getFormatDateTime(date, "mm"))/index;
        if(num == 0 ){
            return stringToDate(getFormatDateTime(date, "yyyy-MM-dd HH"));
        }else{
            return stringToDate(getFormatDateTime(date, "yyyy-MM-dd HH")+":"+index*num, "yyyy-MM-dd HH:mm");
        }
    }



    /**
     */
    public static int compare_date(Date dt1, Date dt2) {


        try {

            if (dt1.getTime() > dt2.getTime()) {
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {

                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static String  conversion(String date , String format){

        Date d=null;
        try {
            date=date.split("GMT")[0];
            d = new SimpleDateFormat("E MMM dd yyyy HH:mm:ss z",Locale.US).parse(date+"GMT+08:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
        String  dd=sf2.format(d);
        return dd;

    }


    public static String  formatByUS(String str) {
        Date date;
        try {
            date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US).parse(str);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dd = format.format(date);
            return dd;

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";

    }

    public static String stampToDate(long s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       // long lt = new Long(s);
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }



//	@Test
//	public void test1(){
//
//		System.out.println(getDaysBetweenDate(stringToDate("2017-04-22"),new Date()));
//
//	}

    /**
     */
    public static String getTimeLast30(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE,-30);
        Date d =c.getTime();
        String s =format.format(d);
        return s ;
    }


    public static String getTimeLast7(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE,-7);
        Date d =c.getTime();
        String s =format.format(d);
        return s ;
    }



    public static String getLastAmonthDay(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }
    public static Integer  getBetweenYear(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date now=new Date();
        String today=sdf.format(now);
        Integer int_today=Integer.valueOf(today);
        Integer int_date=Integer.valueOf(date);
        return (int_today-int_date);
    }

    public static String getFormatNowDate(){
        SimpleDateFormat sim=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newDate = sim.format(new Date().getTime());
        return newDate ;

    }

    public static String getDate() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        String time = format.format(date);
        return time;

    }

    public static String getChannelFormatDate() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = format.format(date);
        return time;
    }

    public static String getDatetime(int oddtime,String endTime) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, oddtime);
        date = calendar.getTime();
        return sdf.format(date);

    }
    public static  String getNowDatetime() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        return sdf.format(date);
    }

    /**
     *
     * @return
     */
    public static String getNowDate(){
        SimpleDateFormat sim=new SimpleDateFormat("yyyy-MM-dd");
        String newDate = sim.format(new Date().getTime());
        return newDate ;
    }

    /**
     *
     * @param date
     * @return
     */
    public static String StringDateToString(String date){
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date2 = sim.parse(date);
            String format = sim.format(date2);
            return format;
        } catch (ParseException e) {
            e.printStackTrace();
            return getNowDate();
        }
    }


    public static Date getDayBegin() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getDayEnd() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    public static Date getBeginDayOfYesterday() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    public static Date getEndDayOfYesterDay() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    public static Date getBeginDayOfTomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayBegin());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getEndDayOfTomorrow() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(getDayEnd());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }

    public static Date getBeginDayOfWeek(Date date) {
		/*Date date = new Date();
		if (date == null) {
			return null;
		}*/
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }



    public static String getBeginDayOfWeek(Date date,String formatPattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatPattern);
		/*Date date = new Date();
		if (date == null) {
			return null;
		}*/
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return simpleDateFormat.format(getDayStartTime(cal.getTime()));
    }

    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }


    //

    public static String getEndDayOfWeek(Date date,String formatPattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatPattern);
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek(date));
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return simpleDateFormat.format(getDayEndTime(weekEndSta));
    }

    public static Date getBeginDayOfLastWeek() {
        Date date = new Date();
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek - 7);
        return getDayStartTime(cal.getTime());
    }

    public static Date getEndDayOfLastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfLastWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    public static Date getBeginDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 2, 1);
        return getDayStartTime(calendar.getTime());
    }

    public static Date getEndDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 2, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 2, day);
        return getDayEndTime(calendar.getTime());
    }

    public static Date getBeginDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DATE, 1);
        return getDayStartTime(cal.getTime());
    }

    public static Date getEndDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, getNowYear());
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DATE, 31);
        return getDayEndTime(cal.getTime());
    }

    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d)
            calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    public static int getDiffDays(Date beginDate, Date endDate) {
        if (beginDate == null || endDate == null) {
            throw new IllegalArgumentException("getDiffDays param is null!");
        }
        long diff = (endDate.getTime() - beginDate.getTime())
                / (1000 * 60 * 60 * 24);
        int days = new Long(diff).intValue();
        return days;
    }

    public static long dateDiff(Date beginDate, Date endDate) {
        long date1ms = beginDate.getTime();
        long date2ms = endDate.getTime();
        return date2ms - date1ms;
    }

    public static Date max(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        }
        if (endDate == null) {
            return beginDate;
        }
        if (beginDate.after(endDate)) {
            return beginDate;
        }
        return endDate;
    }

    public static Date min(Date beginDate, Date endDate) {
        if (beginDate == null) {
            return endDate;
        }
        if (endDate == null) {
            return beginDate;
        }
        if (beginDate.after(endDate)) {
            return endDate;
        }
        return beginDate;
    }

    public static Date getFirstSeasonDate(Date date) {
        final int[] SEASON = { 1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4 };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int sean = SEASON[cal.get(Calendar.MONTH)];
        cal.set(Calendar.MONTH, sean * 3 - 3);
        return cal.getTime();
    }

    public static Date getNextDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i);
        return cal.getTime();
    }

    public static Date getFrontDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - i);
        return cal.getTime();
    }

    public static List getTimeList(int beginYear, int beginMonth, int endYear,
                                   int endMonth, int k) {
        List list = new ArrayList();
        if (beginYear == endYear) {
            for (int j = beginMonth; j <= endMonth; j++) {
                list.add(getTimeList(beginYear, j, k));
            }
        } else {
            {
                for (int j = beginMonth; j < 12; j++) {
                    list.add(getTimeList(beginYear, j, k));
                }
                for (int i = beginYear + 1; i < endYear; i++) {
                    for (int j = 0; j < 12; j++) {
                        list.add(getTimeList(i, j, k));
                    }
                }
                for (int j = 0; j <= endMonth; j++) {
                    list.add(getTimeList(endYear, j, k));
                }
            }
        }
        return list;
    }

    public static List getTimeList(int beginYear, int beginMonth, int k) {
        List list = new ArrayList();
        Calendar begincal = new GregorianCalendar(beginYear, beginMonth, 1);
        int max = begincal.getActualMaximum(Calendar.DATE);
        for (int i = 1; i < max; i = i + k) {
            list.add(begincal.getTime());
            begincal.add(Calendar.DATE, k);
        }
        begincal = new GregorianCalendar(beginYear, beginMonth, max);
        list.add(begincal.getTime());
        return list;
    }

    public static int getDayOfWeek(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        int i = instance.get(Calendar.DAY_OF_WEEK)-1;
        return i;
    }

    public static int getWeek(String str) throws Exception{
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date date =sdf.parse(str);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return week;
    }

    public static String getBeginDayOfLastYear(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        int year = c.get(Calendar.YEAR);
        String start =year +"-01-01 00:00:00";
        return start;
    }

    public static  List<Long> get24HoursKey(Date date){
        try{
            long ms = 3600*1000;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
            List<Long> list = new ArrayList<>();
            for (int i=0;i<24;i++){
                long l = i * ms;
                Long key = format.parse(format.format(date)).getTime() -l;
                list.add(key);
            }
            return list;
        }catch (Exception e){

        }
        return null;
    }

    public static void main(String[] args) {
        Date date = DateUtil.addDaysToDate(new Date(), -1);
        String formatDateTime = DateUtil.getFormatDateTime(date, "yyyy-MM-dd");
        System.out.println(formatDateTime);
        Date d = DateUtil.parseDate(formatDateTime, "yyyy-MM-dd");
        Date today = DateUtil.getDayBegin();
        System.out.println(today);
        System.out.println(String.valueOf(getBeginDayOfYesterday().getTime()));
        System.out.println(addDaysToDate(new Date(),-1));

    }

}
