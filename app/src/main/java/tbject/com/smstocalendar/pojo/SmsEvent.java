package tbject.com.smstocalendar.pojo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class SmsEvent  {
    private String title;
    private String description;
    private String place;
    private Date date;



    /**
     * readDataFromDisk method read all smsEvents from the disk
     * @param context
     * @return
     */
    public static ArrayList<SmsEvent> readDataFromDisk(Context context,SettingsProp settingsProp) {
        Log.d("readDataFromDisk","method start");
        ArrayList<SmsEvent> smsEvents=new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String smsEventsString=preferences.getString(settingsProp.name(),"null");
        if (!smsEventsString.equals("null")){
            String[] smsEventsArray=smsEventsString.split("@@");
            if (!smsEventsArray[0].isEmpty())
                for (int i=0;i<smsEventsArray.length;i++){
                    String[] smsEventArray=smsEventsArray[i].split(";;");
                    SmsEvent smsEvent=new SmsEvent();
                    smsEvent.setTitle(smsEventArray[0]);
                    smsEvent.setDate(new Date(smsEventArray[1]));
                    smsEvent.setPlace(smsEventArray[2]);
                    smsEvent.setDescription(smsEventArray[3]);
                    smsEvents.add(smsEvent);
                }
             }
        Log.d("readDataFromDisk","Read the next smsEvents from disk with header "+settingsProp.name()+": "+smsEvents.toString());
        return smsEvents;
    }

    public static void writeSmsEventsToDist(Context context,ArrayList<SmsEvent> smsEvents,SettingsProp settingsProp){
        String smsEventsString="";
        Log.d("writeSmsEventsToDist","Write to disk with header:"+settingsProp.name()+smsEvents.toString());
        for (SmsEvent smsEvent:smsEvents){
            smsEventsString+= smsEvent.getTitle()+";;"+smsEvent.getDate().toString()+";;"+smsEvent.getPlace()+";;"+smsEvent.getDescription()+"@@";
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(settingsProp.name().toString(),smsEventsString);
        editor.commit();
    }

    public static void deleteSmsEventDataFromDisk(Context context,SettingsProp settingsProp){
        Log.d("deleteSmsEventData","Delete all smsEvent from disk with header:"+settingsProp.name());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(settingsProp.name());
        editor.commit();
    }

    public static void writeSmsEventsToHistory(Context context,ArrayList <SmsEvent> smsEvents){
        Log.d("writeSmsEventsToHistory","method start");
        ArrayList<SmsEvent> historySmsevents=readDataFromDisk(context,SettingsProp.HISTORY_EVENT_DATA);
        Log.d("writeSmsEventsToHistory","Read from file:"+historySmsevents.toString());
        historySmsevents.addAll(historySmsevents.size(),smsEvents);
        Log.d("writeSmsEventsToHistory","Write to file:"+smsEvents.toString());
        writeSmsEventsToDist(context,historySmsevents,SettingsProp.HISTORY_EVENT_DATA);
    }

    public void setTitle(String title) {
        if (title==null ||title.isEmpty())
            title=" ";
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlace(String place) {
        if (place==null||place.isEmpty())
            place= " ";
        this.place = place;
    }

    public String getTitle() {
        return title;
    }

    public String getPlace() {
        return place;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SmsEvent{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", place='" + place + '\'' +
                ", date=" + date +
                '}';
    }
}
