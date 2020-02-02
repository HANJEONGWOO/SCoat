package com.example.blinder_ver01;

import android.app.*;
import android.content.*;
import android.os.*;

public class IntroActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.intro_activity);
	    
	    Handler handler = new Handler();
	    handler.postDelayed(new Runnable()	{
	    	public void run()	{
	    		Intent intent = new Intent(IntroActivity.this, MainActivity.class);
	    		startActivity(intent);
	    		//뒤로가기 했을경우 안나오도록 없애주기 >> finish!!
	    		finish();
	    	}
	    }, 2000);
	    // TODO Auto-generated method stub
	}

}
