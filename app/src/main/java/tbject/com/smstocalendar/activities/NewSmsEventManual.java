package tbject.com.smstocalendar.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import tbject.com.smstocalendar.DataManager;
import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SharePrefKeys;
import tbject.com.smstocalendar.pojo.SmsEvent;

public class NewSmsEventManual extends CommonActivity {

    private EditText eventTitle,eventPlace,eventDes;
    private TextView eventCounter,txtDate, txtTimeStart,txtTimeEnd;
    private ArrayList<SmsEvent>historySmsEvents=new ArrayList<>();
    private int mYear, mMonth, mDay, mHour, mMinute;
    private SmsEvent currentSmsEvent;
    private ImageView time_icon_start,time_icon_end,date_icon;
    private DataManager dataManager;
    private ArrayList<SmsEvent> smsEvents;
    private int totleSmsEvemts;
    private int smsEventCounter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_new_sms_event_manual2);
            dataManager=new DataManager(getApplicationContext());
            smsEvents = dataManager.readDataFromDisk(this, SharePrefKeys.EVENT_DATA);
            totleSmsEvemts=smsEvents.size();
            currentSmsEvent=smsEvents.get(0);
            smsEvents.remove(0);
            smsEventCounter++;
            String currentLan=this.getResources().getConfiguration().locale.getLanguage();
            if (currentLan.equals(this.getString(R.string.hebrew)))
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            else
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            buildNewSmsEventActivity();

        }
    public void finish() {
        Log.d("AlertDialog.finish","finish method start");
        if (smsEvents.size()>0)
            dataManager.writeSmsEventsToDist(this,smsEvents, SharePrefKeys.EVENT_DATA);
        else
            dataManager.deleteSmsEventDataFromDisk(this, SharePrefKeys.EVENT_DATA);
        dataManager.writeSmsEventsToHistory(this,historySmsEvents);
        finishAndRemoveTask ();
    }
    private void buildNewSmsEventActivity() {
        txtDate = (TextView) findViewById(R.id.in_date);
        txtTimeStart = (TextView) findViewById(R.id.in_time_start);
        txtTimeEnd=(TextView) findViewById(R.id.in_time_end);
        time_icon_start = (ImageView) findViewById(R.id.clock_icon_start);
        time_icon_end = (ImageView) findViewById(R.id.clock_icon_end);
        date_icon = (ImageView) findViewById(R.id.calendar_icon);
        eventTitle=(EditText)findViewById(R.id.in_title);
        eventPlace=(EditText)findViewById(R.id.in_place);
        eventDes=(EditText)findViewById(R.id.in_message);
        Button ok=(Button)findViewById(R.id.new_event_ok);//ok.setGravity(gravityEnum.getGravityInt());
        Button cancel=(Button)findViewById(R.id.new_event_Cancel);//cancel.setGravity(gravityEnum.getGravityInt());
        eventCounter=(TextView)findViewById(R.id.event_counter);//eventCounter.setGravity(gravityEnum.getGravityInt());

        eventTitle.setText(currentSmsEvent.getTitle());
        eventPlace.setText(currentSmsEvent.getAddress());
        eventDes.setText(currentSmsEvent.getDescription());
        SimpleDateFormat simpaleSimpleDateFormat=new SimpleDateFormat("HH:mm");
        txtTimeStart.setText(simpaleSimpleDateFormat.format(currentSmsEvent.getDate()));
        txtTimeEnd.setText(simpaleSimpleDateFormat.format(currentSmsEvent.getDateEnd()));
        simpaleSimpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
        txtDate.setText(simpaleSimpleDateFormat.format(currentSmsEvent.getDate()));
        eventCounter.setText(smsEventCounter+"/"+totleSmsEvemts);
        date_icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                openCalendarSelecDailog();
                return false;
            }
        });
        time_icon_start.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                openTimeSelecDailog(v);
                return false;
            }
        });
        time_icon_end.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                openTimeSelecDailog(v);
                return false;
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
                updateActivity();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateActivity();
            }
        });
    }

    private void updateActivity() {
        Log.d("updateActivity","method start");
        if (smsEvents.size()>0) {

            Log.i("updateActivity","The smsEvents list is not empty. Update the activity with the smsEvent:"+currentSmsEvent.toString());
            currentSmsEvent = smsEvents.get(0);
            smsEvents.remove(0);
            smsEventCounter++;
            eventTitle.setText(currentSmsEvent.getTitle());
            eventPlace.setText(currentSmsEvent.getAddress());
            eventDes.setText(currentSmsEvent.getDescription());
            eventCounter.setText(smsEventCounter+"/"+totleSmsEvemts);
            SimpleDateFormat simpaleSimpleDateFormat=new SimpleDateFormat("HH:mm");
            txtTimeStart.setText(simpaleSimpleDateFormat.format(currentSmsEvent.getDate()));
            txtTimeEnd.setText(simpaleSimpleDateFormat.format(currentSmsEvent.getDateEnd()));

            simpaleSimpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
            txtDate.setText(simpaleSimpleDateFormat.format(currentSmsEvent.getDate()));
        }else{
            Log.d("updateActivity","The smsEvents list is empty. Call finish method");
            finish();}
    }


    private void openTimeSelecDailog(View v) {
       if (v==time_icon_start){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        txtTimeStart.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();}
        else{
           TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                   new TimePickerDialog.OnTimeSetListener() {
                       @Override
                       public void onTimeSet(TimePicker view, int hourOfDay,
                                             int minute) {

                           txtTimeEnd.setText(hourOfDay + ":" + minute);
                       }
                   }, mHour, mMinute, false);
           timePickerDialog.show();}

       }


    private void openCalendarSelecDailog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void addEvent() {
        try {
            Log.i("start add event",currentSmsEvent.toString()+" to calendar");
            String origAddress=currentSmsEvent.getAddress();
            currentSmsEvent.setDescription(eventDes.getText().toString().trim());
            String separator= txtDate.getText().toString().replaceAll("[0-9]","").substring(0,1);
            String separatorTime= txtTimeStart.getText().toString().replaceAll("[0-9]","").substring(0,1);
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd"+separator+"MM"+separator+"yyyy HH"+separatorTime+"mm");
            currentSmsEvent.setDate(dateFormat.parse(txtDate.getText().toString().trim()+" "+txtTimeStart.getText().toString().trim()));
            currentSmsEvent.setDateEnd(dateFormat.parse(txtDate.getText().toString().trim()+" "+txtTimeEnd.getText().toString().trim()));
            currentSmsEvent.setAddress(eventPlace.getText().toString().trim());
            String newTitle=eventTitle.getText().toString().trim();
            if (currentSmsEvent.getTitle()!=null && !currentSmsEvent.getTitle().isEmpty() && !newTitle.trim().equals(currentSmsEvent.getTitle().trim())){
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                Set<String> privateTitlePatternKeys=preferences.getStringSet(currentSmsEvent.getPhoneNumber()+"Title",null);
                privateTitlePatternKeys.add(currentSmsEvent.getAddressPattern());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putStringSet(currentSmsEvent.getPhoneNumber()+"Title",privateTitlePatternKeys);
                editor.putString(currentSmsEvent.getPhoneNumber()+currentSmsEvent.getTitle(), newTitle);
                editor.commit();
                currentSmsEvent.setTitle(newTitle);
            }
            currentSmsEvent.setTitle(eventTitle.getText().toString().trim());
            if (currentSmsEvent.getAddress()!=null && !currentSmsEvent.getAddress().isEmpty() &&
                    currentSmsEvent.getAddressPattern()!=null && !currentSmsEvent.getAddressPattern().isEmpty()){
                if (!origAddress.equals(currentSmsEvent.getAddress())) {
                    Log.w("addPrivateAddress","found new address="+ currentSmsEvent.getAddress()+" for number="+currentSmsEvent.getPhoneNumber()+" and addressPattern="+currentSmsEvent.getAddressPattern()+
                            ".The address was added to customAdressesHistory");
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    Set<String> privateAddressPatternKeys=preferences.getStringSet(currentSmsEvent.getPhoneNumber(),null);
                    privateAddressPatternKeys.add(currentSmsEvent.getAddressPattern());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putStringSet(currentSmsEvent.getPhoneNumber(),privateAddressPatternKeys);
                    editor.putString(currentSmsEvent.getPhoneNumber()+currentSmsEvent.getAddressPattern(), currentSmsEvent.getAddress());
                    editor.commit();


                }
            }
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

}