package com.example.blinder_ver01;

import android.location.*;
import android.os.Bundle;
import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

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

public class MainActivity extends Activity {
	View mPage1, mPage2, mPage3, mPage4;
	
	GoogleMap map;	//1) 현재 위칙로 맵을 가져오는 변수
	LocationManager manager;
	
	LocationManager mLocMan;
	TextView mStatus;
	TextView mResult;
	String mProvider;
	int mCount;
	double latitude;
	double longitude;
	
	String provider;
	
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    Handler mHandler = new Handler();

    private static final int REQUEST_ENABLE_BT = 3;	// 2)블루투스를 사용하기 위한 상수, 변수

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		//1)맵을 읽어옴
		
		manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			provider = LocationManager.GPS_PROVIDER;
		}
		else
		{
			provider = LocationManager.NETWORK_PROVIDER;
		}
		
		Location location = manager.getLastKnownLocation(provider);
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(new LatLng(location.getLatitude(),location.getLongitude()))
		.zoom(13)                   
		.bearing(0)                
		.tilt(0)                 
		.build();      
		
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		map.setMyLocationEnabled(true);	//1)현재 위치로 맵을 띄움
		
		mLocMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mStatus = (TextView)findViewById(R.id.status);
		mResult = (TextView)findViewById(R.id.result);
		
		mProvider = mLocMan.getBestProvider(new Criteria(), true);
		// 1)상태 및 결과를 띄우기 위한 텍스트 뷰 설정
		
		 // 내 기기의 블루투스 가능 여부
		
		//BroadcastReceiverIntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND;
		//registerReceiver(mReceiver, filter);
		
        mBluetoothAdapter  = BluetoothAdapter.getDefaultAdapter(); 
        if(mBluetoothAdapter == null) {
        	Toast.makeText(this, "블루투스를 사용할 수 없습니다.", Toast.LENGTH_LONG).show();
        	finish();
        	return ;
        }
        
        // 13. 11. 07 추가
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
		    for (BluetoothDevice device : pairedDevices) {
		        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		    }
		}
        //
		//mBluetoothAdapter.startDiscovery();
		
		/*각 페이지 아이디 설정 및 클릭 리스너 설정 */
		mPage1 = findViewById(R.id.page1);
		mPage2 = findViewById(R.id.page2);
		mPage3 = findViewById(R.id.page3);
		mPage4 = findViewById(R.id.page4);
		
		findViewById(R.id.btnpage1).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage2).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage3).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage4).setOnClickListener(mClickListener);
	}
	
	Button.OnClickListener mClickListener = new Button.OnClickListener()	{
		
		public void onClick(View v)	{
			mPage1.setVisibility(View.VISIBLE);
			mPage2.setVisibility(View.INVISIBLE);
			mPage3.setVisibility(View.INVISIBLE);
			mPage4.setVisibility(View.INVISIBLE);
			
			switch(v.getId())	{
			case R.id.btnpage1:
				mPage1.setVisibility(View.VISIBLE);
				mPage2.setVisibility(View.INVISIBLE);
				mPage3.setVisibility(View.INVISIBLE);
				mPage4.setVisibility(View.INVISIBLE);
				break;
			case R.id.btnpage2:
				mPage1.setVisibility(View.INVISIBLE);
				mPage2.setVisibility(View.VISIBLE);
				mPage3.setVisibility(View.INVISIBLE);
				mPage4.setVisibility(View.INVISIBLE);
				break;
			case R.id.btnpage3:
				mPage1.setVisibility(View.INVISIBLE);
				mPage2.setVisibility(View.INVISIBLE);
				mPage3.setVisibility(View.VISIBLE);
				mPage4.setVisibility(View.INVISIBLE);
				break;
			case R.id.btnpage4:
				mPage1.setVisibility(View.INVISIBLE);
				mPage2.setVisibility(View.INVISIBLE);
				mPage3.setVisibility(View.INVISIBLE);
				mPage4.setVisibility(View.VISIBLE);
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
			}
			public void onProviderDisabled(String provider){}
			public void onProviderEnabled(String provider) {}
			public void onStatusChanged(String provider, int status, Bundle extras){}
		}
		
		public void onStart()	{
			super.onStart();
			// 블루투스 활성화 요청
			if(!mBluetoothAdapter.isEnabled()) {
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); 
				startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			}

		}
		
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			switch(requestCode) {
			case REQUEST_ENABLE_BT:
				if(resultCode == Activity.RESULT_OK) {
					Toast.makeText(this, "블루투스를 활성화하였습니다.", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, "블루투스를 활성화하지 못했습니다.", Toast.LENGTH_LONG).show();
				}
			}
		}
		
		public void onResume() {
			super.onResume();
			mCount = 0;
			mLocMan.requestLocationUpdates(mProvider,  3000,  10,  mListener);
			mStatus.setText("현재 상태 : 서비스 시작");


		}
		
		public void onPause() {
			super.onPause();
			mCount = 0;
			mStatus.setText("현재 상태 : 서비스 정지");
			
		}
		
		LocationListener mListener = new LocationListener()	{
			
			public void onLocationChanged(Location location) {
				mCount++;
				String sloc = String.format("위도 : %f\n 경도 : %f\n", location.getLatitude(), location.getLongitude());
				latitude = location.getLatitude();	//위도값을 넣음
				longitude = location.getLongitude();	//경도값을 넣음 - 다른 어플에 전송예정
				mResult.setText(sloc);
			}
			
			public void onProviderDisabled(String provider)	{
				mStatus.setText("현재 상태 : 서비스 사용 불가");
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				mStatus.setText("현재 상태 : 서비스 사용 가능");
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
		
		BroadcastReceiver mReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		            mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		        }
		    }
		};//
		
		private class ConnectThread extends Thread {
		    private final BluetoothSocket mmSocket;
		    private final BluetoothDevice mmDevice;
		    public ConnectThread(BluetoothDevice device) {
		        // Use a temporary object that is later assigned to mmSocket,
		        // because mmSocket is final
		        BluetoothSocket tmp = null;
		        mmDevice = device;
		        // Get a BluetoothSocket to connect with the given BluetoothDevice
		        try {
		            // MY_UUID is the app's UUID string, also used by the server code
		            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("d0c722b0-7e15-11e1-b0c4-0800200c9a66"));
		        } catch (IOException e) { }
		        mmSocket = tmp;
		    }

		    public void run() {
		        BluetoothAdapter mAdapter;
				// Cancel discovery because it will slow down the connection
		        mBluetoothAdapter.cancelDiscovery();
		        try {
		            // Connect the device through the socket. This will block
		            // until it succeeds or throws an exception
		            mmSocket.connect();
		        } catch (IOException connectException) {
		            // Unable to connect; close the socket and get out
		            try {
		                mmSocket.close();
		            } catch (IOException closeException) { }
		            return;
		        }
		        // Do work to manage the connection (in a separate thread)
		        manageConnectedSocket(mmSocket);
		    }

			private void manageConnectedSocket(BluetoothSocket mmSocket2) {
				// TODO Auto-generated method stub
				//
			}

			/** Will cancel an in-progress connection, and close the socket */
		    public void cancel() {
		        try {
		            mmSocket.close();
		        } catch (IOException e) { }
		    }
		    
		    private class ConnectedThread extends Thread {
		       
				private static final int MESSAGE_READ = 0;
				private final BluetoothSocket mmSocket;
		        private final InputStream mmInStream;
		        private final OutputStream mmOutStream;

		        public ConnectedThread(BluetoothSocket socket) {
		            mmSocket = socket;
		            InputStream tmpIn = null;
		            OutputStream tmpOut = null;
		            // Get the input and output streams, using temp objects because
		            // member streams are final
		            try {
		                tmpIn = socket.getInputStream();
		                tmpOut = socket.getOutputStream();
		            } catch (IOException e) { }
		            mmInStream = tmpIn;
		            mmOutStream = tmpOut;
		        }

		        public void run() {
		            byte[] buffer = new byte[1024];    // buffer store for the stream
		            int bytes; // bytes returned from read()
		            // Keep listening to the InputStream until an exception occurs
		            while (true) {
		                try {
		                    // Read from the InputStream
		                    bytes = mmInStream.read(buffer);
		                    // Send the obtained bytes to the UI Activity
		                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
		                            .sendToTarget();
		                } catch (IOException e) {
		                    break;
		                }
		            }
		        }
		        /* Call this from the main Activity to send data to the remote device */
		        public void write(byte[] bytes) {
		            try {
		                mmOutStream.write(bytes);
		            } catch (IOException e) { }
		        }

		        /* Call this from the main Activity to shutdown the connection */
		        public void cancel() {
		            try {
		                mmSocket.close();
		            } catch (IOException e) { }
		        }
		    }
		    
		}

}

