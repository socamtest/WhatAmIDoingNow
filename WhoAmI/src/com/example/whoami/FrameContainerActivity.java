package com.example.whoami;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

/**
 * AppListFragment Activity
 */

public class FrameContainerActivity extends SingleFragmentActivity {
    @Override
	protected Fragment createFragment() {
    	String usingDay = (String)getIntent().getSerializableExtra(AppListFragment.ARG_APP_USING_DAY);
		return AppListFragment.newInstance(usingDay);
	}
}
