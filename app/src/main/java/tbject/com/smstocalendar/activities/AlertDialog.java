
package tbject.com.smstocalendar.activities;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Calendar;

import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SettingsProp;
import tbject.com.smstocalendar.pojo.SmsEvent;

public class AlertDialog extends Activity {
    SmsEvent currentSmsEvent;
    ArrayList<SmsEvent>historySmsEvents=new ArrayList<>();
    ArrayList<SmsEvent> smsEvents;
    MaterialDialog builMaterialDialog;
    private int smsEventCounter=0;
    private int totleSmsEvemts;
    private GoogleApiClient client;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        smsEvents = SmsEvent.readDataFromDisk(this,SettingsProp.EVENT_DATA);
        totleSmsEvemts=smsEvents.size();
        currentSmsEvent=smsEvents.get(0);
        smsEvents.remove(0);
        smsEventCounter++;
        buildAlertDialog();
    }

        @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction0());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void finish() {
        Log.d("AlertDialog.finish","finish method start");
        if (smsEvents.size()>0)
            SmsEvent.writeSmsEventsToDist(this,smsEvents,SettingsProp.EVENT_DATA);
        else
            SmsEvent.deleteSmsEventDataFromDisk(this,SettingsProp.EVENT_DATA);
        SmsEvent.writeSmsEventsToHistory(this,historySmsEvents);
        builMaterialDialog.dismiss();
        finishAndRemoveTask ();
    }

    @Override
    public void onStop() {
        AppIndex.AppIndexApi.end(client, getIndexApiAction0());
        client.disconnect();
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void buildAlertDialog() {

        Log.d("buildAlertDialog","method start");
        builMaterialDialog=new MaterialDialog.Builder(this)

                .title("("+smsEventCounter+"/"+totleSmsEvemts+")"+"  "+getString(R.string.alertDailogTitle))
                .content(currentSmsEvent.toString())
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .backgroundColor(getColor(R.color.mainBackground))
                .contentColor(getColor(android.R.color.black))
                .titleColor(getColor(android.R.color.black))
                .positiveColor(getColor(android.R.color.darker_gray))
                .negativeColor(getColor(android.R.color.darker_gray))
                .buttonRippleColor(getColor(android.R.color.white))
                    .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        addEvent();
                        updateAlertDialog();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        updateAlertDialog();
                    }
                }).show();
        Log.d("buildAlertDialog","method finish");

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateAlertDialog(){
        Log.d("updateAlertDialog","method start");
        if (smsEvents.size()>0) {
            Log.i("updateAlertDialog","The smsEvents list is not empty. Update the alertDailog with the smsEvent:"+currentSmsEvent.toString());
            currentSmsEvent = smsEvents.get(0);
            smsEvents.remove(0);
            smsEventCounter++;
            builMaterialDialog.setTitle("("+smsEventCounter+"/"+totleSmsEvemts+")"+"  "+getString(R.string.alertDailogTitle));
            builMaterialDialog.setContent(currentSmsEvent.toString());
         //   builMaterialDialog.show();
        }else{
            Log.d("updateAlertDialog","The smsEvents list is empty. Call finish method");

            finish();}
    }

    private void addEvent() {
        try {
            Log.i("start add event",currentSmsEvent.toString()+" to calendar");
            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, currentSmsEvent.getDate().getTime());
            values.put(CalendarContract.Events.DTEND, currentSmsEvent.getDate().getTime() + 60 * 60 * 1000);
            values.put(CalendarContract.Events.TITLE, currentSmsEvent.getTitle());

            values.put(CalendarContract.Events.DESCRIPTION, currentSmsEvent.getDescription());
            if (currentSmsEvent.getPlace() != null)
                values.put(CalendarContract.Events.EVENT_LOCATION, currentSmsEvent.getPlace());
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            setReminder(cr, Long.parseLong(uri.getLastPathSegment()));
            Log.d("addEvent",currentSmsEvent.toString()+" created successfully");
            Toast.makeText(this, "SmsEvent created successfully ",
                    Toast.LENGTH_SHORT).show();
            historySmsEvents.add(currentSmsEvent);
        } catch (Exception e) {
            Log.e("addEvent", e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, "SmsEvent not created. please check log file",
                    Toast.LENGTH_LONG).show();
        }
    }

    // routine to add reminders with the event
    public void setReminder(ContentResolver cr, long eventID) {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            if (preferences.getBoolean(getString(R.string.allowReminder), true)) {
                ContentValues values = new ContentValues();
                int minutesEventReminder=Integer.parseInt(preferences.getString(getString(R.string.minutesEventReminder),null));
                values.put(CalendarContract.Reminders.MINUTES,minutesEventReminder);
                String reminderMethod=preferences.getString(getString(R.string.reminderMethod),null);
                values.put(CalendarContract.Reminders.METHOD,Integer.parseInt(reminderMethod));
                values.put(CalendarContract.Reminders.EVENT_ID, eventID);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
                    return;
                Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
                Cursor c = CalendarContract.Reminders.query(cr, eventID,
                        new String[]{CalendarContract.Reminders.MINUTES});
                c.close();


            } else {
                Toast.makeText(this, "Created new SmsEvent without Reminder.\nSee App settings.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setReminder", e.getMessage(), e);
            Toast.makeText(this, "Reminder not created. please check log file",
                    Toast.LENGTH_LONG).show();
        }
    }


    public Action getIndexApiAction0() {
        Thing object = new Thing.Builder()
                .setName("AlertDialog Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
