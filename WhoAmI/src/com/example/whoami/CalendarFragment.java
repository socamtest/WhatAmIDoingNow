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
	
	// 오늘의 정보를 위한 변수
	private TextView mTodayMarkTV; // 메모
	private TextView mTodayPathTV; // 경로
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
		AppList.get(mContext).saveApps(); //활성화 되기전에 파일에 저장
		
		// 오늘날짜로 초기화
		// TODO: 리스트 삭제시 텍스트뷰 실시간 갱신 필요하므로 onResume()에서 파일 로드
		// 성능이슈 발생하므로 수정해야하는 로직 
		mAppListManager.initForDay(getToday());
		mMomentListManager.initForDay(getToday());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		
		mPollService = new PollService(mContext);
		
		// 오늘의 정보 초기화 및 정보(이동위치, 메모등) 로드
		mAppListManager = new AppListManager(mContext);
		mMomentListManager = new MomentListManager(mContext);

		// 오늘날짜로 초기화
		// TODO: 리스트 삭제시 텍스트뷰 실시간 갱신 필요
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
        
        // 오늘의 정보 셋팅
        // TODO: 리스트 제거 및 추가시 텍스트뷰에 바로 적용되지 않음
        mTodayMarkTV = (TextView)v.findViewById(R.id.todayMarkContent_textView);
        mTodayPathTV = (TextView)v.findViewById(R.id.todayParhList_textView);
        setTodayView(mTodayMarkTV, mTodayPathTV);
        
        mCalToday = Calendar.getInstance();
        mCal = Calendar.getInstance();
        selectedYear = mCal.get(Calendar.YEAR);
        selectedMonth = mCal.get(Calendar.MONTH) +1;
        
        setCalendarDate(selectedYear, selectedMonth);
        
        // 이벤트 처리
        mPrevButton.setBackgroundColor(Color.rgb(108, 198, 76));
        mPrevButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// 버튼 클릭시 색 변경
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
				// 버튼 클릭시 색 변경
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
         *  TODO: 해당 날짜에 대한 대쉬보드 역활 추가
         *  ( 이동 지점, 이동지점 수, 책갈피 갯수 등)
         */
        
    	mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				// 해당 날짜에 대한 리스트
		    	Date selectedDay = mArrDate.get(position);
		    	
		    	// 한자리수 앞에 0붙이기
		    	String usingDay;
		    	usingDay = String.format(Locale.KOREA, "%02d%02d%02d", selectedYear, selectedMonth, selectedDay.getDay());
		    	
		    	// 선택된 날짜에 대한 데이터 확인
		    	try{
			    	ArrayList<App> apps = AppList.get(mContext).loadOndayApps(usingDay);
			    	if(0 == apps.size()){
			    		Toast.makeText(getActivity(), "해당 날짜에 대한 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
			    		return;
			    	}
		    	}catch(Exception e){
		    		
		    	}
		    	/*// 선택된 날짜에 대한 리스트를 보기 위한 액티비티 호출
		    	Intent i = new Intent(getActivity(), FrameContainerActivity.class);
		    	i.putExtra(AppListFragment.ARG_APP_USING_DAY, usingDay);
		    	startActivity(i);*/
			}
		});
    	
    	/**
    	 *  Adapter를 사용하는 뷰에 ContextMenu 사용하기
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
    	 * 현재 날짜를 뷰에 표시하기 위하여 생성자에 현재 달력의 year, month 넘겨줌
    	 * 정확한 날짜는 DateAdapter 내에서 선택된 달력과 현재 날짜를 비교하여 표시
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

		// 서비스 설정(On/Off), 파일 저장
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// 서비스 On/Off 옵션메뉴
			if(item.getItemId() == R.id.menu_item_toggle_polling){
				boolean shoudStartAlarm = !mPollService.isServiceAlarmOn(getActivity());
				mPollService.setServiceAlarm(getActivity(), shoudStartAlarm);
				getActivity().invalidateOptionsMenu();
				return true;
			// 책갈피 추가 옵션메뉴
			}else if(item.getItemId() == R.id.menu_item_momentAdd){
				
				Intent i = new Intent(getActivity(), MomentActivity.class);
				startActivity(i);
			// 오늘의 정보 갱신
			}else if(item.getItemId() == R.id.menu_item_todayRefresh){
				
				/**
				 * TODO: 지저분한 로직이므로 수정 필요
				 * 오늘의 메모 및 앱 리스트 추가 삭제시 텍스트뷰가 바로 갱신 되지 않으므로 
				 * onResume()에서 매번 리스트 재로드 하고 텍스트뷰에 내용 있을 경우
				 * setText("")로 화면 지우고 새로 설정
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
		 * 선택된 날짜에 대한 컨텍스트 메뉴
		 * 1. 맵보기, 2. 메모보기 3. 앱보기
		 */
		@Override
		public boolean onContextItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
			int pos = info.position;
			String usingDay = String.format(Locale.KOREAN,"%02d%02d%02d", selectedYear, selectedMonth, pos+1); // 선택된 날짜(아이템 위치 + 1)
			
			ArrayList<App> mTodayApps = AppList.get(mContext).loadOndayApps(usingDay);
			ArrayList<Moment> todayMoment = MomentList.get(mContext).loadOndayMoments(usingDay);
			
			switch(item.getItemId()){
			
				/**
				 * 선택된 날짜에 대한 맵 보기 메뉴
				 */
				case R.id.day_map_menuItem:
				{	
					Log.i(TAG, "onContextItemSelected's day_map_menuItem");
					try{
				    	// 해당 날짜에 대한 정보가 없을 경우 리턴
				    	//ArrayList<App> mTodayApps = AppList.get(mContext).loadOndayApps(usingDay);
				    	if(1>mTodayApps.size()
				    			&& 1>todayMoment.size()){
				    		Toast.makeText(getActivity(), "해당 날짜에 대한 맵 정보가 없습니다.", Toast.LENGTH_SHORT).show();
				    		return true;
				    	}
						// 맵 프레그먼트 실행
				    	// 선택된 날짜에 대한 리스트를 보기 위한 액티비티 호출
				    	Intent i = new Intent(getActivity(), MapActivity.class);
				    	i.putExtra(MapFragment.ARG_APP_USING_DAY, usingDay);
				    	startActivity(i);
					}catch(Exception e){
						Log.e(TAG, "CalendarFragment onContextItemSelected() fail" + e.toString());
					}
					return true;
				}
				
				/**
				 * 책갈피 보기
				 */
				case R.id.day_momentMarkList_menuItem:
				{
					Log.i(TAG, "onContextItemSelected's day_momentMarkList_menuItem");
					//ArrayList<Moment> todayMoment = MomentList.get(mContext).loadOndayMoments(usingDay);
					if(1>todayMoment.size()){
						Toast.makeText(getActivity(), "해당날짜에 메모가 없습니다.", Toast.LENGTH_SHORT).show();
						return true;
					}
					
					Intent i = new Intent(getActivity(), MomentListActivity.class);
					i.putExtra(MomentListFragment.ARG_MOMENT_USING_DAY, usingDay);
					startActivity(i);
					return true;
				}
				
				/**
				 *  선택된 날짜에 대한 앱 사용 리스트 보기
				 */
				case R.id.day_appList_menuItem:
				{
					Log.i(TAG, "onContextItemSelected's day_appList_menuItem");
					if(1>mTodayApps.size()){
			    		Toast.makeText(getActivity(), "해당 날짜에 대한 앱 사용 정보가 없습니다.", Toast.LENGTH_SHORT).show();
			    		return true;
			    	}
					// 선택된 날짜에 대한 리스트를 보기 위한 액티비티 호출
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
			
			// 텍스트뷰 스크롤 기능 추가
			memo.setMovementMethod(new ScrollingMovementMethod());
			path.setMovementMethod(new ScrollingMovementMethod());
			
			// 메모 관련 셋팅
			ArrayList<Moment> memoList = mMomentListManager.getList();
			for(Moment m : memoList){
				memo.setTextColor(Color.rgb(0, 0, 0)); // 글자 색상
				memo.append(Html.fromHtml("<u>" + "태그 #" + m.getTag() + "</u>"));  // 태그 밑줄
				memo.append("\n");
				memo.append(m.getMurmur());
				memo.append("\n\n");
			}
			
			// TODO: 위치정보 없는경우 제거, 바로이전 위치와 동일한 위치 제거, 머무른 시간 계산
			// 이동위치 관련 셋팅
			ArrayList<App> appList = mAppListManager.getList();
			
			String prevPos = null; // 이전 위치
			String nullPos = "해당 위치에 주소 없음"; // 해당 위치에 주소 없음
			
			for(App a : appList){
				// 이전 위치와 동일하거나 주소 없을 경우 넘어감
				if((a.getAddr().equals(nullPos))
						|| (a.getAddr().equals(prevPos)))
					continue;
				
				path.setTextColor(Color.rgb(0,0,0)); // 글자 색상
				path.append(Html.fromHtml("<u>" + a.getUsingTime() + "</u>"));  // 시간 밑줄
				path.append("\n");
				path.append(a.getAddr());
				
				prevPos = a.getAddr(); // 이전 위치 저장
				
				path.append("\n\n");
			}
		}
}
