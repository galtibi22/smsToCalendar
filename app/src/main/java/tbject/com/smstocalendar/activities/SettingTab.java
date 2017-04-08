package tbject.com.smstocalendar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.preference.PreferenceManager;
import android.view.View;

import tbject.com.smstocalendar.R;

public class SettingTab extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private boolean statusAllowReminder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        statusAllowReminder=((SwitchPreference) findPreference(getString(R.string.allowReminder))).isEnabled();

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
            View preferenceView=(View) this.getActivity().findViewById(R.id.prefernceScreen);
            setDirectionByLan(this.getActivity(),preferenceView);
        }
    }

    /**
     * appLanguage method - set the language of the app by the settings
     */
    private void appLanguage() {
        ListPreference listPreference = (ListPreference) findPreference(getString(R.string.appLanguage));
        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        String selectedLan=listPreference.getValue();
        String currnetLan=getResources().getConfiguration().locale.getLanguage();
        if (!selectedLan.equals(currnetLan)) {
            Intent refresh = new Intent(this, OpeningScreen.class);
            this.startActivity(refresh);
            finish();
        }
    }

    /**
     *setDirectionByLan method - change the direction view by the lan of the app
     */
    public static void setDirectionByLan(Context context, View view){
        String currentLan=view.getResources().getConfiguration().locale.getLanguage();
        if (currentLan.equals(context.getString(R.string.hebrew)))
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    }
    private void allowReminder(){
        SwitchPreference allowReminder= (SwitchPreference) findPreference(getString(R.string.allowReminder));
        if (allowReminder.isChecked()!=statusAllowReminder) {
            ListPreference listPreference = (ListPreference) findPreference(getString(R.string.reminderMethod));
            listPreference.setEnabled(allowReminder.isChecked());
            listPreference = (ListPreference) findPreference(getString(R.string.minutesEventReminder));
            listPreference.setEnabled(allowReminder.isChecked());
            statusAllowReminder=allowReminder.isChecked();
        }
    }




}