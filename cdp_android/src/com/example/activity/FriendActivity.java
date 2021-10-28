package com.example.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.R;

public class FriendActivity extends Activity {
	ArrayList<String> flist;
	
	public int[] mRes = new int[] {
		        R.drawable.p1, R.drawable.p2, R.drawable.p3,
		        R.drawable.p4, R.drawable.p5, R.drawable.p6,
		    
		 };
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_layout);
		flist = new ArrayList<String>();
		flist.add("가");
		flist.add("나");
		flist.add("다");
		flist.add("라");
		RequestListAdapter cl = new RequestListAdapter(FriendActivity.this,R.layout.entry_row2,flist);
		ListView commentl;
		commentl = (ListView)findViewById(R.id.listview2);
		commentl.setAdapter(cl);
		
	}
	class ImageAdapter extends PagerAdapter {
		 
	     @Override
	     public int getCount() {
	         return mRes.length;
	     }
	      @Override
	     public boolean isViewFromObject(View view, Object object) {
	         return view == ((ImageView) object);
	     }
	 
	     @Override
	      public Object instantiateItem(ViewGroup container, int position) {
	          ImageView imageView = new ImageView(getApplicationContext());
	          imageView.setImageResource(mRes[position]);
	          ((ViewPager) container).addView(imageView, 0);
	          return imageView;
	      }
	 
	     @Override
	     public void destroyItem(ViewGroup container, int position, Object object) {
	          ((ViewPager) container).removeView((ImageView) object);
	      }
	   }
	class RequestListAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater inflater;
		ArrayList<String> ci;
		int layout;
		int layout2;
		public RequestListAdapter(Context context,int layout,ArrayList<String> ci)
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
		public void setClist(ArrayList<String> clist)
		{
			this.ci=clist;
		}
		@Override
		public String getItem(int positon) {
			return ci.get(positon);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View cv, ViewGroup parent) 
		{
			if(cv==null)
			{				
				cv = this.inflater.inflate(layout, parent,false);
			}
			
			ViewPager mPager = (ViewPager) cv.findViewById(R.id.fri_pager);
		    ImageAdapter adapter = new ImageAdapter();
		    mPager.setAdapter(adapter);
			if(ci.get(position).equals("가"))
			{
				ImageView image = (ImageView) cv.findViewById(R.id.icon_fri);
				image.setImageResource(R.drawable.icon_album);
			}
			else if(ci.get(position).equals("나"))
			{
				ImageView image = (ImageView) cv.findViewById(R.id.icon_fri);
				image.setImageResource(R.drawable.icon_album2);
			}
			else if(ci.get(position).equals("다"))
			{
				ImageView image = (ImageView) cv.findViewById(R.id.icon_fri);
				image.setImageResource(R.drawable.icon_album3);
			}
			else if(ci.get(position).equals("라"))
			{
				ImageView image = (ImageView) cv.findViewById(R.id.icon_fri);
				image.setImageResource(R.drawable.icon_album4);
			}
			
			return cv;
		}
	}
}
