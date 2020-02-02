package com.gnukkuldak.kkuldak_v1;

import com.gnukkul.kkuldak_v1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivitynotify extends Activity implements Runnable {
	
	Val_Kkuldak glo_val = new Val_Kkuldak();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Acitivity(화면) 초기화
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		// main.xml 파일을 읽어들인다.
		setContentView(R.layout.main_v1);
		glo_val.basket_index=0;
		// 로딩 다이얼로그를 표시한다.
		//CDialog.showLoading(this);

		// 딜레이 시간 수행을 위한 스레드 작업
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		// 3초 딜레이
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}

		// 다이얼로그 닫음
		CDialog.hideLoading();

		// 화면 이동
		Intent intent = new Intent(this, Kkuldak_v1.class);
		startActivity(intent);

		// 단말기 Back버튼을 눌렀을 때, 인트로 화면으로 돌아오지 않도록 인트로 화면을 종료
		finish();
	}
}