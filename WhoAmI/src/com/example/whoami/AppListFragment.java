package com.example.whoami;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
		Log.i(TAG, "newInstance");
		Bundle args = new Bundle();
		args.putSerializable(ARG_APP_USING_DAY, usingDay);
		AppListFragment fragment = new AppListFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		View v = super.onCreateView(inflater, container, savedInstanceState);
		ListView listView = (ListView)v.findViewById(android.R.id.list);
		registerForContextMenu(listView);
		
		return v;
	}
	
	@Override
	public void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		Log.i(TAG, "onResume");
		// TODO Auto-generated method stub
		super.onResume();
		Log.i(TAG, "AppListFragment onresume()");
		AppList.get(mContext).saveApps(); //활성화 되기전에 파일에 저장
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		
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
		
		ArrayAdapter<App> adapter = new AppAdapter(mApps);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(TAG, "onListItemClick");
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
			Log.i(TAG, "AppAdapter constructor");
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i(TAG, "getView");
			ImageView iconImageView;
			TextView titleTextView, startDateTextView, whereTextView;
			
			// 뷰홀더
			AppViewHolder viewHolder;
			
			if(convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_app_list, parent, false);
				// 앱 아이콘
				iconImageView = (ImageView)convertView.findViewById(R.id.icon_imageView);
				
				// 앱 이름
				titleTextView = (TextView)convertView.findViewById(R.id.appTitle_textView);
				
				// 앱 사용 시간
				startDateTextView = (TextView)convertView.findViewById(R.id.startDate_textView);
				
				// 앱 사용 위치
				whereTextView = (TextView)convertView.findViewById(R.id.where_textView);
				
				viewHolder = new AppViewHolder(iconImageView, titleTextView, 
						startDateTextView, whereTextView);
				
				convertView.setTag(viewHolder);
				
			}else{
				viewHolder = (AppViewHolder)convertView.getTag();
			}
			
			viewHolder.icon.setImageDrawable(getItem(position).getAppIcon(mContext));
			viewHolder.title.setText(getItem(position).getAppName(mContext));
			viewHolder.time.setText(getItem(position).getStartDate());
			viewHolder.position.setText(getItem(position).getAddr());
				
			return convertView;
		}

		@Override
		public int getCount() {
			Log.i(TAG, "getCount");
			// TODO Auto-generated method stub
			return mApps.size();
		}

		@Override
		public App getItem(int position) {
			Log.i(TAG, "getItem");
			// TODO Auto-generated method stub
			return mApps.get(position);
		}
	}
	
	// 뷰홀더 클래스
	public static class AppViewHolder{
		public ImageView icon;
		public TextView title;
		public TextView time;
		public TextView position;
		
		public AppViewHolder(ImageView icon, TextView title, TextView time, TextView position){
			this.icon = icon;
			this.title = title;
			this.time = time;
			this.position = position;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		Log.i(TAG, "onAttach");
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.i(TAG, "onCreateOptionsMenu");
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.fragment_apps, menu);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.i(TAG, "onCreateContextMenu");
		getActivity().getMenuInflater().inflate(R.menu.app_list_item_context,  menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.i(TAG, "onContextItemSelected");
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
}
