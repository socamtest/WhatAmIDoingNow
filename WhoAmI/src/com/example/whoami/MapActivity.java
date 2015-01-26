package com.example.whoami;

import java.util.UUID;

import android.support.v4.app.Fragment;

/**
 * MapFragment Activity
 */
public class MapActivity extends SingleFragmentActivity {
	public static final String 	EXTRA_APP_ID="com.example.whoami.app_id";
	
	@Override
	protected Fragment createFragment() {
		UUID id = (UUID)getIntent().getSerializableExtra(MapFragment.ARG_APP_ID);
		return MapFragment.newInstance(id);
	}
}
