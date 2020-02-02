package com.example.blinder_ver01;

import android.app.*;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;


public class Informationproc extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.phone_setup);
	    Button input_info = (Button)findViewById(R.id.input_info);
	   
	    input_info.setOnClickListener(new OnClickListener() {
	    	
	    	public void onClick(View v)	{
	    		Intent intent = getIntent();	//이 액티비티를 시작하게 한 인텐트 호출  		
	    		
	    		EditText blinder_name = (EditText)findViewById(R.id.blinder_name);
	    		EditText protecter_name = (EditText)findViewById(R.id.protecter_name);
	    		EditText protecter_pn = (EditText)findViewById(R.id.protecter_pn);
	    		

	    		if(blinder_name.getText().toString().length() > 5 ||
	    				protecter_name.getText().toString().length() > 5 ||
	    				protecter_pn.getText().toString().length() > 11)
	    		{
	    			//Toast.makeText(this,"hi" , Toast.LENGTH_LONG);
	    			Toast.makeText(Informationproc.this, "이름은 최대 5자, 전화번호는 11자를 넘을 수 없습니다.", Toast.LENGTH_LONG).show();
	    		}
	    		else
	    		{
		    		intent.putExtra("data_blinder_name", blinder_name.getText().toString());
		    		intent.putExtra("data_protecter_name", protecter_name.getText().toString());
		    		intent.putExtra("data_protecter_pn", protecter_pn.getText().toString());
		    		
		    		setResult(RESULT_OK, intent);	//추가 정보를 넣은 후 다시 인텐트를 반환합니다.
		    		finish();	//액티비티 종료
	    		}
	    		
	    		
	    	}
	    });
	    // TODO Auto-generated method stub
	}
}