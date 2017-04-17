package tbject.com.smstocalendar.analyzers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SmsEvent;
import tbject.com.smstocalendar.services.LocationService;

public class SmsEventAnalyzer {
    private SmsEvent smsEvent;
    private Context context;


    public SmsEventAnalyzer(Context context) throws IOException {
        this.context=context;

    }

    public boolean analyzeSms(String body,String phoneNumber){
       boolean success=false;
        smsEvent=new SmsEvent();
        smsEvent.setDescription(body);
        smsEvent.setPhoneNumber((phoneNumber));
        smsEvent.setAccepted(new Date());


        //possitive keys
        if (!initialScreeningKeysPossitve()) {
            Log.d("DEBUG","Filttered out by initialScreeningKeysPossitve "+smsEvent.getDescription());
        }
        //negetive keys
        else if (initialScreeningKeysNegetive()) {
            Log.d("DEBUG","Filttered out by initialScreeningKeysNegetive "+smsEvent.getDescription());
        }
        //date validate
        else if (!validateDateAndTime()) {
            Log.d("DEBUG", "Filttered out by validateDateAndTime " + smsEvent.getDescription());
        }
        //address validation
        else if (!valdateAddress()){
            Log.d("DEBUG", "Filttered out by validateAddress " + smsEvent.getDescription());
        }
        //title validate
        else if (!validateTitle()) {
            Log.d("DEBUG", "Filttered out by validateTitle " + smsEvent.getDescription());
        }
        else{
            success=true;
           // Log.d("DEBUG","analyzeSms suceess: "+body);
            }
        return success;
    }

    private boolean validateTitle() {
        boolean success=false;
        findTitleInCustomTitleHistory();
        if (smsEvent.getTitle()==null || smsEvent.getTitle().isEmpty()) {
            List<String> titleKeys = Arrays.asList(context.getResources().getStringArray(R.array.titles_keys));
            List<String> titles = Arrays.asList(context.getResources().getStringArray(R.array.titles));
            for (int i=0;i<titleKeys.size();i++){
                if (smsEvent.getDescription().contains(titleKeys.get(i))){
                    String title="";
                    String doctor="ד\"ר";
                    String hot="HOT";
                    String yes="YES";
                    String mirpaha="מרפאת";
                    if (titleKeys.get(i).equals(doctor)){
                        String doctorName=smsEvent.getDescription().substring(smsEvent.getDescription().indexOf(doctor)+doctor.length());
                        doctorName=doctorName.trim().split("\\s+")[0];
                        title=titles.get(i) +" "+doctorName;
                    }else if (smsEvent.getDescription().toUpperCase().contains(hot)){
                        title=titles.get(i)+" עם "+hot;
                    }else if (smsEvent.getDescription().toUpperCase().contains(yes)){
                        title=titles.get(i)+" עם "+ yes;
                    }else if (smsEvent.getDescription().contains(mirpaha)){
                        String name=smsEvent.getDescription().substring(smsEvent.getDescription().indexOf(mirpaha)+mirpaha.length());
                        String []nameArray=name.trim().split("\\s+");
                        LocationService locationService=new LocationService(context);
                        String location=locationService.validateAddress(nameArray[0]);
                        if (location!=null || !location.isEmpty())
                            title=titles.get(i)+" "+ nameArray[0];
                        else{
                            location=locationService.validateAddress(nameArray[0]+" "+nameArray[1]);
                            if (location!=null || !location.isEmpty()){
                                title=titles.get(i)+" "+ nameArray[0]+" "+nameArray[1];
                            }else{
                                title=titles.get(i);
                            }
                        }
                    }else{
                        title=titles.get(i);
                    }
                    smsEvent.setTitle(title);
                    success=true;
                    Log.w("foundTitle","Found title for key="+titleKeys.get(i)+ " and the title is:"+titles.get(i));
                    break;

                }
            }
        }else{
            success=true;
        }
        return success;
    }

    private SmsEvent findTitleInCustomTitleHistory() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> privateTitlePatternKeys=new HashSet<>();
        privateTitlePatternKeys=preferences.getStringSet(smsEvent.getPhoneNumber(),privateTitlePatternKeys);
        for (String key:privateTitlePatternKeys){
            if (smsEvent.getDescription().contains(key)){
                smsEvent.setTitle(preferences.getString(smsEvent.getPhoneNumber()+key,null));
                Log.i("foundCustomTitle","found custom title="+smsEvent.getAddress()+
                        "for phoneNumber="+smsEvent.getPhoneNumber());
                break;
            }
        }
        return smsEvent;
    }

    private boolean valdateAddress() {
        AddressAnalyzer addressAnalyzer=new AddressAnalyzer(context);
        addressAnalyzer.findAddress(smsEvent);

        if (smsEvent.getAddress()==null || smsEvent.getAddress().isEmpty() )
            return false;
        return true;
    }

    /**
     * initialScreeningKeysPossitve - first filter for possitive keys - need to return true
     * @return true if the smsEvent.getDescription contain the key
     */
    public boolean initialScreeningKeysPossitve(){
        String body=smsEvent.getDescription();
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
     * @return true if the smsEvent.getDescription() contain the key
     */
    public boolean initialScreeningKeysNegetive(){
        String body=smsEvent.getDescription();
        List<String> keys= Arrays.asList(context.getResources().getStringArray(R.array.initial_screening_keys_negetive));
        for (String key:keys){
            if (body.contains(key)) {
               // Log.d("DEBUG","initialScreeningKeysNegetive.negetive key found:"+key);
                return true;

            }
        }
        return false;
    }

    /**
     * validateDateAndTime method find date and time and put them into the smsEvent.
     * @return true if smsEvent.getDate is not null
     */
    private boolean validateDateAndTime(){
        DateAnalyzer dateAnalyzer=new DateAnalyzer(context);
        smsEvent=dateAnalyzer.findDateAndTime(smsEvent);
        return smsEvent.getDate()!=null;
    }
    public SmsEvent getSmsEvent(){
        return this.smsEvent;
    }

}