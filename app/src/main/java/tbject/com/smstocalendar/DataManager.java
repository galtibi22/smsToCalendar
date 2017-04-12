package tbject.com.smstocalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import tbject.com.smstocalendar.pojo.SettingsProp;
import tbject.com.smstocalendar.pojo.SmsEvent;

/**
 * Created by Gal on 09/04/2017.
 */

public class DataManager {

    private Context context;

    public DataManager(Context context){
        this.context=context;
    }

    /**
     saveDataFile method - save
     */
    public void saveDataFile(){
        try {
            FileOutputStream dataFile = context.openFileOutput("file.txt", Context.MODE_PRIVATE);
            dataFile.flush();

            dataFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * readDataFile method - read data file
     */
    public Properties readSmsEventAnalyzerProp() throws IOException {
        Properties props = new Properties();
        props.load(context.getResources().openRawResource(R.raw.sms_event_enalyzer_rop));
        return props;

    }


    /**
     * readDataFromDisk method read all smsEvents from the disk
     * @param context
     * @return
     */
    public ArrayList<SmsEvent> readDataFromDisk(Context context, SettingsProp settingsProp) {
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
                    smsEvent.setPhoneNumber(smsEventArray[4]);
                    smsEvent.setAccepted(new Date(smsEventArray[5]));
                    smsEvents.add(smsEvent);
                }
        }
        Log.d("readDataFromDisk","Read the next smsEvents from disk with header "+settingsProp.name()+": "+smsEvents.toString());
        return smsEvents;
    }

    public void writeSmsEventsToDist(Context context,ArrayList<SmsEvent> smsEvents,SettingsProp settingsProp){
        String smsEventsString="";
        Log.d("writeSmsEventsToDist","Write to disk with header:"+settingsProp.name()+smsEvents.toString());
        for (SmsEvent smsEvent:smsEvents){
            smsEventsString+= smsEvent.getTitle()+";;"+smsEvent.getDate().toString()+";;"+smsEvent.getPlace()+";;"+smsEvent.getDescription()+";;"+
                    smsEvent.getPhoneNumber()+";;"+smsEvent.getAccepted().toString()+"@@";
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(settingsProp.name().toString(),smsEventsString);
        editor.commit();
    }

    public void deleteSmsEventDataFromDisk(Context context,SettingsProp settingsProp){
        Log.d("deleteSmsEventData","Delete all smsEvent from disk with header:"+settingsProp.name());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(settingsProp.name());
        editor.commit();
    }

    public void writeSmsEventsToHistory(Context context,ArrayList <SmsEvent> smsEvents){
        Log.d("writeSmsEventsToHistory","method start");
        ArrayList<SmsEvent> historySmsevents=readDataFromDisk(context,SettingsProp.HISTORY_EVENT_DATA);
        Log.d("writeSmsEventsToHistory","Read from file:"+historySmsevents.toString());
        smsEvents.addAll(smsEvents.size(),historySmsevents);
        Log.d("writeSmsEventsToHistory","Write to file:"+smsEvents.toString());
        writeSmsEventsToDist(context,smsEvents,SettingsProp.HISTORY_EVENT_DATA);
    }


}
