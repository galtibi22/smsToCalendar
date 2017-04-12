package tbject.com.smstocalendar.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import tbject.com.smstocalendar.DataManager;
import tbject.com.smstocalendar.R;
import tbject.com.smstocalendar.pojo.SettingsProp;

public class SettingTab extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private boolean statusAllowReminder;
    MaterialDialog deleteHistoryMaterialDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        statusAllowReminder=((SwitchPreference) findPreference(getString(R.string.allowReminder))).isEnabled();

        Preference preference = (Preference) findPreference(getString(R.string.deleteHistory));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onPreferenceClick(Preference preference) {
                deleteHistory();
                return true;
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void deleteHistory() {
        GravityEnum gravityEnum;
        String currentLan=this.getResources().getConfiguration().locale.getLanguage();
        if (currentLan.equals(this.getString(R.string.hebrew)))
            gravityEnum = GravityEnum.END;

        else
            gravityEnum=GravityEnum.START;
        deleteHistoryMaterialDialog=new MaterialDialog.Builder(this)
                .title(getString(R.string.pref_are_you_sure_delete_history)).titleGravity(gravityEnum)
                .positiveText(R.string.yes).buttonsGravity(gravityEnum)
                .negativeText(R.string.no)
                .cancelable(false)
                .backgroundColor(getColor(android.R.color.white))
                .contentColor(getColor(android.R.color.black))
                .titleColor(getColor(android.R.color.black))
                .positiveColor(getColor(R.color.dailog_button_text))
                .negativeColor(getColor(R.color.dailog_button_text))
                .buttonRippleColor(getColor(android.R.color.white))
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        DataManager datamanager=new DataManager(getApplicationContext());
                        datamanager.deleteSmsEventDataFromDisk(getApplicationContext(), SettingsProp.HISTORY_EVENT_DATA);
                        Toast.makeText(getApplicationContext(),getString(R.string.history_successfully_deleted),
                                Toast.LENGTH_SHORT).show();
                        deleteHistoryMaterialDialog.dismiss();

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteHistoryMaterialDialog.dismiss();
                    }
                }).show();


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