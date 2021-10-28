package com.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.R;

public class SamSungActivity extends Activity {
	String name;
	ImageView top_text;
	private ViewPager mPager;
	private ViewPager mPager2;
	private ViewPager mPager3;
	public int[] mRes = new int[] {
	        R.drawable.notice_board, R.drawable.text_2, R.drawable.text_2,
	        R.drawable.text_3, R.drawable.text_4, R.drawable.text_5,
	        R.drawable.text_6, R.drawable.text_7, R.drawable.text_8,
	 };
	
	 
	public int[] mRes2 = new int[] {
	        R.drawable.picture1, R.drawable.picture2, R.drawable.picture3,
	        R.drawable.picture4, R.drawable.picture5, R.drawable.picture6,
	        R.drawable.picture7, R.drawable.picture8, R.drawable.picture9,
	        R.drawable.picture10, R.drawable.picture11, R.drawable.picture12,
	        R.drawable.picture13, R.drawable.picture14, R.drawable.picture15,
	        R.drawable.picture16, R.drawable.picture17, R.drawable.picture18,
	        R.drawable.picture19, R.drawable.picture20, R.drawable.picture21,
	        R.drawable.picture22, R.drawable.picture23, R.drawable.picture24,
	        R.drawable.picture25,
	 };
	
	public int[] mRes3 = new int[] {
	        R.drawable.activity_riding, R.drawable.activity_rmountain, R.drawable.activity_rmountain,
	        R.drawable.activity_riding2, R.drawable.activity_rmountain, R.drawable.activity_rmountain,
	        R.drawable.activity_rmountain, R.drawable.activity_rmountain, R.drawable.activity_rmountain,
	 };
	
	public String[] mRes4 = new String[] {
	        "Today","8.14","8.10",
	        "7.23","7.11","6.30",
	        "6.16","5.12","4.14",
	 };
	public String[] mRes5 = new String[] {
	        "한강잠원지구","관악산","수락산",
	        "여의도엑스포","북한산","백두산",
	        "제주도한라산","대호만","소백산",
	 };
	
	class ImageAdapter3 extends PagerAdapter {
		 
	     @Override
	     public int getCount() {
	         return mRes3.length/3;
	     }
	      @Override
	     public boolean isViewFromObject(View view, Object object) {
	         return view == ((AbsoluteLayout) object);
	     }
	 
	     @Override
	      public Object instantiateItem(ViewGroup container, int position) {
	    	 AbsoluteLayout ll = (AbsoluteLayout) LayoutInflater.from(SamSungActivity.this).inflate(R.layout.activity_layout, null, false);
	    	 final int a = position;
	    	 if(3*position<9)
	    	 {
	    		 
	    		 ImageView imageView = (ImageView)ll.findViewById(R.id.ac_1);
		          imageView.setImageResource(mRes3[3*position]);
		          imageView.setOnClickListener(new Button.OnClickListener()
		          {
					@Override
					public void onClick(View v)
					{
						Intent i = new Intent(SamSungActivity.this, HangangActivity.class);
						i.putExtra("name", mRes4[3*a]);
						i.putExtra("when", mRes5[3*a]);
						startActivity(i);
					}
		          });
		          TextView textView = (TextView)ll.findViewById(R.id.ac_1_top);
		          textView.setText(mRes4[3*position]);
		          TextView textView2 = (TextView)ll.findViewById(R.id.ac_1_bottom);
		          textView2.setText(mRes5[3*position]);
		          if(position==0)
		          {
		        	  textView.setTextColor(Color.rgb(106, 188, 100));
		        	  textView2.setTextColor(Color.rgb(106, 188, 100));
		          }
		          
	    	 }
	    	 if(3*position+1<9)
	    	 {
	    		 ImageView imageView = (ImageView)ll.findViewById(R.id.ac_2);
		          imageView.setImageResource(mRes3[3*position+1]);
		          imageView.setOnClickListener(new Button.OnClickListener()
		          {
					@Override
					public void onClick(View v)
					{
						Intent i = new Intent(SamSungActivity.this, GwanAkActivity.class);
						i.putExtra("name", mRes4[3*a+1]);
						i.putExtra("when", mRes5[3*a+1]);
						startActivity(i);
					}
		          });
		          TextView textView = (TextView)ll.findViewById(R.id.ac_2_top);
		          textView.setText(mRes4[3*position+1]);
		          TextView textView2 = (TextView)ll.findViewById(R.id.ac_2_bottom);
		          textView2.setText(mRes5[3*position+1]);
	    	 }
	    	 if(3*position+2<9)
	    	 {
	    		 ImageView imageView = (ImageView)ll.findViewById(R.id.ac_3);
		          imageView.setImageResource(mRes3[3*position+2]);
		          imageView.setOnClickListener(new Button.OnClickListener()
		          {
					@Override
					public void onClick(View v)
					{
						Intent i = new Intent(SamSungActivity.this, GwanAkActivity.class);
						i.putExtra("name", mRes4[3*a+2]);
						i.putExtra("when", mRes5[3*a+2]);
						startActivity(i);
					}
		          });
		          TextView textView = (TextView)ll.findViewById(R.id.ac_3_top);
		          textView.setText(mRes4[3*position+2]);
		          TextView textView2 = (TextView)ll.findViewById(R.id.ac_3_bottom);
		          textView2.setText(mRes5[3*position+2]);
	    	 }
	    	         	          
	          ((ViewPager) container).addView(ll, 0);
	          return ll;
	      }
	 
