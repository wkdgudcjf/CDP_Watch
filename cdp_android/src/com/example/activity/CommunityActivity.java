package com.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.example.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class CommunityActivity extends Activity implements OnClickListener {
	ImageView image3;
	ImageView image2;
	ImageView image1;
	ImageView image4;
	ImageView image5;
	ImageView image6;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.community_layout);
		image3 = (ImageView)findViewById(R.id.band3);
		image3.setOnClickListener(this);
		image1 = (ImageView)findViewById(R.id.band1);
		image1.setOnClickListener(this);
		image2 = (ImageView)findViewById(R.id.band2);
		image2.setOnClickListener(this);
		image4 = (ImageView)findViewById(R.id.band4);
		image4.setOnClickListener(this);
		image5 = (ImageView)findViewById(R.id.band5);
		image5.setOnClickListener(this);
		image6 = (ImageView)findViewById(R.id.band6);
		image6.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
			case R.id.band3:
			{
				Intent i = new Intent(CommunityActivity.this, SamSungActivity.class);
				i.putExtra("name", "samsung");
				startActivity(i);
				break;
			}
			case R.id.band1:
			{
				Intent i = new Intent(CommunityActivity.this, SamSungActivity.class);
				i.putExtra("name", "4050");
				startActivity(i);
				break;
			}
			case R.id.band2:
			{
				Intent i = new Intent(CommunityActivity.this, SamSungActivity.class);
				i.putExtra("name", "cycle");
				startActivity(i);
				break;
			}
			case R.id.band4:
			{
				Intent i = new Intent(CommunityActivity.this, SamSungActivity.class);
				i.putExtra("name", "sudo");
				startActivity(i);
				break;
			}
			case R.id.band5:
			{
				Intent i = new Intent(CommunityActivity.this, SamSungActivity.class);
				i.putExtra("name", "cycle2");
				startActivity(i);
				break;
			}
			case R.id.band6:
			{
				Intent i = new Intent(CommunityActivity.this, SamSungActivity.class);
				i.putExtra("name", "gwang");
				startActivity(i);
				break;
			}
		}
	}
}
