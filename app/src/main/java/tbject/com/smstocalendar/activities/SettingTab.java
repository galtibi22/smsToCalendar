package tbject.com.smstocalendar.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import  android.preference.ListPreference;
import android.support.v7.preference.PreferenceManager;

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
        SwitchPreference allowReminder= (SwitchPreference) findPreference(getString(R.string.allowReminder));
        ListPreference listPreference=(ListPreference)findPreference(getString(R.string.reminderMethod));
        listPreference.setEnabled(allowReminder.isChecked());
        listPreference=(ListPreference)findPreference(getString(R.string.minutesEventReminder));
        listPreference.setEnabled(allowReminder.isChecked());
        System.out.println("onSharedPreferenceChanged");
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

}