	     @Override
	     public void destroyItem(ViewGroup container, int position, Object object) {
	          ((ViewPager) container).removeView((AbsoluteLayout) object);
	      }
	   }
	class ImageAdapter2 extends PagerAdapter {
		 
	     @Override
	     public int getCount() {
	         return mRes2.length/3+1;
	     }
	      @Override
	     public boolean isViewFromObject(View view, Object object) {
	         return view == ((LinearLayout) object);
	     }
	 
	     @Override
	      public Object instantiateItem(ViewGroup container, int position) {
	    	 LinearLayout ll = (LinearLayout) LayoutInflater.from(SamSungActivity.this).inflate(R.layout.picture_layout2, null, false);
	    	 if(3*position<25)
	    	 {
	    		 ImageView imageView = (ImageView)ll.findViewById(R.id.image_1);
		          imageView.setImageResource(mRes2[3*position]);
	    	 }
	    	 if(3*position+1<25)
	    	 {
	    		 ImageView imageView2 = (ImageView)ll.findViewById(R.id.image_2);
		          imageView2.setImageResource(mRes2[3*position+1]);
	    	 }
	    	 if(3*position+2<25)
	    	 {
	    		   ImageView imageView3 = (ImageView)ll.findViewById(R.id.image_3);
			          imageView3.setImageResource(mRes2[3*position+2]);
	    	 }
	    	         	          
	          ((ViewPager) container).addView(ll, 0);
	          return ll;
	      }
	 
	     @Override
	     public void destroyItem(ViewGroup container, int position, Object object) {
	          ((ViewPager) container).removeView((LinearLayout) object);
	      }
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
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.samsung_layout);
		top_text = (ImageView)findViewById(R.id.top_text);
		final Intent localIntent = getIntent();
		name = localIntent.getStringExtra("name");
		mPager = (ViewPager) findViewById(R.id.content_pager);
        ImageAdapter adapter = new ImageAdapter();
        mPager.setAdapter(adapter);
        mPager2 = (ViewPager) findViewById(R.id.pictures2);
        ImageAdapter2 adapter2 = new ImageAdapter2();
	    mPager2.setAdapter(adapter2);
	    mPager3 = (ViewPager) findViewById(R.id.activity_1);
        ImageAdapter3 adapter3 = new ImageAdapter3();
	    mPager3.setAdapter(adapter3);
		switch(name)
		{
			case "samsung":
			{
				top_text.setImageResource(R.drawable.top_text);
				break;
			}
			case "cycle":
			{
				top_text.setImageResource(R.drawable.top_text5);		
				break;
			}
			case "cycle2":
			{
				top_text.setImageResource(R.drawable.top_text6);	
				break;
			}
			case "gwang":
			{
				top_text.setImageResource(R.drawable.top_text2);	
				break;
			}
			case "4050":
			{
				top_text.setImageResource(R.drawable.top_text3);
				break;
			}
			case "sudo":
			{
				top_text.setImageResource(R.drawable.top_text4);
				break;
			}
		}
	}
}
