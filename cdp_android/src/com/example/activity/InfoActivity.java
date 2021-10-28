package com.example.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.R;

public class InfoActivity extends Activity implements OnItemClickListener{
	ArrayList<SanInfo> flist;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_layout);
		flist = new ArrayList<SanInfo>();
		flist.add(new SanInfo("도봉산 원통산 ","2014.08.21",R.drawable.icon_mountain,3));
		flist.add(new SanInfo("수락산            ","2014.08.19",R.drawable.icon_mountain,2));
		flist.add(new SanInfo("여의도한강지구","2014.08.08",R.drawable.icon_river,0));
		flist.add(new SanInfo("잠원한강지구   ","2014.08.01",R.drawable.icon_river,0));
		flist.add(new SanInfo("북한산            ","2014.07.30",R.drawable.icon_mountain,4));
		flist.add(new SanInfo("두만강            ","2014.07.23",R.drawable.icon_river,0));
		flist.add(new SanInfo("한라산            ","2014.07.14",R.drawable.icon_mountain,5));
		flist.add(new SanInfo("소백산            ","2014.06.02",R.drawable.icon_mountain,4));
		flist.add(new SanInfo("금강               ","2014.06.24",R.drawable.icon_river,0));
		flist.add(new SanInfo("백두산        ","2014.05.05",R.drawable.icon_mountain,6));
		RequestListAdapter cl = new RequestListAdapter(InfoActivity.this,R.layout.entry_row,flist);
		ListView commentl;
		commentl = (ListView)findViewById(R.id.listview);
		commentl.setAdapter(cl);
		commentl.setOnItemClickListener(this);
	}
	class RequestListAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater inflater;
		ArrayList<SanInfo> ci;
		int layout;
		int layout2;
		public RequestListAdapter(Context context,int layout,ArrayList<SanInfo> ci)
		{
			this.maincon=context;
			this.layout=layout;
			this.ci=ci;
			this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			return ci.size();
		}
		public void setClist(ArrayList<SanInfo> clist)
		{
			this.ci=clist;
		}
		@Override
		public SanInfo getItem(int positon) {
			return ci.get(positon);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View cv, ViewGroup parent) 
		{
			final int pos = position;
			if(cv==null)
			{				
				cv = this.inflater.inflate(layout, parent,false);
			}
//			cv.setClickable(true);
//			cv.setOnTouchListener((new OnTouchListener() {
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					final LinearLayout ll = (LinearLayout)v;
//					if(event.getAction() == MotionEvent.ACTION_UP)
//					{
//						if ((int) event.getX() < 0
//								|| (int) event.getX() > ll
//										.getWidth()
//								|| (int) event.getY() < 0
//								|| (int) event.getY() > ll
//										.getHeight()) 
//						{
//							ll.setBackgroundColor(Color.WHITE);
//						}
//						else
//						{
//							ll.setBackgroundColor(Color.WHITE);
//							Intent i = new Intent(InfoActivity.this, SanActivity.class);
//							i.putExtra("name", ci.get(pos).getName());
//							i.putExtra("when", ci.get(pos).getWhen());
//							i.putExtra("icon", ci.get(pos).getImage());
//							i.putExtra("level", ci.get(pos).getLevel());
//							startActivity(i);
//						}
//					}
//					else if(event.getAction() == MotionEvent.ACTION_DOWN)
//					{
//						ll.setBackgroundColor(Color.GREEN);
//					}
//					return false;
//				}
//			}));
//			
			TextView when = (TextView)cv.findViewById(R.id.text_when);
			TextView san = (TextView)cv.findViewById(R.id.text_san);
			LinearLayout level = (LinearLayout)cv.findViewById(R.id.text_level);
			ImageView icon = (ImageView)cv.findViewById(R.id.icon_river);
			
			san.setText(ci.get(position).getName());
			when.setText(ci.get(position).getWhen());
			int count = ci.get(position).getLevel();
			icon.setImageResource(ci.get(position).getImage());
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			level.removeAllViews();
			for(int i=0;i<count;i++)
			{
				ImageView iamge = new ImageView(getApplicationContext());
				iamge.setImageResource(R.drawable.icon_circle);
				level.addView(iamge,params);
			}
			return cv;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
	{
		  // 클릭된 아이템의 포지션을 이용해 어댑터뷰에서 아이템을 꺼내온다.
		SanInfo ci = (SanInfo) parent.getItemAtPosition(position);
		Intent i = new Intent(InfoActivity.this, SanActivity.class);
		i.putExtra("name", ci.getName());
		i.putExtra("when", ci.getWhen());
		i.putExtra("icon", ci.getImage());
		i.putExtra("level", ci.getLevel());
		startActivity(i); 
	}
}
