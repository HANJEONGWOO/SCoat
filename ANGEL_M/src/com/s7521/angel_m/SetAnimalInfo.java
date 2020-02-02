package com.s7521.angel_m;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SetAnimalInfo extends Activity {
	private static final int REQ_CODE_PICK_IMAGE = 0;
	private static final String ANIMAL_FILE = "Animal.png";
	ImageView Title,Snippet,BtAnimal;
	private EditText GetTitle,GetSnippet;
	private String TitleString,SnippetString;
	private Button SetAnimal,SetCancel;
	private String ButtonAnimal,SessionID,SessionNickName;
	private double Latitude,Longitude;
	private File AnimalImage;
	private Bitmap selectedImage;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_animal_info);

		Intent intent = getIntent();
		Latitude = intent.getDoubleExtra("Lat",0.00);
		Longitude = intent.getDoubleExtra("Lng",0.00);
		ButtonAnimal = intent.getStringExtra("BtAnimal");

		Log.e("intent 로 받아온 LatLng","는"+Latitude+" "+Longitude);

		intent = new Intent(
				Intent.ACTION_GET_CONTENT,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*"); 

		intent.putExtra("crop", "true"); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
		intent.putExtra("outputFormat",
				Bitmap.CompressFormat.PNG.toString());
		startActivityForResult(intent, REQ_CODE_PICK_IMAGE);

		FindViewByID();

		SetAnimal.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				TitleString = GetTitle.getText().toString();
				SnippetString = GetSnippet.getText().toString();
				Log.e("Title Snippet",TitleString+SnippetString);
				if(TitleString.isEmpty() || SnippetString.isEmpty())
				{
					Toast.makeText(SetAnimalInfo.this,"제목과 특징을 입력해야 합니다",Toast.LENGTH_LONG).show();
				}
				else
					new SetAnimalThread().execute();
			}
		});
		SetCancel.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(SetAnimalInfo.this,SeeMap.class);
				startActivity(intent);
				finish();
			}
		});
	}
	public void FindViewByID()
	{
		Title = (ImageView)findViewById(R.id.title);
		Snippet = (ImageView)findViewById(R.id.imageView1);


		GetTitle = (EditText)findViewById(R.id.GetTitle);	
		GetSnippet = (EditText)findViewById(R.id.GetSnippet);

		SetAnimal = (Button)findViewById(R.id.SetAnimal);
		SetCancel = (Button)findViewById(R.id.SetCancel);
	}

	private Uri getTempUri() 
	{
		return Uri.fromFile(AnimalTemp());
	}

	private File AnimalTemp() 
	{
		if (IsSDcard()) 
		{
			AnimalImage = new File(Environment.getExternalStorageDirectory(),ANIMAL_FILE);
			try 
			{
				AnimalImage.createNewFile();
			} 
			catch (IOException e) 
			{
				Log.e("Exception :",e.toString());
			}
			return AnimalImage;
		} 
		else
			return null;
	}

	private boolean IsSDcard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}

	protected void onActivityResult(int requestCode, int resultCode,Intent imageData) 
	{

		super.onActivityResult(requestCode, resultCode, imageData);
		if(requestCode==REQ_CODE_PICK_IMAGE){
			if (resultCode == RESULT_OK) {
				if (imageData != null) {
					String filePath = Environment.getExternalStorageDirectory()
							+ "/Animal.png";

					Log.e("path :",filePath);

					selectedImage = BitmapFactory.decodeFile(filePath);
					ImageView image = (ImageView) findViewById(R.id.imageView);
					image.setImageBitmap(selectedImage);
				}
			}
		}
	}

	public class SetAnimalThread extends AsyncTask<Void,Void,String>
	{
		public SetAnimalThread()
		{
			Log.e("Thread","SetAnimalThread Called!!");
		}
		protected String doInBackground(Void... params)
		{
			String url = "http://192.168.42.77:8080/ANGEL_M_SERVER/ServiceImage";
			if(MainActivity.context.getAttribute("NickName")!=null)
			{
				SessionID = (String)MainActivity.context.getAttribute("ID");
				SessionNickName = (String)MainActivity.context.getAttribute("NickName");
			}
			String response = "";
			if(SessionID==null)
			{
				Log.e("ERROR","Session is NULL");
			}
			else
			{
				HttpClient client = new DefaultHttpClient(); 
				HttpPost post = new HttpPost(url);

				try{
					ContentBody bin = new FileBody(AnimalImage,"image/png");
					MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
					reqEntity.addPart("File", bin);
					reqEntity.addPart("ID",new StringBody(SessionID,Charset.forName("UTF-8")));
					reqEntity.addPart("NickName",new StringBody(SessionNickName,Charset.forName("UTF-8")));
					reqEntity.addPart("Title",new StringBody(GetTitle.getText().toString(),Charset.forName("UTF-8")));
					reqEntity.addPart("Snippet",new StringBody(GetSnippet.getText().toString(),Charset.forName("UTF-8")));
					reqEntity.addPart("Latitude",new StringBody(Double.toString(Latitude)));
					reqEntity.addPart("Longitude",new StringBody(Double.toString(Longitude)));
					reqEntity.addPart("AnimalFlag",new StringBody(ButtonAnimal,Charset.forName("UTF-8")));
					post.setEntity(reqEntity); 

					HttpResponse execute = client.execute(post);

					InputStream content = execute.getEntity().getContent();
					BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
					String s = "";
					while ((s = buffer.readLine()) != null) 
					{
						response += s;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			return response;
		}
		protected void onPostExecute(String result)
		{
			if(result.equals("SetMarkerSuccess"))
			{
				Intent intent = new Intent(SetAnimalInfo.this,SeeAnimal.class);
				startActivity(intent);
				finish();
			}
			else if(result.equals("SetAnimalFail"))
				Toast.makeText(SetAnimalInfo.this,"유기동물 등록하기 실패",Toast.LENGTH_LONG).show();
		}
	}
}
