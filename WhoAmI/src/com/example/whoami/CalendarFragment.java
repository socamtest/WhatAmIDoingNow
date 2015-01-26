package com.example.whoami;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarFragment extends Fragment {
	
	ArrayList<Date> mArrDate;
	DateAdapter mAdapter;
	Button mPrevButton, mNextButton;
	TextView mTodayText;
	Calendar mCal;
	Calendar mCalToday;
	GridView mGridView;
	int thisYear, thisMonth;
	
	private Context mContext;
	private PollService mPollService;
	
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//AppList.get(mContext).saveApps();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AppList.get(mContext).saveApps(); //Ȱ��ȭ �Ǳ����� ���Ͽ� ����
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
		mPollService = new PollService(mContext);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_calendar, parent, false);
		
		mPrevButton = (Button)v.findViewById(R.id.prev_button);
        mNextButton = (Button)v.findViewById(R.id.next_button);
        mTodayText = (TextView)v.findViewById(R.id.todat_textView);
        mGridView = (GridView)v.findViewById(R.id.calendar_gridView);
        
        mCalToday = Calendar.getInstance();
        mCal = Calendar.getInstance();
        thisYear = mCal.get(Calendar.YEAR);
        thisMonth = mCal.get(Calendar.MONTH) +1;
        
        setCalendarDate(thisYear, thisMonth);
        
        // �̺�Ʈ ó��
        mPrevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(thisMonth > 1){
	    			thisMonth--;
	    			setCalendarDate(thisYear, thisMonth);
	    		}else{
	    			thisYear--;
	    			thisMonth=12;
	    			setCalendarDate(thisYear, thisMonth);
	    		}
			}
		});
        
        mNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(thisMonth < 12){
	    			thisMonth++;
	    			setCalendarDate(thisYear, thisMonth);
	    		}else{
	    			thisYear++;
	    			thisMonth=1;
	    			setCalendarDate(thisYear, thisMonth);
	    		}
			}
		});
        
    	mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				// �ش� ��¥�� ���� ����Ʈ
		    	Date selectedDay = mArrDate.get(position);
		    	
		    	// ���ڸ��� �տ� 0���̱�
		    	String usingDay;
		    	usingDay = String.format("%02d%02d%02d", thisYear, thisMonth, selectedDay.getDay());
		    	//Toast.makeText(getActivity(), usingDay, Toast.LENGTH_SHORT).show();
		    	//Log.i("calendar", usingDay);
		    	
		    	// ���õ� ��¥�� ���� ������ Ȯ��
		    	// TODO: ������ Ȯ�� ������ AppListFragment���� Ȯ�� �� ������� �� ó��
		    	try{
			    	AppJSONSerializer serializer = new AppJSONSerializer(getActivity(), AppList.FILENAME);
			    	ArrayList<App> apps = serializer.loadAppsForDay(usingDay);
			    	if(0 == apps.size()){
			    		Toast.makeText(getActivity(), "�ش� ��¥�� ���� �����Ͱ� �����ϴ�.", Toast.LENGTH_SHORT).show();
			    		return;
			    	}
		    	}catch(Exception e){
		    		
		    	}
		    	// ���õ� ��¥�� ���� ����Ʈ�� ���� ���� ��Ƽ��Ƽ ȣ��
		    	Intent i = new Intent(getActivity(), FrameContainerActivity.class);
		    	i.putExtra(AppListFragment.ARG_APP_USING_DAY, usingDay);
		    	startActivity(i);
			}
		});
		return v;
	}

	public void setCalendarDate(int year, int month){
    	mArrDate = new ArrayList<Date>();
    	mCalToday.set(mCal.get(Calendar.YEAR), month-1, 1);
    	mTodayText.setText(year + "/" + month);
    	
    	int startday = mCalToday.get(Calendar.DAY_OF_WEEK);
    	if(startday != 1){
    		for(int i=0; i<startday-1; i++){
    			mArrDate.add(null);
    		}
    	}
    	mCal.set(Calendar.MONTH,  month-1);
    	
    	for(int i=0; i<mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
    		mCalToday.set(mCal.get(Calendar.YEAR), month-1, (i+1));
    		mArrDate.add(new Date((i+1), mCalToday.get(Calendar.DAY_OF_WEEK)));
    	}
    	mAdapter = new DateAdapter(getActivity(), mArrDate);
    	mGridView.setAdapter(mAdapter);
    }
	
	// test
		// �׽�Ʈ �Ϸ��� AppListFragment�� �ɼ� �޴� �ڵ� �״�� ������
		
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			super.onCreateOptionsMenu(menu, inflater);
			inflater.inflate(R.menu.fragment_apps, menu);
		}

		// ���� ����(On/Off), ���� ����
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			if(item.getItemId() == R.id.menu_item_toggle_polling){
				boolean shoudStartAlarm = !mPollService.isServiceAlarmOn(getActivity());
				mPollService.setServiceAlarm(getActivity(), shoudStartAlarm);
				getActivity().invalidateOptionsMenu();
				return true;
			}else if(item.getItemId() == R.id.menu_item_toggle_save){
				AppList.get(mContext).saveApps();
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		@Override
		public void onPrepareOptionsMenu(Menu menu) {
			super.onPrepareOptionsMenu(menu);
			
			MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
			if(mPollService.isServiceAlarmOn(getActivity())){
				toggleItem.setTitle(R.string.stop_poll_service);
			}else{
				toggleItem.setTitle(R.string.start_poll_service);
			}
			MenuItem toggleItem2 = menu.findItem(R.id.menu_item_toggle_save);
			toggleItem2.setTitle("SAVE");
		}
}
