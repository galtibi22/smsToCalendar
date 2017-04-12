package tbject.com.smstocalendar.analyzers;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SmsEvent;

public class SmsEventAnalyzer {
    private SmsEvent smsEvent;
   // private DataManager dataManager;
    private Context context;
  //  private Properties properties;


    public SmsEventAnalyzer(Context context) throws IOException {
        this.context=context;
        smsEvent=new SmsEvent();
        //    dataManager=new DataManager(context);
         //   properties=dataManager.readSmsEventAnalyzerProp();
    }

    public boolean analyzeSms(String body){
       //possitive keys
        if (!initialScreeningKeysPossitve(body)) {
            //Log.d("DEBUG","Filttered out by initialScreeningKeysPossitve "+body);
            return false;
        }
        //negetive keys
        if (initialScreeningKeysNegetive(body)) {
            //Log.d("DEBUG","Filttered out by initialScreeningKeysNegetive "+body);
            return false;
        }
        DateAnalyzer dateAnalyzer=new DateAnalyzer(context);
        smsEvent=new SmsEvent();
        smsEvent=dateAnalyzer.findDateAndTime(body,smsEvent);
        if (smsEvent==null){
            Log.d("DEBUG","Filttered out by findDateAndTime "+body);
            return false;
        }else{
           // Log.d("DEBUG","analyzeSms suceess: "+body);
            return true;
            }
    }

    /**
     * initialScreeningKeysPossitve - first filter for possitive keys - need to return true
     * @param body
     * @return true if the body contain the key
     */
    public boolean initialScreeningKeysPossitve(String body){

        List<String> keys= Arrays.asList(context.getResources().getStringArray(R.array.initial_screening_keys_possitive));
        for (String key:keys){
            if (body.contains(key)) {
                return true;
                }
            }
        return false;
    }

    /**
     * initialScreeningKeysNegetive - second filter for negetive keys - need to retrun false
     * @param body
     * @return true if the body contain the key
     */
    public boolean initialScreeningKeysNegetive(String body){
        List<String> keys= Arrays.asList(context.getResources().getStringArray(R.array.initial_screening_keys_negetive));
        for (String key:keys){
            if (body.contains(key)) {
               // Log.d("DEBUG","initialScreeningKeysNegetive.negetive key found:"+key);
                return true;

            }
        }
        return false;
    }
    public SmsEvent getSmsEvent(){
        return this.smsEvent;
    }

}