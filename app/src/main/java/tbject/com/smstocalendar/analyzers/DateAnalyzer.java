package tbject.com.smstocalendar.analyzers;


import android.content.Context;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SmsEvent;

public class DateAnalyzer {

    private Context context;

    public DateAnalyzer(Context context){
        this.context=context;
    }

    public SmsEvent findDateAndTime(SmsEvent smsEvent){
        String desc=smsEvent.getDescription();
        List<String> timesRegax= Arrays.asList(context.getResources().getStringArray(R.array.time_regax));

        ArrayList<String> dates=new ArrayList<>();
        ArrayList<String> times=new ArrayList<>();
        String re1=".*?";
        try {
            dates=findDateByString(desc);
            if (dates.size()==0)
                dates=findDateByRegax(desc);
            if (dates.size()==0)
                dates=findDateByMonthHebrew(desc);
            if (dates.size()==0){
                dates=findDateByHebrewDays(desc);
            }
            times=findTimeByRegax(desc);
            if (times.size()==0)
                times=findTimeByString(desc);

            if (dates.size()==0){
                Log.d("DEBUG","Cannot find date with any dateLogics");
            }
            else if( dates.size()==1 && times.size()==0){
                Date eventDate = getDateFromString(dates.get(0),"08:00");
                smsEvent.setDate(eventDate);
                smsEvent.setDateEnd(new Date(eventDate.getTime()+1000*60*60));
                Log.d("DEBUG","Cannot find time with any timeLogics");
            }
            else if (dates.size()==1 && times.size()==1){
                Date eventDate = getDateFromString(dates.get(0),times.get(0));
                smsEvent.setDate(eventDate);
                smsEvent.setDateEnd(new Date(eventDate.getTime()+1000*60*60));
            }
            else if (dates.size()==1 && times.size()==2){
                Date firstDate = getDateFromString(dates.get(0),times.get(0));
                Date secondDate=getDateFromString(dates.get(0),times.get(1));
                if (firstDate.getTime()<secondDate.getTime()) {
                    smsEvent.setDate(firstDate);
                    smsEvent.setDateEnd(secondDate);
                }else{
                    smsEvent.setDate(secondDate);
                    smsEvent.setDateEnd(firstDate);
                }
            }else{
                Log.d("DEBUG","Do not handle find: "+dates.size()+" dates and "+times.size()+" times");
                return null;
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.d("DEBUG","findDate method fail. return false");
        }
        return smsEvent;

    }

    private ArrayList<String> findDateByMonthHebrew(String body){
        ArrayList<String> dates=new ArrayList<>();
        List<String> datesMonthHebrew=  Arrays.asList(context.getResources().getStringArray(R.array.date_month_hebrew));
        int counter=1;
        for (String dateHebrew:datesMonthHebrew){
            int startIndex=body.lastIndexOf(dateHebrew)-dateHebrew.length();
            if (startIndex>0) {
                String dateValue=body.substring(startIndex-5,startIndex+2);
                String intValue = dateValue.replaceAll("[^0-9]", "");
                int date=Integer.parseInt(intValue);
                DecimalFormat formatter = new DecimalFormat("00");
                String dayString = formatter.format(date);
                String mounthString=formatter.format(counter);
                SimpleDateFormat df = new SimpleDateFormat("yyyy");
                String yearString=df.format(new Date());
                dates.add(dayString+"/"+mounthString+"/"+yearString);
                break;
            }
            counter++;
        }
        return dates;
    }
    private ArrayList<String> findDateByHebrewDays(String body){
        ArrayList<String> dates=new ArrayList<>();
        List<String> days=  Arrays.asList(context.getResources().getStringArray(R.array.days_hebrew));
        int counter=1;
        for(String day:days){
            if (body.contains(day)){
                if (body.contains("הקרוב")|| body.contains("הבא")){
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTime(new Date());
                    int c=calendar.get(Calendar.DAY_OF_WEEK);
                    while ( c!= counter) {
                        calendar.add(Calendar.DATE, 1);
                        c=calendar.get(Calendar.DAY_OF_WEEK);
                    }
                    SimpleDateFormat f=new SimpleDateFormat("dd/MM/yy");
                    dates.add(f.format(calendar.getTime()));
                    break;
                }
            }
            counter++;
        }
        return dates;
    }
    private ArrayList<String> findDateByString(String body){
        ArrayList<String> dates=new ArrayList<>();
        List<String> datesHebrew=  Arrays.asList(context.getResources().getStringArray(R.array.date_hebrew));
        int counter=0;
        for (String dateHebrew:datesHebrew){
            if (body.contains(dateHebrew)) {
                dates.add(createDateAfterDays(counter));
                break;
            }
            counter++;
        }
        return dates;

    }
    private ArrayList<String> findTimeByString(String body){
        ArrayList<String> times=new ArrayList<>();
        List<String> timesHebrew=  Arrays.asList(context.getResources().getStringArray(R.array.time_hebrew_evning));
        for (String timeHebrew:timesHebrew){
            if (body.contains(timeHebrew)) {
                times.add("20:00");
                break;
            }
        }
        if (body.contains("שעות")){
            Pattern p = Pattern.compile("(\\d+)(-)(\\d+)",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher m = p.matcher(body);
            if (m.find()) {
                String hours = m.group();
                String[] hoursSplit = hours.split("-");
                if (hoursSplit[0].length() == 1)
                    hoursSplit[0] = "0" + hoursSplit[0];
                if (hoursSplit[1].length() == 1)
                    hoursSplit[1] = "0" + hoursSplit[1];
                times.add(hoursSplit[0] + ":" + hoursSplit[1]);
            }
            }
        return times;

    }

    private ArrayList<String> findDateByRegax(String body){
        List<String> datesRegax= Arrays.asList(context.getResources().getStringArray(R.array.date_regax));
        ArrayList<String> dates=new ArrayList<>();
        for (String dateRegax:datesRegax){
            Pattern p = Pattern.compile(dateRegax,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher m = p.matcher(body);
            while (m.find()){
                dates.add(m.group());
            }
            if (dates.size()>=1)
                break;

        }
        return dates;
    }

    private ArrayList<String> findTimeByRegax(String body){
        List<String> timesRegax= Arrays.asList(context.getResources().getStringArray(R.array.time_regax));
        ArrayList<String> times=new ArrayList<>();
        for (String timeRegax:timesRegax){
            Pattern p = Pattern.compile(timeRegax,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher m = p.matcher(body);
            while (m.find()){
                times.add(m.group());
            }
            if (times.size()>=1)
                break;

        }
        return times;
    }

    private Date getDateFromString(String date, String time) throws Exception {
        Date dateReturn;
        String dateFormat="";
        String timeFormat="";
        //handle dates
        //dd/MM/yyyy dd.MM/yyyy
        if (date.length()==10){
            String separator=date.substring(2,3);
            dateFormat="dd"+separator+"MM"+separator+"yyyy";
        }
        //d/M/yy d.MM.yy dd-MM-yy
        else if (date.length()<=8 && date.length()>=6){
            String separator= date.replaceAll("[0-9]","").substring(0,1);
            if (separator.equals("."))
                separator="\\.";
            String dateSplit[]=date.split(separator);

            if (dateSplit[0].length()==1)
                dateSplit[0]="0"+dateSplit[0];
            if (dateSplit[1].length()==1)
                dateSplit[1]="0"+dateSplit[1];
            date=dateSplit[0]+"/"+dateSplit[1]+"/"+dateSplit[2];
            dateFormat="dd/MM/yy";

        }
        //dd-MM dd:MM
        else if (date.length()<=5 && date.length()>=3){
            String separator= date.replaceAll("[0-9]","").substring(0,1);
            dateFormat="dd"+separator+"MM";
        }

        //HH-mm HH:mm -support all time separator
        if (time.length()==5 || time.length()==4){
            String separator=time.replaceAll("[0-9]","");
            timeFormat="HH"+separator+"mm";
        }
        else if (time.length()==8){
            String separator=time.replaceAll("[0-9]","").substring(0,1);
            timeFormat="HH"+separator+"mm"+separator+"ss";
        }else if(time.length()==3){

        }

        try {
            //convert format to date
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat+" "+timeFormat);
            Date returnDate= formatter.parse(date + " " + time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(returnDate);
            cal.add(Calendar.HOUR_OF_DAY,0);
            return  cal.getTime();
        }catch (ParseException e){
            Log.d("DEBUG","getDateFromString cannot parse date:"+ date +" "+ "time:"+time);
            throw new Exception("getDateFromString cannot finish.");
        }
    }

    /**
     * createDateAfterDays method create string of date with format dd/mm/yy from now with extra days
     * @param day
     * @return
     */
    private String createDateAfterDays(int day) {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, day);
        dt = c.getTime();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yy");
        return simpleDateFormat.format(dt);
    }
}
