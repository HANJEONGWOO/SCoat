package com.example.blinder_ver01;


import android.location.*;
//import android.os.Bundle;
//import android.app.*;
import android.content.*;
import android.speech.tts.*;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.*;
//import android.os.*;
import android.util.*;
//import android.view.*;
//import android.widget.*;

import com.google.android.gms.internal.v;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;

import android.bluetooth.BluetoothAdapter;	//블루투스를 사용하기 위한 클래스
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothClass;

import java.io.*;
import java.util.*;


import com.example.blinder_ver01.*;
import com.example.*;

//블루투스챗에서 쓰는 import 클래스들
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnInitListener {
	
	View mPage1, mPage2, mPage3, mPage4;
	
	GoogleMap map;	//1) 현재 위치로 맵을 가져오는 변수
	LocationManager manager;
	MyLocationListener listener;
	
	//LocationManager mLocMan;
	//TextView mStatus;
	//TextView mResult;
	
	String mProvider;
	int mCount;
	
	String provider;
	       
    //Handler mHandler = new Handler();
    
    
 // Message types sent from the BluetoothChatService Handler
    // 블루투스 서비스 핸들러
    
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    // 리퀘스트 코드들
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    
    // Layout Views
    // 리스트 뷰, 에디트 텍스트, 버튼
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;
    private ImageView green_button;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    
    Blinder_val B_val = new Blinder_val();
    int Intent_status = 0;	//인텐트를 구별하기 위한 변수
    
    //TTS 변수 설정
    TextToSpeech talker;
    
    //Picture_val 변수 설정
    Picture_val P_val = new Picture_val();
    
    //OnResume 이 처음에 한번만 나오게 하는 변수
    int ResumeCount=0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		//1)맵을 읽어옴
		
		// 여기 GPS 만 했다하면 죽어버리네요 - 요놈 잡았다! (MyLocationListener 를 선언해주니 잡음)
		manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			provider = LocationManager.NETWORK_PROVIDER;
			Log.d(TAG, "NETWORK_PROVIDER CALL");
		}
		else if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			provider = LocationManager.GPS_PROVIDER;
			Log.d(TAG, "GPS_PROVIDER CALL");
		}
		
		Location location = manager.getLastKnownLocation(provider);	
						
			
			//gps 
			CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(new LatLng(location.getLatitude(),location.getLongitude()))
			.zoom(13)                   
			.bearing(0)                
			.tilt(0)                 
			.build();  
			
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			map.setMyLocationEnabled(true);	//1)현재 위치로 맵을 띄움
			
			
			map.setOnMyLocationChangeListener((OnMyLocationChangeListener) listener);	//
			
			listener = new MyLocationListener();	//
			listener.onLocationChanged(location);	//
			
			manager.requestLocationUpdates(provider,0,0, listener);	//
			
		 // 내 기기의 블루투스 가능 여부
		
        mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter(); 
        
        if(mBluetoothAdapter == null) {
        	Toast.makeText(this, "블루투스를 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
        	finish();
        	return ;
        }
        
        //TTS 변수 설정
        talker = new TextToSpeech(this, this);
		
		//각 페이지 아이디 설정 및 클릭 리스너 설정    
		mPage1 = findViewById(R.id.page1);
		mPage2 = findViewById(R.id.page2);
		mPage3 = findViewById(R.id.page3);
		mPage4 = findViewById(R.id.page4);
		
		mPage1.setVisibility(View.INVISIBLE);	//여기서 안보이게 하였다.
		mPage2.setVisibility(View.INVISIBLE);
		mPage3.setVisibility(View.VISIBLE);
		mPage4.setVisibility(View.INVISIBLE);
		
		findViewById(R.id.btnpage1).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage2).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage3).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage4).setOnClickListener(mClickListener);
		
		
		final Button phone = (Button)findViewById(R.id.phone);	//SNS 설정 버튼 ID 설정
		
        phone.setOnClickListener(new Button.OnClickListener(){	//phone
        	
        	public void onClick(View v)	{
        		Intent intent = new Intent(MainActivity.this, Informationproc.class);
        		startActivityForResult(intent, 1);
        		Intent_status = 1;
        		say("SMS 설정");
        	}
        });
        
        mSendButton = (Button) findViewById(R.id.button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
                String message = view.getText().toString();
                sendMessage(message);
            }
        });
        
        green_button = (ImageView) findViewById(R.id.green_button);
        green_button.setOnClickListener(new OnClickListener()	{
        	public void onClick(View v)	{
        		String sendTo = B_val.data_protecter_pn;
                SmsManager smsManager = SmsManager.getDefault();
                
                String myMessage = 
            			"!" + "위도:" + B_val.latitude + "경도:" + B_val.longitude + B_val.data_protecter_name + "님에게 " + B_val.data_blinder_name + "님이 위험메시지";
            	
            	smsManager.sendTextMessage(sendTo, null, myMessage, null, null);
        	}
        });
        
        
	}
	
	
	OnClickListener mClickListener = new Button.OnClickListener()	{
		
		//이건 또 왜 첫번째 페이지에서 다보이냐 안보이게 해라했는데
		public void onClick(View v)	{

			switch(v.getId())	{
			case R.id.btnpage1:
				mPage1.setVisibility(View.VISIBLE);
				mPage2.setVisibility(View.INVISIBLE);
				mPage3.setVisibility(View.INVISIBLE);
				mPage4.setVisibility(View.INVISIBLE);
				
				say("지도! 현재 위치는 위도 " + B_val.latitude + " 이고 " + " 경도 " + B_val.longitude + "입니다.");
				break;
			case R.id.btnpage2:
				mPage1.setVisibility(View.INVISIBLE);
				mPage2.setVisibility(View.VISIBLE);
				mPage3.setVisibility(View.INVISIBLE);
				mPage4.setVisibility(View.INVISIBLE);
				
				Bitmap bmimg = byteArrayToBitmap(P_val.picture_buffer);
				
				ImageView img_pic = (ImageView)findViewById(R.id.img_pic);
				img_pic.setImageBitmap(bmimg);
				say("사진!");
				break;
			case R.id.btnpage3:
				mPage1.setVisibility(View.INVISIBLE);
				mPage2.setVisibility(View.INVISIBLE);
				mPage3.setVisibility(View.VISIBLE);
				mPage4.setVisibility(View.INVISIBLE);
		
				say("문자메시지 현재 보호자의 이름은 " + B_val.data_protecter_name + " 이고 " + "전화번호는 " + B_val.data_protecter_pn + "입니다!" + "아래의 버튼을 누르시면 문자메시지를 전송합니다.");
				break;
			case R.id.btnpage4:
				mPage1.setVisibility(View.INVISIBLE);
				mPage2.setVisibility(View.INVISIBLE);
				mPage3.setVisibility(View.INVISIBLE);
				mPage4.setVisibility(View.VISIBLE);
				
				say("블루투스!");
				break;
			}
		}
	};



		@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		
		
		
		public class MyLocationListener implements LocationListener{
			public MyLocationListener()
			{
				Log.e("LocationListener","LocationListener Called!!!");
			}
			public void onLocationChanged(Location location)
			{
				Log.e("onLocationChanged","Changing");
				
				B_val.latitude = location.getLatitude();	//위도값을 넣음
				B_val.longitude = location.getLongitude();	//경도값을 넣음 - 다른 어플에 전송예정
				//MyLocationListener에서 위치를 바꿀 수 있다. - 여기서 바꾸어야 한다 다른데서 바꾸면 안되지
			
			}
			public void onProviderDisabled(String provider){}
			public void onProviderEnabled(String provider) {}
			public void onStatusChanged(String provider, int status, Bundle extras){}
		}//
		
		public void onStart()	{
			super.onStart();
			
			if(D) Log.e(TAG, "++ ON START ++");
			//OnStart에서 가져옴
			

		}

		public void onResume() {
			if(D) Log.e(TAG, "++ ON RESUME ++");
			super.onResume();
			mCount = 0;
			//mLocMan.requestLocationUpdates(mProvider,  3000,  10,  mListener);
			//mStatus.setText("현재 상태 : 서비스 시작");
			manager.requestLocationUpdates(provider,0,0, listener);
			
	        // Performing this check in onResume() covers the case in which BT was
	        // not enabled during onStart(), so we were paused to enable it...
	        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
			
			if(ResumeCount == 0)	//Resume을 한 횟수가 0이라면 실행
			{ 
			say("애플리케이션에서 블루투스를 켜는 권한을 요청하고 있습니다. 허용하시려면 오른쪽 버튼을 눌러주세요!");
			// 블루투스 활성화 요청
			if(!mBluetoothAdapter.isEnabled()) {
				
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
					//허용 안내 메시지
				
			} else {
	            if (mChatService == null) setupChat();	//채팅 셋업 하는듯
	            ResumeCount=1;	//1이 되면 권한요청을 하지 않는다.
	        }
			
			}
			
			
	        if (mChatService != null) {
	            // Only if the state is STATE_NONE, do we know that we haven't started already
	            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
	              // Start the Bluetooth chat services
	              mChatService.start();
	            }
	        }
	        

	        if(D) Log.e(TAG, "++ ON RESUME COMPLETE ++");
		}
		
		   private void setupChat() {
		        Log.d(TAG, "setupChat()");

		        // Initialize the array adapter for the conversation thread
		        // 통신 관련 쓰레드 초기화
		        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		        mConversationView = (ListView) findViewById(R.id.in);
		        mConversationView.setAdapter(mConversationArrayAdapter);

		        // Initialize the compose field with a listener for the return key
		        // send 할 edit text 초기화
		        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		        mOutEditText.setOnEditorActionListener(mWriteListener);

		        // Initialize the send button with a listener that for click events
		        // sendbutton 초기화 및 기능 설정(메시지 문자열 배열에 보내기)
		       
		        
		        // Initialize the BluetoothChatService to perform bluetooth connections
		        // 블루투스 커넥션을 수행하기 위한 블루투스 서비스 초기화
		        mChatService = new BluetoothChatService(this, mHandler);

		        // Initialize the buffer for outgoing messages
		        // 출력 메시지 배열을 초기화
		        mOutStringBuffer = new StringBuffer("");
		       
		        Log.d(TAG, "setupChatComplete()");
		    }
		
		public void onPause() {
			super.onPause();
			mCount = 0;
			//mStatus.setText("현재 상태 : 서비스 정지");
			manager.removeUpdates(listener);

		}
		
		public void onStop() {
			super.onStop();
		}
		
	    @Override
	    public void onDestroy() {
	        //TTS 종료 설정
	    	if(talker != null)	{
	    		talker.stop();
	    		talker.shutdown();
	    	}
	    		    	
	    	
	    	super.onDestroy();
	        // Stop the Bluetooth chat services
	        if (mChatService != null) mChatService.stop();
	        
	        
	    }
	    
		LocationListener mListener = new LocationListener()	{
			
			public void onLocationChanged(Location location) {
				//mResult.setText(sloc);
			}
			
			public void onProviderDisabled(String provider)	{
				//mStatus.setText("현재 상태 : 서비스 사용 불가");
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				//mStatus.setText("현재 상태 : 서비스 사용 가능");
			}
			
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				String sStatus = "";
				switch(status)	{
				case LocationProvider.OUT_OF_SERVICE:
					sStatus = "범위 벗어남";
					break;
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					sStatus = "일시적 불능";
					break;
				case LocationProvider.AVAILABLE:
					sStatus = "사용 가능";
					break;
				}
			}
					
		};
		
	    private void ensureDiscoverable() {
	        if(D) Log.d(TAG, "ensure discoverable");
	        if (mBluetoothAdapter.getScanMode() !=
	            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
	            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
	            startActivity(discoverableIntent);
	        }
	    }
	    
	    // 메시지를 보내는 메소드
	    private void sendMessage(String message) {
	        // Check that we're actually connected before trying anything
	    	// 커넥트 된 상태가 아니라면 그에 맞는 토스트 메시지 출력
	        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
	            //Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
	            return;
	        }

	        // Check that there's actually something to send
	        // 만약 메시지 길이가 0 이상이라면 보낸다.
	        if (message.length() > 0) {
	            // Get the message bytes and tell the BluetoothChatService to write
	        	// 메시지를 바이트 단위로 읽어서 보낸다.
	            byte[] send = message.getBytes();
	            mChatService.write(send);
	            
	            // Reset out string buffer to zero and clear the edit text field
	            // 버퍼 초기화 및 텍스트 필드 초기화
	            mOutStringBuffer.setLength(0);
	            mOutEditText.setText(mOutStringBuffer);
	        }
	    }
	    
	    // The action listener for the EditText widget, to listen for the return key
	    // EditText의 액션리스너는 리턴키를 기다리고 있다.
	    private TextView.OnEditorActionListener mWriteListener =
	        new TextView.OnEditorActionListener() {
	        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
	            // If the action is a key-up event on the return key, send the message
	        	// 만약 액션이 키업 이벤트? 라면 메시지를 보낸다.(정확히 모르겠다.)
	            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
	                String message = view.getText().toString();
	                sendMessage(message);	//무슨 메시지 보내는건 확실한듯
	            }
	            if(D) Log.i(TAG, "END onEditorAction");
	            return true;
	        }
	    };
	    
	    private final void setStatus(int resId) {	//상태설정
	        final ActionBar actionBar = getActionBar();
	        actionBar.setSubtitle(resId);
	    }

	    private final void setStatus(CharSequence subTitle) {	
	        final ActionBar actionBar = getActionBar();
	        actionBar.setSubtitle(subTitle);
	    }
	    
	    
	 // The Handler that gets information back from the BluetoothChatService
	    private final Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {// 메시지를 핸들링 하는듯
	            switch (msg.what) {	//1중 스위치
	            case MESSAGE_STATE_CHANGE:	//만약 메시지 상태가 바뀌면
	                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
	                switch (msg.arg1) {	//2중 스위치
	                case BluetoothChatService.STATE_CONNECTED:
	                    setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
	                    mConversationArrayAdapter.clear();
	                    break;
	                case BluetoothChatService.STATE_CONNECTING:
	                    setStatus(R.string.title_connecting);
	                    break;
	                case BluetoothChatService.STATE_LISTEN:
	                case BluetoothChatService.STATE_NONE:
	                    setStatus(R.string.title_not_connected);
	                    break;
	                }
	                break;
	            case MESSAGE_WRITE:	//2 메시지 라이트
	                byte[] writeBuf = (byte[]) msg.obj;
	                // construct a string from the buffer
	                String writeMessage = new String(writeBuf);
	                mConversationArrayAdapter.add("Me:  " + writeMessage);
	                break;
	            case MESSAGE_READ:	//2 메시지 리드
	                byte[] readBuf = (byte[]) msg.obj;
	                // construct a string from the valid bytes in the buffer
	                String readMessage = new String(readBuf, 0, msg.arg1);
	                mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
	                break;
	            case MESSAGE_DEVICE_NAME:	//2 메시지 디바이스 네임
	                // save the connected device's name
	                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
	                Toast.makeText(getApplicationContext(), "Connected to "
	                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
	                break;
	            case MESSAGE_TOAST:	//2 메시지 토스트
	                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
	                               Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };
	    
	    

	    
	 // 액티비티의 결과 메소드
	    @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if(D) Log.d(TAG, "onActivityResult " + resultCode);
	        
	        if(Intent_status == 1)
	        {
	        
	        super.onActivityResult(requestCode, resultCode, data);
	        TextView blinder_name = (TextView)findViewById(R.id.blinder_name);
	        TextView protecter_name = (TextView)findViewById(R.id.protecter_name);
	        TextView protecter_pn = (TextView)findViewById(R.id.protecter_pn);
	        
	        if(resultCode == RESULT_OK)
	        {
	        	if(requestCode == 1)
	        	{
	        		blinder_name.setText(data.getStringExtra("data_blinder_name"));
	        		protecter_name.setText(data.getStringExtra("data_protecter_name"));
	        		protecter_pn.setText(data.getStringExtra("data_protecter_pn"));
	        		
	        		B_val.data_blinder_name = data.getStringExtra("data_blinder_name");
	        		B_val.data_protecter_name = data.getStringExtra("data_protecter_name");
	        		B_val.data_protecter_pn = data.getStringExtra("data_protecter_pn");
	        		// 값 넘기기
	        	}
	        }
	        
	        	Intent_status = -1;
	        }
	        else if(Intent_status == 2)
	        {
	        	
		        switch (requestCode) {	//2번째 매개변수인 resultCode
		        case REQUEST_CONNECT_DEVICE_SECURE:		// 보안 연결
		            // When DeviceListActivity returns with a device to connect
		            if (resultCode == Activity.RESULT_OK) {
		                connectDevice(data, true);
		            }
		            break;
		        case REQUEST_CONNECT_DEVICE_INSECURE:	// 보안되지 않은 연결
		            // When DeviceListActivity returns with a device to connect
		            if (resultCode == Activity.RESULT_OK) {
		                connectDevice(data, false);
		            }
		            break;
		        case REQUEST_ENABLE_BT:	//블루투스에게 요청 인에이블
		            // When the request to enable Bluetooth returns
		            if (resultCode == Activity.RESULT_OK) {
		                // Bluetooth is now enabled, so set up a chat session
		                setupChat();
		            } else {
		                // User did not enable Bluetooth or an error occurred
		                Log.d(TAG, "BT not enabled");
		                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
		                finish();
		            }
		        }
		        Intent_status = -1;
	        }
	        
	    }
	    
	    
	    
	    private void connectDevice(Intent data, boolean secure) {	//연결된 디바이스
	        // Get the device MAC address
	        String address = data.getExtras()
	            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);	//디바이스의 맥어드레스를 가져옴
	        // Get the BluetoothDevice object
	        // 블루투스 디바이스 객체의 리모트 디바이스 주소를 들고온다.
	        //BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
	        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("00:01:95:1A:82:BC");
	        // Attempt to connect to the device
	        // 블루투스 디바이스와 연결 시도
	        
	        setupChat();	//setupChat을 하니 어플이 죽지 않는다.
	        mChatService.connect(device, secure);
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        Intent serverIntent = null;
	        switch (item.getItemId()) {
	        case R.id.secure_connect_scan:
	            // Launch the DeviceListActivity to see devices and do scan
	        	// 보안된 연결을 스캔할 경우
	            serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
	            Intent_status = 2;	//Intent_status 는 무슨 인텐트를 호출하는지 알 수 있게한다.
	            return true;
	        case R.id.insecure_connect_scan:
	            // Launch the DeviceListActivity to see devices and do scan
	        	// 보안되지 않은 연결을 스캔할 경우
	            serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
	            Intent_status = 2;	//	""
	            return true;
	        case R.id.discoverable:
	            // Ensure this device is discoverable by others
	        	// 스마트폰을 다른 기기에서 페어링 가능한 상태로 설정
	            ensureDiscoverable();
	            Intent_status = 2;	//	""
	            return true;
	        }
	        return false;
	    }
	    	    
	    //TTS를 위한 메소드 정의
	    
	    public void say(String text2say)	{
	    	talker.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);
	    }
	    
	    public void onInit(int status)	{
	    	say("시각장애인을 위한 웨어러블 컴퓨터 어플리케이션");
	    }
	    
	    public Bitmap byteArrayToBitmap( byte[] $byteArray ) {  
	        Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;  
	        return bitmap ;  
	     } 	    

		}





