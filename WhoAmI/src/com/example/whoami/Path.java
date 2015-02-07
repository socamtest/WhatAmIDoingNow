package com.example.whoami;

public class Path {
	
	private String mTime;
	private AppPosition mPos;
	private String mAddr;
	
	public Path(){
		mTime = null;
		mPos = new AppPosition();
		mAddr = null;
	}
	
	public Path(String time, AppPosition pos, String addr){
		this.mTime = time;
		this.mPos = pos;
		this.mAddr = addr;
	}
	
	public Path(Path p){
		this.mTime = p.mTime;
		this.mPos = p.mPos;
		this.mAddr = p.mAddr;
	}

	public String getTime() {
		return mTime;
	}

	public void setTime(String time) {
		mTime = time;
	}

	public AppPosition getPos() {
		return mPos;
	}

	public void setPos(AppPosition pos) {
		mPos = pos;
	}

	public String getAddr() {
		return mAddr;
	}

	public void setAddr(String addr) {
		mAddr = addr;
	}
	
}
