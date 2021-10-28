package com.example.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.R;

public class SanActivity extends Activity 
{
	String name;
	String when;
	int level;
	int icon;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.san_layout);
		final Intent localIntent = getIntent();
		name = localIntent.getStringExtra("name");
		when = localIntent.getStringExtra("when");
		icon = localIntent.getIntExtra("icon",0);
		level = localIntent.getIntExtra("level",0);
		TextView textview = (TextView)findViewById(R.id.text_sanname);
		TextView textview2 = (TextView)findViewById(R.id.text_sanwhen);
		ImageView image = (ImageView)findViewById(R.id.icon_where);
		textview.setText(name);
		textview2.setText(when);
		if(icon == R.drawable.icon_mountain)
		{
			image.setImageResource(R.drawable.mountain_icon);
		}
		else
		{
			image.setImageResource(R.drawable.activity_riding2);
		}
	}
}
