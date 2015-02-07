package com.example.whoami;

import android.support.v4.app.Fragment;

public class MomentListActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		String usingDay = (String)getIntent().getSerializableExtra(MomentListFragment.ARG_MOMENT_USING_DAY);
		return new MomentListFragment().newInstance(usingDay);
	}

}
