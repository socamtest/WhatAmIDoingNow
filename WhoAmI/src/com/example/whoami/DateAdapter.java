package com.example.whoami;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DateAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Date> mArr;
	private LayoutInflater mInflater;
	
	private String mSelectedYearMon;
	private String mToday;
	
	public DateAdapter(Context c, ArrayList<Date> arr){
		this.mContext = c;
		this.mArr = arr;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public DateAdapter(Context c, ArrayList<Date> arr, String selectedYearMon){
		this.mContext = c;
		this.mArr = arr;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.mSelectedYearMon = selectedYearMon;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mArr.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mArr.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.calemdar_date_item, parent, false);
		}
		
		TextView mTextView = (TextView)convertView.findViewById(R.id.calendarDate_textView);
		//textView.setTextSize(30);
		if(mArr.get(position) == null){
			mTextView.setText("");
		}else{
			// 날짜 숫자->문자
			int selDay = mArr.get(position).getDay();
			String day1 = String.format(Locale.KOREA, "%d", selDay); // 한자리 날짜
			String day2 = String.format(Locale.KOREA, "%02d", selDay); // 두자리 날짜
			
			mTextView.setText(day1);
			if(mArr.get(position).getDayOfWeek() == 1){
				mTextView.setTextColor(Color.RED);
			}else if(mArr.get(position).getDayOfWeek() == 7){
				mTextView.setTextColor(Color.BLUE);
			}else{
				mTextView.setTextColor(Color.BLACK);
			}
			mTextView.setBackgroundResource(R.drawable.lineframe);
			//textView.setBackgroundColor(Color.GREEN);
			
			// 현재 날짜에 색표시
			if(true == checkCurrentYearMonth(mSelectedYearMon)){
				if(day2.equals(mToday))
						mTextView.setBackgroundColor(Color.rgb(0,176,240));
			}
		}
		return convertView;
	}
	
	// 달력 연도/달 과 현재의 연도/달 비교
	public boolean checkCurrentYearMonth(String selectedYearMon){
		Calendar mCal = Calendar.getInstance();
        int thisYear = mCal.get(Calendar.YEAR);
        int thisMonth = mCal.get(Calendar.MONTH) +1;
        int thisDay = mCal.get(Calendar.DATE);
        
        String CurreentYearMon = String.format(Locale.KOREA,"%02d%02d", thisYear, thisMonth);
        mToday = String.format("%02d",  thisDay);
        
        if(true == CurreentYearMon.equals(selectedYearMon))
        	return true;
        
        return false;
	}

}
