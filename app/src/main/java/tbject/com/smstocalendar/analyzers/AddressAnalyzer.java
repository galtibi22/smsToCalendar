package tbject.com.smstocalendar.analyzers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SmsEvent;
import tbject.com.smstocalendar.services.LocationService;

/**
 * Created by gal.tibi on 12-Apr-17.
 */

public class AddressAnalyzer {
    Context context;
    LocationService locationService;

    public AddressAnalyzer(Context context){
        this.context=context;
        locationService=new LocationService(context);
    }

    /**
     * findAddress method find for every smsEvent object
     * @param smsEvent - the object to find the address for him
     * @return smsEvent with address and addressPattern
     */
    public SmsEvent findAddress(SmsEvent smsEvent){
        smsEvent=findAddressInCustomAdressesHistory(smsEvent);
        if (smsEvent.getAddress()==null ||smsEvent.getAddress().isEmpty())
            smsEvent=locationService.findAddressInLocalHistory(smsEvent);
        if (smsEvent.getAddress()==null ||smsEvent.getAddress().isEmpty())
            smsEvent=findAddressByGeneralWords(smsEvent);
        if (smsEvent.getAddress()==null || smsEvent.getAddress().isEmpty())
            smsEvent=findAddressByGoogleApi(smsEvent);
        return smsEvent;
    }

    private SmsEvent findAddressInCustomAdressesHistory(SmsEvent smsEvent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> privateAddressPatternKeys=new HashSet<>();
        privateAddressPatternKeys=preferences.getStringSet(smsEvent.getPhoneNumber(),privateAddressPatternKeys);
        for (String key:privateAddressPatternKeys){
            if (smsEvent.getDescription().contains(key)){
                smsEvent.setAddressPattern(key);
                smsEvent.setAddress(preferences.getString(smsEvent.getPhoneNumber()+key,null));
                Log.i("foundCustomAddress","found custom address="+smsEvent.getAddress()+
                        " for addressPattern="+smsEvent.getAddressPattern() + "for phoneNumber="+smsEvent.getPhoneNumber());
                break;
            }
        }
        return smsEvent;
    }

