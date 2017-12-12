package org.ei.opensrp.path.activity;

import org.ei.opensrp.path.BuildConfig;
import org.ei.opensrp.path.R;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.view.activity.DrishtiApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import util.PathConstants;

import static org.ei.opensrp.util.Log.logError;
import static org.ei.opensrp.util.Log.logInfo;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }


    public static class MyPreferenceFragment extends PreferenceFragment {

        public static int clicksonpreference = 0;
        private PreferenceCategory hidden_preference;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = super.onCreateView(inflater,container,savedInstanceState);

            return v;
        }
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference baseUrlPreference = findPreference("DRISHTI_BASE_URL");
            if (baseUrlPreference != null) {
                EditTextPreference baseUrlEditTextPreference = (EditTextPreference) baseUrlPreference;
                baseUrlEditTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue != null) {
                            updateUrl(newValue.toString());
                        }
                        return true;
                    }
                });
                baseUrlEditTextPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        Log.v("you clicked", ""+clicksonpreference);
                        clicksonpreference ++;
                        if(clicksonpreference > 20){
                            Toast.makeText(DrishtiApplication.getInstance(),"You have unlocked hidden settings",Toast.LENGTH_LONG).show();
                            showHiddenPreference();
                            clicksonpreference = 0;
                        }else{
//                            hideHiddenPreference();
                        }

                        return true;
                    }
                });

            }
            Preference source = findPreference("OPENMRS_UNIQUE_ID_SOURCE");
            if (source != null) {
                EditTextPreference OPENMRS_UNIQUE_ID_SOURCE = (EditTextPreference) source;
                OPENMRS_UNIQUE_ID_SOURCE.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue != null) {
                            updateSource(newValue.toString());
                        }
                        return true;
                    }
                });
            }
            Preference vaccinetime = findPreference("VACCINE_SYNC_TIME");
            if (source != null) {
                EditTextPreference VACCINE_SYNC_TIME = (EditTextPreference) vaccinetime;
                VACCINE_SYNC_TIME.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue != null) {
                            updateVaccineTime(newValue.toString());
                        }
                        return true;
                    }
                });
            }
            Preference openmrsUrl = findPreference("OPENMRS_URL");
            if (source != null) {
                EditTextPreference OPENMRS_URL = (EditTextPreference) openmrsUrl;
                OPENMRS_URL.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue != null) {
                            updateOpenMrsURL(newValue.toString());
                        }
                        return true;
                    }


                });
            }
            hideHiddenPreference();
        }

        private void hideHiddenPreference() {
            PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference(getResources().getString(R.string.serverPreferenceScreen));
            hidden_preference = (PreferenceCategory) findPreference("hidden_preference");
            preferenceScreen.removePreference(hidden_preference);
        }
        private void showHiddenPreference() {
            PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference(getResources().getString(R.string.serverPreferenceScreen));
//            PreferenceCategory hidden_preference = (PreferenceCategory) findPreference("hidden_preference");
            preferenceScreen.addPreference(hidden_preference);
        }

        private void updateOpenMrsURL(String s) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
            allSharedPreferences.savePreference("OPENMRS_URL",s);
        }

        private void updateVaccineTime(String s) {
            if(!s.isEmpty()) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
                allSharedPreferences.savePreference("VACCINE_SYNC_TIME", s);
            }
        }

        private void updateSource(String s) {
            if(!s.isEmpty()) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
                allSharedPreferences.savePreference("OPENMRS_UNIQUE_ID_SOURCE", s);
            }
        }


        private void updateUrl(String baseUrl) {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

                URL url = new URL(baseUrl);

                String base = url.getProtocol() + "://" + url.getHost();
                int port = url.getPort();

                logInfo("Base URL: " + base);
                logInfo("Port: " + port);

                allSharedPreferences.saveHost(base);
                allSharedPreferences.savePort(port);

                logInfo("Saved URL: " + allSharedPreferences.fetchHost(""));
                logInfo("Port: " + allSharedPreferences.fetchPort(0));
            } catch (MalformedURLException e) {
                logError("Malformed Url: " + baseUrl);
            }
        }


    }

}
