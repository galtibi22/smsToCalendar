package tbject.com.smstocalendar.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import tbject.com.smstocalendar.Adapters.SmsEventAdapter;
import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.ContactInfo;
import tbject.com.smstocalendar.pojo.SettingsProp;
import tbject.com.smstocalendar.pojo.SmsEvent;

public class HistoryTab extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my);
        setContentView(R.layout.activity_history_tab);
        buildHistoryEvents();

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void buildHistoryEvents(){
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        SettingTab.setDirectionByLan(this,recList);

        recList.setLayoutManager(llm);
        ArrayList<SmsEvent> smsEvents=SmsEvent.readDataFromDisk(this, SettingsProp.HISTORY_EVENT_DATA);
        SmsEventAdapter ca = new SmsEventAdapter(smsEvents);
        recList.setAdapter(ca);
    }
    private List<ContactInfo> createList(int size) {

        List<ContactInfo> result = new ArrayList<ContactInfo>();
        for (int i=1; i <= size; i++) {
            ContactInfo ci = new ContactInfo();
            ci.name = ContactInfo.NAME_PREFIX + i;
            ci.surname = ContactInfo.SURNAME_PREFIX + i;
            ci.email = ContactInfo.EMAIL_PREFIX + i + "@test.com";

            result.add(ci);

        }

        return result;
    }
    @Override
    public void onResume(){
        super.onResume();
        buildHistoryEvents();
    }
}



