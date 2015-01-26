package com.example.whoami;

import java.util.ArrayList;

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
	
	public DateAdapter(Context c, ArrayList<Date> arr){
		this.mContext = c;
		this.mArr = arr;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		TextView textView = (TextView)convertView.findViewById(R.id.calendarDate_textView);
		textView.setTextSize(30);
		if(mArr.get(position) == null){
			textView.setText("");
		}else{
			textView.setText(mArr.get(position).getDay()+"");
			if(mArr.get(position).getDayOfWeek() == 1){
				textView.setTextColor(Color.RED);
			}else if(mArr.get(position).getDayOfWeek() == 7){
				textView.setTextColor(Color.BLUE);
			}else{
				textView.setTextColor(Color.BLACK);
			}
			textView.setBackgroundColor(Color.green(Color.MAGENTA));
		}
		return convertView;
	}

}
