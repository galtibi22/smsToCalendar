package tbject.com.smstocalendar.activities;

import android.Manifest;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import tbject.com.smstocalendar.R;

public class Menu extends TabActivity implements OnTabChangeListener {
    public static final int NUM_OF_PLAYERS = 10;
    private static Menu instance;
    public Point screenSize;
    private TabHost tabHost;
    private GoogleApiClient client;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide android upper bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        instance = this;
        requestPermissions(new String[]{Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.READ_SMS,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_CONTACTS},1);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setScreenSize();
        initTabs();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public void onTabChanged(String tabId) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initTabs() {
        // Get TabHost Refference
        tabHost = getTabHost();
        String currentLan=getResources().getConfiguration().locale.getLanguage();
        if (currentLan.equals(getString(R.string.hebrew)))
            tabHost.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else
            tabHost.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        // Set TabChangeListener called when tab changed
        tabHost.setOnTabChangedListener(this);

        TabHost.TabSpec spec;
        Intent intent;

        // Create  Intents to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, SettingTab.class);
        spec = tabHost.newTabSpec("First").setIndicator(buildTabIndicator(getString(R.string.setting),this,R.drawable.tab_setting))
                .setContent(intent);
        //Add intent to tab
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, HistoryTab.class);
        spec = tabHost.newTabSpec("Second").setIndicator(buildTabIndicator(getString(R.string.history),this,R.drawable.tab_history))
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.getTabWidget().setCurrentTab(0);
    }

    private void setScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);
    }

    public static Menu getInstance() {
        return instance;
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Menu Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private View buildTabIndicator(String text, Context context,int icon) {

        // Inflate the layout file we defined above
        View view = LayoutInflater.from(context).inflate(R.layout.tab_indicator, null);

        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text);
        ImageView imageView=(ImageView) view.findViewById(R.id.tabIcon);
        imageView.setImageDrawable(getDrawable(icon));
        return view;

    }
}
