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
	
	private Button input, search, show;// input��ư, search��ư, show��ư ���� ��ü
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
		
		//------------------------------------php�� ���ڸ� �����ϱ����� ���� ������ �޴°��� --------------------------------------------
		Calendar cal = Calendar.getInstance();
		time = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1) + ""
				+ cal.get(Calendar.DAY_OF_MONTH) + "";
		Log.d("time ", time);
		//------------------------------------php�� ���ڸ� �����ϱ����� ���� ������ �޴°��� End --------------------------------------------

		final Button back_btn = (Button) findViewById(R.id.back_btn);
		final Button refresh_btn = (Button) findViewById(R.id.refresh_btn);
		//listCheck = (TextView)findViewById(R.id.listCheck);
		data = new ArrayList<String>();

		// ����� ����
		adapter = new ArrayAdapter<String>(this,
				R.layout.simpleitem, data);

		// ����� ����
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
			// URL �����ϰ� �����ϱ�
			URL url = new URL("http://203.255.39.254/show_data_test.php"); // URL
			// ����
			HttpURLConnection http = (HttpURLConnection) url.openConnection(); // ����
			// ���� ��� ���� - �⺻���� �����̴�
			http.setDefaultUseCaches(false);
			http.setDoInput(true);// �������� �б� ��� ����
			http.setDoOutput(true);// ������ ���� ��� ����
			http.setRequestMethod("POST");// ���� ����� POST <- ���ȶ����� �̰� ���ܴ�..
			// �������� ������ <Form>���� ���� �Ѿ�� �Ͱ� ���� ������� ó���϶�� �� �˷��ش�
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
			// �������� ���۹ޱ�
			// --------------------------
			InputStreamReader tmp = new InputStreamReader(
					http.getInputStream(), "UTF8");
			BufferedReader reader = new BufferedReader(tmp);
			StringBuilder builder = new StringBuilder();
			String str;
			while ((str = reader.readLine()) != null) {// �������� ���δ����� ������ ���̹Ƿ�
														// ���δ����� �д´�
				builder.append(str + "\n");
				outCount++; // �޴�����Ʈ ������ ��Ÿ���� ��
				if(str.indexOf("������")!=-1 || str.indexOf("�������")!=-1)
				{
					listCheckCount++;
				}
				
			}	
			Log.d("LISTVIEW ADD", "FINISH");

			// �����͸� ����Ѵ�.
			output_Result = builder.toString();// �˻� ����� ���� ������ ����
		} catch (MalformedURLException e) {
			//
		} catch (IOException e) {
			error = e.toString();
		} // try
		ThreadEnd=true;
		//listCheck.setText(listCheckCount+"");
		Log.d("�������??",listCheckCount+"��");
		return output_Result;
	} // HttpPostData

	public String searchData() {
		try {
			// URL �����ϰ� �����ϱ�
			URL url = new URL("http://203.255.39.254/search_data_phone.php"); // URL
																				// ����
			HttpURLConnection http = (HttpURLConnection) url.openConnection(); // ����
			// ���� ��� ���� - �⺻���� �����̴�
			http.setDefaultUseCaches(false);
			http.setDoInput(true);// �������� �б� ��� ����
			http.setDoOutput(true);// ������ ���� ��� ����
			http.setRequestMethod("POST");// ���� ����� POST <- ���ȶ����� �̰� ���ܴ�..

			// �������� ������ <Form>���� ���� �Ѿ�� �Ͱ� ���� ������� ó���϶�� �� �˷��ش�
			http.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");

			// ������ �ǵ帱 �κ��� ���⼭ ���ʹ�.(������ �⺻ �����̴� �����Ϸ����������� �� Ctrl+C -> Ctrl+V�ض�)
			// ������ �� ����
			// ���ۿ��� StringBuffer�� ��Ŷ���� ���� �������Ѵ�.
			// ��� ���� String�� ��Ŷ���� ���� ���� �������� ������ �������ִ� php���Ͽ� ������ ����ǰ� ó���ȴ�.
			StringBuffer buffer = new StringBuffer();
			buffer.append("time").append("=").append(time).append("&");// php
																		// ������ ��
																		// ����
			buffer.append("phone").append("=").append(glo_val.phone);
			// Log.d("����ȣ",glo_val.phone);

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
			String str;
			Log.d("position", "������");
			while ((str = reader.readLine()) != null) {// �������� ���δ����� ������ ���̹Ƿζ��δ����� �д´�
				Log.d("str",str);
				//================str�޴¼���=================//
				//str.split("/")[0]=�޴��̸�
				//str.split("/")[1]=��������
				//str.split("/")[2]=�ֹ��ð�
				//==========================================//
				
				data.add("  [�ֹ�����] : "+str.split("/")[2]+"\n"+"  [��        ��] : "+ str.split("/")[0]+"\n"+  "  [��������] : "+ str.split("/")[1]);
				myMenuCount++;
				//-----------------���� �߻��� ���α׷��� �״� ���� �������� ����ó��--------------------
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
				//-----------------���� �߻��� ���α׷��� �״� ���� �������� ����ó��--------------------
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
		//-------------------------3G/4G/Wifi������¸� Ȯ���ϱ����Ѱ���------------------------
		cManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
		mobile = cManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
		wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 				 
		//-------------------------3G/4G/Wifi������¸� Ȯ���ϱ����Ѱ���------------------------
		
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
		    //3G �Ǵ� WiFi �� ����Ǿ� ���� ��� 
			new searchDB().execute();// �˻� ������ ����
			/*try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			while(!ThreadEnd){Log.d("while","while");}
			ThreadEnd=false;
			
			new showDB().execute();// �˻� ������ ����
			while(!ThreadEnd){Log.d("while","while");}
			Toast.makeText( MypageKkuldak_v1.this, listCheckCount+"�� �� ��� ���Դϴ�.", 5000).show();
			if(myMenuCount!=0)
				mPool.play(mDdok, 1, 1, 0, 0, 1);
		}
		else
			Toast.makeText( MypageKkuldak_v1.this, "3G/4G �Ǵ� Wifi�� ����Ǿ����� �ʽ��ϴ�. ������ �ٽýõ����ּ���.", Toast.LENGTH_SHORT).show();

		Log.d("Thread Run OnStart call!!","call!!");

	}

}