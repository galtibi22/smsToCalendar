package tbject.com.smstocalendar.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.activities.AlertDialog;
import tbject.com.smstocalendar.pojo.SettingsProp;
import tbject.com.smstocalendar.pojo.SmsEvent;

public class IncomingSmsService extends BroadcastReceiver {
    private final int NOTIFICATION_ID=281988;
    final SmsManager sms = SmsManager.getDefault();
    ArrayList<SmsEvent> smsEvents=new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onReceive(Context context, Intent intent){
        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Log.i("BroadcastReceiver", "Broadcast Receiver start");
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                smsEvents=SmsEvent.readDataFromDisk(context,SettingsProp.EVENT_DATA);
                SmsEvent smsEvent=null;
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    Log.d("SmsReceiver", "senderNum: "+ phoneNumber + "; message: " + message);
                    smsEvent = isSmsEvent(currentMessage);
                    if (smsEvent != null) {
                        smsEvents.add(smsEvent);
                    }
                }
                if (smsEvent!=null) {
                    SmsEvent.writeSmsEventsToDist(context,smsEvents, SettingsProp.EVENT_DATA);
                    addNotification(context, smsEvents.size());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SmsReceiver", "Exception smsReceiver",e);

        }
    }



    private SmsEvent isSmsEvent (SmsMessage smsMessage){
        SmsEvent smsEvent=new SmsEvent();
        Date date=new Date();
        date=new Date(date.getTime()+(1000 * 60 * 60 * 24));
        smsEvent.setTitle( "תור לרופא שיניים ");
        smsEvent.setPlace("השקמה 55 פרדסיה");
        smsEvent.setDateString(date.toString());
        Log.i("smsIsEventSms", "Success to parse sms to eventSms");
        return smsEvent;
    }

    private void addNotification(Context context,int numOfSmsEvent) {
        Notification.Builder nBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("יש לך " +numOfSmsEvent +" אירועים חדשים");
        Intent contentIntent = new Intent(context,AlertDialog.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(pendingIntent);
        //nBuilder.setDefaults(Notification.DEFAULT_SOUND);
        nBuilder.setAutoCancel(true);
        nBuilder.setPriority(Notification.PRIORITY_DEFAULT);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, nBuilder.build());

    }



}

