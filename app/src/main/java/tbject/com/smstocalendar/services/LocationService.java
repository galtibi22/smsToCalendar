package tbject.com.smstocalendar.services;

import android.content.Context;
import android.location.Address;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import tbject.com.smstocalendar.DataManager;
import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SmsEvent;

/**
 * LocationService - Verified address by google location service and localHistory
 */

public class LocationService  {
    Context context;
    HashMap<String,String> historyAddresses;
    DataManager dataManager;
    int historyAddrOrigSize;
    private  String googleAddres;

    public LocationService (Context context){
        this.context=context;
        dataManager=new DataManager(context);
        historyAddresses=dataManager.readAddressesHistory();
        historyAddrOrigSize=historyAddresses.size();
    }


    /**
     * findAddressInLocalHistory method - check if address exist in local history

     */
    public SmsEvent findAddressInLocalHistory(SmsEvent smsEvent){
        Set<String> keys=historyAddresses.keySet();
        String body=smsEvent.getDescription();
        for (String key:keys){
            if (body.contains(key.trim())){
                smsEvent.setAddress(historyAddresses.get(key));
                smsEvent.setAddressPattern(key);
                Log.d("foundInLocalHistory","found address in local history. address="+smsEvent.getAddress()+
                        " for addressPattern="+smsEvent.getAddressPattern());
                break;
            }
        }
        return smsEvent;
    }

    /**
     * validateAddress method check if string is valide address by google map and return the response of google map api
     * @param addr
     * @return
     */
    public String validateAddress(String addr) {
        List<String> wordsOut= Arrays.asList(context.getResources().getStringArray(R.array.words_out));
       String addrClean=addr;
        for (String word:wordsOut){
            addrClean=addrClean.replaceAll(word," ");
        }
        addr = addr.replaceAll("[0-9][0-9](.)[0-9][0-9](.)[0-9][0-9]", "");
        addrClean=addrClean.replaceAll("[0-9][0-9](.)[0-9][0-9](.)[0-9][0-9]", "");
        addr = addr.replaceAll("[0-9][0-9](.)[0-9][0-9]", "");
        addrClean=addrClean.replaceAll("[0-9][0-9](.)[0-9][0-9]", "");
        addr = addr.replaceAll("[0-9](.)[0-9][0-9]", "");
        addrClean=addrClean.replaceAll("[0-9](.)[0-9][0-9]", "");
        addr = addr.replaceAll("[0-9](.)[0-9]", "");
        addrClean=addrClean.replaceAll("[0-9](.)[0-9]", "");
        List<String> splite=Arrays.asList(addrClean.trim().split("[-+–\\.^:,\\s]"));
        addrClean="";
        for (int i=0;i<splite.size();i++) {
            String key = splite.get(i);
            if (!key.equals("")){
                String number="";
                if (key.length()==1) {
                    try {
                        number = Integer.parseInt(key) + "";
                        addrClean+=number+" ";
                    } catch (Exception e) {}
                }else {
                    addrClean += key + " ";

                }
            }


        }

        if (addrClean.split(" ").length<2 ||addrClean.length()<4)
            return "";
        boolean flag=false;
        List<Address> addresses;
        try {
            if (addr==null ||addr.isEmpty())
                flag=false;
            else if (historyAddresses.get(addr)!=null){
                    flag=true;
            }else {

                addresses=getAddrByWeb(getLocationInfo(addrClean.replaceAll("[-+.^:,]","")));
                if (addresses.size() >= 1 && addresses.get(0).getAddressLine(0).contains("ישראל") || addresses.get(0).getAddressLine(0).toLowerCase().contains("israel")) {
                    flag = true;
                    setGoogleAddres(addresses.get(0).getAddressLine(0).replaceAll("\n","").trim());
                    Log.d("historyAddresses","Add key="+addr+" value="+googleAddres);
                    historyAddresses.put(addr.trim(),googleAddres);
                }
                Log.d("resultGoogleApi", "Get " + addresses.size() + " addresses results  from google for address:" + addr);
            }
        } catch (Exception e) {
           // e.printStackTrace();
        }
        if (historyAddresses.size()>historyAddrOrigSize)
            dataManager.saveAddressesHistory(historyAddresses);
        return googleAddres;

    }

    private JSONObject getLocationInfo(String address) {
        final StringBuilder out = new StringBuilder();
        try {
            String currentLan = context.getResources().getConfiguration().locale.getLanguage();
            String lan = "he";
            if (currentLan.equals(context.getString(R.string.hebrew)))
                lan = context.getString(R.string.hebrew_he);
            address = address.replaceAll(" ", "%20");

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?language=" + lan + "&address=" + address + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            final int bufferSize = 1024;
            final char[] buffer = new char[bufferSize];
            Reader in = new InputStreamReader(stream, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        Log.i("httpResponse",out.toString());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(out.toString());
        } catch (JSONException e) {
           // e.printStackTrace();
        }

        return jsonObject;
    }
    private List<Address> getAddrByWeb(JSONObject jsonObject){
        List<Address> res = new ArrayList<Address>();
        try
        {
            JSONArray array = (JSONArray) jsonObject.get("results");
            for (int i = 0; i < array.length(); i++)
            {
                Double lon = new Double(0);
                Double lat = new Double(0);
                String name = "";
                try
                {
                    lon = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                    lat = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    name = array.getJSONObject(i).getString("formatted_address");
                    Address addr = new Address(Locale.getDefault());
                    addr.setLatitude(lat);
                    addr.setLongitude(lon);
                    addr.setAddressLine(0, name != null ? name : "");
                    res.add(addr);
                }
                catch (JSONException e)
                {
                   // e.printStackTrace();

                }
            }
        }
        catch (Exception e)
        {
          //  e.printStackTrace();

        }

        return res;
    }

    public String getGoogleAddres() {
        return googleAddres;
    }

    public void setGoogleAddres(String googleAddres) {
        this.googleAddres = googleAddres;
    }
}
