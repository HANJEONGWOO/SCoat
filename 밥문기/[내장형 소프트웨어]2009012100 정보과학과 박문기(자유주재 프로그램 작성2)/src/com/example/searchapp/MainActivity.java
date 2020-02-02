package com.example.searchapp;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static final int REQUEST_CODE_ANOTHER = 1001;
	
	public EditText search;
	//Dialog Box
	private AlertDialog createDialogBox() {
		AlertDialog myBox = new AlertDialog.Builder(this)
		.setTitle("information")
		.setMessage("Quit?")
		.setPositiveButton("YES", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				moveTaskToBack(true);
				finish();
				ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
				am.restartPackage(getPackageName());//완전히 프로세스 상 메모리 해제
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				
			}
		})
		.create();
		return myBox;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		search = (EditText)findViewById(R.id.textbox01);
		Button ex = (Button)findViewById(R.id.quit);
		//버튼 구성
		ex.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		AlertDialog diaBox = createDialogBox();
        		diaBox.show();
        	}
        });
		//activity 전환
		Button change = (Button)findViewById(R.id.act_change);
		change.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast t = Toast.makeText(getBaseContext(), "Search!!", Toast.LENGTH_LONG);
				t.show();
				Intent intent = new Intent(getBaseContext(), Search.class);
				intent.putExtra("query", search.getText().toString());
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
