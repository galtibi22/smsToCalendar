package tbject.com.smstocalendar.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import tbject.com.smstocalendar.Adapters.SmsEventAdapter;
import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SettingsProp;
import tbject.com.smstocalendar.pojo.SmsEvent;

public class HistoryTab extends Activity {
    private RecyclerView recList;
    private ArrayList<SmsEvent> smsEvents;
    private SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private MaterialDialog materialDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_tab);
        buildHistoryEvents();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void buildHistoryEvents(){
        recList = (RecyclerView) findViewById(R.id.cardList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        SettingTab.setDirectionByLan(this,recList);

        recList.setLayoutManager(llm);
        smsEvents=SmsEvent.readDataFromDisk(this, SettingsProp.HISTORY_EVENT_DATA);
        SmsEventAdapter ca = new SmsEventAdapter(smsEvents);
        recList.setAdapter(ca);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initDetailsDailog(View view){
        int index = recList.getChildLayoutPosition(view);
        buildDetailsDialog(smsEvents.get(index));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void buildDetailsDialog(SmsEvent smsEvent) {
        Log.d("buildSmsEventDialog","method start");
        GravityEnum gravityEnum;
        String currentLan=this.getResources().getConfiguration().locale.getLanguage();
        if (currentLan.equals(this.getString(R.string.hebrew)))
            gravityEnum = GravityEnum.END;

        else
            gravityEnum=GravityEnum.START;
        String content=getString(R.string.phone_number)+":"+smsEvent.getPhoneNumber()+"\n"
                +getString(R.string.date_accepted)+":"+dateFormat.format(smsEvent.getAccepted())+"\n"
                +getString(R.string.orig_message)+":"+"\n"
                +smsEvent.getDescription();

        materialDialog=new MaterialDialog.Builder(this)
                .title(getString(R.string.details_title)).titleGravity(gravityEnum)
                .content(content).contentGravity(gravityEnum)
                .positiveText(R.string.close).buttonsGravity(gravityEnum)
                .cancelable(false)
                .backgroundColor(getColor(R.color.mainBackground))
                .contentColor(getColor(android.R.color.black))
                .titleColor(getColor(android.R.color.black))
                .positiveColor(getColor(android.R.color.darker_gray))
                .buttonRippleColor(getColor(android.R.color.white))
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        materialDialog.dismiss();

                    }
                }) .show();
        Log.d("buildSmsEventDialog","method finish");

    }

    @Override
    public void onResume(){
        super.onResume();
        buildHistoryEvents();
    }
}



