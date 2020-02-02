package com.example.searchapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class Search extends Activity{
	private	ImageView image1;
	private ImageView image2;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second_activity);	
       
		 Resources res = getResources();
	        BitmapDrawable bitmap = (BitmapDrawable) res.getDrawable(R.drawable.naver);
	        BitmapDrawable bitmap2 = (BitmapDrawable) res.getDrawable(R.drawable.google);
	        
	        int bitmapWidth = bitmap.getIntrinsicWidth();
	        int bitmapHeight = bitmap.getIntrinsicHeight();
	        
	        image1 = (ImageView)findViewById(R.id.naver);
	        image2 = (ImageView)findViewById(R.id.google);
	        
	        
	        image1.setImageDrawable(bitmap);
	        image1.getLayoutParams().width = bitmapWidth;
	        image1.getLayoutParams().height = bitmapHeight;
	        
	        image2.setImageDrawable(bitmap2);
	        image2.getLayoutParams().width = bitmapWidth;
	        image2.getLayoutParams().height = bitmapHeight;
	        //토스트 공간
	        
	        image1.setOnClickListener(new OnClickListener(){
	        	public void onClick(View v){
	        		Toast t = Toast.makeText(getApplicationContext(), "Naver Search!", Toast.LENGTH_LONG);
	        		t.setGravity(Gravity.BOTTOM, 0, 0);
	        		t.show();
	        		Intent it = getIntent();
	        		String query = it.getStringExtra("query");
	        		Uri uri = Uri.parse("http://search.naver.com/search.naver?ie=utf8&where=nexearch&query="+query); 
	        		it = new Intent(Intent.ACTION_VIEW, uri);
	        		startActivity(it);
	        	}
	        });
	        image2.setOnClickListener(new View.OnClickListener(){
	        	public void onClick(View v){
	        		Toast t = Toast.makeText(getApplicationContext(), "Google Search!", Toast.LENGTH_LONG);
	        		t.setGravity(Gravity.BOTTOM, 0, 0);
	        		t.show();
	        		Intent it = getIntent();
	        		String query = it.getStringExtra("query");
	        		Uri uri = Uri.parse("http://gog.is/"+query);
	        		it = new Intent(Intent.ACTION_VIEW, uri);
	        		startActivity(it);
	        	}
	        });
	        //복귀버튼 구간
	         Button back = (Button)findViewById(R.id.back);
			back.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					Intent resultIntent = getIntent();
					Toast t = Toast.makeText(getApplicationContext(), "Searched : "+resultIntent.getStringExtra("query"), Toast.LENGTH_LONG);
					t.show();
					setResult(1, resultIntent);
					finish();
				}
			});
	}
	
}
