package com.example.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.R;
import com.example.service.CdpLocationService;

public class MainActivity extends Activity implements OnClickListener
{
    private ArrayList<RoomInfo> mArrayList = new ArrayList<RoomInfo>();
	CustomAdapter cl;
	ListView commentl;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		commentl = (ListView)findViewById(R.id.roomList);
		Button make_room = (Button)findViewById(R.id.make_room);
		make_room.setOnClickListener(this);
		Button refresh_room = (Button)findViewById(R.id.refresh_room);
		refresh_room.setOnClickListener(this);
	
		cl = new CustomAdapter(MainActivity.this,R.layout.room_row,mArrayList);
		commentl.setAdapter(cl);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.make_room)
		{
			startService(new Intent(this, CdpLocationService.class));
		}
		else if(v.getId() == R.id.refresh_room)
		{
			stopService(new Intent(this, CdpLocationService.class));
			//다시 mArrayList 받아오기
//			cl = new CustomAdapter(MainActivity.this,R.layout.room_row,mArrayList);
//			commentl.setAdapter(cl);
		}
	}
	class CustomAdapter extends BaseAdapter
	{
		Context maincon;
		LayoutInflater inflater;
		ArrayList<RoomInfo> ci;
		int layout;
		int layout2;
		public CustomAdapter(Context context,int layout,ArrayList<RoomInfo> ci)
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
		public void setClist(ArrayList<RoomInfo> clist)
		{
			this.ci=clist;
		}
		@Override
		public RoomInfo getItem(int positon) {
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
			
			TextView room_name = (TextView)cv.findViewById(R.id.room_name);
			TextView room_num = (TextView)cv.findViewById(R.id.room_num);
			
			room_name.setText("제목 : "+ci.get(position).getRoomName());
			room_name.setText("인원 : ("+ci.get(position).getCur()+"/"+ci.get(position).getMax()+")");
			
			final Button btn_joinroom = (Button)cv.findViewById(R.id.btn_joinroom);
			btn_joinroom.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View arg0) 
				{
					new AlertDialog.Builder(MainActivity.this)
					.setTitle("정말로 입장하시겠습니까?")
					.setView(null)
					.setPositiveButton("확인", new DialogInterface.OnClickListener()
					{
					public void onClick(DialogInterface arg0, int arg1)
					{
						if(!AppManager.getInstance().isPossibleInternet())
						{
							FollovDialog.showDialog("인터넷 접속 상태를 확인해 주세요.", MainActivity.this);
						}
						else
						{
							//입장하는 코드 방입장.
						}
						
					}})
					.setNegativeButton("취소", null)
					.show();
				}
			});
			return cv;
		}
	}
}