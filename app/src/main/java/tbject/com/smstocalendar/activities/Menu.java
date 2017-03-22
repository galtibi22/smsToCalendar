package tbject.com.smstocalendar.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import tbject.com.smstocalendar.R;

public class Menu extends TabActivity implements OnTabChangeListener{
    public static final int NUM_OF_PLAYERS=10;
    private static Menu instance;
    public Point screenSize;
    private TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide android upper bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        instance=this;
        setScreenSize();
        initTabs();

    }

    @Override
    public void onTabChanged(String tabId) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initTabs(){
        // Get TabHost Refference
        tabHost = getTabHost();

        // Set TabChangeListener called when tab changed
        tabHost.setOnTabChangedListener(this);

        TabHost.TabSpec spec;
        Intent intent;

        // Create  Intents to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, SettingTab.class);
        spec = tabHost.newTabSpec("First").setIndicator("Setting")
                .setContent(intent);
        //Add intent to tab
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, HistoryTab.class);
        spec = tabHost.newTabSpec("Second").setIndicator("History")
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.getTabWidget().setCurrentTab(0);
    }

    private void setScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);
    }

    public static Menu getInstance() {
        return instance;
    }

}
