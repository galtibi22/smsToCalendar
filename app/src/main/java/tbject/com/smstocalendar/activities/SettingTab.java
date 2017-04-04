package tbject.com.smstocalendar.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

import tbject.com.smstocalendar.R;

public class SettingTab extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        allowReminder();
        appLanguage();
    }



    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    public void appLanguage() {
        ListPreference listPreference = (ListPreference) findPreference(getString(R.string.appLanguage));
        String lang = listPreference.getValue();
        Locale local = new Locale(lang);
        String currentLocal = this.getResources().getConfiguration().locale.getDisplayName();
        if (!local.getDisplayName().equals(currentLocal)) {
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = local;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, OpeningScreen.class);
            startActivity(refresh);
            finish();
        }
    }
    public void allowReminder(){
        SwitchPreference allowReminder= (SwitchPreference) findPreference(getString(R.string.allowReminder));
        if (allowReminder.isChecked()) {
            ListPreference listPreference = (ListPreference) findPreference(getString(R.string.reminderMethod));
            listPreference.setEnabled(allowReminder.isChecked());
            listPreference = (ListPreference) findPreference(getString(R.string.minutesEventReminder));
            listPreference.setEnabled(allowReminder.isChecked());
        }
    }

}