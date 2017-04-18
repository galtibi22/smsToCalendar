package tbject.com.smstocalendar.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import tbject.com.smstocalendar.DataManager;
import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.activities.NewSmsEventManual;
import tbject.com.smstocalendar.activities.OpeningScreen;
import tbject.com.smstocalendar.analyzers.SmsEventAnalyzer;
import tbject.com.smstocalendar.pojo.SharePrefKeys;
import tbject.com.smstocalendar.pojo.SmsEvent;

//import tbject.com.smstocalendar.activities.AlertDialog;

    public class IncomingSmsService extends BroadcastReceiver {
    private final int NOTIFICATION_ID=281988;
    final SmsManager sms = SmsManager.getDefault();
    ArrayList<SmsEvent> smsEvents=new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onReceive(Context context, Intent intent){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        DataManager dataManager=new DataManager(context);
        OpeningScreen.setAppLang(context);
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Log.i("BroadcastReceiver", "Broadcast Receiver start");
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                smsEvents=dataManager.readDataFromDisk(context, SharePrefKeys.EVENT_DATA);
                SmsEvent smsEvent=null;
                if (pdusObj.length>0){
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[0]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    Log.i("SmsReceiver", "senderNum: "+ phoneNumber + "; message: " + message);
                    for (int i=1;i<pdusObj.length;i++){
                        message+=SmsMessage.createFromPdu((byte[]) pdusObj[i]).getDisplayMessageBody()+" ";
                    }
                    smsEvent = isSmsEvent(message,phoneNumber,context);
                    if (smsEvent != null) {
                        smsEvents.add(smsEvent);
                    }
                }

                if (smsEvent!=null) {
                    dataManager.writeSmsEventsToDist(context,smsEvents, SharePrefKeys.EVENT_DATA);
                    addNotification(context, smsEvents.size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SmsReceiver", "Exception smsReceiver",e);

        }
    }



    private SmsEvent isSmsEvent (String body,String phoneNumber,Context context){
        SmsEvent smsEvent=null;
        try {
            SmsEventAnalyzer smsEventAnalyzer=new SmsEventAnalyzer(context);
            if (smsEventAnalyzer.analyzeSms(body,phoneNumber.substring(1,4)+"-"+phoneNumber.substring(4))) {
                smsEvent = smsEventAnalyzer.getSmsEvent();
                Log.i("smsIsEventSms", "Success to parse sms to eventSms="+smsEvent.toString());

            }
       /* Date date=new Date();
        date=new Date(date.getTime()+(1000 * 60 * 60 * 24));
        smsEvent.setTitle( "תור לרופא שיניים ");
        smsEvent.setAddress("השקמה 55 פרדסיה");
        smsEvent.setDate(date);
        date=new Date(date.getTime()+(1000 * 60 * 60 * 24)+(1000 * 60 * 60));
        smsEvent.setDateEnd(date);
        smsEvent.setDescription(smsMessage.getDisplayMessageBody());
        smsEvent.setPhoneNumber(smsMessage.getDisplayOriginatingAddress().substring(1,4)+"-"+smsMessage.getDisplayOriginatingAddress().substring(4));
**/        } catch (IOException e) {
            e.printStackTrace();
        }
        return smsEvent;
    }

    private void addNotification(Context context,int numOfSmsEvent) {
        Notification.Builder nBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getString(R.string.notification_title_1)+" " +numOfSmsEvent +" "+context.getString(R.string.notification_title_2));
        Intent contentIntent = new Intent(context,NewSmsEventManual.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingIntent);
        nBuilder.setDefaults(Notification.DEFAULT_SOUND);
        nBuilder.setAutoCancel(true);
        nBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, nBuilder.build());
        Log.i("createNotification","Recive new smsEvent - create new notification message");

    }



}

