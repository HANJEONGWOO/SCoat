package com.gnukkuldak.kkuldak_v1;

import java.util.ArrayList;

import com.gnukkul.kkuldak_v1.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewManager;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

public class MenuListKkuldak_v1 extends Activity {

	public Intent viewPage;
	Intent orderintent;
	static int click_count = 0;
	Button basket_btn;
	Animation anim = null;

	Val_Kkuldak glo_val = new Val_Kkuldak();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window win = getWindow();
		win.setContentView(R.layout.mainlist_v1);
		
		Log.d("액티비티 호출이다","call!!!!!");

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log.d("액티비티 호출이다22","call!!!!!");
		final LinearLayout linear = (LinearLayout) inflater.inflate(
				R.layout.overay_kkuldak_v1, null);

		final Intent intent = new Intent(MenuListKkuldak_v1.this,
				BasketListKkuldak_v1.class);
		Log.d("액티비티 호출이다33","call!!!!!");
		Button end_btn = (Button) linear.findViewById(R.id.testid);
		basket_btn = (Button) findViewById(R.id.basket_btn);

		basket_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					basket_btn
							.setBackgroundResource(R.drawable.basketbtn_tablet_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					basket_btn
							.setBackgroundResource(R.drawable.basketbtn_tablet_b);
				}
				return false;
			}
		});
		final Button back_btn = (Button) findViewById(R.id.back_btn);

		basket_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//finish();
				startActivity(intent);
			}

		});

		back_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}

		});

		back_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					back_btn.setBackgroundResource(R.drawable.barbtn_tablet_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					back_btn.setBackgroundResource(R.drawable.barbtn_tablet_b);
				}
				return false;
			}
		});

		ListView lvList = (ListView) findViewById(R.id.main_lv_list);
		orderintent = new Intent(this, Order_Kkuldak_v1.class);
		Log.d("액티비티 호출이다44ss","call!!!!!");
		final LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		if (click_count == 0) {
			win.addContentView(linear, paramlinear);
		}
		
		ArrayList<MenuKkuldak_v1> personList = new ArrayList<MenuKkuldak_v1>();
		personList.add(new MenuKkuldak_v1(R.drawable.a1));
		personList.add(new MenuKkuldak_v1(R.drawable.a2));
		personList.add(new MenuKkuldak_v1(R.drawable.a3));
		personList.add(new MenuKkuldak_v1(R.drawable.a4));
		personList.add(new MenuKkuldak_v1(R.drawable.a5));

		Menu_AdaptKkuldak_v1 personAdapter = new Menu_AdaptKkuldak_v1(this,
				R.layout.menulist_kkuldak_v1, personList, 2);
		Log.d("액티비티 호출이다55","call!!!!!");

		lvList.setAdapter(personAdapter);

		Log.d("액티비티 호출이다666","call!!!!!");
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				orderintent.putExtra("menu", position);
				startActivity(orderintent);
			}
		});
		

		end_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				click_count++;
				Log.d("count", click_count + "");
				((ViewManager) linear.getParent()).removeView(linear);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kkuldak_v1, menu);
		return true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (glo_val.basket_index == 0) {
			basket_btn.setVisibility(View.INVISIBLE);
		}

		else {
			basket_btn.setVisibility(View.VISIBLE);
			anim =  new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, 
					Animation.RELATIVE_TO_SELF, 0.5f);
			anim.setDuration(1000);
			basket_btn.startAnimation(anim);
		}
	}

}