package com.example.whoami;

import android.support.v4.app.Fragment;

/**
 * MapFragment Activity
 */
public class MapActivity extends SingleFragmentActivity {
	public static final String 	EXTRA_APP_ID="com.example.whoami.app_id";
	
	@Override
	protected Fragment createFragment() {
		String usingDay = (String)getIntent().getSerializableExtra(MapFragment.ARG_APP_USING_DAY);
		return MapFragment.newInstance(usingDay);
	}
}
