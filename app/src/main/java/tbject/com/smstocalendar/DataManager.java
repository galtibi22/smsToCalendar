package tbject.com.smstocalendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import tbject.com.smstocalendar.pojo.SharePrefKeys;
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
    public ArrayList<SmsEvent> readDataFromDisk(Context context, SharePrefKeys sharePrefKeys) {
        Log.i("readDataFromDisk","method start");
        ArrayList<SmsEvent> smsEvents=new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String smsEventsString=preferences.getString(sharePrefKeys.name(),"null");
        if (!smsEventsString.equals("null")){
            String[] smsEventsArray=smsEventsString.split("@@");
            if (!smsEventsArray[0].isEmpty())
                for (int i=0;i<smsEventsArray.length;i++){
                    String[] smsEventArray=smsEventsArray[i].split(";;");
                    SmsEvent smsEvent=new SmsEvent();
                    smsEvent.setTitle(smsEventArray[0]);
                    smsEvent.setDate(new Date(smsEventArray[1]));
                    smsEvent.setAddress(smsEventArray[2]);
                    smsEvent.setDescription(smsEventArray[3]);
                    smsEvent.setPhoneNumber(smsEventArray[4]);
                    smsEvent.setAccepted(new Date(smsEventArray[5]));
                    smsEvent.setDateEnd(new Date(smsEventArray[6]));
                    smsEvents.add(smsEvent);
                }
        }
        Log.i("readDataFromDisk","Read the next smsEvents from disk with header "+ sharePrefKeys.name()+": "+smsEvents.toString());
        return smsEvents;
    }

    public void writeSmsEventsToDist(Context context,ArrayList<SmsEvent> smsEvents,SharePrefKeys sharePrefKeys){
        String smsEventsString="";
        Log.i("writeSmsEventsToDist","Write to disk with header:"+ sharePrefKeys.name()+smsEvents.toString());
        for (SmsEvent smsEvent:smsEvents){
            smsEventsString+= smsEvent.getTitle()+";;"+smsEvent.getDate().toString()+";;"+smsEvent.getAddress()+";;"+smsEvent.getDescription()+";;"+
                    smsEvent.getPhoneNumber()+";;"+smsEvent.getAccepted().toString()+";;"+smsEvent.getDateEnd().toString()+";;"+"@@";
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(sharePrefKeys.name().toString(),smsEventsString);
        editor.commit();
    }

    public void deleteSmsEventDataFromDisk(Context context,SharePrefKeys sharePrefKeys){
        Log.i("deleteSmsEventData","Delete all smsEvent from disk with header:"+ sharePrefKeys.name());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(sharePrefKeys.name());
        editor.commit();
    }

    public void writeSmsEventsToHistory(Context context,ArrayList <SmsEvent> smsEvents){
        Log.i("writeSmsEventsToHistory","method start");
        ArrayList<SmsEvent> historySmsevents=readDataFromDisk(context, SharePrefKeys.HISTORY_EVENT_DATA);
        Log.i("writeSmsEventsToHistory","Read from file:"+historySmsevents.toString());
        smsEvents.addAll(smsEvents.size(),historySmsevents);
        Log.i("writeSmsEventsToHistory","Write to file:"+smsEvents.toString());
        writeSmsEventsToDist(context,smsEvents, SharePrefKeys.HISTORY_EVENT_DATA);
    }

    public void saveAddressesHistory(HashMap<String,String> addressesMap){
        Log.i("saveAddressesHistory","method start");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> addresses=new HashSet<>();
        for (String key:addressesMap.keySet()){
            addresses.add(key+"@@"+addressesMap.get(key));
        }
        editor.putStringSet(SharePrefKeys.ADDDRESSES_HISTORY.name(),addresses);
        editor.commit();
        Log.i("saveAddressesHistory","method finish");

    }

    public HashMap<String,String> readAddressesHistory(){
        Log.i("readAddressesHistory","method start");
        Set <String> addresses=new HashSet<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        addresses=preferences.getStringSet(SharePrefKeys.ADDDRESSES_HISTORY.name(),addresses);
        HashMap<String,String> addressesMap=new HashMap<>();
        for (String address:addresses){
            String addressSplitter[]=address.split("@@");
            addressesMap.put(addressSplitter[0],addressSplitter[1]);
        }
        Log.i("readAddressesHistory","method finish");
        return addressesMap;
    }


}
