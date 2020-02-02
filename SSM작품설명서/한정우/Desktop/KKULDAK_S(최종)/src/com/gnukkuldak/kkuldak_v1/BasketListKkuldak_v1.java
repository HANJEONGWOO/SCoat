package com.gnukkuldak.kkuldak_v1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import com.gnukkul.kkuldak_v1.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewManager;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class BasketListKkuldak_v1 extends Activity implements
		SwipeDismissListViewTouchListener.OnDismissCallback {

	public Intent viewPage;
	Intent intent;

	ConnectivityManager cManager;
	NetworkInfo mobile;
	NetworkInfo wifi;

	Val_Kkuldak glo_val = new Val_Kkuldak();
	ArrayAdapter<MenuKkuldak_v1> adapt;
	ArrayList<MenuKkuldak_v1> personList;

	TextView all_price;
	ListView lvList;

	Button basket_btn;
	Button mypage_btn;

	// -----�ڿ��� �����ϱ����� ����-----//
	static int click_count = 0;
	int temp;
	int price = 0;
	int img_id[];
	int amout[];
	int input_Amount[];
	int input_Money[];

	private String input_Name[], input_Taste[], input_Size[], output_Result;
	private String error;
	String menu[];
	String taste[];
	String money[];
	String textPrice;
	String time;
	String phone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window win = getWindow();
		win.setContentView(R.layout.basket_list_v1);
		// -------------------------3G/4G/Wifi������¸�
		// Ȯ���ϱ����Ѱ���------------------------

		cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		// -------------------------3G/4G/Wifi������¸�
		// Ȯ���ϱ����Ѱ���------------------------

		Calendar cal = Calendar.getInstance();
		time = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1) + ""
				+ cal.get(Calendar.DAY_OF_MONTH) + "";
		Log.d("time ", time);

		mypage_btn = (Button) findViewById(R.id.mypage_btn);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final LinearLayout linear = (LinearLayout) inflater.inflate(
				R.layout.basketoveray_kkuldak_v1, null);

		final LinearLayout linear2 = (LinearLayout) inflater.inflate(
				R.layout.basket_list_kkuldak_v1, null);

		final LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);

		if (click_count == 0) {
			win.addContentView(linear, paramlinear);
		}

		TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		glo_val.phone = telManager.getLine1Number();

		Log.d("glo_val.phone", glo_val.phone + "");

		Button end_btn = (Button) linear.findViewById(R.id.testid);
		final Button back_btn = (Button) findViewById(R.id.back_btn);
		final TextView lay = (TextView) linear2.findViewById(R.id.name);

		back_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}

		});

		mypage_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				intent = new Intent(BasketListKkuldak_v1.this,
						MypageKkuldak_v1.class);

				startActivity(intent);
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

		mypage_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					mypage_btn
							.setBackgroundResource(R.drawable.mypagebtn_tablet_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mypage_btn
							.setBackgroundResource(R.drawable.mypagebtn_tablet_b);
				}
				return false;
			}
		});

		// -----xml�ڿ� ȣ��-----//
		lvList = (ListView) findViewById(R.id.main_bk_list);
		basket_btn = (Button) findViewById(R.id.order);
		all_price = (TextView) findViewById(R.id.all_price);

		basket_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					basket_btn.setBackgroundResource(R.drawable.basket_btn_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					basket_btn.setBackgroundResource(R.drawable.basket_btn_b);
				}
				return false;
			}
		});

		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				view.setBackgroundColor(Color.RED);
			}
		});

		if (glo_val.basket_index == 0) {
			basket_btn.setVisibility(View.INVISIBLE);
			all_price.setVisibility(View.INVISIBLE);
		}

		else {
			basket_btn.setVisibility(View.VISIBLE);
			all_price.setVisibility(View.VISIBLE);
		}

		personList = new ArrayList<MenuKkuldak_v1>();
		Menu_AdaptKkuldak_v1 personAdapter = new Menu_AdaptKkuldak_v1(this,
				R.layout.basket_list_kkuldak_v1, personList, 1);

		menu = glo_val.basket_menu;
		taste = glo_val.basket_taste;
		money = glo_val.basket_money;
		img_id = glo_val.img_id;
		amout = glo_val.basket_amount;

		price = 0;
		for (int i = 0; i < glo_val.basket_index; i++) {
			personList.add(new MenuKkuldak_v1("[ �޴��� ] " + menu[i], "[��] "
					+ taste[i], "[����] " + glo_val.basket_amount[i] + "",
					"[����] " + money[i], img_id[i]));
			Log.d("repaint", "repaint");

			// ----������ �Ľ��ϴ� �κ� ----//
			glo_val.menu_price[i] = (Integer.parseInt(((money[i].split(" ")[1])
					.split(" ")[0]).split("��")[0])) * glo_val.basket_amount[i];
			price = price + glo_val.menu_price[i];
			// ----end----//
		}
		textPrice = "�� �����ݾ� :" + price + "��";
		all_price.setText(textPrice);
		adapt = new ArrayAdapter<MenuKkuldak_v1>(this,
				android.R.layout.simple_list_item_1, personList);

		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				lvList, BasketListKkuldak_v1.this);
		lvList.setAdapter(personAdapter);
		lvList.setOnTouchListener(touchListener);
		lvList.setOnScrollListener(touchListener.makeScrollListener());

		end_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				click_count++;
				((ViewManager) linear.getParent()).removeView(linear);
			}
		});

		basket_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				glo_val.phone = glo_val.phone + "";
				Log.d("�������", "here");
				if (!glo_val.phone.equals(null+"")) {
					Log.d("�������2", "here");
					if (mobile.isConnected() || wifi.isConnected()) {
						// 3G �Ǵ� WiFi �� ����Ǿ� ���� ���
						if (!glo_val.phone.equals(null)) {
							Log.d("�������", "here");
							AlertDialog diaBox = createDialogBox();
							diaBox.show();
						}

					} else
						Toast.makeText(BasketListKkuldak_v1.this,
								"3G/4G �Ǵ� Wifi�� ����Ǿ����� �ʽ��ϴ�. ������ �ٽýõ����ּ���.^^",
								Toast.LENGTH_SHORT).show();

				}

				else
					Toast.makeText(BasketListKkuldak_v1.this,
							"�˼��մϴ�. ����ϰ��ִ� �������δ� ����� �Ұ����մϴ�.",
							Toast.LENGTH_SHORT).show();
			}

		});

	}

	private class inputDB extends AsyncTask<Void, Void, String> {
		@Override
		// ��׶��忡�� �۾��� �۾��� ������.
		protected String doInBackground(Void... params) {
			String temp;
			Log.d("here", "i am2");
			temp = inputData();
			return temp;
		}

		protected void onPostExecute(String temp) {

		}
		// ��׶��� �۾� ������(���⼭�� inputData�� ���������� ������)
		// ������ �۾�.
		// �� �̰� ���ĸ� doInBackground���� ui thread(onCreate)�κ� �ǵ�� ����������.
		// �׷��� onPostExecute���� �۾� ����� ǥ���ϵ��� �Ѵ�.

	}

	// �� �����忡�� ����� �� �Լ����� ����
	// 1. input_btn�� �����忡 ž��� �Լ�
	public String inputData() {
		try {

			// for���� ���� ����
			int i = 0;

			// ��ٱ��Ͽ��ִ� ��系���� DB�� ���������� For��
			for (i = 0; i < glo_val.basket_index; i++) {

				// URL �����ϰ� �����ϱ�
				URL url = new URL("http://203.255.39.254/insert_menu_test.php"); // URL
																					// ����
				HttpURLConnection http = (HttpURLConnection) url
						.openConnection(); // ����
				// ���� ��� ���� - �⺻���� �����̴�
				http.setDefaultUseCaches(false);
				http.setDoInput(true);// �������� �б� ��� ����
				http.setDoOutput(true);// ������ ���� ��� ����
				http.setRequestMethod("POST");// ���� ����� POST <- ���ȶ����� �̰� ���ܴ�..

				// �������� ������ <Form>���� ���� �Ѿ�� �Ͱ� ���� ������� ó���϶�� �� �˷��ش�
				http.setRequestProperty("content-type",
						"application/x-www-form-urlencoded");

				// ������ �ǵ帱 �κ��� ���⼭ ���ʹ�.(������ �⺻ �����̴� �����Ϸ����������� �� Ctrl+C ->
				// Ctrl+V�ض�)
				// ������ �� ����
				// ���ۿ��� StringBuffer�� ��Ŷ���� ���� �������Ѵ�.
				// ��� ���� String�� ��Ŷ���� ���� ���� �������� ������ �������ִ� php���Ͽ� ������ ����ǰ�
				// ó���ȴ�.
				StringBuffer buffer = new StringBuffer();
				buffer.append("time").append("=").append(time).append("&");// php
																			// ������
																			// ��
																			// ����
				buffer.append("name").append("=").append(input_Name[i])
						.append("&");// php ������ �� ����
				buffer.append("taste").append("=").append(input_Taste[i])
						.append("&");// php ���� �տ� '$' ������ �ʴ´�
				buffer.append("amount").append("=").append(input_Amount[i])
						.append("&");// php ���� �տ� '$' ������ �ʴ´�
				buffer.append("size").append("=").append(input_Size[i])
						.append("&");// php ���� �տ� '$' ������ �ʴ´�
				buffer.append("money").append("=").append(input_Money[i])
						.append("&");// php ���� �տ� '$' ������ �ʴ´�
				buffer.append("phone").append("=").append(glo_val.phone)
						.append("&");// php ���� �տ� '$' ������ �ʴ´�
				buffer.append("state").append("=").append("�������");// ����������'&'���

				OutputStreamWriter outStream = new OutputStreamWriter(
						http.getOutputStream(), "UTF8");

				PrintWriter writer = new PrintWriter(outStream);
				writer.write(buffer.toString());
				writer.flush();

				// --------------------------
				// �������� ���۹ޱ�
				// --------------------------
				InputStreamReader tmp = new InputStreamReader(
						http.getInputStream(), "UTF8");

				BufferedReader reader = new BufferedReader(tmp);
				StringBuilder builder = new StringBuilder();
				String str;
				while ((str = reader.readLine()) != null) {// �������� ���δ����� ������
															// ���̹Ƿ� ���δ����� �д´�
					builder.append(str + "\n");
				}
				output_Result = builder.toString();// ���۰���� ���� ������ ����
				Log.d("here", "i am3");
			}

		} catch (MalformedURLException e) {
			//
		} catch (IOException e) {
			error = e.toString();
		} // try
		glo_val.basket_index = 0;// ��ٱ��� �ʱ�ȭ
		return output_Result;// �۾� ��� �Ѱܹ������� ������?
	} // HttpPostData

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kkuldak_v1, menu);
		return true;
	}

	@Override
	public void onDismiss(ListView listView, int[] reverseSortedPositions) {
		int id = 0;
		for (int i = 0; i < reverseSortedPositions.length; i++) {
			Log.v("MainActivity", i + " position " + reverseSortedPositions[i]);
			adapt.remove(adapt.getItem(reverseSortedPositions[i]));
			id = reverseSortedPositions[i];
		}
		glo_val.removeData(id);
		glo_val.basket_index--;
		glo_val.printData();
		Log.d("onDismiss", "call!!");
		Log.d("count", glo_val.basket_index + "");
		price = 0;
		personList.clear();
		for (int i = 0; i < glo_val.basket_index; i++) {

			Log.d("log", "i" + i + "" + menu[i]);
			Log.d("log", "i" + i + "" + taste[i]);
			Log.d("log", "i" + i + "" + money[i]);
			Log.d("repaint", "repaint2");
			personList.add(new MenuKkuldak_v1("[ �޴��� ] " + menu[i], "[��] "
					+ taste[i], "[����] " + glo_val.basket_amount[i] + "",
					"[����] " + money[i], img_id[i]));

			price = price
					+ (Integer
							.parseInt(((money[i].split(" ")[1]).split(" ")[0])
									.split("��")[0])) * glo_val.basket_amount[i];
		}

		if (glo_val.basket_index == 0) {
			basket_btn.setVisibility(View.INVISIBLE);
			all_price.setVisibility(View.INVISIBLE);
		}

		else {
			basket_btn.setVisibility(View.VISIBLE);
			all_price.setVisibility(View.VISIBLE);
		}

		textPrice = "�� �����ݾ� :" + price + "��";
		all_price.setText(textPrice);
		adapt.notifyDataSetChanged();
	}

	private AlertDialog createDialogBox() {
		AlertDialog mybox = new AlertDialog.Builder(this)
				.setTitle("Ȯ��")
				.setMessage("�ֹ��Ͻðڽ��ϱ�?(������ ��ȭ��ȣ�� ���۵˴ϴ�.)")
				.setPositiveButton("��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {// yes��������
																			// �ؾ�����
						input_Name = glo_val.basket_menu;
						input_Taste = glo_val.basket_taste;
						input_Amount = glo_val.basket_amount;
						input_Money = glo_val.menu_price;
						input_Size = glo_val.basket_money;
						new inputDB().execute(); // �Է� ������ ����.
						intent = new Intent(BasketListKkuldak_v1.this,
								MypageKkuldak_v1.class);
						startActivity(intent);
						finish();
					}
				})
				.setNegativeButton("�ƴϿ�",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						})

				.create();
		return mybox;
	}
}
