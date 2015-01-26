package com.example.whoami;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 앱 리스트 프레그먼트
 */

public class AppListFragment extends ListFragment {
	
	private static final String TAG = "whoami.applistfragment";
	public static final String ARG_APP_USING_DAY = "APP_USING_DAY";
	private PollService mPollService;
	private static Context mContext;
	private ArrayList<App> mApps;
	private AppJSONSerializer mSerialize;
	private String mUsingDay;
	
	public static AppListFragment newInstance(String usingDay){
		Bundle args = new Bundle();
		args.putSerializable(ARG_APP_USING_DAY, usingDay);
		AppListFragment fragment = new AppListFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		ListView listView = (ListView)v.findViewById(android.R.id.list);
		registerForContextMenu(listView);
		
		return v;
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
		Log.i(TAG, "AppListFragment onresume()");
		AppList.get(mContext).saveApps(); //활성화 되기전에 파일에 저장
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		mPollService = new PollService(mContext);
		
		// 인자로 받은 날짜에 대한 리스트 생성
		mUsingDay = (String)getArguments().getSerializable(ARG_APP_USING_DAY);
		
		mSerialize = new AppJSONSerializer(mContext, AppList.FILENAME);
		try{
			mApps = mSerialize.loadAppsForDay(mUsingDay);
			if(0 == mApps.size()){
				Toast.makeText(getActivity(), "해당 날짜에 대한 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
			}
			Log.i(TAG, "AppListFragment onCreate() mSerialize.loadApps() Call");
		}catch(Exception e){
			Log.e(TAG, "AppListFragment onCreate() mSerialize.loadApps() Call fail");
			mApps = new ArrayList<App>();
		}
		
		// TODO: 
		//main ui에서 리스트뷰 갱신 안하면 error(런타임 오류 발생)
		//mApps = AppList.get(mContext).getApps();
		ArrayAdapter<App> adapter = new AppAdapter(mApps);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		try{
			App app = (App)(getListAdapter()).getItem(position);
			
			// 맵 프레그먼트 실행
			
			Intent i = new Intent(getActivity(), MapActivity.class);
			i.putExtra(MapFragment.ARG_APP_ID, app.getUId());
			startActivity(i);
		}catch(Exception e){
			Log.e(TAG, "AppListFragment onListItemClick() fail" + e.toString());
		}
	}

	private class AppAdapter extends ArrayAdapter<App>{
		public AppAdapter(ArrayList<App> apps){
			super(getActivity(), 0, apps);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_app_list, null);
			}
			
			App a = getItem(position);
			ImageView iconImageView = (ImageView)convertView.findViewById(R.id.icon_imageView);
			try{
				// 앱 아이콘 가져오기
				PackageManager pm = getActivity().getPackageManager();
				Drawable icon = pm.getApplicationIcon(a.getTitle());
				iconImageView.setImageDrawable(icon);
				
				TextView titleTextView = (TextView)convertView.findViewById(R.id.appTitle_textView);
				titleTextView.setText(a.getTitle());
				
				TextView startDateTextView = (TextView)convertView.findViewById(R.id.startDate_textView);
				startDateTextView.setText(a.getStartDate());
				
				TextView whatTextView = (TextView)convertView.findViewById(R.id.where_textView);
				whatTextView.setText(getAddress(a.getAppPos().getLatitude(), a.getAppPos().getLongitude()));
				
			}catch(Exception e){
				Log.e(TAG, "AppListFragment AppAdapter getView() fail " + e.toString());
			}
			return convertView;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_apps, menu);
	}

	// 서비스 설정(On/Off), 파일 저장
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.app_list_item_context,  menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int pos = info.position;
		AppAdapter adapter = (AppAdapter)getListAdapter();
		App app = adapter.getItem(pos);
		
		switch(item.getItemId()){
			case R.id.menu_item_delete_app:
			{
				mApps.remove(pos);
				AppList.get(mContext).deleteApp(app);
				adapter.notifyDataSetChanged();
				return true;
			}
		}
		return super.onContextItemSelected(item);
	}
	
	public String getAddress(double la, double lo){
		return GPSManager.get(mContext).getAddressFromLocation(la, lo);
	}
}
