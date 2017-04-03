package tbject.com.smstocalendar.pojo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SmsEvent implements Parcelable {
    private String title;
    private String description;
    private String place;
    private Date date;
    private String dateString;

    public SmsEvent(){

    }
    protected SmsEvent(Parcel in) {
        title = in.readString();
        description = in.readString();
        place = in.readString();
        date=new Date(Date.parse(in.readString()));
    }

    public static final Creator<SmsEvent> CREATOR = new Creator<SmsEvent>() {
        @Override
        public SmsEvent createFromParcel(Parcel in) {
            return new SmsEvent(in);
        }

        @Override
        public SmsEvent[] newArray(int size) {
            return new SmsEvent[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date){
        this.date=date;
    }

    public String toString(){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return "פגישה בנושא: "  +title+" תתקיים בתאריך: " +df.format(date) +" במקום: " +place;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
        this.date=new Date(Date.parse(dateString));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(place);
        dest.writeString(dateString);
    }

    /**
     * readDataFromDisk method read all smsEvents from the disk
     * @param context
     * @return
     */
    public static ArrayList<SmsEvent> readDataFromDisk(Context context,SettingsProp settingsProp) {
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
                    smsEvent.setDateString(smsEventArray[1]);
                    smsEvent.setPlace(smsEventArray[2]);
                    smsEvent.setDescription(smsEventArray[3]);
                    smsEvents.add(smsEvent);
                }
             }
        return smsEvents;
    }

    public static void writeSmsEventsToDist(Context context,ArrayList<SmsEvent> smsEvents,SettingsProp settingsProp){
        String smsEventsString="";
        for (SmsEvent smsEvent:smsEvents){
            smsEventsString+= smsEvent.getTitle()+";;"+smsEvent.getDate().toString()+";;"+smsEvent.getPlace()+";;"+smsEvent.getDescription()+"@@";
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(settingsProp.name().toString(),smsEventsString);
        editor.commit();
    }

    public static void deleteSmsEventDataFromDisk(Context context,SettingsProp settingsProp){
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
}
