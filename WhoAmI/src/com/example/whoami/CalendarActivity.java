package com.example.whoami;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class CalendarActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new CalendarFragment();
	}
}
