package com.example.whoami;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarFragment extends Fragment {
	
	private static final String TAG = "whoami.calendarfragment";
	ArrayList<Date> mArrDate;
	DateAdapter mAdapter;
	Button mPrevButton, mNextButton;
	TextView mTodayText;
	Calendar mCal;
	Calendar mCalToday;
	GridView mGridView;
	int selectedYear, selectedMonth;
	private View mProgressContainer;
	
	// ������ ������ ���� ����
	private TextView mTodayMarkTV; // �޸�
	private TextView mTodayPathTV; // ���
	private AppListManager mAppListManager;
	private MomentListManager mMomentListManager;
	
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
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AppList.get(mContext).saveApps(); //Ȱ��ȭ �Ǳ����� ���Ͽ� ����
		
		// ���ó�¥�� �ʱ�ȭ
		// TODO: ����Ʈ ������ �ؽ�Ʈ�� �ǽð� ���� �ʿ��ϹǷ� onResume()���� ���� �ε�
		// �����̽� �߻��ϹǷ� �����ؾ��ϴ� ���� 
		mAppListManager.initForDay(getToday());
		mMomentListManager.initForDay(getToday());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		mPollService = new PollService(mContext);
		
		// ������ ���� �ʱ�ȭ �� ����(�̵���ġ, �޸��) �ε�
		mAppListManager = new AppListManager(mContext);
		mMomentListManager = new MomentListManager(mContext);

		// ���ó�¥�� �ʱ�ȭ
		// TODO: ����Ʈ ������ �ؽ�Ʈ�� �ǽð� ���� �ʿ�
		mAppListManager.initForDay(getToday());
		mMomentListManager.initForDay(getToday());
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_calendar, parent, false);
		
		mPrevButton = (Button)v.findViewById(R.id.prev_button);
        mNextButton = (Button)v.findViewById(R.id.next_button);
        mTodayText = (TextView)v.findViewById(R.id.todat_textView);
        mGridView = (GridView)v.findViewById(R.id.calendar_gridView);
        
        mProgressContainer = v.findViewById(R.id.calendar_progressContainer);
        mProgressContainer.setVisibility(View.GONE);
        
        // ������ ���� ����
        // TODO: ����Ʈ ���� �� �߰��� �ؽ�Ʈ�信 �ٷ� ������� ����
        mTodayMarkTV = (TextView)v.findViewById(R.id.todayMarkContent_textView);
        mTodayPathTV = (TextView)v.findViewById(R.id.todayParhList_textView);
        setTodayView(mTodayMarkTV, mTodayPathTV);
        
        mCalToday = Calendar.getInstance();
        mCal = Calendar.getInstance();
        selectedYear = mCal.get(Calendar.YEAR);
        selectedMonth = mCal.get(Calendar.MONTH) +1;
        
        setCalendarDate(selectedYear, selectedMonth);
        
        // �̺�Ʈ ó��
        mPrevButton.setBackgroundColor(Color.rgb(108, 198, 76));
        mPrevButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// ��ư Ŭ���� �� ����
				if(MotionEvent.ACTION_DOWN == event.getAction()){
					mPrevButton.setBackgroundColor(Color.argb(122, 175, 224, 158));
				}
				if(MotionEvent.ACTION_UP == event.getAction()){
					mPrevButton.setBackgroundColor(Color.rgb(108, 198, 76));
				}
				
				return false;
			}
		});
        mPrevButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(selectedMonth > 1){
	    			selectedMonth--;
	    			setCalendarDate(selectedYear, selectedMonth);
	    		}else{
	    			selectedYear--;
	    			selectedMonth=12;
	    			setCalendarDate(selectedYear, selectedMonth);
	    		}
			}
		});
        
        mNextButton.setBackgroundColor(Color.rgb(229, 87, 202));
        mNextButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// ��ư Ŭ���� �� ����
				if(MotionEvent.ACTION_DOWN == event.getAction()){
					mNextButton.setBackgroundColor(Color.argb(122, 239, 151, 222));
				}
				if(MotionEvent.ACTION_UP == event.getAction()){
					mNextButton.setBackgroundColor(Color.rgb(229, 87, 202));
				}
				
				return false;
			}
		});
        mNextButton.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View v) {
				if(selectedMonth < 12){
	    			selectedMonth++;
	    			setCalendarDate(selectedYear, selectedMonth);
	    		}else{
	    			selectedYear++;
	    			selectedMonth=1;
	    			setCalendarDate(selectedYear, selectedMonth);
	    		}
			}
		});
        
        /**
         *  TODO: �ش� ��¥�� ���� �뽬���� ��Ȱ �߰�
         *  ( �̵� ����, �̵����� ��, å���� ���� ��)
         */
        
    	mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				// �ش� ��¥�� ���� ����Ʈ
		    	Date selectedDay = mArrDate.get(position);
		    	
		    	// ���ڸ��� �տ� 0���̱�
		    	String usingDay;
		    	usingDay = String.format(Locale.KOREA, "%02d%02d%02d", selectedYear, selectedMonth, selectedDay.getDay());
		    	
		    	// ���õ� ��¥�� ���� ������ Ȯ��
		    	try{
			    	ArrayList<App> apps = AppList.get(mContext).loadOndayApps(usingDay);
			    	if(0 == apps.size()){
			    		Toast.makeText(getActivity(), "�ش� ��¥�� ���� �����Ͱ� �����ϴ�.", Toast.LENGTH_SHORT).show();
			    		return;
			    	}
		    	}catch(Exception e){
		    		
		    	}
		    	/*// ���õ� ��¥�� ���� ����Ʈ�� ���� ���� ��Ƽ��Ƽ ȣ��
		    	Intent i = new Intent(getActivity(), FrameContainerActivity.class);
		    	i.putExtra(AppListFragment.ARG_APP_USING_DAY, usingDay);
		    	startActivity(i);*/
			}
		});
    	
    	/**
    	 *  Adapter�� ����ϴ� �信 ContextMenu ����ϱ�
    	 */
    	registerForContextMenu(mGridView);
    	mGridView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				getActivity().getMenuInflater().inflate(R.menu.calendar_day_item_context, menu);
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
    	
    	/**
    	 * ���� ��¥�� �信 ǥ���ϱ� ���Ͽ� �����ڿ� ���� �޷��� year, month �Ѱ���
    	 * ��Ȯ�� ��¥�� DateAdapter ������ ���õ� �޷°� ���� ��¥�� ���Ͽ� ǥ��
    	 */
    	String selctedYearMon = getSelectedMonth(year, month);
    	mAdapter = new DateAdapter(getActivity(), mArrDate, selctedYearMon);
    	mGridView.setAdapter(mAdapter);
    }
	
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			super.onCreateOptionsMenu(menu, inflater);
			inflater.inflate(R.menu.fragment_apps, menu);
		}

		// ���� ����(On/Off), ���� ����
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// ���� On/Off �ɼǸ޴�
			if(item.getItemId() == R.id.menu_item_toggle_polling){
				boolean shoudStartAlarm = !mPollService.isServiceAlarmOn(getActivity());
				mPollService.setServiceAlarm(getActivity(), shoudStartAlarm);
				getActivity().invalidateOptionsMenu();
				return true;
			// å���� �߰� �ɼǸ޴�
			}else if(item.getItemId() == R.id.menu_item_momentAdd){
				
				Intent i = new Intent(getActivity(), MomentActivity.class);
				startActivity(i);
			// ������ ���� ����
			}else if(item.getItemId() == R.id.menu_item_todayRefresh){
				
				/**
				 * TODO: �������� �����̹Ƿ� ���� �ʿ�
				 * ������ �޸� �� �� ����Ʈ �߰� ������ �ؽ�Ʈ�䰡 �ٷ� ���� ���� �����Ƿ� 
				 * onResume()���� �Ź� ����Ʈ ��ε� �ϰ� �ؽ�Ʈ�信 ���� ���� ���
				 * setText("")�� ȭ�� ����� ���� ����
				 */
				
				if(0 != mTodayMarkTV.getText().length()){
					mTodayMarkTV.setText("");
				}
				if(0 != mTodayPathTV.getText().length()){
					mTodayPathTV.setText("");
				}
				setTodayView(mTodayMarkTV, mTodayPathTV);
				
			}
			return super.onOptionsItemSelected(item);
		}

		@Override
		public void onPrepareOptionsMenu(Menu menu) {
			super.onPrepareOptionsMenu(menu);
			
			MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
			if(mPollService.isServiceAlarmOn(getActivity())){
				//toggleItem.setTitle(R.string.stop_poll_service);
				toggleItem.setIcon(android.R.drawable.btn_star_big_on);
			}else{
				//toggleItem.setTitle(R.string.start_poll_service);
				toggleItem.setIcon(android.R.drawable.btn_star_big_off);
			}
		}

		/**
		 * ���õ� ��¥�� ���� ���ؽ�Ʈ �޴�
		 * 1. �ʺ���, 2. �޸𺸱� 3. �ۺ���
		 */
		@Override
		public boolean onContextItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
			int pos = info.position;
			String usingDay = String.format(Locale.KOREAN,"%02d%02d%02d", selectedYear, selectedMonth, pos+1); // ���õ� ��¥(������ ��ġ + 1)
			
			ArrayList<App> mTodayApps = AppList.get(mContext).loadOndayApps(usingDay);
			ArrayList<Moment> todayMoment = MomentList.get(mContext).loadOndayMoments(usingDay);
			
			switch(item.getItemId()){
			
				/**
				 * ���õ� ��¥�� ���� �� ���� �޴�
				 */
				case R.id.day_map_menuItem:
				{	
					Log.i(TAG, "onContextItemSelected's day_map_menuItem");
					try{
				    	// �ش� ��¥�� ���� ������ ���� ��� ����
				    	//ArrayList<App> mTodayApps = AppList.get(mContext).loadOndayApps(usingDay);
				    	if(1>mTodayApps.size()
				    			&& 1>todayMoment.size()){
				    		Toast.makeText(getActivity(), "�ش� ��¥�� ���� �� ������ �����ϴ�.", Toast.LENGTH_SHORT).show();
				    		return true;
				    	}
						// �� �����׸�Ʈ ����
				    	// ���õ� ��¥�� ���� ����Ʈ�� ���� ���� ��Ƽ��Ƽ ȣ��
				    	Intent i = new Intent(getActivity(), MapActivity.class);
				    	i.putExtra(MapFragment.ARG_APP_USING_DAY, usingDay);
				    	startActivity(i);
					}catch(Exception e){
						Log.e(TAG, "CalendarFragment onContextItemSelected() fail" + e.toString());
					}
					return true;
				}
				
				/**
				 * å���� ����
				 */
				case R.id.day_momentMarkList_menuItem:
				{
					Log.i(TAG, "onContextItemSelected's day_momentMarkList_menuItem");
					//ArrayList<Moment> todayMoment = MomentList.get(mContext).loadOndayMoments(usingDay);
					if(1>todayMoment.size()){
						Toast.makeText(getActivity(), "�ش糯¥�� �޸� �����ϴ�.", Toast.LENGTH_SHORT).show();
						return true;
					}
					
					Intent i = new Intent(getActivity(), MomentListActivity.class);
					i.putExtra(MomentListFragment.ARG_MOMENT_USING_DAY, usingDay);
					startActivity(i);
					return true;
				}
				
				/**
				 *  ���õ� ��¥�� ���� �� ��� ����Ʈ ����
				 */
				case R.id.day_appList_menuItem:
				{
					Log.i(TAG, "onContextItemSelected's day_appList_menuItem");
					if(1>mTodayApps.size()){
			    		Toast.makeText(getActivity(), "�ش� ��¥�� ���� �� ��� ������ �����ϴ�.", Toast.LENGTH_SHORT).show();
			    		return true;
			    	}
					// ���õ� ��¥�� ���� ����Ʈ�� ���� ���� ��Ƽ��Ƽ ȣ��
			    	Intent i = new Intent(getActivity(), FrameContainerActivity.class);
			    	i.putExtra(AppListFragment.ARG_APP_USING_DAY, usingDay);
			    	startActivity(i);
					return true;
				}
			}
			return super.onContextItemSelected(item);
		}
		
		public String getSelectedMonth(int year, int month){
			String selectedMon = String.format("%02d%02d", year, month);
			return selectedMon;
		}
		
		public String getToday(){
			Calendar mCal = Calendar.getInstance();
	        int year = mCal.get(Calendar.YEAR);
	        int mon = mCal.get(Calendar.MONTH) +1;
	        int day = mCal.get(Calendar.DATE);
	        
	        return String.format(Locale.KOREA, "%02d%02d%02d", year, mon, day);
		}
		
		public void setTodayView(TextView memo, TextView path){
			
			// �ؽ�Ʈ�� ��ũ�� ��� �߰�
			memo.setMovementMethod(new ScrollingMovementMethod());
			path.setMovementMethod(new ScrollingMovementMethod());
			
			// �޸� ���� ����
			ArrayList<Moment> memoList = mMomentListManager.getList();
			for(Moment m : memoList){
				memo.setTextColor(Color.rgb(0, 0, 0)); // ���� ����
				memo.append(Html.fromHtml("<u>" + "�±� #" + m.getTag() + "</u>"));  // �±� ����
				memo.append("\n");
				memo.append(m.getMurmur());
				memo.append("\n\n");
			}
			
			// TODO: ��ġ���� ���°�� ����, �ٷ����� ��ġ�� ������ ��ġ ����, �ӹ��� �ð� ���
			// �̵���ġ ���� ����
			ArrayList<App> appList = mAppListManager.getList();
			
			String prevPos = null; // ���� ��ġ
			String nullPos = "�ش� ��ġ�� �ּ� ����"; // �ش� ��ġ�� �ּ� ����
			
			for(App a : appList){
				// ���� ��ġ�� �����ϰų� �ּ� ���� ��� �Ѿ
				if((a.getAddr().equals(nullPos))
						|| (a.getAddr().equals(prevPos)))
					continue;
				
				path.setTextColor(Color.rgb(0,0,0)); // ���� ����
				path.append(Html.fromHtml("<u>" + a.getUsingTime() + "</u>"));  // �ð� ����
				path.append("\n");
				path.append(a.getAddr());
				
				prevPos = a.getAddr(); // ���� ��ġ ����
				
				path.append("\n\n");
			}
		}
}
