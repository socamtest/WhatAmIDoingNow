package com.example.whoami;

public class Date {
	private int mDay;
	private int mDayOfWeek;
	
	public Date(int day, int dayOfWeek){
		mDay = day;
		mDayOfWeek = dayOfWeek;
	}

	public int getDay() {
		return mDay;
	}

	public void setDay(int day) {
		mDay = day;
	}

	public int getDayOfWeek() {
		return mDayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		mDayOfWeek = dayOfWeek;
	}

	@Override
	public String toString() {
		return ""+mDay + mDayOfWeek;
	}
}
