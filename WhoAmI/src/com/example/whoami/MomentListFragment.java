package com.example.whoami;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 메모 리스트뷰 프레그먼트
 * TODO: 사진 추가
 */

public class MomentListFragment extends ListFragment {
	
	private static final String TAG = "whoami.momentlistfragment";
	public static final String ARG_MOMENT_USING_DAY = "MOMENT_USING_DAY";
	private Context mContext;
	private ArrayList<Moment> mMoments;
	
	// MapFragment 생성시 선택된 App UUID를 받기위한 함수
	public static MomentListFragment newInstance(String usingDay){
		Bundle args = new Bundle();
		args.putSerializable(ARG_MOMENT_USING_DAY, usingDay);
		
		MomentListFragment fragment = new MomentListFragment();
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			String usingDay = (String)getArguments().getSerializable(ARG_MOMENT_USING_DAY);
			mMoments = MomentList.get(getActivity()).loadOndayMoments(usingDay);
		}catch(Exception e){
			mMoments = new ArrayList<Moment>();
		}
		
		ArrayAdapter<Moment> adapter = new MomentAdapter(mMoments);
		setListAdapter(adapter);
	}
	
	// MomentAdapter
	private class MomentAdapter extends ArrayAdapter<Moment> {

		
		public MomentAdapter(ArrayList<Moment> moments){
			super(getActivity(), 0, moments);
		}
		@Override
		public int getCount() {
			return mMoments.size();
		}

		@Override
		public Moment getItem(int position) {
			return mMoments.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView picIV;
			TextView whenTV, whereTV, contentTV,tagTV;
			MomentViewHolder viewHolder;
			
			if(null == convertView){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_moment_list, parent, false);
				picIV = (ImageView)convertView.findViewById(R.id.momentListPic_imageView);
				whenTV = (TextView)convertView.findViewById(R.id.momentListWhen_textView);
				whereTV = (TextView)convertView.findViewById(R.id.momentListWhere_textView);
				contentTV = (TextView)convertView.findViewById(R.id.momentListContent_textView);
				tagTV = (TextView)convertView.findViewById(R.id.momentListTag_textView);
				viewHolder = new MomentViewHolder(picIV, whenTV, whereTV, contentTV, tagTV);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (MomentViewHolder)convertView.getTag();
			}
			
			// TODO: 사진 및 카메라 로직 추가
			//viewHolder.pic.setImageResource(R.drawable.ic_launcher);
			viewHolder.when.setText(getItem(position).getStartDate());
			viewHolder.where.setText(getItem(position).getAddr());
			viewHolder.content.setText(getItem(position).getMurmur());
			viewHolder.tag.setText(getItem(position).getTag());
			
			return convertView;
		}
	}
	
	private class MomentViewHolder{
		public ImageView pic;
		public TextView when;
		public TextView where;
		public TextView content;
		public TextView tag;
		
		public MomentViewHolder(ImageView pic, TextView when, TextView where, TextView content, TextView tag){
			this.pic = pic;
			this.when = when;
			this.where = where;
			this.content = content;
			this.tag = tag;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int pos = info.position;
		MomentAdapter adapter = (MomentAdapter)getListAdapter();
		Moment m = adapter.getItem(pos);
		
		switch(item.getItemId()){
			case R.id.menu_item_delete_moment:
			{
				mMoments.remove(pos);
				MomentList.get(mContext).deleteMoment(m);
				adapter.notifyDataSetChanged();
				return true;
			}
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.moment_list_item_context, menu);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mContext = activity;
	}
	
	
}
