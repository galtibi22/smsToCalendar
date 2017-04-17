package tbject.com.smstocalendar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Locale;

import tbject.com.smstocalendar.R;

public class OpeningScreen extends CommonActivity {
    private final int TIME_OUT = 2000;
    private static OpeningScreen instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppLang(this);
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_opening_screen);
        final View myLayout = findViewById(R.id.activity_opening_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(OpeningScreen.this, Menu.class);
                startActivity(i);
                finish();
            }
        }, TIME_OUT);

    }



    public static OpeningScreen getInstance() {
        return instance;
    }

    public static void setAppLang(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang= preferences.getString(context.getString(R.string.appLanguage),context.getString(R.string.hebrew));
        Locale local = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = local;
        res.updateConfiguration(conf, dm);
    }

}