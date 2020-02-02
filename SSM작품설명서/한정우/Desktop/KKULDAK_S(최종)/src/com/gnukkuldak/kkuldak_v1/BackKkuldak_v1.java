package com.gnukkuldak.kkuldak_v1;

import com.gnukkul.kkuldak_v1.R;

import android.app.Activity; 
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle; 
import android.os.Handler; 
import android.util.Log; 
import android.view.Menu; 
import android.view.View; 
import android.view.View.OnClickListener; 
import android.webkit.JsResult; 
import android.webkit.WebChromeClient; 
import android.webkit.WebSettings; 
import android.webkit.WebView; 
import android.widget.Button; 
import android.widget.EditText; 
import android.widget.TextView;
  
/** 
 * 웹뷰를 화면안에 넣고 앱과 웹 사이에 상호 호출하는 기능을 알아볼 수 있습니다. 
 *  
 * @author Mike 
 */ 

public class BackKkuldak_v1 extends Activity {
    /** 
     * 로그를 위한 태그 
     */
    private static final String TAG = "MainActivity"; 
      
    /** 
     * 웹사이트 로딩을 위한 버튼 
     */
    private Button siteBtn; 
    @SuppressWarnings("unused")
	private Button faceBtn;
  
    /** 
     * 핸들러 객체 
     */
    private Handler mHandler = new Handler(); 
  
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.back_kkuldak_v1); 
        /* 경상대학교 꿀닭 사이트 연동 */  
        siteBtn = (Button) findViewById(R.id.siteBtn);
        siteBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent myIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.gnukkuldak.com"));
        		startActivity(myIntent);
        	}
        });
        

        
        /*경상대학교 꿀닭 페이스북 연동*/
  
        
        faceBtn = (Button) findViewById(R.id.faceBtn);
        faceBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent myIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/gnukkuldak?fref=ts"));
        		startActivity(myIntent);
        	}
        });
    } 
      
}