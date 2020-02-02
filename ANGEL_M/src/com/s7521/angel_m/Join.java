package com.s7521.angel_m;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Join extends Activity{
	EditText ID,PassWord,PassWord2,NickName,Introduce;
	ImageView IDView,PWView,PWView2,NNView,IntroView;
	ImageView Join,Cancel;
	Context context;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.angel_join);
		
		context = this;
		ID = (EditText)findViewById(R.id.ID);
		PassWord = (EditText)findViewById(R.id.PassWord);
		PassWord2 = (EditText)findViewById(R.id.PassWord2);
		NickName = (EditText)findViewById(R.id.NickName);
		Introduce = (EditText)findViewById(R.id.Introduce);
		IDView = (ImageView)findViewById(R.id.imageView1);
		PWView = (ImageView)findViewById(R.id.imageView2);
		PWView2 = (ImageView)findViewById(R.id.imageView3);
		NNView = (ImageView)findViewById(R.id.imageView4);
		IntroView = (ImageView)findViewById(R.id.imageView5);
		
		Join = (ImageView)findViewById(R.id.JoinButton);
		Cancel = (ImageView)findViewById(R.id.CancelButton);
		
		Join.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				new JoinThread().execute();
			}
		});
		Cancel.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(Join.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
	}
	public class JoinThread extends AsyncTask<Void,Void,String>
	{
		public JoinThread()
		{
			Log.e("Thread : ","JoinThread Called!!");
		}
		protected String doInBackground(Void... params) {
			String url = "http://192.168.42.77:8080/ANGEL_M_SERVER/Service";
			String getID = ID.getText().toString();
			String getPW = PassWord.getText().toString();
			String getPW2 = PassWord2.getText().toString();
			String getNN = NickName.getText().toString();
			String getIntro = Introduce.getText().toString();
			String response = new String();
			
			System.out.println(getPW2+"2번"+getPW);
			if(getPW.equals(getPW2))
			{
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				try {

					List parameter = new ArrayList();
					parameter.add(new BasicNameValuePair("CASE","AngelJoin"));
					parameter.add(new BasicNameValuePair("ID",getID));
					parameter.add(new BasicNameValuePair("PassWord",getPW));
					parameter.add(new BasicNameValuePair("NickName",getNN));
					parameter.add(new BasicNameValuePair("Introduce",getIntro));
					
					UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameter,HTTP.UTF_8);
					httpPost.setEntity(ent);
					HttpResponse execute = client.execute(httpPost);

					InputStream content = execute.getEntity().getContent();
					BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) {
						response += s;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
			{
				Log.e("ERROR","Password Doesn't Correct");
				response="NotSamePassWord";
			}
			Log.e("response",response);
			return response;
		}
		protected void onPostExecute(String result)
		{
			Response(result);
		}
		public void Response(String result)
		{
			if(result.equals("JoinSuccess"))
			{
				Toast.makeText(context,"회원가입 완료",Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Join.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
			else if(result.equals("SameID"))
			{
				Toast.makeText(context,"같은 아이디가 존재합니다",Toast.LENGTH_LONG).show();
				Log.e("SameID","같은 아이디가 존재합니다");
			}
			else if(result.equals("NotSamePassWord"))
			{
				Toast.makeText(context,"비밀번호가 다릅니다",Toast.LENGTH_LONG).show();
				Log.e("비밀번호가 다름","비밀번호가 다름");
			}
		}
	}
}
