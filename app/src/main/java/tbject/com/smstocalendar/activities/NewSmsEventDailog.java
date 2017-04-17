
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
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import tbject.com.smstocalendar.DataManager;
import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SharePrefKeys;
import tbject.com.smstocalendar.pojo.SmsEvent;

public class NewSmsEventDailog extends Activity {
    SmsEvent currentSmsEvent;
    ArrayList<SmsEvent>historySmsEvents=new ArrayList<>();
    ArrayList<SmsEvent> smsEvents;
    MaterialDialog builMaterialDialog;
    private int smsEventCounter=0;
    private int totleSmsEvemts;
    private GoogleApiClient client;
    private SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private DataManager dataManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataManager=new DataManager(getApplicationContext());
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        smsEvents = dataManager.readDataFromDisk(this, SharePrefKeys.EVENT_DATA);
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
            dataManager.writeSmsEventsToDist(this,smsEvents, SharePrefKeys.EVENT_DATA);
        else
            dataManager.deleteSmsEventDataFromDisk(this, SharePrefKeys.EVENT_DATA);
        dataManager.writeSmsEventsToHistory(this,historySmsEvents);
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
        GravityEnum gravityEnum;
        String content="";
        String currentLan=this.getResources().getConfiguration().locale.getLanguage();
        if (currentLan.equals(this.getString(R.string.hebrew)))
            gravityEnum = GravityEnum.END;

        else
            gravityEnum=GravityEnum.START;
        content = getString(R.string.alert_meet_subject) + " " + currentSmsEvent.getTitle() + " "
                + getString(R.string.alert_place) + currentSmsEvent.getAddress()
                + " " + getString(R.string.alert_date) + " " + dateFormat.format(currentSmsEvent.getDate());
        builMaterialDialog = new MaterialDialog.Builder(this)
                    .title("(" + smsEventCounter + "/" + totleSmsEvemts + ")" + "  " + getString(R.string.create_new_event_main_title)).titleGravity(gravityEnum)
                .content(content).contentGravity(gravityEnum)
                    .positiveText(R.string.yes).buttonsGravity(gravityEnum)
                    .negativeText(R.string.no)
                    .cancelable(false)
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
            builMaterialDialog.setTitle("("+smsEventCounter+"/"+totleSmsEvemts+")"+"  "+getString(R.string.create_new_event_main_title));
            String content=getString(R.string.alert_meet_subject)+" "+currentSmsEvent.getTitle()+" "+getString(R.string.alert_place)+currentSmsEvent.getAddress()+" "+getString(R.string.alert_date)+" "+dateFormat.format(currentSmsEvent.getDate());
            builMaterialDialog.setContent(content);
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
            values.put(CalendarContract.Events.DTEND, currentSmsEvent.getDateEnd().getTime());
            values.put(CalendarContract.Events.TITLE, currentSmsEvent.getTitle());

            values.put(CalendarContract.Events.DESCRIPTION, currentSmsEvent.getDescription());
            if (currentSmsEvent.getAddress() != null)
                values.put(CalendarContract.Events.EVENT_LOCATION, currentSmsEvent.getAddress());
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            setReminder(cr, Long.parseLong(uri.getLastPathSegment()));
            Log.d("addEvent",currentSmsEvent.toString()+" created successfully");
            Toast.makeText(this, getString(R.string.toast_event_successfully),
                    Toast.LENGTH_SHORT).show();
            historySmsEvents.add(0,currentSmsEvent);
        } catch (Exception e) {
            Log.e("addEvent", e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.toast_event_unsuccessfully),
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
                Toast.makeText(this, getString(R.string.toast_smsEvent_without_reminder), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setReminder", e.getMessage(), e);
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
