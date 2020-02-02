package com.gnukkuldak.kkuldak_v1;

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
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class Kkuldak_v1 extends Activity {

	public Intent viewPage;
	Val_Kkuldak glo_val = new Val_Kkuldak();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_kkuldak_v1);
/*		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout linear = (LinearLayout) inflater.inflate(
				R.layout.sorry, null);*/


/*		final LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);

	   win.addContentView(linear, paramlinear);*/

		
		// ----xml id 호출----//
		final Button order_btn = (Button) findViewById(R.id.mainorder_btn);
		final Button store_btn = (Button) findViewById(R.id.mainstore_btn);
		final Button mypage_btn = (Button) findViewById(R.id.mainmypage_btn);
		final Button back_btn = (Button) findViewById(R.id.mainback_btn);
		// ----end----//

		// ----버튼클릭시 이벤트 발생 함수----//
		order_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				viewPage = new Intent(Kkuldak_v1.this, MenuListKkuldak_v1.class);
				Log.d("name", "Kkuldak_v1 < 주문버튼 클릭 OK>");
				viewPage.putExtra("part", 0);
				startActivity(viewPage);
			}

		});

		// -----주문버튼클릭시 이미지 변환-----//
		order_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					order_btn.setBackgroundResource(R.drawable.menubtn_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					order_btn.setBackgroundResource(R.drawable.menubtn_b);
				}
				return false;
			}
		});

		store_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				viewPage = new Intent(Kkuldak_v1.this,
						BasketListKkuldak_v1.class);

				Log.d("name", "Kkuldak_v1 <장바구니버튼 클릭 OK>");
				viewPage.putExtra("part", 1);
				startActivity(viewPage);
			}

		});

		// -----주문버튼클릭시 이미지 변환-----//
		store_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					store_btn.setBackgroundResource(R.drawable.basketbtn_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					store_btn.setBackgroundResource(R.drawable.basketbtn_b);
				}
				return false;
			}
		});

		mypage_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				viewPage = new Intent(Kkuldak_v1.this,MypageKkuldak_v1.class);
				Log.d("name", "Kkuldak_v1 <마이페이지버튼 클릭 OK>");
				viewPage.putExtra("part", 2);
				startActivity(viewPage);
			}

		});

		// -----주문버튼클릭시 이미지 변환-----//
		mypage_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mypage_btn.setBackgroundResource(R.drawable.mypagebtn_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mypage_btn.setBackgroundResource(R.drawable.mypagebtn_b);
				}
				return false;
			}
		});
		
		/*
		* 종료 버튼 클릭시
		*/
		back_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				viewPage = new Intent(Kkuldak_v1.this,BackKkuldak_v1.class);
				Log.d("name", "Kkuldak_v1 <기타버튼 클릭 OK>");
				viewPage.putExtra("part", 3);
				startActivity(viewPage);
			}

		});

		// -----주문버튼클릭시 이미지 변환-----//
		back_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					back_btn.setBackgroundResource(R.drawable.backbtn_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					back_btn.setBackgroundResource(R.drawable.backbtn_b);
				}
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kkuldak_v1, menu);
		return true;
	}

}
