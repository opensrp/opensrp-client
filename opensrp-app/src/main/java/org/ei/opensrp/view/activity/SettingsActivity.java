package org.ei.opensrp.view.activity;

import org.ei.opensrp.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;

public class SettingsActivity extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState);

		 addPreferencesFromResource(R.xml.preferences); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(Menu.NONE, 0, 0, "Show current settings");
		return super.onCreateOptionsMenu(menu);
	}
}
