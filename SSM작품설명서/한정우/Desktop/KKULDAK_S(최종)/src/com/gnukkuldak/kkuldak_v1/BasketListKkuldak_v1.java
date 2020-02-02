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

	// -----자원을 관리하기위한 변수-----//
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
		// -------------------------3G/4G/Wifi연결상태를
		// 확인하기위한과정------------------------

		cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		// -------------------------3G/4G/Wifi연결상태를
		// 확인하기위한과정------------------------

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

		// -----xml자원 호출-----//
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
			personList.add(new MenuKkuldak_v1("[ 메뉴명 ] " + menu[i], "[맛] "
					+ taste[i], "[수량] " + glo_val.basket_amount[i] + "",
					"[가격] " + money[i], img_id[i]));
			Log.d("repaint", "repaint");

			// ----가격을 파싱하는 부분 ----//
			glo_val.menu_price[i] = (Integer.parseInt(((money[i].split(" ")[1])
					.split(" ")[0]).split("원")[0])) * glo_val.basket_amount[i];
			price = price + glo_val.menu_price[i];
			// ----end----//
		}
		textPrice = "총 결제금액 :" + price + "원";
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
				Log.d("여기까지", "here");
				if (!glo_val.phone.equals(null+"")) {
					Log.d("여기까지2", "here");
					if (mobile.isConnected() || wifi.isConnected()) {
						// 3G 또는 WiFi 에 연결되어 있을 경우
						if (!glo_val.phone.equals(null)) {
							Log.d("여기까지", "here");
							AlertDialog diaBox = createDialogBox();
							diaBox.show();
						}

					} else
						Toast.makeText(BasketListKkuldak_v1.this,
								"3G/4G 또는 Wifi가 연결되어있지 않습니다. 연결후 다시시도해주세요.^^",
								Toast.LENGTH_SHORT).show();

				}

				else
					Toast.makeText(BasketListKkuldak_v1.this,
							"죄송합니다. 사용하고있는 기종으로는 사용이 불가능합니다.",
							Toast.LENGTH_SHORT).show();
			}

		});

	}

	private class inputDB extends AsyncTask<Void, Void, String> {
		@Override
		// 백그라운드에서 작업할 작업을 지시함.
		protected String doInBackground(Void... params) {
			String temp;
			Log.d("here", "i am2");
			temp = inputData();
			return temp;
		}

		protected void onPostExecute(String temp) {

		}
		// 백그라운드 작업 끝나고(여기서는 inputData가 정상적으로 끝나고)
		// 실행할 작업.
		// 왜 이걸 쓰냐면 doInBackground에서 ui thread(onCreate)부분 건들면 뒈져버린다.
		// 그래서 onPostExecute에서 작업 결과를 표시하든지 한다.

	}

	// 각 스레드에서 사용할 각 함수들을 정의
	// 1. input_btn용 스레드에 탑재될 함수
	public String inputData() {
		try {

			// for문을 위한 변수
			int i = 0;

			// 장바구니에있는 모든내역을 DB로 보내기위한 For문
			for (i = 0; i < glo_val.basket_index; i++) {

				// URL 설정하고 접속하기
				URL url = new URL("http://203.255.39.254/insert_menu_test.php"); // URL
																					// 설정
				HttpURLConnection http = (HttpURLConnection) url
						.openConnection(); // 접속
				// 전송 모드 설정 - 기본적인 설정이다
				http.setDefaultUseCaches(false);
				http.setDoInput(true);// 서버에서 읽기 모드 지정
				http.setDoOutput(true);// 서버로 쓰기 모드 지정
				http.setRequestMethod("POST");// 전송 방식은 POST <- 보안때문에 이거 쓴단다..

				// 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
				http.setRequestProperty("content-type",
						"application/x-www-form-urlencoded");

				// 실제로 건드릴 부분은 여기서 부터다.(위에는 기본 설정이니 이해하려고하지말고 걍 Ctrl+C ->
				// Ctrl+V해라)
				// 서버로 값 전송
				// 전송에는 StringBuffer로 패킷으로 만들어서 보내야한다.
				// 모든 보낼 String을 패킷으로 만들어서 밑의 형식으로 보내면 서버에있는 php파일에 변수가 저장되고
				// 처리된다.
				StringBuffer buffer = new StringBuffer();
				buffer.append("time").append("=").append(time).append("&");// php
																			// 변수에
																			// 값
																			// 대입
				buffer.append("name").append("=").append(input_Name[i])
						.append("&");// php 변수에 값 대입
				buffer.append("taste").append("=").append(input_Taste[i])
						.append("&");// php 변수 앞에 '$' 붙이지 않는다
				buffer.append("amount").append("=").append(input_Amount[i])
						.append("&");// php 변수 앞에 '$' 붙이지 않는다
				buffer.append("size").append("=").append(input_Size[i])
						.append("&");// php 변수 앞에 '$' 붙이지 않는다
				buffer.append("money").append("=").append(input_Money[i])
						.append("&");// php 변수 앞에 '$' 붙이지 않는다
				buffer.append("phone").append("=").append(glo_val.phone)
						.append("&");// php 변수 앞에 '$' 붙이지 않는다
				buffer.append("state").append("=").append("접수대기");// 변수구분은'&'사용

				OutputStreamWriter outStream = new OutputStreamWriter(
						http.getOutputStream(), "UTF8");

				PrintWriter writer = new PrintWriter(outStream);
				writer.write(buffer.toString());
				writer.flush();

				// --------------------------
				// 서버에서 전송받기
				// --------------------------
				InputStreamReader tmp = new InputStreamReader(
						http.getInputStream(), "UTF8");

				BufferedReader reader = new BufferedReader(tmp);
				StringBuilder builder = new StringBuilder();
				String str;
				while ((str = reader.readLine()) != null) {// 서버에서 라인단위로 보내줄
															// 것이므로 라인단위로 읽는다
					builder.append(str + "\n");
				}
				output_Result = builder.toString();// 전송결과를 전역 변수에 저장
				Log.d("here", "i am3");
			}

		} catch (MalformedURLException e) {
			//
		} catch (IOException e) {
			error = e.toString();
		} // try
		glo_val.basket_index = 0;// 장바구니 초기화
		return output_Result;// 작업 결과 넘겨버려야지 ㅇㅅㅇ?
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
			personList.add(new MenuKkuldak_v1("[ 메뉴명 ] " + menu[i], "[맛] "
					+ taste[i], "[수량] " + glo_val.basket_amount[i] + "",
					"[가격] " + money[i], img_id[i]));

			price = price
					+ (Integer
							.parseInt(((money[i].split(" ")[1]).split(" ")[0])
									.split("원")[0])) * glo_val.basket_amount[i];
		}

		if (glo_val.basket_index == 0) {
			basket_btn.setVisibility(View.INVISIBLE);
			all_price.setVisibility(View.INVISIBLE);
		}

		else {
			basket_btn.setVisibility(View.VISIBLE);
			all_price.setVisibility(View.VISIBLE);
		}

		textPrice = "총 결제금액 :" + price + "원";
		all_price.setText(textPrice);
		adapt.notifyDataSetChanged();
	}

	private AlertDialog createDialogBox() {
		AlertDialog mybox = new AlertDialog.Builder(this)
				.setTitle("확인")
				.setMessage("주문하시겠습니까?(고객님의 전화번호가 전송됩니다.)")
				.setPositiveButton("넵", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {// yes눌렀을시
																			// 해야할일
						input_Name = glo_val.basket_menu;
						input_Taste = glo_val.basket_taste;
						input_Amount = glo_val.basket_amount;
						input_Money = glo_val.menu_price;
						input_Size = glo_val.basket_money;
						new inputDB().execute(); // 입력 스레드 시작.
						intent = new Intent(BasketListKkuldak_v1.this,
								MypageKkuldak_v1.class);
						startActivity(intent);
						finish();
					}
				})
				.setNegativeButton("아니요",
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
