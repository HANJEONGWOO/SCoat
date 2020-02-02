package com.gnukkuldak.kkuldak_v1;

import com.gnukkul.kkuldak_v1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements Runnable {
	
	Val_Kkuldak glo_val = new Val_Kkuldak();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Acitivity(ȭ��) �ʱ�ȭ
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		// main.xml ������ �о���δ�.
		setContentView(R.layout.sorry);
		glo_val.basket_index=0;
		// �ε� ���̾�α׸� ǥ���Ѵ�.

		CDialog.showLoading(this);

		// ������ �ð� ������ ���� ������ �۾�
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		// 3�� ������
		try {
			Thread.sleep(3500);
		} catch (Exception e) {
		}

		// ���̾�α� ����
		CDialog.hideLoading();

		// ȭ�� �̵�
		Intent intent = new Intent(this, MainActivitynotify.class);
		startActivity(intent);

		// �ܸ��� Back��ư�� ������ ��, ��Ʈ�� ȭ������ ���ƿ��� �ʵ��� ��Ʈ�� ȭ���� ����
		finish();
	}
}