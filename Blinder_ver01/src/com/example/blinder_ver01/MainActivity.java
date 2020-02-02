package com.example.blinder_ver01;

import android.os.Bundle;
import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
	View mPage1, mPage2, mPage3, mPage4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	

		mPage1 = findViewById(R.id.page1);
		mPage2 = findViewById(R.id.page2);
		mPage3 = findViewById(R.id.page3);
		mPage4 = findViewById(R.id.page4);
		
		findViewById(R.id.btnpage1).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage2).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage3).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage4).setOnClickListener(mClickListener);
	}
	
	Button.OnClickListener mClickListener = new Button.OnClickListener()	{
		
		public void onClick(View v)	{
			mPage1.setVisibility(View.VISIBLE);
			mPage2.setVisibility(View.INVISIBLE);
			mPage3.setVisibility(View.INVISIBLE);
			mPage4.setVisibility(View.INVISIBLE);
			
			switch(v.getId())	{
			case R.id.btnpage1:
				mPage1.setVisibility(View.VISIBLE);
				break;
			case R.id.btnpage2:
				mPage2.setVisibility(View.VISIBLE);
				break;
			case R.id.btnpage3:
				mPage3.setVisibility(View.VISIBLE);
				break;
			case R.id.btnpage4:
				mPage4.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

	
		@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
