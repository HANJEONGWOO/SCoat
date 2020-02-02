package com.s7521.angel_m;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button SeeMap,BeAngel;
	private String LoginID,LoginPassWord;
	public EditText LoginEdit,PassEdit;
	public static HttpContext context;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FindViewByID();
		SetOnClickListener();
		context = new BasicHttpContext();
		context.setAttribute("NickName","손");
	}
	public void FindViewByID()
	{
		SeeMap = (Button)findViewById(R.id.SeeMap);
		BeAngel = (Button)findViewById(R.id.BeAngel);
	}
	public void SetOnClickListener()
	{
		SeeMap.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				Intent intent = new Intent(MainActivity.this,SeeAnimal.class);
				startActivity(intent);
			}
		});
		BeAngel.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				BeAngelDialog dialog= new BeAngelDialog(MainActivity.this);
				dialog.show();
		
			}
		});
	}
	public class BeAngelDialog extends Dialog implements android.view.View.OnClickListener{
		ImageButton Login;
		ImageButton Join;
		public BeAngelDialog(Context context)
		{
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_beangel);
			Login = (ImageButton)findViewById(R.id.Login);
			Join = (ImageButton)findViewById(R.id.Join);
			LoginEdit = (EditText)findViewById(R.id.Id);
			PassEdit = (EditText)findViewById(R.id.Password);
			Login.setOnClickListener(this);
			Join.setOnClickListener(this);
		}
		public void onClick(View v) {
			if(v==Login)
			{
				LoginID = LoginEdit.getText().toString();
				LoginPassWord = PassEdit.getText().toString();
				if(LoginID.isEmpty() || LoginPassWord.isEmpty())
				{
					Toast.makeText(MainActivity.this,"아이디 또는 비밀번호를 입력하지 않으셨습니다",Toast.LENGTH_LONG).show();
				}
				else
				{
					this.cancel();
					new LoginThread().execute();
				}
			}
			if(v==Join)
			{
				Intent intent = new Intent(MainActivity.this,Join.class);
				startActivity(intent);
				finish();
			}
		}
	}
	public class LoginThread extends AsyncTask<Void,Void,String>
	{
		String SessionID;
		String SessionNickName;
		public LoginThread()
		{
			Log.e("Thread","LoginThread Called!!");
			SessionID="";
			SessionNickName="";
		}
		protected String doInBackground(Void... params) 
		{
			String url = "http://192.168.42.77:8080/ANGEL_M_SERVER/Service";
			String response = "";
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			try {
				List<BasicNameValuePair> parameter = new ArrayList<BasicNameValuePair>();
				parameter.add(new BasicNameValuePair("CASE","AngelLogin"));
				parameter.add(new BasicNameValuePair("ID",LoginID));
				parameter.add(new BasicNameValuePair("PassWord",LoginPassWord));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameter,HTTP.UTF_8);
				httpPost.setEntity(ent);
				HttpResponse execute = client.execute(httpPost);
				InputStream content = execute.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				int i = 0;
				while ((s = buffer.readLine()) != null) 
				{
					//response += s;
					if(i==0)
					{
						SessionID=s;
					}
					if(i==1)
					{
						SessionNickName=s;
					}
					i++;
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			return response;
		}
		protected void onPostExecute(String result)
		{
			if(result.equals("LoginFail"))
			{
				Toast.makeText(MainActivity.this,"로그인 실패",Toast.LENGTH_LONG).show();
			}
			else
			{
				context.setAttribute("ID",SessionID);
				context.setAttribute("NickName",SessionNickName);
				Intent intent = new Intent(MainActivity.this,SeeMap.class);
				intent.putExtra("ButtonNum",9);
				startActivity(intent);
			}
		}
	}
}