    private SmsEvent findAddressByGoogleApi(SmsEvent smsEvent){
        String body=smsEvent.getDescription();
        String address="";
        String addressPattern="";
        List<String> specicPlaces= Arrays.asList(context.getResources().getStringArray(R.array.specific_places));
        for(String pattern:specicPlaces) {
            if (body.contains(pattern)) {
                if (address == null || address.isEmpty()) {
                    String bodyPattern[] = body.split("[\\.,\\s]");
                    for (int i = 0; i < bodyPattern.length; i++) {
                        if (bodyPattern[i].contains(pattern)) {
                            int numOfWord[] = {3,2,1};
                            for (int x : numOfWord) {
                                if (i + x < bodyPattern.length) {
                                    StringBuilder addToCheck = new StringBuilder(bodyPattern[i]+" ");
                                    for (int y = 1; y <= x; y++) {
                                        addToCheck.append(bodyPattern[i + y] + " ");
                                    }

                                    String substring = addToCheck.toString();//.replaceFirst("[0-9][0-9]:[0-9][0-9]", "");
                                    String addr = locationService.validateAddress(substring);
                                    if (addr != null && !addr.isEmpty()) {
                                        address = addr;
                                        addressPattern = addToCheck.toString();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        if (address==null|| address.isEmpty()) {
            List<String> patterns = Arrays.asList(context.getResources().getStringArray(R.array.addresses_pattern));
            for (String pattern : patterns) {
                if (body.contains(pattern)) {
                    int lastIndex = body.indexOf(pattern) + pattern.length();
                    ;
                    String substring = hebSubString(body, lastIndex);
                    String[] bodySplite = substring.toString().split("\\.");
                    if (bodySplite != null && bodySplite.length > 0) {
                        String addPat = bodySplite[0];
                        address = locationService.validateAddress(addPat);
                        if (address != null && !address.isEmpty()) {
                            addressPattern = addPat;
                            break;
                        }
                        String spilte[] = addPat.split("(-|–|:|_)");
                        for (String sp : spilte) {
                            if (sp.split(" ").length >= 2) {
                                addPat = sp;
                                address = locationService.validateAddress(addPat);
                                if (address != null && !address.isEmpty()) {
                                    addressPattern = addPat;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            for (String pattern : patterns) {
                if (body.contains(pattern)) {
                    if (address == null || address.isEmpty()) {
                        String bodyPattern[] = body.split(" ");
                        for (int i = 0; i < bodyPattern.length; i++) {
                            if (bodyPattern[i].contains(pattern)) {
                                int numOfWord[] = {3, 4, 5};
                                for (int x : numOfWord) {
                                    if (i + x < bodyPattern.length) {
                                        StringBuilder addToCheck = new StringBuilder();
                                        for (int y = 1; y <= x; y++) {
                                            addToCheck.append(bodyPattern[i + y] + " ");
                                        }

                                        String substring = addToCheck.toString();//.replaceFirst("[0-9][0-9]:[0-9][0-9]", "");
                                        String addr = locationService.validateAddress(substring);
                                        if (addr != null && !addr.isEmpty()) {
                                            address = addr;
                                            addressPattern = addToCheck.toString();
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        smsEvent.setAddress(address);
        smsEvent.setAddressPattern(addressPattern);
        return smsEvent;
    }

    private SmsEvent findAddressByGeneralWords(SmsEvent smsEvent) {
        String body=smsEvent.getDescription();
        List<String> contactAddress= Arrays.asList(context.getResources().getStringArray(R.array.contact_Address));
        for(String key:contactAddress){
            if (body.contains(key)){
                String address=getContactAddress(smsEvent.getPhoneNumber());
                String contactName=getContactName(context,smsEvent.getPhoneNumber());
                if ( address!=null && !address.isEmpty()) {
                    smsEvent.setAddress(address);
                    smsEvent.setAddressPattern(key);
                }else if (contactName!=null && !contactName.isEmpty()){
                    smsEvent.setAddress("אצל "+contactName);
                    smsEvent.setAddressPattern(key);
                }else{
                    smsEvent.setAddress("אצל " +smsEvent.getPhoneNumber());
                    smsEvent.setAddressPattern(key);
                }

            }
        }
        if (smsEvent.getAddress()==null || smsEvent.getAddress().isEmpty()){
            List<String> my_address= Arrays.asList(context.getResources().getStringArray(R.array.my_address));
            for(String key:my_address) {
                if (body.contains(key)) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    smsEvent.setAddress(preferences.getString(context.getString(R.string.homeAddress), context.getString(R.string.your_home)));
                    smsEvent.setAddressPattern(key);
                }
            }
        }
        if (smsEvent.getAddress()==null || smsEvent.getAddress().isEmpty()){
            List<String> my_address= Arrays.asList(context.getResources().getStringArray(R.array.work_address));
            for(String key:my_address) {
                if (body.contains(key)) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    smsEvent.setAddress(preferences.getString(context.getString(R.string.workAddress), context.getString(R.string.your_work)));
                    smsEvent.setAddressPattern(key);
                }
            }
        }
        if (smsEvent.getAddress()!=null && !smsEvent.getAddress().isEmpty()){
            Log.w("foundNewAddress","found address with generic words. address="+smsEvent.getAddress()+" addressPatern="+smsEvent.getAddressPattern());
        }
        return smsEvent;
    }

    private String hebSubString(String str,int startIndex){
        char [] chars=str.toCharArray();
        StringBuilder substring=new StringBuilder();
        for (int i=startIndex;i<chars.length;i++)
            substring.append(chars[i]);
        return substring.toString();
    }

    private String getContactAddress(String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        String contactId=null;
        if(cursor!=null) {
            while(cursor.moveToNext()){
                 contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }
        String address=null;
        if (contactId!=null && !contactId.isEmpty()){
            Uri postal_uri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
            Cursor postal_cursor  = context.getContentResolver().query(postal_uri,null,  ContactsContract.Data.CONTACT_ID + "="+contactId.toString(), null,null);
            postal_cursor.moveToNext();
            while(postal_cursor.moveToNext())
            {
                address=postal_cursor.getString(postal_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                if (address!=null && !address.isEmpty())
                    break;
            }
            postal_cursor.close();
        }
        return address;
    }
    private String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }
}
