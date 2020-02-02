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

import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MypageKkuldak_v1 extends Activity {

	ConnectivityManager cManager; 
	NetworkInfo mobile; 
	NetworkInfo wifi; 
	
	public Intent intent;
	
	private String output_state[];
	private String output_Result;
	private String error;
	String time;
	
	AudioManager mAm;
	SoundPool mPool;
	
	private Button input, search, show;// input버튼, search버튼, show버튼 담을 객체
	//TextView listCheck;
	
	boolean ThreadEnd = false;

	int outCount = 0;
	int listCheckCount=0;
	int mDdok;
	int myMenuCount=0;
	
	Val_Kkuldak glo_val = new Val_Kkuldak();
	ArrayAdapter<String> adapter;
	ArrayList<String> data;
	ListView list;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mypagelist_v1);
		
		//------------------------------------php에 날자를 전달하기위한 날자 데이터 받는과정 --------------------------------------------
		Calendar cal = Calendar.getInstance();
		time = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1) + ""
				+ cal.get(Calendar.DAY_OF_MONTH) + "";
		Log.d("time ", time);
		//------------------------------------php에 날자를 전달하기위한 날자 데이터 받는과정 End --------------------------------------------

		final Button back_btn = (Button) findViewById(R.id.back_btn);
		final Button refresh_btn = (Button) findViewById(R.id.refresh_btn);
		//listCheck = (TextView)findViewById(R.id.listCheck);
		data = new ArrayList<String>();

		// 어댑터 생성
		adapter = new ArrayAdapter<String>(this,
				R.layout.simpleitem, data);

		// 어댑터 연결
		ListView list = (ListView) findViewById(R.id.mypage_list);
		list.setAdapter(adapter);
		// -------------------------------------------------------------------------------------------------------------
		

		refresh_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
				intent = new Intent(MypageKkuldak_v1.this,
						MypageKkuldak_v1.class);

				startActivity(intent);
			}

		});
		
		refresh_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					refresh_btn.setBackgroundResource(R.drawable.refresh_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					refresh_btn.setBackgroundResource(R.drawable.refresh_b);
				}
				return false;
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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kkuldak_v1, menu);
		return true;
	}

	private class searchDB extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			this.cancel(false);
			this.publishProgress(null);
			String temp;
			temp = searchData();
			return temp;
		}
		protected void onPostExecute(String temp) {   
		}
	}
	
	private class showDB extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			this.cancel(false);
			this.publishProgress(null);
			String temp;
			temp = showData();
			return temp;
		}

		protected void onPostExecute(String temp) {

		}
	}
	public String showData() {
		try {
			// URL 설정하고 접속하기
			URL url = new URL("http://203.255.39.254/show_data_test.php"); // URL
			// 설정
			HttpURLConnection http = (HttpURLConnection) url.openConnection(); // 접속
			// 전송 모드 설정 - 기본적인 설정이다
			http.setDefaultUseCaches(false);
			http.setDoInput(true);// 서버에서 읽기 모드 지정
			http.setDoOutput(true);// 서버로 쓰기 모드 지정
			http.setRequestMethod("POST");// 전송 방식은 POST <- 보안때문에 이거 쓴단다..
			// 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
			http.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");

			StringBuffer buffer = new StringBuffer();
			buffer.append("time").append("=").append(time);// php
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
			while ((str = reader.readLine()) != null) {// 서버에서 라인단위로 보내줄 것이므로
														// 라인단위로 읽는다
				builder.append(str + "\n");
				outCount++; // 메뉴리스트 갯수를 나타내는 수
				if(str.indexOf("조리중")!=-1 || str.indexOf("접수대기")!=-1)
				{
					listCheckCount++;
				}
				
			}	
			Log.d("LISTVIEW ADD", "FINISH");

			// 데이터를 기록한다.
			output_Result = builder.toString();// 검색 결과를 전역 변수에 저장
		} catch (MalformedURLException e) {
			//
		} catch (IOException e) {
			error = e.toString();
		} // try
		ThreadEnd=true;
		//listCheck.setText(listCheckCount+"");
		Log.d("몇명남았지??",listCheckCount+"명");
		return output_Result;
	} // HttpPostData

	public String searchData() {
		try {
			// URL 설정하고 접속하기
			URL url = new URL("http://203.255.39.254/search_data_phone.php"); // URL
																				// 설정
			HttpURLConnection http = (HttpURLConnection) url.openConnection(); // 접속
			// 전송 모드 설정 - 기본적인 설정이다
			http.setDefaultUseCaches(false);
			http.setDoInput(true);// 서버에서 읽기 모드 지정
			http.setDoOutput(true);// 서버로 쓰기 모드 지정
			http.setRequestMethod("POST");// 전송 방식은 POST <- 보안때문에 이거 쓴단다..

			// 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
			http.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");

			// 실제로 건드릴 부분은 여기서 부터다.(위에는 기본 설정이니 이해하려고하지말고 걍 Ctrl+C -> Ctrl+V해라)
			// 서버로 값 전송
			// 전송에는 StringBuffer로 패킷으로 만들어서 보내야한다.
			// 모든 보낼 String을 패킷으로 만들어서 밑의 형식으로 보내면 서버에있는 php파일에 변수가 저장되고 처리된다.
			StringBuffer buffer = new StringBuffer();
			buffer.append("time").append("=").append(time).append("&");// php
																		// 변수에 값
																		// 대입
			buffer.append("phone").append("=").append(glo_val.phone);
			// Log.d("폰번호",glo_val.phone);

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
			String str;
			Log.d("position", "들어가기전");
			while ((str = reader.readLine()) != null) {// 서버에서 라인단위로 보내줄 것이므로라인단위로 읽는다
				Log.d("str",str);
				//================str받는순서=================//
				//str.split("/")[0]=메뉴이름
				//str.split("/")[1]=조리상태
				//str.split("/")[2]=주문시간
				//==========================================//
				
				data.add("  [주문일자] : "+str.split("/")[2]+"\n"+"  [메        뉴] : "+ str.split("/")[0]+"\n"+  "  [조리상태] : "+ str.split("/")[1]);
				myMenuCount++;
				//-----------------예외 발생시 프로그럼이 죽는 것을 막기위한 예외처리--------------------
				try {      
					 adapter.notifyDataSetChanged();   
					 Log.d("TRY CALL","TRY");
				 } catch (final Error e) {     
					 Log.d("ERORR1","ERROR1");
				 } catch (final IllegalStateException e){      
					 Log.d("ERORR2","ERROR2");
				 } catch (final RuntimeException e) {    
					 Log.d("ERORR3","ERROR3");	e.printStackTrace();
				 } catch (final Throwable e) {      
					 Log.d("ERORR4","ERROR4");
				 }   
				//-----------------예외 발생시 프로그럼이 죽는 것을 막기위한 예외처리--------------------
				outCount++;
			}
			ThreadEnd=true;
			Log.d("true","true");
			output_Result = "success";
		} catch (MalformedURLException e) {
			//
		} catch (IOException e) {
			error = e.toString();
		} // try
		return output_Result;
	} // HttpPostData
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//-------------------------3G/4G/Wifi연결상태를 확인하기위한과정------------------------
		cManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 				 
		//-------------------------3G/4G/Wifi연결상태를 확인하기위한과정------------------------
		
		mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		mDdok = mPool.load(this, R.raw.ddok, 1);
		mAm = (AudioManager)getSystemService(AUDIO_SERVICE);
				
		if(mobile.isConnected() || wifi.isConnected())
		{

			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    //3G 또는 WiFi 에 연결되어 있을 경우 
			new searchDB().execute();// 검색 스레드 시작
			/*try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			while(!ThreadEnd){Log.d("while","while");}
			ThreadEnd=false;
			
			new showDB().execute();// 검색 스레드 시작
			while(!ThreadEnd){Log.d("while","while");}
			Toast.makeText( MypageKkuldak_v1.this, listCheckCount+"명 이 대기 중입니다.", 5000).show();
			if(myMenuCount!=0)
				mPool.play(mDdok, 1, 1, 0, 0, 1);
		}
		else
			Toast.makeText( MypageKkuldak_v1.this, "3G/4G 또는 Wifi가 연결되어있지 않습니다. 연결후 다시시도해주세요.", Toast.LENGTH_SHORT).show();

		Log.d("Thread Run OnStart call!!","call!!");

	}

}