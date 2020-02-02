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
 * ���並 ȭ��ȿ� �ְ� �۰� �� ���̿� ��ȣ ȣ���ϴ� ����� �˾ƺ� �� �ֽ��ϴ�. 
 *  
 * @author Mike 
 */ 

public class BackKkuldak_v1 extends Activity {
    /** 
     * �α׸� ���� �±� 
     */
    private static final String TAG = "MainActivity"; 
      
    /** 
     * ������Ʈ �ε��� ���� ��ư 
     */
    private Button siteBtn; 
    @SuppressWarnings("unused")
	private Button faceBtn;
  
    /** 
     * �ڵ鷯 ��ü 
     */
    private Handler mHandler = new Handler(); 
  
    @Override
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.back_kkuldak_v1); 
        /* �����б� �ܴ� ����Ʈ ���� */  
        siteBtn = (Button) findViewById(R.id.siteBtn);
        siteBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent myIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.gnukkuldak.com"));
        		startActivity(myIntent);
        	}
        });
        

        
        /*�����б� �ܴ� ���̽��� ����*/
  
        
        faceBtn = (Button) findViewById(R.id.faceBtn);
        faceBtn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		Intent myIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com/gnukkuldak?fref=ts"));
        		startActivity(myIntent);
        	}
        });
    } 
      
}